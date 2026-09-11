# -*- coding: utf-8 -*-
"""F 约束词法门禁（Plan B，grill Q2；frontend-design-system §9.1 F1/F2/F6）。

detekt 2.0 处于 alpha（F6 规则加载不可靠，2026-09-11 实测），本脚本承担 F1/F2/F6 门禁。
detekt 2.0 转正后迁移回 buildLogic/detekt-rebecca（规则本体保留在仓库），届时删除本脚本。

用法：python scripts/lint/f_gates.py [root]
退出码：0 = 全过；1 = 存在违规。
"""
import re
import sys
from dataclasses import dataclass
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent.parent
SCAN_DIR = ROOT / "app" / "shared" / "src" / "commonMain" / "kotlin"

# ---- 白名单与豁免（与 detekt 规则语义同构）----
F1_WHITELIST_DIRS = ("design/tokens/", "design/theme/palettes/")
F1_EXEMPT_CONSTANTS = ("Transparent", "Unspecified")          # 无色彩语义，全库通用
F2_WHITELIST_VALUES = {"0.dp", "1.dp", "2.dp", "0.sp", "1.sp", "2.sp"}  # 0/1/2 dp/sp 白名单
F6_EXEMPT_SUFFIX = "Test.kt"
F6_EXEMPT_DIRS = ("design/showcase/", "feature/showcase/")   # 陈列屏（F6 豁免：showcase 文案是示例数据）

# ---- 词法模式（与 detekt 规则同一判定口径）----
# F1a: Color(0x...) 字面量构造
F1_CALL_RE = re.compile(r"Color\s*\(\s*0x[0-9A-Fa-f]{6,8}\s*\)")
# F1b: Color.White 等常量访问（排除豁免常量）
F1_CONST_RE = re.compile(r"Color\.([A-Z][A-Za-z0-9]*)\b")
# F2: 魔法 dp/sp（整数或小数）
F2_DIM_RE = re.compile(r"\b\d+\.\d*\s*\.\s*(dp|sp)\b|\b\d+\s*\.\s*(dp|sp)\b")
# F6: Text("...") 字面量调用
F6_TEXT_RE = re.compile(r"\bText\s*\(\s*\"[^\"]*\"\s*[),]")


@dataclass(frozen=True)
class Violation:
    rule: str
    rel_path: str
    lineno: int
    text: str

    def __str__(self) -> str:
        return f"{self.rel_path}:{self.lineno}: {self.rule}: {self.text.strip()[:120]}"


def _rel(path: Path) -> str:
    try:
        return path.relative_to(ROOT).as_posix()
    except ValueError:
        return path.as_posix()  # 统一正斜杠（白名单按正斜杠匹配；Windows 路径不能直接 str()）


def scan_file(path: Path) -> list[Violation]:
    """对单个 Kotlin 文件跑 F1/F2/F6，返回违规列表。"""
    rel = _rel(path)
    violations: list[Violation] = []
    try:
        text = path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return violations

    in_whitelist_dir = any(seg in rel for seg in F1_WHITELIST_DIRS)
    in_f6_exempt_dir = any(d in rel for d in F6_EXEMPT_DIRS)
    is_test = rel.endswith(F6_EXEMPT_SUFFIX)
    has_preview = "@Preview" in text and "/design/" in rel  # 含 @Preview 的 design/ 文件豁免 F6

    lines = text.splitlines()

    # ---- F1 ----
    if not in_whitelist_dir:
        for i, line in enumerate(lines, 1):
            for m in F1_CALL_RE.finditer(line):
                violations.append(Violation("F1", rel, i, m.group(0)))
            for m in F1_CONST_RE.finditer(line):
                if m.group(1) not in F1_EXEMPT_CONSTANTS:
                    violations.append(Violation("F1", rel, i, m.group(0)))

    # ---- F2 ----
    if "design/tokens/" not in rel:
        for i, line in enumerate(lines, 1):
            for m in F2_DIM_RE.finditer(line):
                dim = m.group(0).replace(" ", "")
                if dim not in F2_WHITELIST_VALUES:
                    violations.append(Violation("F2", rel, i, m.group(0)))

    # ---- F6 ----
    if not is_test and not in_f6_exempt_dir and not has_preview:
        for i, line in enumerate(lines, 1):
            for m in F6_TEXT_RE.finditer(line):
                violations.append(Violation("F6", rel, i, m.group(0)))

    return violations


def scan(root: Path = SCAN_DIR) -> list[Violation]:
    violations: list[Violation] = []
    for kt in sorted(root.rglob("*.kt")):
        violations.extend(scan_file(kt))
    return violations


def main(argv: list[str]) -> int:
    root = Path(argv[1]) if len(argv) > 1 else SCAN_DIR
    violations = scan(root)
    if violations:
        for v in violations:
            print(f"e: {v}")
        print(f"f_gates: FAIL ({len(violations)} violations)")
        return 1
    print("f_gates: PASS")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))

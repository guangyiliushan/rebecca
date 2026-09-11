# -*- coding: utf-8 -*-
"""COMPONENTS.md 生成器（frontend-design-system §9.2-5：单一真源 = ShowcaseRegistry.kt）。

用法：python scripts/lint/gen_components_md.py
输出：app/shared/src/commonMain/kotlin/top/guangyiliushan/rebecca/design/COMPONENTS.md
"""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent.parent
REGISTRY = (
    ROOT
    / "app/shared/src/commonMain/kotlin/top/guangyiliushan/rebecca/design/showcase"
    / "ShowcaseRegistry.kt"
)
OUT = (
    ROOT
    / "app/shared/src/commonMain/kotlin/top/guangyiliushan/rebecca/design"
    / "COMPONENTS.md"
)

ENTRY_RE = re.compile(
    r"ShowcaseEntry\(\s*component\s*=\s*\"(\w+)\",\s*variants\s*=\s*listOf\((.*?)\),\s*a11y\s*=\s*listOf\((.*?)\),\s*\)",
    re.S,
)
STR_RE = re.compile(r"\"([^\"]*)\"")


def _strings(block: str) -> list[str]:
    return STR_RE.findall(block)


def generate() -> str:
    src = REGISTRY.read_text(encoding="utf-8")
    lines = [
        "# COMPONENTS.md — 组件目录（由 ShowcaseRegistry.kt 生成，勿手改）",
        "",
        "> 单一真源：`design/showcase/ShowcaseRegistry.kt`。本文件由",
        "> `scripts/lint/gen_components_md.py` 生成；AI/新人入口：先读本文件再写组件。",
        "",
        "## 组件清单",
        "",
    ]
    for m in ENTRY_RE.finditer(src):
        name, variants_block, a11y_block = m.group(1), m.group(2), m.group(3)
        variants = ", ".join(_strings(variants_block))
        a11y = "; ".join(_strings(a11y_block))
        lines.append(f"### {name}")
        lines.append("")
        lines.append(f"- **变体**: {variants}")
        lines.append(f"- **语义承诺（F15）**: {a11y}")
        lines.append("")
    return "\n".join(lines)


def main(argv: list[str]) -> int:
    OUT.write_text(generate(), encoding="utf-8", newline="\n")
    print(f"generated: {OUT}")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))

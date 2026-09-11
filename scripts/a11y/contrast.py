# -*- coding: utf-8 -*-
"""对比度门禁（frontend-design-system §10.3 / F16）。

单一真源：design/theme/palettes/TealPalette.kt（全仓库唯一允许 Color 字面量的文件）。
判定硬阈值：文本对 >= 4.5（WCAG AA 1.4.3）；识别性非文本（input/ring）>= 3.0（1.4.11）。
另做 doc-vs-code 一致性检查（§3.1 表格 vs TealPalette.kt）。

用法：python scripts/a11y/contrast.py
退出码：0 = 全过闸；1 = 存在不达标或文档漂移。
"""
import re
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent.parent
KT = (REPO / "app/shared/src/commonMain/kotlin/top/guangyiliushan/rebecca"
      "/design/theme/palettes/TealPalette.kt").read_text(encoding="utf-8")
DOC = (REPO.parent / "architecture/frontend-design-system.md").read_text(encoding="utf-8")


def lum(hex6: str) -> float:
    def f(c: float) -> float:
        return c / 12.92 if c <= 0.03928 else ((c + 0.055) / 1.055) ** 2.4
    r, g, b = (int(hex6[i:i + 2], 16) / 255 for i in (0, 2, 4))
    return 0.2126 * f(r) + 0.7152 * f(g) + 0.0722 * f(b)


def ratio(a: str, b: str) -> float:
    la, lb = lum(a), lum(b)
    hi, lo = max(la, lb), min(la, lb)
    return (hi + 0.05) / (lo + 0.05)


# ---- 1) 从 TealPalette.kt 取色（唯一真源）----
code: dict[str, dict[str, str]] = {}
cur = None
for m in re.finditer(r"(light|dark)\s*=\s*AppColors\(|(\w+)\s*=\s*Color\(0xFF([0-9A-Fa-f]{6})\)", KT):
    if m.group(1):
        cur = m.group(1)
    elif m.group(2) and cur:
        code.setdefault(cur, {})[m.group(2)] = m.group(3).upper()

# ---- 2) doc §3.1 表格值（一致性检查）----
doc: dict[str, dict[str, str]] = {"light": {}, "dark": {}}
for line in DOC.splitlines():
    cells = line.split("|")
    if len(cells) < 5:
        continue
    names = re.findall(r"`(\w+)`", cells[1])
    if not names:
        continue
    l = re.findall(r"`#([0-9A-Fa-f]{6})`", cells[2])
    d = re.findall(r"`#([0-9A-Fa-f]{6})`", cells[3])
    if not l or not d:
        continue
    if len(names) == 2 and len(l) == 2 and len(d) == 2:
        doc["light"][names[0]] = l[0].upper()
        doc["light"][names[1]] = l[1].upper()
        doc["dark"][names[0]] = d[0].upper()
        doc["dark"][names[1]] = d[1].upper()
    elif len(names) == 1:
        doc["light"][names[0]] = l[0].upper()
        doc["dark"][names[0]] = d[0].upper()

# ---- 3) doc-vs-code ----
fail = 0
for theme in ("light", "dark"):
    for slot in doc[theme]:
        if code.get(theme, {}).get(slot) != doc[theme][slot]:
            print(f"MISMATCH {theme}.{slot}: doc={doc[theme][slot]} code={code.get(theme, {}).get(slot)}")
            fail += 1
print(f"doc-vs-code: {'PASS' if fail == 0 else f'FAIL({fail})'}")

# ---- 4) 硬阈值 ----
TEXT_PAIRS = [
    ("background", "onBackground"), ("surface", "onSurface"),
    ("muted", "onMuted"), ("primary", "onPrimary"), ("accent", "onAccent"),
    ("destructive", "onDestructive"), ("success", "onSuccess"), ("warning", "onWarning"),
]
NONTEXT = [("input", "background"), ("ring", "background")]

for theme in ("light", "dark"):
    c = code[theme]
    for a, b in TEXT_PAIRS:
        r = ratio(c[a], c[b])
        if r < 4.5:
            print(f"FAIL {theme}.{a}/{b}: {r:.2f} < 4.5")
            fail += 1
    for a, b in NONTEXT:
        r = ratio(c[a], c[b])
        if r < 3.0:
            print(f"FAIL {theme}.{a}/{b}: {r:.2f} < 3.0")
            fail += 1

print("GATE:", "PASS" if fail == 0 else f"FAIL({fail})")
sys.exit(1 if fail else 0)

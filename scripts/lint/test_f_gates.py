# -*- coding: utf-8 -*-
"""f_gates 单元测试（Plan B 防漂移：构造违规/豁免样例文件断言脚本行为）。

与 buildLogic/detekt-rebecca 的 RebeccaRulesTest 同构——detekt 转正迁移时
两边测试对拍，保证词法口径一致。
"""
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent.parent / "scripts" / "lint"))
import f_gates  # noqa: E402


def _scan(contents: dict[str, str]) -> list[f_gates.Violation]:
    with tempfile.TemporaryDirectory() as tmp:
        base = Path(tmp)
        for name, text in contents.items():
            p = base / name
            p.parent.mkdir(parents=True, exist_ok=True)
            p.write_text(text, encoding="utf-8")
        return f_gates.scan(base)


class F1ColorTest(unittest.TestCase):
    def test_color_literal_reported(self):
        vs = _scan({"design/Screen.kt": "val x = Color(0xFF123456)\n"})
        self.assertEqual(1, len([v for v in vs if v.rule == "F1"]))

    def test_color_constant_reported(self):
        vs = _scan({"design/Screen.kt": "val x = Color.White\n"})
        self.assertEqual(1, len([v for v in vs if v.rule == "F1"]))

    def test_transparent_exempt(self):
        vs = _scan({"design/Screen.kt": "val x = Color.Transparent\n"})
        self.assertEqual(0, len([v for v in vs if v.rule == "F1"]))

    def test_palette_whitelisted(self):
        vs = _scan({"design/theme/palettes/Teal.kt": "val x = Color(0xFFFFFFFF)\n"})
        self.assertEqual(0, len([v for v in vs if v.rule == "F1"]))


class F2DimensionTest(unittest.TestCase):
    def test_magic_dp_reported(self):
        vs = _scan({"design/Screen.kt": "val x = 42.dp\n"})
        self.assertEqual(1, len([v for v in vs if v.rule == "F2"]))

    def test_hairline_whitelisted(self):
        vs = _scan({"design/Screen.kt": "val a = 1.dp\nval b = 0.dp\n"})
        self.assertEqual(0, len([v for v in vs if v.rule == "F2"]))

    def test_tokens_whitelisted(self):
        vs = _scan({"design/tokens/AppSpacing.kt": "val x = 16.dp\n"})
        self.assertEqual(0, len([v for v in vs if v.rule == "F2"]))


class F6TextTest(unittest.TestCase):
    def test_hardcoded_text_reported(self):
        vs = _scan({"design/Screen.kt": 'fun s() { Text("hardcoded") }\n'})
        self.assertEqual(1, len([v for v in vs if v.rule == "F6"]))

    def test_test_file_exempt(self):
        vs = _scan({"design/ScreenTest.kt": 'fun s() { Text("hardcoded") }\n'})
        self.assertEqual(0, len([v for v in vs if v.rule == "F6"]))

    def test_showcase_exempt(self):
        vs = _scan({"design/showcase/S.kt": 'fun s() { Text("hardcoded") }\n'})
        self.assertEqual(0, len([v for v in vs if v.rule == "F6"]))

    def test_feature_showcase_exempt(self):
        vs = _scan({"feature/showcase/S.kt": 'fun s() { Text("hardcoded") }\n'})
        self.assertEqual(0, len([v for v in vs if v.rule == "F6"]))

    def test_preview_file_exempt(self):
        vs = _scan(
            {"design/Screen.kt": "@Preview\nfun s() { Text(\"hardcoded\") }\n"},
        )
        self.assertEqual(0, len([v for v in vs if v.rule == "F6"]))

    def test_catalog_usage_not_reported(self):
        vs = _scan({"design/Screen.kt": 'fun s() { Text(stringResource(Res.string.x)) }\n'})
        self.assertEqual(0, len([v for v in vs if v.rule == "F6"]))


if __name__ == "__main__":
    unittest.main()

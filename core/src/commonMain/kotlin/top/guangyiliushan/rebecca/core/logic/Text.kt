package top.guangyiliushan.rebecca.core.logic

/** v1 通用实现（英文标点切句）；语言相关分词 0.7.x 经 LanguageProfile 适配器替换。 */
fun splitSentences(text: String): List<String> {
    val out = mutableListOf<String>()
    val sb = StringBuilder()
    for (ch in text) {
        sb.append(ch)
        if (ch == '.' || ch == '!' || ch == '?') {
            val s = sb.toString().trim()
            if (s.isNotEmpty()) out += s
            sb.clear()
        }
    }
    val tail = sb.toString().trim()
    if (tail.isNotEmpty()) out += tail
    return out
}

/** v1 通用实现（Unicode 字母 + 撇号）；返回去重小写候选词。 */
fun extractWords(text: String): List<String> =
    Regex("[\\p{L}']+").findAll(text).map { it.value.lowercase() }.toList().distinct()

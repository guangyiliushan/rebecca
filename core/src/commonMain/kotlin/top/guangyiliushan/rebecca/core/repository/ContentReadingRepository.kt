package top.guangyiliushan.rebecca.core.repository

import top.guangyiliushan.rebecca.core.model.Book

/** 内容平面·书库（只读）。 */
interface ContentReadingRepository {
    fun books(): List<Book>
    fun book(id: String): Book?
}

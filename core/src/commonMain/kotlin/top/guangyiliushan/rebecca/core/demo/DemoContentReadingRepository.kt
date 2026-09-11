package top.guangyiliushan.rebecca.core.demo

import top.guangyiliushan.rebecca.core.model.Book
import top.guangyiliushan.rebecca.core.repository.ContentReadingRepository

internal object DemoContentReadingRepository : ContentReadingRepository {
    override fun books(): List<Book> = DEMO_BOOKS

    override fun book(id: String): Book? = DEMO_BOOKS.firstOrNull { it.id == id }
}

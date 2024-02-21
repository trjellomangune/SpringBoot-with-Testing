package twist.resources.codingexercise.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import twist.resources.codingexercise.model.Author;
import twist.resources.codingexercise.model.Book;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BookControllerTest {
    @Autowired
    BookController bookController;

    @Test
    void getBooks_ExistingBooks_ReturnListOfBooks() {
        assertThat(bookController.getBooks())
                .contains(Book
                        .builder()
                        .id(1)
                        .name("Angel Beats")
                        .author(Author.builder()
                                .id(1)
                                .name("Jun Maeda")
                                .build())
                        .build());
    }

    @Test
    void saveBook_NewBook_ReturnsCreated() {
        String authorName = "New Author";
        String bookName = "New Book";

        Book savedBook = bookController.saveBook(Map.of("author_name", authorName, "book_name", bookName)).getBody();

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getName()).isEqualTo(bookName);
        assertThat(savedBook.getAuthor().getName()).isEqualTo(authorName);
    }

    @Test
    void getBooksByName_ExistingBooks_ReturnListOfMatchingBooks() {
        String bookName = "Angel Beats";

        assertThat(bookController.getBooksByName(bookName))
                .extracting(Book::getName)
                .contains(bookName);
    }

    @Test
    void updateBookAndAuthor_LastBook_ReturnsUpdatedBook() {
        List<Book> books = bookController.getBooks();
        int lastBookId = books.get(books.size() - 1).getId();
        String newAuthorName = "Updated Author";
        String newBookName = "Updated Book";

        Book updatedBook = bookController.updateBookAndAuthor(lastBookId, Map.of("author_name", newAuthorName, "book_name", newBookName)).getBody();

        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getName()).isEqualTo(newBookName);
        assertThat(updatedBook.getAuthor().getName()).isEqualTo(newAuthorName);
    }

    @Test
    void deleteBookById_LastBook_ReturnsSuccessMessage() {
        List<Book> books = bookController.getBooks();
        int lastBookId = books.get(books.size() - 1).getId();

        Map<String, Object> response = bookController.deleteBookById(lastBookId).getBody();

        assertThat(response).isNotNull();
        assertThat(response.get("message")).isEqualTo("Book deleted successfully");
    }
}

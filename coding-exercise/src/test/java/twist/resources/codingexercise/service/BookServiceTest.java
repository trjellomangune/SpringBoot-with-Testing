package twist.resources.codingexercise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import twist.resources.codingexercise.entity.AuthorEntity;
import twist.resources.codingexercise.entity.BookEntity;
import twist.resources.codingexercise.model.Author;
import twist.resources.codingexercise.model.Book;
import twist.resources.codingexercise.repository.AuthorRepository;
import twist.resources.codingexercise.repository.BookRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    AuthorRepository authorRepository;

    @InjectMocks
    BookService bookService;

    @BeforeEach
    void setUp(){
        bookService = new BookService(bookRepository, authorRepository);
    }

    @Test
    void findAll_ShouldReturn_AllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(BookEntity
                .builder()
                .id(1)
                .name("Angel Beats")
                .author(AuthorEntity.builder()
                        .id(1)
                        .name("Jun Maeda")
                        .build())
                .build()));
        assertThat(bookService.getBooks()).containsExactlyInAnyOrder(Book
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
    void findAllByName_ShouldReturn_AllBookContainingName() {
        when(bookRepository.findAllByNameContainingIgnoreCase("Angel")).thenReturn(List.of(BookEntity
                .builder()
                .id(1)
                .name("Angel Beats")
                .author(AuthorEntity.builder()
                        .id(1)
                        .name("Jun Maeda")
                        .build())
                .build()));
        assertThat(bookService.getBooksByName("Angel")).contains(Book
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
    void saveBook_ShouldSave_BookWithExistingAuthor() {
        when(authorRepository.findByName("Jun Maeda"))
                .thenReturn(List.of(AuthorEntity.builder()
                        .id(1)
                        .name("Jun Maeda")
                        .build()));

        BookEntity mockBookEntity = BookEntity.builder()
                .id(1)
                .name("Angel Beats")
                .author(AuthorEntity.builder().id(1).name("Jun Maeda").build())
                .build();
        when(bookRepository.save(any(BookEntity.class)))
                .thenReturn(mockBookEntity);

        Book savedBook = bookService.saveBookAndAuthor("Jun Maeda", "Angel Beats");

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isEqualTo(1);
        assertThat(savedBook.getName()).isEqualTo("Angel Beats");
        assertThat(savedBook.getAuthor()).isNotNull();
        assertThat(savedBook.getAuthor().getId()).isEqualTo(1);
        assertThat(savedBook.getAuthor().getName()).isEqualTo("Jun Maeda");
    }

    @Test
    void saveBook_ShouldSave_BookWithoutExistingAuthor() {
        when(authorRepository.findByName("Jun Maeda")).thenReturn(Collections.emptyList());

        AuthorEntity savedAuthorEntity = AuthorEntity.builder()
                .id(1)
                .name("Jun Maeda")
                .build();
        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(savedAuthorEntity);

        BookEntity mockBookEntity = BookEntity.builder()
                .id(1)
                .name("Angel Beats")
                .author(savedAuthorEntity)
                .build();
        when(bookRepository.save(any(BookEntity.class))).thenReturn(mockBookEntity);

        Book savedBook = bookService.saveBookAndAuthor("Jun Maeda", "Angel Beats");

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isEqualTo(1);
        assertThat(savedBook.getName()).isEqualTo("Angel Beats");
        assertThat(savedBook.getAuthor()).isNotNull();
        assertThat(savedBook.getAuthor().getId()).isEqualTo(1);
        assertThat(savedBook.getAuthor().getName()).isEqualTo("Jun Maeda");
    }

    @Test
    void updateBookAndAuthor_ShouldUpdate_BookWithNewAuthor() {
        BookEntity existingBook = BookEntity.builder()
                .id(30)
                .name("Angel Beats")
                .author(AuthorEntity.builder().id(30).name("Jun Maeda").build())
                .build();

        when(bookRepository.findById(30)).thenReturn(Optional.of(existingBook));

        when(authorRepository.findByName("Key")).thenReturn(Collections.emptyList());

        when(bookRepository.findByAuthorName("Jun Maeda")).thenReturn(Collections.emptyList());

        BookEntity updatedBookEntity = BookEntity.builder()
                .id(30)
                .name("Angel Beats2")
                .author(AuthorEntity.builder().id(30).name("Key").build())
                .build();

        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(AuthorEntity.builder().id(30).name("Key").build());

        when(bookRepository.save(any(BookEntity.class))).thenReturn(updatedBookEntity);

        Book updatedBook = bookService.updateBookAndAuthor(30, "Key", "Angel Beats2");

        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getId()).isEqualTo(30);
        assertThat(updatedBook.getName()).isEqualTo("Angel Beats2");
        assertThat(updatedBook.getAuthor()).isNotNull();
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(30);
        assertThat(updatedBook.getAuthor().getName()).isEqualTo("Key");
    }

    @Test
    void updateBookAndAuthor_ShouldCreate_NewAuthor_WhenUpdated() {
        BookEntity existingBook = BookEntity.builder()
                .id(1)
                .name("Angel Beats")
                .author(AuthorEntity.builder().id(1).name("Jun Maeda").build())
                .build();

        when(bookRepository.findById(1)).thenReturn(Optional.of(existingBook));

        when(authorRepository.findByName("Key")).thenReturn(Collections.emptyList());

        when(bookRepository.findByAuthorName("Jun Maeda")).thenReturn(List.of(existingBook));

        BookEntity updatedBookEntity = BookEntity.builder()
                .id(1)
                .name("Clannad")
                .author(AuthorEntity.builder().id(2).name("Key").build())
                .build();

        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(AuthorEntity.builder().id(2).name("Key").build());

        when(bookRepository.save(any(BookEntity.class))).thenReturn(updatedBookEntity);

        Book updatedBook = bookService.updateBookAndAuthor(1, "Key", "Clannad");

        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getId()).isEqualTo(1);
        assertThat(updatedBook.getName()).isEqualTo("Clannad");
        assertThat(updatedBook.getAuthor()).isNotNull();
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(2);
        assertThat(updatedBook.getAuthor().getName()).isEqualTo("Key");
    }

    @Test
    void updateBookAndAuthor_ShouldUpdate_BookWithExistingAuthor() {
        BookEntity existingBook = BookEntity.builder()
                .id(1)
                .name("Angel Beats")
                .author(AuthorEntity.builder().id(1).name("Jun Maeda").build())
                .build();

        when(bookRepository.findById(1)).thenReturn(Optional.of(existingBook));

        BookEntity updatedBookEntity = BookEntity.builder()
                .id(1)
                .name("Clannad")
                .author(AuthorEntity.builder().id(1).name("Jun Maeda").build())
                .build();

        when(bookRepository.save(any(BookEntity.class))).thenReturn(updatedBookEntity);

        Book updatedBook = bookService.updateBookAndAuthor(1, "Jun Maeda", "Clannad");

        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getId()).isEqualTo(1);
        assertThat(updatedBook.getName()).isEqualTo("Clannad");
        assertThat(updatedBook.getAuthor()).isNotNull();
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(1);
        assertThat(updatedBook.getAuthor().getName()).isEqualTo("Jun Maeda");
    }

    @Test
    void deleteBookById_ShouldDelete_ExistingBook() {
        Integer bookId = 1;
        BookEntity existingBook = BookEntity.builder()
                .id(bookId)
                .name("Test Book")
                .author(AuthorEntity.builder().id(1).name("Test Author").build())
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

        String message = bookService.deleteBookById(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
        assertEquals("Book deleted successfully", message);
    }

    @Test
    void deleteBookById_ShouldReturn_MessageForNonExistingBook() {
        Integer bookId = 1;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        String message = bookService.deleteBookById(bookId);

        verify(bookRepository, never()).deleteById(bookId);
        assertEquals("Book not found with ID: " + bookId, message);
    }

}

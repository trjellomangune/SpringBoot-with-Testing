package twist.resources.codingexercise.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import twist.resources.codingexercise.entity.AuthorEntity;
import twist.resources.codingexercise.entity.BookEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Test
    void findAll_ShouldReturn_AllBooks() {
        assertThat(bookRepository.findAll()).contains(BookEntity
                .builder()
                .id(1)
                .name("Angel Beats")
                .author(AuthorEntity.builder()
                        .id(1)
                        .name("Jun Maeda")
                        .build())
                .build());
    }

    @Test
    void save_ShouldSave_BookWithAuthor() {
        AuthorEntity author = AuthorEntity.builder()
                .name("Test Author")
                .build();

        AuthorEntity savedAuthor = authorRepository.save(author);

        BookEntity bookToSave = BookEntity.builder()
                .name("Test Book")
                .author(savedAuthor)
                .build();

        BookEntity savedBook = bookRepository.save(bookToSave);

        assertThat(savedBook).isNotNull();

        assertThat(savedBook.getId()).isNotNull();

        assertThat(savedBook.getName()).isEqualTo("Test Book");
        assertThat(savedBook.getAuthor().getName()).isEqualTo("Test Author");

        bookRepository.delete(savedBook);
    }

}

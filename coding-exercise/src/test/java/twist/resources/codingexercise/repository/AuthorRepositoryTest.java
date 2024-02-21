package twist.resources.codingexercise.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import twist.resources.codingexercise.entity.AuthorEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AuthorRepositoryTest {
    @Autowired
    private AuthorRepository authorRepository;

    @Test
    public void whenSaveAuthor_thenAuthorIsPersisted() {
        AuthorEntity author = AuthorEntity.builder()
                .name("Test Author")
                .build();

        authorRepository.save(author);

        AuthorEntity savedAuthor = authorRepository.findById(author.getId()).orElse(null);
        assertThat(savedAuthor).isNotNull();
        assertThat(savedAuthor.getName()).isEqualTo("Test Author");

        authorRepository.delete(savedAuthor);
    }

}

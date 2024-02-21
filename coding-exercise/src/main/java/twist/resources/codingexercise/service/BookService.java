package twist.resources.codingexercise.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import twist.resources.codingexercise.entity.AuthorEntity;
import twist.resources.codingexercise.entity.BookEntity;
import twist.resources.codingexercise.model.Author;
import twist.resources.codingexercise.model.Book;
import twist.resources.codingexercise.repository.AuthorRepository;
import twist.resources.codingexercise.repository.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public List<Book> getBooks() {
        return bookRepository.findAll().stream()
                .map(bookEntity -> Book.builder()
                        .id(bookEntity.getId())
                        .name(bookEntity.getName())
                        .author(Author.builder()
                                .id(bookEntity.getAuthor().getId())
                                .name(bookEntity.getAuthor().getName())
                                .build())
                        .build()).collect(Collectors.toList());
    }

    public List<Book> getBooksByName(String name) {
        return bookRepository.findAllByNameContainingIgnoreCase(name).stream()
                .map(bookEntity -> Book.builder()
                        .id(bookEntity.getId())
                        .name(bookEntity.getName())
                        .author(Author.builder()
                                .id(bookEntity.getAuthor().getId())
                                .name(bookEntity.getAuthor().getName())
                                .build())
                        .build()).collect(Collectors.toList());
    }

    //Create author if not yet existing, use the existing author if author exists
    public Book saveBookAndAuthor(String authorName, String bookName) {
        List<AuthorEntity> authors = authorRepository.findByName(authorName);

        AuthorEntity authorEntity;
        if (!authors.isEmpty()) {
            authorEntity = authors.get(0);
        } else {
            authorEntity = AuthorEntity.builder()
                    .name(authorName)
                    .build();
            authorEntity = authorRepository.save(authorEntity);
        }

        BookEntity bookEntity = BookEntity.builder()
                .name(bookName)
                .author(authorEntity)
                .build();

        bookEntity = bookRepository.save(bookEntity);

        return Book.builder()
                .id(bookEntity.getId())
                .name(bookEntity.getName())
                .author(Author.builder()
                        .id(bookEntity.getAuthor().getId())
                        .name(bookEntity.getAuthor().getName())
                        .build())
                .build();
    }

    public Book updateBookAndAuthor(Integer bookId, String newAuthorName, String newBookName) {
        BookEntity bookEntity = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

        AuthorEntity authorEntity = bookEntity.getAuthor();

        if (!authorEntity.getName().equals(newAuthorName)) {
            List<AuthorEntity> existingAuthors = authorRepository.findByName(newAuthorName);
            if (!existingAuthors.isEmpty()) {
                authorEntity = existingAuthors.get(0);
            } else {
                List<BookEntity> booksWithSameAuthor = bookRepository.findByAuthorName(authorEntity.getName());
                if (booksWithSameAuthor.size() > 1) {
                    authorEntity = AuthorEntity.builder()
                            .name(newAuthorName)
                            .build();
                    authorEntity = authorRepository.save(authorEntity);
                } else {
                    authorEntity.setName(newAuthorName);
                    authorEntity = authorRepository.save(authorEntity);
                }
            }
        }

        bookEntity.setName(newBookName);
        bookEntity.setAuthor(authorEntity);

        bookEntity = bookRepository.save(bookEntity);

        return Book.builder()
                .id(bookEntity.getId())
                .name(bookEntity.getName())
                .author(Author.builder()
                        .id(authorEntity.getId())
                        .name(authorEntity.getName())
                        .build())
                .build();
    }

    public String deleteBookById(Integer bookId) {
        Optional<BookEntity> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isPresent()) {
            bookRepository.deleteById(bookId);
            return "Book deleted successfully";
        } else {
            return "Book not found with ID: " + bookId;
        }
    }

}

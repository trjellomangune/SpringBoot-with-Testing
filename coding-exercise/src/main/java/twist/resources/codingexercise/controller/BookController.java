package twist.resources.codingexercise.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import twist.resources.codingexercise.service.BookService;
import twist.resources.codingexercise.model.Book;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class BookController {
    private final BookService bookService;

    @GetMapping("/books")
    public List<Book> getBooks() {
        return bookService.getBooks();
    }

    @GetMapping("/books/{name}")
    public List<Book> getBooksByName(@PathVariable String name) {
        return bookService.getBooksByName(name);
    }

    @PostMapping("/books")
    public ResponseEntity<Book> saveBook(@RequestBody Map<String, String> requestBody) {
        String authorName = requestBody.get("author_name");
        String bookName = requestBody.get("book_name");
        Book savedBook = bookService.saveBookAndAuthor(authorName, bookName);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @PutMapping("/books/{bookId}")
    public ResponseEntity<Book> updateBookAndAuthor(
            @PathVariable Integer bookId,
            @RequestBody Map<String, String> requestBody) {
        String newAuthorName = requestBody.get("author_name");
        String newBookName = requestBody.get("book_name");
        Book updatedBook = bookService.updateBookAndAuthor(bookId, newAuthorName, newBookName);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Map<String, Object>> deleteBookById(@PathVariable Integer bookId) {
        String message = bookService.deleteBookById(bookId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", message);

        return ResponseEntity.ok(response);
    }
}

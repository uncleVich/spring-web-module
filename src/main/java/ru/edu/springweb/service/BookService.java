package ru.edu.springweb.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import ru.edu.springweb.entity.Book;
import ru.edu.springweb.exception.BookAlreadyExistsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private List<Book> books = new ArrayList<Book>();

    @PostConstruct
    public void init() {
        books.add(new Book(1, "Book1", "Author1"));
        books.add(new Book(2, "Book2", "Author2"));
        books.add(new Book(3, "Book3", "Author3"));
        books.add(new Book(4, "Book4", "Author4"));
    }

    public List<Book> getBooks() {
        return books;
    }

    public Book getBook(int bookId) {
        return getBookById(bookId).orElse(null);
    }

    public void addBook(Book book) {
        books.stream()
                .filter(b -> b.equals(book))
                .findAny()
                .ifPresent(b -> {
                    throw new BookAlreadyExistsException("User already exists");
                });
        books.add(book);
    }

    public void updateBook(Book book) {
        getBookById(book.getId()).ifPresent(b -> {
            b.setAuthor(book.getAuthor());
            b.setTitle(book.getTitle());
        });
    }

    public void deleteBook(int bookId) {
        books.removeIf(b -> b.getId() == bookId);
    }

    private Optional<Book> getBookById(int id) {
        return books.stream().filter(book -> book.getId() == id).findFirst();
    }

}

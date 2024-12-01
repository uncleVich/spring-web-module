package ru.edu.springweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.edu.springweb.config.WebApplicationConfig;
import ru.edu.springweb.entity.Book;
import ru.edu.springweb.service.BookService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebApplicationConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookControllerTest {

    private static final String URI = "/api/v1/books";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BookService bookService;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testGetBooks() throws Exception {
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        var books = MAPPER.readValue(content, List.class);
        assertEquals(4, books.size());
    }

    @Test
    void testGetBookById() throws Exception {
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(URI + "/2")
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        var book = MAPPER.readValue(content, Book.class);
        assertAll(
                () -> assertEquals(2, book.getId()),
                () -> assertEquals("Book2", book.getTitle()),
                () -> assertEquals("Author2", book.getAuthor())
        );
    }

    @Test
    void testCreateBook() throws Exception {
        Book book = new Book(5, "Book5", "Author5");
        String bookJson = MAPPER.writeValueAsString(book);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(URI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(bookJson))
                .andReturn();

        assertEquals(201, result.getResponse().getStatus());
        assertEquals(5, bookService.getBooks().size());
    }

    @Test
    void testCreateBookThenException() throws Exception {
        Book book = new Book(4, "Book4", "Author4");
        String bookJson = MAPPER.writeValueAsString(book);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(URI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(bookJson))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(409, response.getStatus());
    }


    @Test
    void testUpdateBook() throws Exception {
        Book book = new Book(4, "Book4444", "Author4444");
        String bookJson = MAPPER.writeValueAsString(book);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(URI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(bookJson))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        var updatedBook = bookService.getBook(book.getId());
        assertAll(
                () -> assertEquals(4, updatedBook.getId()),
                () -> assertEquals("Book4444", updatedBook.getTitle()),
                () -> assertEquals("Author4444", updatedBook.getAuthor())
        );
    }

    @Test
    void testDeleteBook() throws Exception {
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(URI + "/2"))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(3, bookService.getBooks().size());
        assertNull(bookService.getBook(2));
    }
}
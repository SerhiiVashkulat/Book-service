package ua.vashkulat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ua.vashkulat.dto.request.CreateBookDto;
import ua.vashkulat.dto.request.UpdateBookDto;
import ua.vashkulat.dto.response.BookDto;
import ua.vashkulat.exception.BookNotFoundException;
import ua.vashkulat.service.BookService;


import java.time.LocalDate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private BookService bookService;
	private final String API_BY_ID = "/api/v1/books/{id}";
	private final String API_BOOKS = "/api/v1/books";
	private final String API_BOOKS_FILTER = "/api/v1/books/filter";


	@Test
	void givenValidId_bookByID_shouldReturnStatusOkWithBook() throws Exception {

		Long validId = 1L;
		BookDto bookDto = new BookDto(validId, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565");

		when(bookService.getById(validId)).thenReturn(bookDto);
		mockMvc.perform(get(API_BY_ID ,validId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(validId));

	}
	@Test
	void givenInvalidId_bookByID_shouldReturnExceptionWithBadRequest() throws Exception {

		Long invalidId = -1L;

		mockMvc.perform(get(API_BY_ID ,invalidId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
		verify(bookService,never()).getById(invalidId);

	}

	@Test
	void givenValidPayload_createBook_shouldReturnStatusCreatedWithBookDto() throws Exception {
		CreateBookDto validBookDto = new CreateBookDto("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565");
		BookDto createdBookDto = new BookDto(1L, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565");

		when(bookService.create(validBookDto)).thenReturn(createdBookDto);

		mockMvc.perform(post(API_BOOKS)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validBookDto)))
				.andExpect(status().isCreated())
				.andExpect(header().string(HttpHeaders.LOCATION, "/api/v1/books/1"))
				.andExpect(jsonPath("$.id").value(createdBookDto.id()))
				.andExpect(jsonPath("$.ISBN").value(createdBookDto.ISBN()));
	}
	@Test
	void givenInvalidPayload_createBook_shouldReturnStatusBadRequestWithException() throws Exception {
		CreateBookDto createBookDto = new CreateBookDto("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("2026-04-10"), "9780743273565");

		mockMvc.perform(post(API_BOOKS)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createBookDto)))
				.andExpect(status().isBadRequest())
				.andExpect(result ->  assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));
		verify(bookService,never()).create(createBookDto);

	}

	@Test
	void givenValidPayload_updateBook_shouldReturnStatusOkWithUpdatedBook() throws Exception {
		Long bookId = 1L;
		UpdateBookDto updateBookDto = new UpdateBookDto("Updated Title", "Updated Author", "Updated Genre", LocalDate.parse("2019-04-10"), "9780743273565");
		BookDto updatedBookDto = new BookDto(bookId, "Updated Title", "Updated Author", "Updated Genre", LocalDate.parse("2019-04-10"), "9780743273565");

		when(bookService.updateById(bookId, updateBookDto)).thenReturn(updatedBookDto);

		mockMvc.perform(put(API_BY_ID, bookId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updatedBookDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated Title"))
				.andExpect(jsonPath("$.ISBN").value("9780743273565"));
	}
	@Test
	void givenInvalidUpdateBookDto_updateBook_shouldReturnBadRequestWithException() throws Exception {
		Long bookId = 1L;
		UpdateBookDto invalidUpdateBookDto = new UpdateBookDto("Valid Title", "Valid Author", "Valid Genre", LocalDate.parse("2019-04-10"), "1234567890");

		mockMvc.perform(put(API_BY_ID, bookId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidUpdateBookDto)))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class,result.getResolvedException()));
		verify(bookService,never()).updateById(bookId,invalidUpdateBookDto);
	}

	@Test
	void givenValidId_deleteBook_shouldReturnStatusNoContent() throws Exception {
		Long bookId = 1L;

		mockMvc.perform(delete(API_BY_ID, bookId))
				.andExpect(status().isNoContent());

		verify(bookService).deleteById(bookId);
	}

	@Test
	void givenValidPayload_allBooks_shouldReturnStatusOkWithListOfBooks() throws Exception {
		int page = 0;
		int size = 10;

		List<BookDto> bookDtos = List.of(
				new BookDto(1L, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565"),
				new BookDto(2L, "Pride and Prejudice", "Jane Austen", "Romance", LocalDate.parse("1935-06-11"), "9780743473565")
		);

		when(bookService.getAll(page, size)).thenReturn(bookDtos);

		mockMvc.perform(get(API_BOOKS)
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(size))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$.[0].title").value("The Great Gatsby"))
				.andExpect(jsonPath("$.[1].title").value("Pride and Prejudice"));

		verify(bookService).getAll(page, size);
	}
	@Test
	void givenInvalidPayload_allBooks_shouldReturnStatusBadRequestWithException() throws Exception {
		int invalidPage = -1;
		int size = 10;

		mockMvc.perform(get(API_BOOKS)
						.param("page", String.valueOf(invalidPage))
						.param("size", String.valueOf(size))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));


		verify(bookService, never()).getAll(invalidPage, size);
	}
	@Test
	void givenNoFilterParameters_booksByFilter_shouldReturnStatusOkWithListOfBooks() throws Exception {

		BookDto bookDto = new BookDto(1L, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565");
		List<BookDto> bookDtos = List.of(bookDto);

		when(bookService.getByFilter(null, null, null)).thenReturn(bookDtos);


		mockMvc.perform(get(API_BOOKS_FILTER)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(bookDtos.size()))
				.andExpect(jsonPath("$[0].title").value(bookDto.title()))
				.andExpect(jsonPath("$[0].author").value(bookDto.author()));
	}
	@Test
	void givenFilterParameters_booksByFilter_shouldReturnStatusOkWithListOfBooks() throws Exception {

		String author = "cott Fitzgerald";
		String title = "reat Gatsby";
		String genre = "ction";
		List<BookDto> bookDtos = List.of(new BookDto(1L, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565"));

		when(bookService.getByFilter(author, title, genre)).thenReturn(bookDtos);

		mockMvc.perform(get(API_BOOKS_FILTER)
						.param("author", author)
						.param("title", title)
						.param("genre", genre)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(bookDtos.size()))
				.andExpect(jsonPath("$[0].title").value("The Great Gatsby"))
				.andExpect(jsonPath("$[0].author").value("F. Scott Fitzgerald"));
	}
	@Test
	void givenInvalidFilterParameters_booksByFilter_shouldReturnStatusNotFoundWithException() throws Exception {

		String author = "123";
		String title = "123";
		String genre = "123";

		when(bookService.getByFilter(author, title, genre)).thenThrow(BookNotFoundException.class);

		mockMvc.perform(get(API_BOOKS_FILTER)
						.param("author", author)
						.param("title", title)
						.param("genre", genre)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(result -> assertInstanceOf(BookNotFoundException.class, result.getResolvedException()));
	}

}
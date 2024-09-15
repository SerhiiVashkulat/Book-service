package ua.vashkulat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ua.vashkulat.dto.request.CreateBookDto;
import ua.vashkulat.dto.request.UpdateBookDto;
import ua.vashkulat.dto.response.BookDto;
import ua.vashkulat.entity.Book;
import ua.vashkulat.exception.BookNotFoundException;
import ua.vashkulat.exception.IsbnExistsException;
import ua.vashkulat.repository.BookRepository;
import ua.vashkulat.service.impl.BookServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
	@Mock
	private BookRepository repository;
	@InjectMocks
	private BookServiceImpl bookService;

	@BeforeEach
	void setUp() {

	}

	@Test
	void givenInvalidId_getById_shouldTrowsException() {
		Long invalidId = 100_000_000_000_000L;

		when(repository.findById(invalidId)).thenThrow(BookNotFoundException.class);

		assertThrows(BookNotFoundException.class, () -> bookService.getById(invalidId));
		verify(repository).findById(invalidId);
	}

	@Test
	void givenValidId_getById_shouldReturnBook() {
		Long validId = 1L;
		Book validBook = new Book(validId, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565");

		when(repository.findById(validId)).thenReturn(Optional.of(validBook));

		BookDto result = bookService.getById(validId);

		assertEquals(result.ISBN(), validBook.getISBN());
		verify(repository).findById(validId);
	}

	@Test
	void givenExistsISBN_create_shouldTrowsException() {
		String existsISBN = "11111111111111";
		CreateBookDto invalidDto = new CreateBookDto("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), existsISBN);

		when(repository.existsByISBN(existsISBN)).thenReturn(true);

		assertThrows(IsbnExistsException.class, () -> bookService.create(invalidDto));
		verify(repository, never()).save(any(Book.class));

	}

	@Test
	void givenValidPayload_create_shouldReturnCreatedBook() {
		CreateBookDto validDto = new CreateBookDto("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565");
		Book validBook = new Book(1L, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565");

		when(repository.existsByISBN(validDto.ISBN())).thenReturn(false);
		when(repository.save(any(Book.class))).thenReturn(validBook);

		BookDto result = bookService.create(validDto);

		assertEquals(result.ISBN(), validBook.getISBN());
		assertEquals(result.id(), 1L);

	}

	@Test
	void givenNotEmptyStorage_getAll_shouldReturnListBooks() {
		List<Book> list = List.of(new Book(1L, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565"),
				new Book(2L, "Pride and Prejudice", "Jane Austen", "Romance", LocalDate.parse("1935-06-11"), "9780743473565"));
		Pageable pageable = PageRequest.of(0, 5);

		Page<Book> pageBooks = new PageImpl<>(list);

		when(repository.findAll(pageable)).thenReturn(pageBooks);

		List<BookDto> result = bookService.getAll(pageable.getPageNumber(), pageable.getPageSize());

		assertEquals(result.size(), list.size());
		assertEquals(result.get(0).ISBN(), list.get(0).getISBN());
		verify(repository).findAll(pageable);
	}

	@Test
	void givenValidPayload_updateById_shouldReturnUpdatedBook() {
		Long updateId = 1L;
		UpdateBookDto updateDto = new UpdateBookDto("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565");
		Book existingBook = new Book(updateId, "The Hobbit", "J.R.R. Tolkien", "Fantasy", LocalDate.parse("1999-06-11"), "9780064400557");
		Book updateBook = new Book(updateId, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565");

		when(repository.findById(updateId)).thenReturn(Optional.of(existingBook));
		when(repository.existsByISBN(updateDto.ISBN())).thenReturn(false);
		when(repository.save(any(Book.class))).thenReturn(updateBook);

		BookDto result = bookService.updateById(updateId, updateDto);

		assertEquals(result.ISBN(), updateDto.ISBN());
		verify(repository).save(any(Book.class));
		verifyNoMoreInteractions(repository);
	}

	@Test
	void givenInvalidPayload_updateById_shouldTrowsException() {
		Long updateId = 1L;
		UpdateBookDto updateDto = new UpdateBookDto("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780064400557");
		Book existingBook = new Book(updateId, "The Hobbit", "J.R.R. Tolkien", "Fantasy", LocalDate.parse("1999-06-11"), "9780064400557");

		when(repository.findById(updateId)).thenReturn(Optional.of(existingBook));
		when(repository.existsByISBN(updateDto.ISBN())).thenReturn(true);

		assertThrows(IsbnExistsException.class, () -> bookService.updateById(updateId, updateDto));
		verify(repository, never()).save(any(Book.class));
	}

	@Test
	void givenValidId_deleteById_shouldDeleteBook() {
		Long bookId = 1L;
		Book existingBook = new Book(bookId, "The Hobbit", "J.R.R. Tolkien", "Fantasy", LocalDate.parse("1999-06-11"), "9780064400557");
		when(repository.findById(bookId)).thenReturn(Optional.of(existingBook));

		bookService.deleteById(bookId);

		verify(repository).findById(bookId);
		verify(repository).delete(existingBook);
	}

	@Test
	void givenValidFilter_getByFilter_shouldReturnListOfBooks() {
		String author = "scott Fitzgerald";
		String title = "reat Gatsby";
		String genre = null;

		List<Book> books = List.of(new Book(1L, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1999-06-11"), "9780743273565"));

		when(repository.searchBooksByFilter(author, title, genre)).thenReturn(books);

		List<BookDto> result = bookService.getByFilter(author, title, genre);

		assertEquals(books.size(), result.size());
		assertEquals(books.get(0).getTitle(), result.get(0).title());
		verify(repository).searchBooksByFilter(author, title, genre);
	}

	@Test
	void givenNoMatchingBooks_getByFilter_shouldThrowException() {

		String author = "Unknown Author";
		String title = "Unknown Title";
		String genre = "Unknown Genre";

		when(repository.searchBooksByFilter(author, title, genre)).thenReturn(List.of());

		assertThrows(BookNotFoundException.class, () -> bookService.getByFilter(author, title, genre));
		verify(repository).searchBooksByFilter(author, title, genre);
	}
}
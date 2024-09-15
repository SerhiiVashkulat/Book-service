package ua.vashkulat.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.vashkulat.dto.request.CreateBookDto;
import ua.vashkulat.dto.request.UpdateBookDto;
import ua.vashkulat.dto.response.BookDto;
import ua.vashkulat.service.BookService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookController {
	private final BookService bookService;

	@GetMapping("/{id}")
	public ResponseEntity<BookDto> bookByID(@Positive @PathVariable Long id){
		log.info("Received request to fetch book with id: {}", id);
		return ResponseEntity.ok(bookService.getById(id));
	}

	@PostMapping()
	public ResponseEntity<BookDto> createBook(@Valid @RequestBody CreateBookDto dto ,
											  HttpServletRequest request){
		log.info("Received request to create a new book: {}", dto);
		BookDto createdBook = bookService.create(dto);
		log.info("Book created with successfully");
		return ResponseEntity.created(URI.create(request.getRequestURI() + "/" + createdBook.id()))
				.body(createdBook);
	}
	@PutMapping("/{id}")
	public ResponseEntity<BookDto> updateBook(@Positive @PathVariable Long id, @Valid @RequestBody UpdateBookDto updateBookDto){
		log.info("Received request to update book with id: {}", id);
		return ResponseEntity.ok(bookService.updateById(id,updateBookDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBook(@Positive @PathVariable Long id) {
		log.info("Received request to delete book with id: {}", id);
		bookService.deleteById(id);
		log.info("Successfully deleted book with id: {}", id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping()
	public ResponseEntity<List<BookDto>> allBooks( @RequestParam(defaultValue = "0") @Min(0) int page,
												   @RequestParam(defaultValue = "10") @Min(1) int size) {
		log.info("Received request to fetch all books, page: {}, size: {}", page, size);
		return ResponseEntity.ok(bookService.getAll(page, size));
	}

	@GetMapping("/filter")
	public ResponseEntity<List<BookDto>> booksByFilter(
			@RequestParam(required = false) String author,
			@RequestParam(required = false) String title,
			@RequestParam(required = false) String genre) {
		log.info("Received request to fetch books by filter - Author: {}, Title: {}, Genre: {}", author, title, genre);
		return ResponseEntity.ok(bookService.getByFilter(author, title, genre));
	}
}

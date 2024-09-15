package ua.vashkulat.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.vashkulat.dto.request.CreateBookDto;
import ua.vashkulat.dto.request.UpdateBookDto;
import ua.vashkulat.dto.response.BookDto;
import ua.vashkulat.entity.Book;
import ua.vashkulat.exception.BookNotFoundException;
import ua.vashkulat.exception.IsbnExistsException;
import ua.vashkulat.mapper.BookMapper;
import ua.vashkulat.repository.BookRepository;
import ua.vashkulat.service.BookService;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {
	private final BookRepository repository;
	private final BookMapper mapper = BookMapper.mapper;

	@Override
	@Transactional(readOnly = true)
	public BookDto getById(Long id) {
		log.info("Fetching book with id: {}", id);
		return mapper.toDto(findBookById(id));
	}

	@Override
	@Transactional
	public BookDto create(CreateBookDto bookDto) {
		log.info("Fetching book with ISBN: {}", bookDto.ISBN());
		existsBookByISBN(bookDto.ISBN());
		Book savedBook = repository.save(mapper.toBook(bookDto));
		log.info("Book created with id: {}", savedBook.getId());
		return mapper.toDto(savedBook);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookDto> getAll(int page, int size) {
		log.info("Fetching all books, page: {}, size: {}", page, size);
		Pageable pageable = PageRequest.of(page, size);
		return repository.findAll(pageable).stream()
				.map(mapper::toDto)
				.toList();
	}

	@Override
	@Transactional
	public BookDto updateById(Long id, UpdateBookDto updatedData) {
		log.info("Updating book with id: {}", id);
		Book updatedBook = findBookById(id);
		log.info("Fetching book with ISBN: {}", updatedData.ISBN());
		existsBookByISBN(updatedData.ISBN());

		updatedBook.setTitle(updatedData.title());
		updatedBook.setAuthor(updatedData.author());
		updatedBook.setGenre(updatedData.genre());
		updatedBook.setPublicationYear(updatedData.publicationYear());
		updatedBook.setISBN(updatedData.ISBN());

		return mapper.toDto(repository.save(updatedBook));
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		log.info("Deleting book with id: {}", id);
		Book deletedBook = findBookById(id);
		repository.delete(deletedBook);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookDto> getByFilter(String author, String title, String genre) {
		log.info("Fetching books with filter - Author: {}, Title: {}, Genre: {}", author, title, genre);
		List<Book> booksByFilter = repository.searchBooksByFilter(author, title, genre);
		if (booksByFilter.isEmpty()){
			log.warn("No books found with the filters");
			throw new  BookNotFoundException("Books not found");
		}
		return booksByFilter.stream()
				.map(mapper::toDto)
				.toList();
	}

	private Book findBookById(Long id) {
		log.debug("Looking for book with id: {}", id);
		return repository.findById(id)
				.orElseThrow(() -> {
					log.error("Book with id {} not found", id);
					return new BookNotFoundException("Book with id " + id + " not found");
				});
	}
	private void existsBookByISBN(String ISBN) {
		log.debug("Checking if book with ISBN {} exists", ISBN);
		if (repository.existsByISBN(ISBN)) {
			log.error("ISBN {} already exists", ISBN);
			throw new IsbnExistsException("ISBN already present, try using a different one");
		}
	}
}

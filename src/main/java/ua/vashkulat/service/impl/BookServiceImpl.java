package ua.vashkulat.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
public class BookServiceImpl implements BookService {
	private final BookRepository repository;
	private final BookMapper mapper = BookMapper.mapper;

	@Override
	public BookDto getById(Long id) {
		return mapper.toDto(findBookById(id));
	}

	@Override
	public BookDto create(CreateBookDto bookDto) {
		existsBookByISBN(bookDto.ISBN());
		Book savedBook = repository.save(mapper.toBook(bookDto));

		return mapper.toDto(savedBook);
	}

	@Override
	public List<BookDto> getAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return repository.findAll(pageable).stream()
				.map(mapper::toDto)
				.toList();
	}

	@Override
	public BookDto updateById(Long id, UpdateBookDto updatedData) {
		Book updatedBook = findBookById(id);
		existsBookByISBN(updatedData.ISBN());

		updatedBook.setTitle(updatedData.title());
		updatedBook.setAuthor(updatedData.author());
		updatedBook.setGenre(updatedData.genre());
		updatedBook.setPublicationYear(updatedData.publicationYear());
		updatedBook.setISBN(updatedData.ISBN());

		return mapper.toDto(repository.save(updatedBook));
	}

	@Override
	public void deleteById(Long id) {
		Book deletedBook = findBookById(id);
		repository.delete(deletedBook);
	}

	@Override
	public List<BookDto> getByFilter(String author, String title, String genre) {
		List<Book> booksByFilter = repository.searchBooksByFilter(author, title, genre);
		if (booksByFilter.isEmpty()){
			throw new  BookNotFoundException("Books not found");
		}
		return booksByFilter.stream()
				.map(mapper::toDto)
				.toList();
	}

	private Book findBookById(Long id){
		return repository.findById(id)
				.orElseThrow(() -> new BookNotFoundException("Book whit id " + id + " not found"));
	}
	private void existsBookByISBN(String ISBN){
		if (repository.existsByISBN(ISBN)){
			throw new IsbnExistsException("ISBN already present, try use different");
		}
	}
}

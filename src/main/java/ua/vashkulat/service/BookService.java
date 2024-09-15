package ua.vashkulat.service;

import ua.vashkulat.dto.request.CreateBookDto;
import ua.vashkulat.dto.request.UpdateBookDto;
import ua.vashkulat.dto.response.BookDto;

import java.util.List;

public interface BookService {
	BookDto getById(Long id);
	BookDto create(CreateBookDto bookDto);
	List<BookDto> getAll(int page, int size);
	BookDto updateById(Long id, UpdateBookDto updateBook);

	void deleteById(Long id);
	List<BookDto> getByFilter(String author, String title, String genre);

}

package ua.vashkulat.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ua.vashkulat.dto.request.CreateBookDto;
import ua.vashkulat.dto.response.BookDto;
import ua.vashkulat.entity.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {
	BookMapper mapper = Mappers.getMapper(BookMapper.class);

	BookDto toDto(Book book);
	Book toBook(CreateBookDto dto);

}

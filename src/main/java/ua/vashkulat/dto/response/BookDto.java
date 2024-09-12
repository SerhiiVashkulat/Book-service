package ua.vashkulat.dto.response;

import java.time.LocalDate;

public record BookDto(Long id,
					  String title,
					  String author,
					  String genre,
					  LocalDate publicationYear,
					  String ISBN) {}

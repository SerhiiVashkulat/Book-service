package ua.vashkulat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;


public record CreateBookDto(
		@NotBlank(message = "title can be empty or null")
		String title,
		@NotBlank(message = "author can be empty or null")
		String author,
		String genre,
		@Past
		LocalDate publicationYear,
		@NotBlank(message = "ISBN can be empty or null")
		@Size(min = 13, max = 13, message = "ISBN must contains 13 symbols")
		String ISBN
) {
}

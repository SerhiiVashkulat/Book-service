package ua.vashkulat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;

@Entity
@Table(name = "books")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "title",nullable = false)
	private String title;
	@Column(name = "author",nullable = false)
	private String author;
	private String genre;
	@Column(name = "publication_year",nullable = false)
	private LocalDate publicationYear;
	@Column(name = "isbn",nullable = false,length = 13,unique = true)
	private String ISBN;

}

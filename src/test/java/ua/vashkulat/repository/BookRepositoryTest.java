package ua.vashkulat.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ua.vashkulat.entity.Book;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = true)
class BookRepositoryTest {
	@Autowired
	private BookRepository repository;

	@Test
	void existsByISBN() {
		String existingISBN = "9780743273565";
		Book book = new Book(1L, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565");
		repository.save(book);
		boolean exists = repository.existsByISBN(existingISBN);
		assertTrue(exists);
	}

	@Test
	void searchBooksByFilter() {
		String titleFilter = "gats";
		String authorFilter = "fitzge";
		String genreFilter = null;

		List<Book> bookList = List.of( new Book(1L, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.parse("1925-04-10"), "9780743273565"),
				new Book(2L,"Through the Looking-Glass","Lewis Carroll", "Children’s Fiction", LocalDate.parse("1999-12-13"), "9000486270503"));
		repository.saveAll(bookList);
		List<Book> result = repository.searchBooksByFilter(authorFilter,titleFilter,genreFilter);

		assertEquals(1, result.size());
		assertEquals(result.get(0).getISBN(),bookList.get(0).getISBN());
	}
}
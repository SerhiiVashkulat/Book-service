package ua.vashkulat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.vashkulat.entity.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {

	boolean existsByISBN(String ISBN);
	@Query("SELECT b FROM Book b WHERE " +
			"(:author IS NULL OR b.author ILIKE %:author%) AND " +
			"(:title IS NULL OR b.title ILIKE %:title%) AND " +
			"(:genre IS NULL OR b.genre ILIKE %:genre%)")
	List<Book> searchBooksByFilter(@Param("author") String author,
								   @Param("title") String title,
								   @Param("genre") String genre);

}

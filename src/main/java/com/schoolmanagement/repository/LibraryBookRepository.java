package com.schoolmanagement.repository;

import com.schoolmanagement.entity.LibraryBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryBookRepository extends JpaRepository<LibraryBook, Long> {
    
    // Find books by title (case-insensitive)
    @Query("SELECT b FROM LibraryBook b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) AND b.isActive = true")
    List<LibraryBook> findByTitleContainingIgnoreCase(@Param("title") String title);
    
    // Find books by author (case-insensitive)
    @Query("SELECT b FROM LibraryBook b WHERE LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')) AND b.isActive = true")
    List<LibraryBook> findByAuthorContainingIgnoreCase(@Param("author") String author);
    
    // Find books by ISBN
    Optional<LibraryBook> findByIsbnAndIsActiveTrue(String isbn);
    
    // Find books by category
    @Query("SELECT b FROM LibraryBook b WHERE LOWER(b.category) = LOWER(:category) AND b.isActive = true")
    List<LibraryBook> findByCategory(@Param("category") String category);
    
    // Find books by subject
    @Query("SELECT b FROM LibraryBook b WHERE LOWER(b.subject) = LOWER(:subject) AND b.isActive = true")
    List<LibraryBook> findBySubject(@Param("subject") String subject);
    
    // Find books by status
    List<LibraryBook> findByStatusAndIsActiveTrue(LibraryBook.BookStatus status);
    
    // Find available books
    @Query("SELECT b FROM LibraryBook b WHERE b.availableCopies > 0 AND b.isActive = true")
    List<LibraryBook> findAvailableBooks();
    
    // Search books by multiple criteria
    @Query("SELECT b FROM LibraryBook b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:category IS NULL OR LOWER(b.category) = LOWER(:category)) AND " +
           "(:subject IS NULL OR LOWER(b.subject) = LOWER(:subject)) AND " +
           "b.isActive = true")
    Page<LibraryBook> searchBooks(@Param("title") String title, 
                                  @Param("author") String author, 
                                  @Param("category") String category, 
                                  @Param("subject") String subject, 
                                  Pageable pageable);
    
    // Find books with pagination
    Page<LibraryBook> findByIsActiveTrue(Pageable pageable);
    
    // Find books by publication year range
    @Query("SELECT b FROM LibraryBook b WHERE b.publicationYear BETWEEN :startYear AND :endYear AND b.isActive = true")
    List<LibraryBook> findByPublicationYearBetween(@Param("startYear") Integer startYear, 
                                                   @Param("endYear") Integer endYear);
    
    // Find books by price range
    @Query("SELECT b FROM LibraryBook b WHERE b.price BETWEEN :minPrice AND :maxPrice AND b.isActive = true")
    List<LibraryBook> findByPriceBetween(@Param("minPrice") java.math.BigDecimal minPrice, 
                                        @Param("maxPrice") java.math.BigDecimal maxPrice);
    
    // Count books by status
    @Query("SELECT COUNT(b) FROM LibraryBook b WHERE b.status = :status AND b.isActive = true")
    Long countByStatus(@Param("status") LibraryBook.BookStatus status);
    
    // Find books by location
    @Query("SELECT b FROM LibraryBook b WHERE LOWER(b.location) LIKE LOWER(CONCAT('%', :location, '%')) AND b.isActive = true")
    List<LibraryBook> findByLocationContainingIgnoreCase(@Param("location") String location);
}

package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.LibraryBookDto;
import com.schoolmanagement.entity.LibraryBook;
import com.schoolmanagement.service.LibraryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@Slf4j
public class LibraryController {
    
    private final LibraryService libraryService;
    
    // Create a new book
    @PostMapping("/books")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<LibraryBookDto>> createBook(@Valid @RequestBody LibraryBookDto bookDto) {
        log.info("Creating new library book: {}", bookDto.getTitle());
        ApiResponse<LibraryBookDto> response = libraryService.createBook(bookDto);
        return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
    }
    
    // Get all books with pagination
    @GetMapping("/books")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<Page<LibraryBookDto>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get all library books - page: {}, size: {}", page, size);
        ApiResponse<Page<LibraryBookDto>> response = libraryService.getAllBooks(page, size);
        return ResponseEntity.ok(response);
    }
    
    // Get book by ID
    @GetMapping("/books/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<LibraryBookDto>> getBookById(@PathVariable Long id) {
        log.info("Get library book by ID: {}", id);
        ApiResponse<LibraryBookDto> response = libraryService.getBookById(id);
        return ResponseEntity.ok(response);
    }
    
    // Update book
    @PutMapping("/books/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<LibraryBookDto>> updateBook(@PathVariable Long id, @Valid @RequestBody LibraryBookDto bookDto) {
        log.info("Update library book with ID: {}", id);
        ApiResponse<LibraryBookDto> response = libraryService.updateBook(id, bookDto);
        return ResponseEntity.ok(response);
    }
    
    // Delete book
    @DeleteMapping("/books/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<String>> deleteBook(@PathVariable Long id) {
        log.info("Delete library book with ID: {}", id);
        ApiResponse<String> response = libraryService.deleteBook(id);
        return ResponseEntity.ok(response);
    }
    
    // Search books
    @GetMapping("/books/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<Page<LibraryBookDto>>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String subject,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Search books - title: {}, author: {}, category: {}, subject: {}", title, author, category, subject);
        ApiResponse<Page<LibraryBookDto>> response = libraryService.searchBooks(title, author, category, subject, page, size);
        return ResponseEntity.ok(response);
    }
    
    // Get books by category
    @GetMapping("/books/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<LibraryBookDto>>> getBooksByCategory(@PathVariable String category) {
        log.info("Get books by category: {}", category);
        ApiResponse<List<LibraryBookDto>> response = libraryService.getBooksByCategory(category);
        return ResponseEntity.ok(response);
    }
    
    // Get books by subject
    @GetMapping("/books/subject/{subject}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<LibraryBookDto>>> getBooksBySubject(@PathVariable String subject) {
        log.info("Get books by subject: {}", subject);
        ApiResponse<List<LibraryBookDto>> response = libraryService.getBooksBySubject(subject);
        return ResponseEntity.ok(response);
    }
    
    // Get available books
    @GetMapping("/books/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<LibraryBookDto>>> getAvailableBooks() {
        log.info("Get available books");
        ApiResponse<List<LibraryBookDto>> response = libraryService.getAvailableBooks();
        return ResponseEntity.ok(response);
    }
    
    // Get books by status
    @GetMapping("/books/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<LibraryBookDto>>> getBooksByStatus(@PathVariable LibraryBook.BookStatus status) {
        log.info("Get books by status: {}", status);
        ApiResponse<List<LibraryBookDto>> response = libraryService.getBooksByStatus(status);
        return ResponseEntity.ok(response);
    }
    
    // Borrow book
    @PostMapping("/books/{id}/borrow")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<LibraryBookDto>> borrowBook(@PathVariable Long id) {
        log.info("Borrow book with ID: {}", id);
        ApiResponse<LibraryBookDto> response = libraryService.borrowBook(id);
        return ResponseEntity.ok(response);
    }
    
    // Return book
    @PostMapping("/books/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<LibraryBookDto>> returnBook(@PathVariable Long id) {
        log.info("Return book with ID: {}", id);
        ApiResponse<LibraryBookDto> response = libraryService.returnBook(id);
        return ResponseEntity.ok(response);
    }
    
    // Get books by title search
    @GetMapping("/books/title")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<LibraryBookDto>>> getBooksByTitle(@RequestParam String query) {
        log.info("Search books by title: {}", query);
        ApiResponse<Page<LibraryBookDto>> response = libraryService.searchBooks(query, null, null, null, 0, 100);
        return ResponseEntity.ok(ApiResponse.success("Books found", response.getData().getContent()));
    }
    
    // Get books by author search
    @GetMapping("/books/author")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<LibraryBookDto>>> getBooksByAuthor(@RequestParam String query) {
        log.info("Search books by author: {}", query);
        ApiResponse<Page<LibraryBookDto>> response = libraryService.searchBooks(null, query, null, null, 0, 100);
        return ResponseEntity.ok(ApiResponse.success("Books found", response.getData().getContent()));
    }
}

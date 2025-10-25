package com.schoolmanagement.service;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.LibraryBookDto;
import com.schoolmanagement.entity.LibraryBook;
import com.schoolmanagement.repository.LibraryBookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LibraryService {
    
    private final LibraryBookRepository libraryBookRepository;
    
    // Create a new book
    @Transactional
    public ApiResponse<LibraryBookDto> createBook(LibraryBookDto bookDto) {
        try {
            log.info("Creating new library book: {}", bookDto.getTitle());
            
            // Check if book with same ISBN already exists
            if (bookDto.getIsbn() != null && !bookDto.getIsbn().trim().isEmpty()) {
                Optional<LibraryBook> existingBook = libraryBookRepository.findByIsbnAndIsActiveTrue(bookDto.getIsbn());
                if (existingBook.isPresent()) {
                    return ApiResponse.error("Book with ISBN " + bookDto.getIsbn() + " already exists");
                }
            }
            
            LibraryBook book = LibraryBook.builder()
                    .title(bookDto.getTitle())
                    .author(bookDto.getAuthor())
                    .isbn(bookDto.getIsbn())
                    .publisher(bookDto.getPublisher())
                    .publicationYear(bookDto.getPublicationYear())
                    .edition(bookDto.getEdition())
                    .category(bookDto.getCategory())
                    .subject(bookDto.getSubject())
                    .totalCopies(bookDto.getTotalCopies())
                    .availableCopies(bookDto.getAvailableCopies())
                    .borrowedCopies(bookDto.getBorrowedCopies() != null ? bookDto.getBorrowedCopies() : 0)
                    .price(bookDto.getPrice())
                    .description(bookDto.getDescription())
                    .location(bookDto.getLocation())
                    .status(bookDto.getStatus() != null ? bookDto.getStatus() : LibraryBook.BookStatus.AVAILABLE)
                    .isActive(true)
                    .build();
            
            LibraryBook savedBook = libraryBookRepository.save(book);
            LibraryBookDto responseDto = convertToDto(savedBook);
            
            log.info("Successfully created library book with ID: {}", savedBook.getId());
            return ApiResponse.success("Book created successfully", responseDto);
            
        } catch (Exception e) {
            log.error("Error creating library book: {}", e.getMessage());
            return ApiResponse.error("Failed to create book: " + e.getMessage());
        }
    }
    
    // Get all books with pagination
    public ApiResponse<Page<LibraryBookDto>> getAllBooks(int page, int size) {
        try {
            log.info("Fetching all library books - page: {}, size: {}", page, size);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<LibraryBook> books = libraryBookRepository.findByIsActiveTrue(pageable);
            Page<LibraryBookDto> bookDtos = books.map(this::convertToDto);
            
            return ApiResponse.success("Books retrieved successfully", bookDtos);
            
        } catch (Exception e) {
            log.error("Error fetching all books: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch books: " + e.getMessage());
        }
    }
    
    // Get book by ID
    public ApiResponse<LibraryBookDto> getBookById(Long id) {
        try {
            log.info("Fetching library book by ID: {}", id);
            
            Optional<LibraryBook> book = libraryBookRepository.findById(id);
            if (book.isEmpty() || !book.get().getIsActive()) {
                return ApiResponse.error("Book not found");
            }
            
            LibraryBookDto bookDto = convertToDto(book.get());
            return ApiResponse.success("Book retrieved successfully", bookDto);
            
        } catch (Exception e) {
            log.error("Error fetching book by ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to fetch book: " + e.getMessage());
        }
    }
    
    // Update book
    @Transactional
    public ApiResponse<LibraryBookDto> updateBook(Long id, LibraryBookDto bookDto) {
        try {
            log.info("Updating library book with ID: {}", id);
            
            Optional<LibraryBook> existingBook = libraryBookRepository.findById(id);
            if (existingBook.isEmpty() || !existingBook.get().getIsActive()) {
                return ApiResponse.error("Book not found");
            }
            
            LibraryBook book = existingBook.get();
            book.setTitle(bookDto.getTitle());
            book.setAuthor(bookDto.getAuthor());
            book.setIsbn(bookDto.getIsbn());
            book.setPublisher(bookDto.getPublisher());
            book.setPublicationYear(bookDto.getPublicationYear());
            book.setEdition(bookDto.getEdition());
            book.setCategory(bookDto.getCategory());
            book.setSubject(bookDto.getSubject());
            book.setTotalCopies(bookDto.getTotalCopies());
            book.setAvailableCopies(bookDto.getAvailableCopies());
            book.setBorrowedCopies(bookDto.getBorrowedCopies());
            book.setPrice(bookDto.getPrice());
            book.setDescription(bookDto.getDescription());
            book.setLocation(bookDto.getLocation());
            book.setStatus(bookDto.getStatus());
            
            LibraryBook updatedBook = libraryBookRepository.save(book);
            LibraryBookDto responseDto = convertToDto(updatedBook);
            
            log.info("Successfully updated library book with ID: {}", id);
            return ApiResponse.success("Book updated successfully", responseDto);
            
        } catch (Exception e) {
            log.error("Error updating book with ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to update book: " + e.getMessage());
        }
    }
    
    // Delete book (soft delete)
    @Transactional
    public ApiResponse<String> deleteBook(Long id) {
        try {
            log.info("Deleting library book with ID: {}", id);
            
            Optional<LibraryBook> book = libraryBookRepository.findById(id);
            if (book.isEmpty() || !book.get().getIsActive()) {
                return ApiResponse.error("Book not found");
            }
            
            LibraryBook existingBook = book.get();
            existingBook.setIsActive(false);
            libraryBookRepository.save(existingBook);
            
            log.info("Successfully deleted library book with ID: {}", id);
            return ApiResponse.success("Book deleted successfully");
            
        } catch (Exception e) {
            log.error("Error deleting book with ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to delete book: " + e.getMessage());
        }
    }
    
    // Search books
    public ApiResponse<Page<LibraryBookDto>> searchBooks(String title, String author, String category, String subject, int page, int size) {
        try {
            log.info("Searching books - title: {}, author: {}, category: {}, subject: {}", title, author, category, subject);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<LibraryBook> books = libraryBookRepository.searchBooks(title, author, category, subject, pageable);
            Page<LibraryBookDto> bookDtos = books.map(this::convertToDto);
            
            return ApiResponse.success("Search completed successfully", bookDtos);
            
        } catch (Exception e) {
            log.error("Error searching books: {}", e.getMessage());
            return ApiResponse.error("Failed to search books: " + e.getMessage());
        }
    }
    
    // Get books by category
    public ApiResponse<List<LibraryBookDto>> getBooksByCategory(String category) {
        try {
            log.info("Fetching books by category: {}", category);
            
            List<LibraryBook> books = libraryBookRepository.findByCategory(category);
            List<LibraryBookDto> bookDtos = books.stream()
                    .map(this::convertToDto)
                    .toList();
            
            return ApiResponse.success("Books retrieved successfully", bookDtos);
            
        } catch (Exception e) {
            log.error("Error fetching books by category {}: {}", category, e.getMessage());
            return ApiResponse.error("Failed to fetch books: " + e.getMessage());
        }
    }
    
    // Get books by subject
    public ApiResponse<List<LibraryBookDto>> getBooksBySubject(String subject) {
        try {
            log.info("Fetching books by subject: {}", subject);
            
            List<LibraryBook> books = libraryBookRepository.findBySubject(subject);
            List<LibraryBookDto> bookDtos = books.stream()
                    .map(this::convertToDto)
                    .toList();
            
            return ApiResponse.success("Books retrieved successfully", bookDtos);
            
        } catch (Exception e) {
            log.error("Error fetching books by subject {}: {}", subject, e.getMessage());
            return ApiResponse.error("Failed to fetch books: " + e.getMessage());
        }
    }
    
    // Get available books
    public ApiResponse<List<LibraryBookDto>> getAvailableBooks() {
        try {
            log.info("Fetching available books");
            
            List<LibraryBook> books = libraryBookRepository.findAvailableBooks();
            List<LibraryBookDto> bookDtos = books.stream()
                    .map(this::convertToDto)
                    .toList();
            
            return ApiResponse.success("Available books retrieved successfully", bookDtos);
            
        } catch (Exception e) {
            log.error("Error fetching available books: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch available books: " + e.getMessage());
        }
    }
    
    // Get books by status
    public ApiResponse<List<LibraryBookDto>> getBooksByStatus(LibraryBook.BookStatus status) {
        try {
            log.info("Fetching books by status: {}", status);
            
            List<LibraryBook> books = libraryBookRepository.findByStatusAndIsActiveTrue(status);
            List<LibraryBookDto> bookDtos = books.stream()
                    .map(this::convertToDto)
                    .toList();
            
            return ApiResponse.success("Books retrieved successfully", bookDtos);
            
        } catch (Exception e) {
            log.error("Error fetching books by status {}: {}", status, e.getMessage());
            return ApiResponse.error("Failed to fetch books: " + e.getMessage());
        }
    }
    
    // Borrow book
    @Transactional
    public ApiResponse<LibraryBookDto> borrowBook(Long bookId) {
        try {
            log.info("Borrowing book with ID: {}", bookId);
            
            Optional<LibraryBook> book = libraryBookRepository.findById(bookId);
            if (book.isEmpty() || !book.get().getIsActive()) {
                return ApiResponse.error("Book not found");
            }
            
            LibraryBook existingBook = book.get();
            if (existingBook.getAvailableCopies() <= 0) {
                return ApiResponse.error("No copies available for borrowing");
            }
            
            existingBook.setAvailableCopies(existingBook.getAvailableCopies() - 1);
            existingBook.setBorrowedCopies(existingBook.getBorrowedCopies() + 1);
            existingBook.setStatus(LibraryBook.BookStatus.BORROWED);
            
            LibraryBook updatedBook = libraryBookRepository.save(existingBook);
            LibraryBookDto responseDto = convertToDto(updatedBook);
            
            log.info("Successfully borrowed book with ID: {}", bookId);
            return ApiResponse.success("Book borrowed successfully", responseDto);
            
        } catch (Exception e) {
            log.error("Error borrowing book with ID {}: {}", bookId, e.getMessage());
            return ApiResponse.error("Failed to borrow book: " + e.getMessage());
        }
    }
    
    // Return book
    @Transactional
    public ApiResponse<LibraryBookDto> returnBook(Long bookId) {
        try {
            log.info("Returning book with ID: {}", bookId);
            
            Optional<LibraryBook> book = libraryBookRepository.findById(bookId);
            if (book.isEmpty() || !book.get().getIsActive()) {
                return ApiResponse.error("Book not found");
            }
            
            LibraryBook existingBook = book.get();
            if (existingBook.getBorrowedCopies() <= 0) {
                return ApiResponse.error("No copies to return");
            }
            
            existingBook.setAvailableCopies(existingBook.getAvailableCopies() + 1);
            existingBook.setBorrowedCopies(existingBook.getBorrowedCopies() - 1);
            existingBook.setStatus(LibraryBook.BookStatus.AVAILABLE);
            
            LibraryBook updatedBook = libraryBookRepository.save(existingBook);
            LibraryBookDto responseDto = convertToDto(updatedBook);
            
            log.info("Successfully returned book with ID: {}", bookId);
            return ApiResponse.success("Book returned successfully", responseDto);
            
        } catch (Exception e) {
            log.error("Error returning book with ID {}: {}", bookId, e.getMessage());
            return ApiResponse.error("Failed to return book: " + e.getMessage());
        }
    }
    
    // Convert entity to DTO
    private LibraryBookDto convertToDto(LibraryBook book) {
        return LibraryBookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .publicationYear(book.getPublicationYear())
                .edition(book.getEdition())
                .category(book.getCategory())
                .subject(book.getSubject())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .borrowedCopies(book.getBorrowedCopies())
                .price(book.getPrice())
                .description(book.getDescription())
                .location(book.getLocation())
                .status(book.getStatus())
                .isActive(book.getIsActive())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}

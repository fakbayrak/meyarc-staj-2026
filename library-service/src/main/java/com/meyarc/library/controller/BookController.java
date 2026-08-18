package com.meyarc.library.controller;

import com.meyarc.library.dto.BookRequest;
import com.meyarc.library.dto.BookResponse;
import com.meyarc.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        BookResponse created = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @GetMapping
    public Page<BookResponse> getAll(Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

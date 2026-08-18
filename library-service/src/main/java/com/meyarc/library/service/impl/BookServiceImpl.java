package com.meyarc.library.service.impl;

import com.meyarc.library.dto.BookRequest;
import com.meyarc.library.dto.BookResponse;
import com.meyarc.library.entity.Author;
import com.meyarc.library.entity.Book;
import com.meyarc.library.entity.Category;
import com.meyarc.library.exception.ResourceNotFoundException;
import com.meyarc.library.mapper.BookMapper;
import com.meyarc.library.repository.AuthorRepository;
import com.meyarc.library.repository.BookRepository;
import com.meyarc.library.repository.CategoryRepository;
import com.meyarc.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    @Override
    public BookResponse create(BookRequest request) {
        Book book = bookMapper.toEntity(request);
        book.setAuthor(resolveAuthor(request.getAuthorId()));
        book.setCategories(resolveCategories(request.getCategoryIds()));
        Book saved = bookRepository.save(book);
        return bookMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getById(Long id) {
        Book book = findBookOrThrow(id);
        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toResponse);
    }

    @Override
    public BookResponse update(Long id, BookRequest request) {
        Book book = findBookOrThrow(id);
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublicationYear(request.getPublicationYear());
        book.setAuthor(resolveAuthor(request.getAuthorId()));
        book.setCategories(resolveCategories(request.getCategoryIds()));
        return bookMapper.toResponse(book);
    }

    @Override
    public void delete(Long id) {
        Book book = findBookOrThrow(id);
        bookRepository.delete(book);
    }

    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book bulunamadi, id=" + id));
    }

    private Author resolveAuthor(Long authorId) {
        if (authorId == null) {
            return null;
        }
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author bulunamadi, id=" + authorId));
    }

    private Set<Category> resolveCategories(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(categoryRepository.findAllById(categoryIds));
    }
}

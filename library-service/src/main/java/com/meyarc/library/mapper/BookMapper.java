package com.meyarc.library.mapper;

import com.meyarc.library.dto.BookRequest;
import com.meyarc.library.dto.BookResponse;
import com.meyarc.library.entity.Book;
import com.meyarc.library.entity.Category;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public Book toEntity(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublicationYear(request.getPublicationYear());
        return book;
    }

    public BookResponse toResponse(Book book) {
        String authorName = book.getAuthor() != null ? book.getAuthor().getName() : null;
        Set<String> categoryNames = book.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toSet());

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublicationYear(),
                authorName,
                categoryNames
        );
    }
}

package com.meyarc.library.mapper;

import com.meyarc.library.dto.AuthorRequest;
import com.meyarc.library.dto.AuthorResponse;
import com.meyarc.library.entity.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author toEntity(AuthorRequest request) {
        Author author = new Author();
        author.setName(request.getName());
        return author;
    }

    public AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName()
        );
    }
}

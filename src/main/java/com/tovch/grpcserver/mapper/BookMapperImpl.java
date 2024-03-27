package com.tovch.grpcserver.mapper;

import com.tovch.grpc.Book;
import com.tovch.grpcserver.model.BookModel;
import org.springframework.stereotype.Service;

@Service
public class BookMapperImpl implements BookMapper {

    @Override
    public BookModel grpcBookToBook(Book book) {
        return BookModel.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .quantity(book.getQuantity())
                .build();
    }

    @Override
    public Book bookToGrpcBook(BookModel bookModel) {
        return Book.newBuilder()
                .setId(bookModel.getId())
                .setTitle(bookModel.getTitle())
                .setAuthor(bookModel.getAuthor())
                .setIsbn(bookModel.getIsbn())
                .setQuantity(bookModel.getQuantity())
                .build();
    }
}

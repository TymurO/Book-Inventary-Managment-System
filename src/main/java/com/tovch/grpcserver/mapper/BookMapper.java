package com.tovch.grpcserver.mapper;

import com.tovch.grpc.Book;
import com.tovch.grpcserver.model.BookModel;
import org.mapstruct.Mapper;

@Mapper
public interface BookMapper {

    BookModel grpcBookToBook(Book book);
    Book bookToGrpcBook(BookModel bookModel);
}

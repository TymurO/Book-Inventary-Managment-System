package com.tovch.grpcserver.service;

import com.tovch.grpc.Book;
import com.tovch.grpc.BookRequest;
import com.tovch.grpc.BookResponse;
import com.tovch.grpc.BookServiceGrpc;
import com.tovch.grpcserver.mapper.BookMapperImpl;
import com.tovch.grpcserver.model.BookModel;
import com.tovch.grpcserver.repository.BookRepository;
import com.tovch.grpcserver.util.IsbnValidator;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityNotFoundException;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@GrpcService
public class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapperImpl bookMapper;

    @Override
    public void print(Book request, StreamObserver<Book> responseObserver) {
        System.out.println(request.toString());
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }

    @Override
    public void list(BookRequest request, StreamObserver<BookResponse> responseObserver) {
        System.out.println("yes");
        if (request.hasBook()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Invalid argument(s)").asException());
        }
        else {
            if (request.hasId()) {
                try {
                    BookModel bookModel = bookRepository.getBookModelById(request.getId()).orElse(null);
                    if (bookModel == null) {
                        throw new EntityNotFoundException();
                    }
                    BookResponse bookResponse = BookResponse.newBuilder()
                            .setMessage("Book was retrieved")
                            .setBook(bookMapper.bookToGrpcBook(bookModel))
                            .build();
                    responseObserver.onNext(bookResponse);
                    responseObserver.onCompleted();
                }
                catch (EntityNotFoundException exception) {
                    responseObserver.onError(Status.NOT_FOUND.withDescription("Book with this id wasn't found").asException());
                }
            }
            else {
                List<BookModel> list = bookRepository.findAll();
                for (BookModel bookModel: list) {
                    BookResponse bookResponse = BookResponse.newBuilder()
                            .setMessage("Book " + bookModel.getTitle() + " was retrieved")
                            .setBook(bookMapper.bookToGrpcBook(bookModel))
                            .build();
                    responseObserver.onNext(bookResponse);
                }
                responseObserver.onCompleted();
            }
        }
    }

    @Override
    public void add(BookRequest request, StreamObserver<BookResponse> responseObserver) {
        if (!request.hasBook()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Can't save book, send some data").asException());
        }
        else {
            BookModel book = bookMapper.grpcBookToBook(request.getBook());
            if (!IsbnValidator.isValidIsbn10(book.getIsbn()) && !IsbnValidator.isValidIsbn13(book.getIsbn())) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Isbn is invalid").asException());
            }
            else {
                try {
                    BookModel bookModel = bookRepository.save(bookMapper.grpcBookToBook(request.getBook()));
                    BookResponse bookResponse = BookResponse.newBuilder()
                            .setMessage("Book was saved")
                            .setBook(bookMapper.bookToGrpcBook(bookModel))
                            .build();
                    responseObserver.onNext(bookResponse);
                    responseObserver.onCompleted();
                }
                catch (DataIntegrityViolationException exception) {
                    responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Book with isbn:" + request.getBook().getIsbn() + " already exists").asException());
                }
            }
        }
    }

    @Override
    public void update(BookRequest request, StreamObserver<BookResponse> responseObserver) {
        if (!request.hasBook()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Can't update book, send some data").asException());
        }
        else if (!request.hasId() && !request.getBook().hasId()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Can't update book, no identifier").asException());
        }
        else {
            if (!request.getBook().hasId()) {
                BookModel bookModelForUpdate = bookMapper.grpcBookToBook(request.getBook());
                bookModelForUpdate.setId(request.getId());
                BookModel bookModel = bookRepository.save(bookModelForUpdate);
                BookResponse bookResponse = BookResponse.newBuilder()
                        .setMessage("Book was updated")
                        .setBook(bookMapper.bookToGrpcBook(bookModel))
                        .build();
                responseObserver.onNext(bookResponse);
                responseObserver.onCompleted();
            }
            else if (!request.hasId()) {
                BookModel bookModelForUpdate = bookMapper.grpcBookToBook(request.getBook());
                BookModel bookModel = bookRepository.save(bookModelForUpdate);
                BookResponse bookResponse = BookResponse.newBuilder()
                        .setMessage("Book was updated")
                        .setBook(bookMapper.bookToGrpcBook(bookModel))
                        .build();
                responseObserver.onNext(bookResponse);
                responseObserver.onCompleted();
            }
            else if (request.getId() != request.getBook().getId()) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Can't update book, indicated different ids").asException());
            }
        }
    }

    @Override
    public void delete(BookRequest request, StreamObserver<BookResponse> responseObserver) {
        if (!request.hasId()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Can't delete book, no identifier").asException());
        }
        else if (request.hasBook()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Invalid argument(s)").asException());
        }
        else {
            try {
                BookModel bookModel = bookRepository.getBookModelById(request.getId()).orElse(null);
                if (bookModel == null) {
                    throw new EntityNotFoundException();
                }
                bookRepository.deleteById(request.getId());
                BookResponse bookResponse = BookResponse.newBuilder()
                        .setMessage("Book was deleted")
                        .setBook(bookMapper.bookToGrpcBook(bookModel))
                        .build();
                responseObserver.onNext(bookResponse);
                responseObserver.onCompleted();
            }
            catch (EntityNotFoundException exception) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("Book with this id wasn't found").asException());
            }
        }
    }
}

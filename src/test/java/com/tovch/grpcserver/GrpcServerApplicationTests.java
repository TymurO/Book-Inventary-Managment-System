package com.tovch.grpcserver;

import com.tovch.grpc.Book;
import com.tovch.grpc.BookRequest;
import com.tovch.grpc.BookResponse;
import com.tovch.grpc.BookServiceGrpc;
import com.tovch.grpcserver.mapper.BookMapperImpl;
import com.tovch.grpcserver.model.BookModel;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.citrusframework.annotations.CitrusTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = TestGrpcServerApplication.class)
class GrpcServerApplicationTests {

    @Autowired
    private BookMapperImpl mapper;

    private final Channel channel = ManagedChannelBuilder
            .forAddress("localhost", 9090)
            .usePlaintext()
            .build();

    private final BookServiceGrpc.BookServiceBlockingStub stub = BookServiceGrpc.newBlockingStub(channel);

    @Test
    void contextLoads() {
    }

    @Test
    public void mapBookModel_ReturnBook() {
        BookModel bookModel = BookModel.builder()
                .id(1L)
                .title("Test book")
                .author("Test Body")
                .isbn("1234567890")
                .quantity(10)
                .build();

        Book book = Book.newBuilder()
                .setId(1L)
                .setTitle("Test book")
                .setAuthor("Test Body")
                .setIsbn("1234567890")
                .setQuantity(10)
                .build();

        assertThat(mapper.bookToGrpcBook(bookModel)).isEqualTo(book);
    }

    @CitrusTest(name = "Adding_Book")
    @Test
    public void addRequest_ReturnResponse() {
        BookResponse response = stub.add(BookRequest.newBuilder()
                .setBook(Book.newBuilder()
                        .setTitle("Green forest")
                        .setAuthor("Wily Bily")
                        .setIsbn("7539846214")
                        .setQuantity(8)
                        .build())
                .build());

        assertThat(response).isEqualTo(response);
    }

    @CitrusTest(name = "Getting_Books")
    @Test
    public void getRequest_ReturnList() {
        List<Book> added = new ArrayList<>();
        List<Book> got= new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            BookRequest request = BookRequest.newBuilder()
                    .setBook(Book.newBuilder()
                            .setId(i)
                            .setTitle("Tree number " + i)
                            .setAuthor("Author " + i)
                            .setIsbn("1234567890")
                            .setQuantity(i)
                            .build())
                    .build();
            BookResponse response = stub.add(request);
            added.add(response.getBook());
        }
        Iterator<BookResponse> response = stub.list(BookRequest.newBuilder().build());

        while (response.hasNext()) {
            BookResponse book = response.next();
            got.add(book.getBook());
        }

        assertThat(got).isEqualTo(added);
    }

    @CitrusTest(name = "Send_Incorrect_Add_Request")
    @Test
    public void addRequest_ReturnError() {
        Exception exception = assertThrows(StatusRuntimeException.class, () -> {
            stub.add(BookRequest.newBuilder().build());
        });
        assertThat(exception.getMessage()).contains("Can't save book, send some data");
    }
}

package com.tovch.grpcserver.repository;

import com.tovch.grpcserver.model.BookModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BookRepository extends JpaRepository<BookModel, Long> {

    Optional<BookModel> getBookModelById(Long id);
}

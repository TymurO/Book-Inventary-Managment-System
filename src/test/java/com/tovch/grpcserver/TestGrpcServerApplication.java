package com.tovch.grpcserver;

import com.tovch.grpc.Book;
import com.tovch.grpc.BookRequest;
import com.tovch.grpc.BookResponse;
import com.tovch.grpc.BookServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.citrusframework.annotations.CitrusTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestConfiguration(proxyBeanMethods = false)
public class TestGrpcServerApplication {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }

    public static void main(String[] args) {
        SpringApplication.from(GrpcServerApplication::main).with(TestGrpcServerApplication.class).run(args);
    }

}

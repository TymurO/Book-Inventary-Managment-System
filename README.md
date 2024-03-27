# Book Inventory Management System

## Spring boot App using gRPC, protobuf msgs and PostgreSQL database

### How to run project on your own pc
- Download project and unarchive it
- Run it as a project im your IDE
- Change settings in `application.yaml` for your own
- Build your project so it will create necessary classes from `.proto` files
- Add dependency for you db driver(PostgreSql database is used in this project)
- Change docker image in TestGrpcServerApplication in test folder to use your db for tests

Now you can run app and get access to it on `localhost:9090`. To send request to gRPC i use  `grpcurl` - check this link to install [grpcurl](https://github.com/fullstorydev/grpcurl).
Alo you can use other tools(`Postman` etc.)
To run tests you need to install `Docker` so container with your database can be deployed

### Endpoints
- com.tovch.grpc.BookService/list - send `{"id": $id}` or `{ }` to get book by its `$id` or list of books
- com.tovch.grpc.BookService/add - send json like this <br/>
`{"book": {
  "title":"$title",
  "author":"$author",
  "isbn":"$isbn",
  "quantity":$quantity
}}`<br/>
Notice that `isbn` field is a unique, so you can't add book if its `isbn` already is in database. Also `isbn` should comply with specific structure - [see more](https://kdp.amazon.com/en_US/help/topic/G201834170#:~:text=An%20ISBN%2C%20or%20International%20Standard,to%20efficiently%20search%20for%20books.)<br/>
- com.tovch.grpc.BookService/update - send json with `new book` with a specific id like this <br/>
`{"book": {
  "id":$id,
  "title":"$title",
  "author":"$author",
  "isbn":"$isbn",
  "quantity":$quantity
}}`<br/>
or like this <br/>
`{"id":$id,
  "book": {
    "title":"$title",
    "author":"$author",
    "isbn":"$isbn",
    "quantity":$quantity
}}`<br/>
- com.tovch.grpc.BookService/update - send `{"id":$id}` to delete book with specific `$id`

### Examples of sending request to local grpc server
There is a second request if you get an error about `\'` symbol<br/>
Request to retrieve book with `id = 1` or list of books `send empty json`<br/>
`grpcurl -plaintext -d '{"id":1}'/'{}' localhost:9090 com.tovch.grpc.BookService/list`<br/>
`grpcurl -plaintext -d "{\"id\":1}"/"{}" localhost:9090 com.tovch.grpc.BookService/list`<br/>
Request to add book<br/>
`grpcurl -plaintext -d '{"book":{"title":"Black river", "author":"Tom Jelly", "isbn":"2266111566","quantity":10}}' localhost:9090 com.tovch.grpc.BookService/add`<br/>
`grpcurl -plaintext -d "{\"book\":{\"title\":\"Black river\", \"author\":\"Tom Jelly\", \"isbn\":\"2266111566\",\"quantity\":10}}" localhost:9090 com.tovch.grpc.BookService/add`<br/>
Request to update book with `id = 1`<br/>
`grpcurl -plaintext -d '{"book":{"id":1 ,"title":"Black river", "author":"Tom Jelly", "isbn":"2266111566","quantity":5}}' localhost:9090 com.tovch.grpc.BookService/update`<br/>
`grpcurl -plaintext -d "{\"book\":{\"id\":1 ,\"title\":\"Black river\", \"author\":\"Tom Jelly\", \"isbn\":\"2266111566\",\"quantity\":5}}" localhost:9090 com.tovch.grpc.BookService/update`<br/>
Request to delete book with `id = 2`<br/>
`grpcurl -plaintext -d '{"id":2}' localhost:9090 com.tovch.grpc.BookService/delete`<br/>
`grpcurl -plaintext -d "{\"id\":2}" localhost:9090 com.tovch.grpc.BookService/delete`<br/>
If you want to send request to remote server replace `localhost:9090` on its `$url`<br/>
For example server is deployed on ngRok and has this url `tcp://0.tcp.eu.ngrok.io:11216` change request to this<br/>
`grpcurl -plaintext -d '{"id":1}'/'{}' 0.tcp.eu.ngrok.io:11216 com.tovch.grpc.BookService/list`<br/>
`grpcurl -plaintext -d "{\"id\":1}"/"{}" 0.tcp.eu.ngrok.io:11216 com.tovch.grpc.BookService/list`<br/>

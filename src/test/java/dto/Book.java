package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Book {
    int id;

    public Book(String name, String author, String publication, String category, int pages, BigDecimal price) {
        this.name = name;
        this.author = author;
        this.publication = publication;
        this.category = category;
        this.pages = pages;
        this.price = price;
    }

    String name;
    String author;
    String publication;
    String category;
    int pages;
    BigDecimal price;
}

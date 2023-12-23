package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Book {
    int id;
    @NonNull
    String name;
    @NonNull
    String author;
    @NonNull
    String publication;
    @NonNull
    String category;
    @NonNull
    int pages;
    @NonNull
    BigDecimal price;
}

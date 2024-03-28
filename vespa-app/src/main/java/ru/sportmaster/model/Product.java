package ru.sportmaster.model;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public abstract class Product {

    long id;

    String name;

    String producer;

    BigDecimal price;

    String description;
}

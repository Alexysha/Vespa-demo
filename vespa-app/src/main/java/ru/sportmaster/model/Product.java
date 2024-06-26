package ru.sportmaster.model;

import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public abstract class Product {

    long id;

    String name;

    String description;

    BigDecimal price;

    List<Availability> availabilities;
}

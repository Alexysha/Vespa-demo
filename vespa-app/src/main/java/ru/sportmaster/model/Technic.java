package ru.sportmaster.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@EqualsAndHashCode(callSuper = true)
public abstract class Technic extends Product {

    boolean credit;

    boolean abroad;

    int weight;

    float height;

    float width;

    float depth;
}

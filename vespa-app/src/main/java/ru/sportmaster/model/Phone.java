package ru.sportmaster.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class Phone extends Technic {

    String color;

    String operation;

    float screen;

    List<String> material;
}

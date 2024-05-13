package ru.sportmaster.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.sportmaster.model.enums.Gender;
import ru.sportmaster.model.enums.Season;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;


@Data
@FieldDefaults(level = PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class Shoes extends Product {

    Season season;

    Map<String, Integer> material;

    Gender gender;
}

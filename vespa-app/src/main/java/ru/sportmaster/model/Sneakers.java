package ru.sportmaster.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.sportmaster.model.enums.Pavement;
import ru.sportmaster.model.enums.Sport;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class Sneakers extends Shoes {

    Sport sport;

    @JsonAlias("pavement_demo_rename")
    Pavement pavement;

    boolean insole;
}

package ru.sportmaster.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Vegetables extends Food {

    boolean seasonal;
}

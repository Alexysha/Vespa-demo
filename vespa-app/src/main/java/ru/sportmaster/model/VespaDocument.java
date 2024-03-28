package ru.sportmaster.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class VespaDocument<T> {

    Fields<T> fields;

    @Data
    public static class Fields<T> {

        @JsonUnwrapped
        T document;
    }
}

package ru.sportmaster.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class VespaDocument<T> {

    Fields<T> fields;

    Root<T> root;

    @Data
    @FieldDefaults(level = PRIVATE)
    public static class Fields<T> {

        @JsonUnwrapped
        T document;
    }

    @Data
    @FieldDefaults(level = PRIVATE)
    public static class Root<T> {

        List<Children<T>> children;

        @Data
        @FieldDefaults(level = PRIVATE)
        public static class Children<T> {

            Fields<T> fields;
        }
    }
}

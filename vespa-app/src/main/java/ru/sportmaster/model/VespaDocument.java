package ru.sportmaster.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@Data
public class VespaDocument<T> {

    Fields<T> fields;

    @Data
    public static class Fields<T> {

        @JsonUnwrapped
        T document;
    }
}

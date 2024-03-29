package ru.sportmaster.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class SearchBody {

    String yql;

    @JsonProperty("presentation.summary")
    String presentation;
}

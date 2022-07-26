package org.GroupWireMockTest.dto;


import lombok.AllArgsConstructor;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    public String cast;

    public long movie_id;

    public String name;

    public LocalDate release_date;

    public Integer year;

}

package org.GroupWireMockTest.constants;


import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.function.Function;

public class MoviesAppConstants {

    public static final String GET_ALL_MOVIES_V1 = "/movieservice/v1/allMovies";
    public static final String GET_MOVIE_BY_ID = "/movieservice/v1/movie/{movieId}";
    public static final String POST_NEW_Movie = "/movieservice/v1/movie";
    public static final String GET_MOVIE_BY_NAME="/movieservice/v1/movieName";
    public static final String UPDATE_MOVIE_BY_ID="/movieservice/v1/{movieId}";
}

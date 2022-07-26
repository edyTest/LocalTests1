package org.GroupWireMockTest.service;
import org.GroupWireMockTest.constants.MoviesAppConstants;
import org.GroupWireMockTest.dto.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class MovieRestClient {
    private WebClient webClient;

    public MovieRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Movie> retrieveAllMovies(){
       //http://localhost:8081/movieservice/v1/allMovies
       return webClient.get().uri(MoviesAppConstants.GET_ALL_MOVIES_V1)
                .retrieve()
                .bodyToFlux(Movie.class)
                .collectList()
                .block();
    }
}

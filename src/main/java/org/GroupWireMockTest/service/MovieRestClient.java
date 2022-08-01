package org.GroupWireMockTest.service;

import lombok.extern.slf4j.*;
import org.GroupWireMockTest.constants.MoviesAppConstants;
import org.GroupWireMockTest.dto.*;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
public class MovieRestClient {
    private WebClient webClient;

    public MovieRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Movie> retrieveAllMovies() {
        //http://localhost:8081/movieservice/v1/allMovies
        return webClient.get().uri(MoviesAppConstants.GET_ALL_MOVIES_V1)
                .retrieve()
                .bodyToFlux(Movie.class)
                .collectList()
                .block();
    }

    public Movie retrieveMovieById(Integer movieId) {
        Movie movieReturn = null;
        try {
            movieReturn = webClient.get().uri(MoviesAppConstants.GET_MOVIE_BY_ID, movieId)
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();

        } catch (WebClientResponseException exR) {
            log.error("WebClientResponseException in retrievedMovieId. status code is '{}'  and the message is '{}'", exR.getStatusCode(), exR.getResponseBodyAsString());
            throw exR;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw ex;
        }

        return movieReturn;
    }

    public Movie addNewMovie(Movie movieAdded) {
        Movie movieReturn = null;
        try {


            movieReturn = webClient.post().uri(MoviesAppConstants.POST_NEW_Movie)
                    .body(BodyInserters.fromObject(movieAdded))
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
        } catch (WebClientResponseException exR) {
            log.error("WebClientResponseException in retrievedMovieId. status code is '{}'  and the message is '{}'", exR.getStatusCode(), exR.getResponseBodyAsString());
            throw exR;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw ex;
        }
        return movieReturn;
    }


    public List<Movie> retrieveMovieName(String movieName) {
        List<Movie> movieReturn = null;
        String retrieveByNameUri= UriComponentsBuilder.fromUriString(MoviesAppConstants.GET_MOVIE_BY_NAME)
                .queryParam("movie_name",movieName)
                .buildAndExpand()
                .toUriString();
        try {
            movieReturn = webClient.get().uri(retrieveByNameUri)
                    .retrieve()
                    .bodyToFlux(Movie.class)
                    .collectList()
                    .block();

        } catch (WebClientResponseException exR) {
            log.error("WebClientResponseException in retrievedMovieId. status code is '{}'  and the message is '{}'", exR.getStatusCode(), exR.getResponseBodyAsString());
            throw exR;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw ex;
        }

        return movieReturn;
    }

    public Movie updateMovie(Movie movieAdded, long movieId) {
        Movie movieReturn = null;
        try {


            movieReturn = webClient.put().uri(MoviesAppConstants.UPDATE_MOVIE_BY_ID,movieId)
                    .body(BodyInserters.fromObject(movieAdded))
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
        } catch (WebClientResponseException exR) {
            log.error("WebClientResponseException in retrievedMovieId. status code is '{}'  and the message is '{}'", exR.getStatusCode(), exR.getResponseBodyAsString());
            throw exR;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw ex;
        }
        return movieReturn;
    }
}

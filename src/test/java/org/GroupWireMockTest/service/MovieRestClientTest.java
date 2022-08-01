package org.GroupWireMockTest.service;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.GroupWireMockTest.constants.MoviesAppConstants;
import org.GroupWireMockTest.dto.*;

import org.apache.http.HttpStatus;
import org.apache.http.HttpHeaders;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.tcp.TcpClient;



import java.awt.*;
import java.time.LocalDate;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.GroupWireMockTest.constants.MoviesAppConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class MovieRestClientTest {

    Options options = wireMockConfig()
            .port(8083)
            .notifier(new ConsoleNotifier(true))
            .extensions(new ResponseTemplateTransformer(true));
    @Rule
            public WireMockRule wireMockRule = new WireMockRule(options);

        TcpClient tcpClient =  TcpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
        .doOnConnected(connection ->{
            connection.
                    addHandlerLast(new ReadTimeoutHandler(5))
                    .addHandlerLast(new ReadTimeoutHandler(5));});
    MovieRestClient movieRestClient;
    WebClient webClient;

    @Before
    public void setUp() {
        int port = wireMockRule.port();
        String baseURL = String.format("http://localhost:%s/", port);
        // String baseUrl = "http://localhost:8081";
        System.out.println("Base URL::" + baseURL);
      webClient = WebClient.create(baseURL);
      stubFor(any(anyUrl()).willReturn(aResponse().proxiedFrom("http://localhost:8081")));
       // webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
       //         .baseUrl(baseURL)
       //        .build();
        movieRestClient = new MovieRestClient(webClient);

    }

    @Test
    public void retrieveAllMovies() {
        //given
        stubFor(get(anyUrl())
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)

                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("all-movies.json")));
        //when
        List<Movie> movieList = movieRestClient.retrieveAllMovies();
        System.out.printf("Movie List: " + movieList);
        assertTrue(movieList.size() > 0);
    }
    @Test
    public void retrieveAllMovies_matchesURL() {
        //given
        stubFor(get(urlPathEqualTo(MoviesAppConstants.GET_ALL_MOVIES_V1))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)

                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("all-movies.json")));
        //when
        List<Movie> movieList = movieRestClient.retrieveAllMovies();
        System.out.printf("Movie List: " + movieList);
        assertTrue(movieList.size() > 0);
    }


    @Test
   public void retrieveMovieNameById() {
        //given
        stubFor(get(urlMatching("/movieservice/v1/movie/[0-9]"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("movie.json")));

        Integer idMovie = 1;
        Movie myMovie = movieRestClient.retrieveMovieById(idMovie);
        System.out.printf("Movie List: " + myMovie);
        assertEquals("Batman Begins1", myMovie.getName());

    }




    @Test
    public void retrieveMovieNameByIdResponseTemplate() {
        //given
        stubFor(get(urlMatching("/movieservice/v1/movie/[0-9]"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("movie-template.json")));

        Integer idMovie = 8;
        Movie myMovie = movieRestClient.retrieveMovieById(idMovie);
        System.out.printf("Movie List: " + myMovie);
        assertEquals("Batman Begins1", myMovie.getName());
       //assertEquals(9,  myMovie.getMovie_id().intValue());

    }
    @Test
    public void retrieveMovieNameByName() {
        //given
        String movieName = "Avengers";
        stubFor(get(urlEqualTo(GET_MOVIE_BY_NAME+"?movie_name="+movieName))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("avengers.json")));


        List<Movie> mymovies = movieRestClient.retrieveMovieName(movieName);
        System.out.printf("Movie List: " + mymovies);
        assertEquals(3,mymovies.size());
        //assertEquals(9,  myMovie.getMovie_id().intValue());

    }

    @Test
    public void retrieveMovieNameByNameTemplate() {
        //given
        String movieName = "Avengers";
        stubFor(get(urlEqualTo(GET_MOVIE_BY_NAME+"?movie_name="+movieName))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("avengers-template-byName.json")));


        List<Movie> mymovies = movieRestClient.retrieveMovieName(movieName);
        System.out.printf("Movie List: " + mymovies);
        assertEquals(3,mymovies.size());
        //assertEquals(9,  myMovie.getMovie_id().intValue());

    }
    @Test
    public void addNewMovie() {

        Movie myMovie = new Movie();
        myMovie.setMovie_id(11);
        myMovie.setCast("Leonardo DiCaprio, Katie Holmes , Liam Neeson");
        myMovie.setName("Titanic");
        myMovie.setRelease_date(LocalDate.parse("1997-06-15"));
        myMovie.setYear(1997);

        stubFor(post(urlEqualTo(POST_NEW_Movie))
                .withRequestBody(matchingJsonPath(("$.name")))
                .withRequestBody(matchingJsonPath(("$.cast"),containing("Leonardo"))) //adding assert
                .withRequestBody(matchingJsonPath(("$.year")))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("add-Movie.json")));
        System.out.printf("Movie List: " + myMovie);

        Movie movieResponse = movieRestClient.addNewMovie(myMovie);
        assertTrue(movieResponse.getName() != null);

    }

    @Test
    public void addNewMovieTemplate() {

        Movie myMovie = new Movie();
        myMovie.setMovie_id(11);
        myMovie.setCast("Leonardo DiCaprio, Katie Holmes , Liam Neeson");
        myMovie.setName("Titanic");
        myMovie.setRelease_date(LocalDate.parse("1997-06-15"));
        myMovie.setYear(1997);




        stubFor(post(urlEqualTo(POST_NEW_Movie))
                .withRequestBody(matchingJsonPath(("$.name")))
                .withRequestBody(matchingJsonPath(("$.cast"),containing("Leonardo"))) //adding assert
                .withRequestBody(matchingJsonPath(("$.year")))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("add-Movie.json")));
        System.out.printf("Movie List: " + myMovie);

        Movie movieResponse = movieRestClient.addNewMovie(myMovie);
        assertTrue(movieResponse.getName() != null);
        verify(moreThanOrExactly(1),postRequestedFor(urlPathMatching(POST_NEW_Movie))
                .withRequestBody(matchingJsonPath(("$.name")))
                .withRequestBody(matchingJsonPath(("$.cast"),containing("Leonardo"))) //adding assert
                .withRequestBody(matchingJsonPath(("$.year"))));

    }

    @Test
    public void updateMovie() {
        int movie_id=6;
        Movie myMovie = new Movie();
       // myMovie.setMovie_id(5);
        myMovie.setCast("Leonardo DiCaprio, Katie Holmes , Liam Neeson");
        myMovie.setName("Titanic");
        myMovie.setRelease_date(LocalDate.parse("1997-06-15"));
        myMovie.setYear(1997);

        stubFor(put(urlPathMatching("/movieservice/v1/[0-9]+"))
               .withRequestBody(matchingJsonPath(("$.cast"),containing("Leonardo"))) //adding assert
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("update-movie-template.json")));
        System.out.printf("Movie List: " + myMovie);

        Movie movieResponse = movieRestClient.updateMovie(myMovie,movie_id);
        assertTrue(movieResponse.getName() != null);

        //validate endpoit is invoked
        verify(moreThan(0),putRequestedFor(urlPathMatching("/movieservice/v1/[0-9]+"))
                .withRequestBody(matchingJsonPath(("$.cast"),containing("Leonardo"))));

    }
}

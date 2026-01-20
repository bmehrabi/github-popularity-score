package com.popularity.score.client;

import com.popularity.score.dto.GitHubOwner;
import com.popularity.score.dto.GitHubRepoItem;
import com.popularity.score.dto.GitHubSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GitHubSearchClientTest {

    private GitHubSearchClient client;

    private ExchangeFunction exchangeFunction;

    private WebClient webClient;

    private ClientResponse clientResponse;

    @BeforeEach
    void setup() {
        // Mock ExchangeFunction
        exchangeFunction = mock(ExchangeFunction.class);

        webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        client = new GitHubSearchClient(webClient);
    }

    @Test
    void search_returns_items_from_github() {
        List<GitHubRepoItem> items = List.of(
                new GitHubRepoItem(
                        "repo1",
                        new GitHubOwner("user1"),
                        10,
                        5,
                        Instant.parse("2022-01-01T00:00:00Z"),
                        Instant.parse("2022-02-01T00:00:00Z"),
                        "Java",
                        1.0
                )
        );

        GitHubSearchResponse mockResponse = new GitHubSearchResponse(items);

        clientResponse = mock(ClientResponse.class);
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(clientResponse));
        when(clientResponse.statusCode()).thenReturn(org.springframework.http.HttpStatus.OK); // <-- important
        when(clientResponse.bodyToMono(GitHubSearchResponse.class))
                .thenReturn(Mono.just(mockResponse));


        List<GitHubRepoItem> result = client.search(LocalDate.of(2022, 1, 1), "Java", 1, 10);

        // Assertions
        assertEquals(1, result.size());
        assertEquals("repo1", result.get(0).getName());
        assertEquals("user1", result.get(0).getOwner().getLogin());

        // Verify exchangeFunction called
        verify(exchangeFunction, times(1)).exchange(any());
        verify(clientResponse, times(1)).bodyToMono(GitHubSearchResponse.class);
    }
}

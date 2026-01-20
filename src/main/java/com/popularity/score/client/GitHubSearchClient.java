package com.popularity.score.client;

import com.popularity.score.dto.GitHubRepoItem;
import com.popularity.score.dto.GitHubSearchResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class GitHubSearchClient {

    private final WebClient githubWebClient;

    public GitHubSearchClient(WebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }

    public List<GitHubRepoItem> search(
            LocalDate createdAfter,
            String language,
            int page,
            int size
    ) {
        String query = "language:" + language + " created:>=" + createdAfter;

        GitHubSearchResponse response = githubWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/repositories")
                        .queryParam("q", query)
                        .queryParam("sort", "stars")
                        .queryParam("order", "desc")
                        .queryParam("page", page)
                        .queryParam("per_page", size)
                        .build()
                )
                .retrieve()
                .bodyToMono(GitHubSearchResponse.class)
                .block();

        return response != null ? response.getItems() : Collections.emptyList();
    }
}

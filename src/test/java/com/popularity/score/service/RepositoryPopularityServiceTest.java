package com.popularity.score.service;

import com.popularity.score.client.GitHubSearchClient;
import com.popularity.score.dto.GitHubOwner;
import com.popularity.score.dto.GitHubRepoItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RepositoryPopularityServiceTest {

    private GitHubSearchClient client;
    private RepositoryPopularityService service;

    @BeforeEach
    void setUp() {
        client = mock(GitHubSearchClient.class);
        service = new RepositoryPopularityService(client);
    }

    @Test
    void getPopularRepositories_calculatesPopularityScoreAndSortsDescending() {
        Instant now = Instant.now();

        GitHubRepoItem repo1 = new GitHubRepoItem(
                "repo1",
                new GitHubOwner("user1"),
                100, // stargazers_count
                50,  // forks_count
                now.minusSeconds(2 * 24 * 3600), // updated_at
                Instant.now(),                    // created_at
                "Kotlin",
                0.0
        );

        GitHubRepoItem repo2 = new GitHubRepoItem(
                "repo2",
                new GitHubOwner("user2"),
                50,   // stargazers_count
                100,  // forks_count
                now.minusSeconds(40 * 24 * 3600), // updated_at
                Instant.now(),                    // created_at
                "Kotlin",
                0.0
        );

        when(client.search(any(LocalDate.class), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(repo1, repo2));

        List<GitHubRepoItem> result = service.getPopularRepositories(LocalDate.now().minusMonths(1), "Kotlin", 1, 30);

        // Ensure sorted descending by popularityScore
        assertTrue(result.get(0).getPopularityScore() >= result.get(1).getPopularityScore());

        // Ensure all scores are > 0
        assertTrue(result.stream().allMatch(r -> r.getPopularityScore() > 0.0));

        // Ensure both repo names are present
        assertTrue(result.stream().map(GitHubRepoItem::getName).toList().containsAll(List.of("repo1", "repo2")));

        double scoreRepo1 = result.stream().filter(r -> r.getName().equals("repo1"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("repo1 not found"))
                .getPopularityScore();
        double scoreRepo2 = result.stream().filter(r -> r.getName().equals("repo2"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("repo1 not found"))
                .getPopularityScore();

        assertTrue(scoreRepo1 > scoreRepo2);
    }

    @Test
    void getPopularRepositories_handlesEmptyList() {
        when(client.search(any(LocalDate.class), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());

        List<GitHubRepoItem> result = service.getPopularRepositories(LocalDate.now().minusMonths(1), "Kotlin", 1, 30);

        assertEquals(0, result.size());
    }
}

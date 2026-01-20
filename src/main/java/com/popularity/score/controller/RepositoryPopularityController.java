package com.popularity.score.controller;

import com.popularity.score.client.GitHubSearchClient;
import com.popularity.score.dto.GitHubRepoItem;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/repositories")
public class RepositoryPopularityController {

    private final GitHubSearchClient gitHubSearchClient;

    public RepositoryPopularityController(GitHubSearchClient gitHubSearchClient) {
        this.gitHubSearchClient = gitHubSearchClient;
    }

    @GetMapping
    public List<GitHubRepoItem> searchRepositories(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate createdAfter,

            @RequestParam String language,

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        return gitHubSearchClient.search(createdAfter, language, page, size);
    }
}

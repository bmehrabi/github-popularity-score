package com.popularity.score.service;

import com.popularity.score.client.GitHubSearchClient;
import com.popularity.score.dto.GitHubRepoItem;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.log;

@Service
public class RepositoryPopularityService {

    private final GitHubSearchClient client;
    private static final double STAR_IMPORTANCE_FACTOR = 0.7;

    public RepositoryPopularityService(GitHubSearchClient client) {
        this.client = client;
    }

    public List<GitHubRepoItem> getPopularRepositories(
            LocalDate createdAfter,
            String language,
            int page,
            int size
    ) {
        List<GitHubRepoItem> repos = client.search(createdAfter, language, page, size);
        Instant now = Instant.now();

        return repos.stream()
                .map(repo -> {
                    GitHubRepoItem copy = new GitHubRepoItem(
                            repo.getName(),
                            repo.getOwner(),
                            repo.getStargazersCount(),
                            repo.getForksCount(),
                            repo.getUpdatedAt(),
                            repo.getCreatedAt(),
                            repo.getLanguage(),
                            0.0d
                    );
                    copy.setPopularityScore(calculatePopularityScore(repo, now));
                    return copy;
                })
                .sorted(Comparator.comparingDouble(GitHubRepoItem::getPopularityScore).reversed())
                .limit(50)
                .collect(Collectors.toList());
    }

    private double calculateRecencyFactor(long daysSinceUpdate) {
        if (daysSinceUpdate <= 7) {
            return 1.5;
        } else if (daysSinceUpdate <= 30) {
            return 1.2;
        } else {
            return 1.0;
        }
    }

    private double calculatePopularityScore(GitHubRepoItem repo, Instant now) {
        double starsLog = log(repo.getStargazersCount() + 1) / log(2.0);
        double forksLog = log(repo.getForksCount() + 1) / log(2.0);

        long daysSinceUpdate = ChronoUnit.DAYS.between(repo.getUpdatedAt(), now);
        double recencyFactor = calculateRecencyFactor(daysSinceUpdate);

        return (starsLog * STAR_IMPORTANCE_FACTOR + forksLog * (1 - STAR_IMPORTANCE_FACTOR)) * recencyFactor;
    }
}

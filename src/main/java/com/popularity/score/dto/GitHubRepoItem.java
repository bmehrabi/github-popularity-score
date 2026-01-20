package com.popularity.score.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GitHubRepoItem {

    private String name;
    private GitHubOwner owner;

    @JsonProperty("stargazers_count")
    private int stargazersCount;

    @JsonProperty("forks_count")
    private int forksCount;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    @JsonProperty("created_at")
    private Instant createdAt;

    private String language;

    @JsonProperty("popularity_score")
    private double popularityScore = 0.0;
}

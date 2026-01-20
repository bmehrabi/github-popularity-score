package com.popularity.score.dto;

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
    private int stargazers_count;
    private int forks_count;
    private Instant updated_at;
    private Instant created_at;
    private String language;
}

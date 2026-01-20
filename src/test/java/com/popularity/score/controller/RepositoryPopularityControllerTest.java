package com.popularity.score.controller;

import com.popularity.score.dto.GitHubOwner;
import com.popularity.score.dto.GitHubRepoItem;
import com.popularity.score.service.RepositoryPopularityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RepositoryPopularityController.class)
class RepositoryPopularityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepositoryPopularityService repositoryPopularityService;

    private List<GitHubRepoItem> testItems;

    @BeforeEach
    void setup() {
        testItems = List.of(
                new GitHubRepoItem(
                        "repo1",
                        new GitHubOwner("user1"),
                        10,
                        5,
                        Instant.parse("2022-01-01T00:00:00Z"),
                        Instant.parse("2022-02-01T00:00:00Z"),
                        "Kotlin",
                        0.0
                )
        );

        when(repositoryPopularityService.getPopularRepositories(
                LocalDate.of(2022, 1, 1),
                "Kotlin",
                1,
                30
        )).thenReturn(testItems);
    }

    @Test
    void get_repositories_returns_json_list() throws Exception {
        mockMvc.perform(
                        get("/api/v1/repositories")
                                .param("createdAfter", "2022-01-01")
                                .param("language", "Kotlin")
                                .param("page", "1")
                                .param("size", "30")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("repo1"))
                .andExpect(jsonPath("$[0].owner.login").value("user1"));

        // Verify that the controller called the client correctly
        verify(repositoryPopularityService).getPopularRepositories(
                LocalDate.of(2022, 1, 1),
                "Kotlin",
                1,
                30
        );
    }
}

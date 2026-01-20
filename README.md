# GitHub Repository Popularity Score API

A Java Spring Boot REST API that calculates and returns a **popularity score** for GitHub repositories based on stars, forks, and recency of updates. The API is designed to be fast, scalable, and easy to extend.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Popularity Score Calculation](#popularity-score-calculation)
- [Performance Optimizations](#performance-optimizations)
- [Endpoints](#endpoints)
- [Running the Project](#running-the-project)

---

## Features

- Fetch repositories from GitHub based on **creation date** and **programming language**.
- Calculate a **popularity score** using a logarithmic algorithm and recency factor.
- Returns the **top repositories sorted by popularity**.
- Optimized for performance when handling large responses.

---

## Architecture

The project follows a **layered architecture**:

### 1. Controller Layer
- **`RepositoryPopularityController`** exposes REST endpoints.
- Thin layer: delegates all business logic to the service.
- Handles input parameters and returns JSON responses.

### 2. Service Layer
- **`RepositoryPopularityService`** contains the main business logic.
- Fetches repositories via the **GitHub client**.
- Calculates the popularity score for each repository.
- Sorts repositories by popularity and limits the number of results (top N).

### 3. Client Layer
- **`GitHubSearchClient`** communicates with the **GitHub API**.
- Handles constructing requests and parsing responses.

---

## Popularity Score Calculation

The popularity score is calculated using the formula:

```
popularityScore = (log2(stars + 1) * 0.7 + log2(forks + 1) * 0.3) * recencyFactor
```

Where:
- **Stars and forks** are logarithmically scaled to reduce the dominance of extremely popular repositories.
- **Recency factor** boosts recently updated repositories:

| Days Since Update | Recency Factor |
|------------------|----------------|
| ≤ 7 days         | 1.5            |
| ≤ 30 days        | 1.2            |
| > 30 days        | 1.0            |

### Why logarithmic scaling?

- Prevents repositories with huge star/fork counts from dominating the ranking.
- Gives smaller repositories a fair chance to appear in the top results.
- Smooths out extreme differences while preserving relative popularity.

---

## Performance Optimizations

- **Lazy-like processing with Streams**: The service uses Java `Stream`s to process GitHub responses efficiently, mapping and sorting repositories without creating unnecessary intermediate collections.
- **Top N selection**: Only the top 50 repositories are returned to reduce memory usage and payload size.
- **Precomputed timestamp**: `Instant.now()` is calculated once per request to avoid repeated calls during score computation.
- **Recency-aware scoring**: The popularity score calculation uses the last update timestamp to prioritize recently active repositories.
- **Weighted scoring**: Stars and forks are combined with a configurable importance factor to compute a meaningful popularity metric.
- **Optional caching**: GitHub API responses can still be cached for a few minutes to reduce network calls and improve performance.

---

## Endpoints

### GET `/api/v1/repositories`

Fetch repositories and return popularity scores.

**Query Parameters:**

| Parameter      | Type   | Description                       |
|----------------|--------|-----------------------------------|
| `createdAfter` | Date   | ISO date (YYYY-MM-DD)             |
| `language`     | String | Programming language              |
| `page`         | Int    | Page number (default 1)           |
| `size`         | Int    | Page size (default 30)            |

**Example Request:**

```http
GET /api/v1/repositories?createdAfter=2023-01-01&language=Kotlin&page=1&size=30
```

## Running the Project

Follow these steps to run the GitHub Repository Popularity Score API locally:

### 1. Clone the repository

```bash
git clone https://github.com/bmehrabi/popularity-score.git
cd popularity-score
```

### 2. Build and run the project

`./gradlew bootRun`

### 3. Access the API

Use your browser or tools like curl or Postman to make requests:

```
GET http://localhost:8080/api/v1/repositories?createdAfter=2022-01-01&language=Kotlin
```




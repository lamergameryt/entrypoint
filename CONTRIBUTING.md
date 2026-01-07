# Contributing to EntryPoint

Thank you for your interest in contributing! This guide outlines how to propose changes and what we expect from contributions.

## Ground Rules

- Be respectful and follow the [Code of Conduct](CODE_OF_CONDUCT.md).
- Keep changes focused and minimal.
- Use clear commit messages and descriptive pull request titles.

## Getting Started

1. Fork the repository and create a feature branch from `main`.
2. Install JDK 17.
3. From `entrypoint-backend`, run the test suite:
   ```bash
   ./gradlew test
   ```
4. Format code before submitting:
   ```bash
   ./gradlew spotlessApply
   ```

### Running the application locally

For local development against MySQL, create an `env.properties` file in `entrypoint-backend` (see `README.md`) and run:

```bash
./gradlew bootRun
```

Tests use an in-memory H2 database and do **not** require MySQL or `env.properties`.

## Submitting Changes

1. Ensure your branch is up to date with `main`.
2. Add tests when you change behavior or add features.
3. Verify checks locally (`./gradlew test` and `./gradlew spotlessCheck`).
4. Open a pull request that includes:
   - A concise summary of the change and motivation.
   - Any manual testing performed.
   - Screenshots for UI-affecting changes (if applicable).

By submitting a contribution, you agree that your work will be licensed under the AGPL-3.0 license that governs this project.

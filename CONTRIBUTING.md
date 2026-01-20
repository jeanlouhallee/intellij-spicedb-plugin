# Contributing to SpiceDB IntelliJ Plugin

Thank you for your interest in contributing to the SpiceDB IntelliJ Plugin!

## Getting Started

1. Fork the repository
2. Clone your fork locally
3. Set up your development environment (see README.md)

## Development

### Prerequisites

- Java 21+
- Maven 3.8+
- IntelliJ IDEA (for testing)

### Building

```bash
mvn clean package
```

### Testing Changes

1. Build the plugin: `mvn clean package`
2. In IntelliJ: **Settings → Plugins → ⚙️ → Install Plugin from Disk...**
3. Select `target/intellij-spicedb-plugin-1.0.0-SNAPSHOT.zip`
4. Restart IntelliJ

## Submitting Changes

1. Create a feature branch from `main`
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## Code Style

- Follow existing code patterns
- Keep changes focused and minimal
- Add comments for complex logic

## Reporting Issues

- Use GitHub Issues
- Include IntelliJ version and OS
- Provide steps to reproduce

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.

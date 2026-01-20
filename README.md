# SpiceDB IntelliJ Plugin

Community plugin for [SpiceDB](https://github.com/authzed/spicedb) schema files (`.zed`).

> **Note:** This is a community-maintained plugin, not affiliated with or endorsed by AuthZed.

## Features

- Syntax highlighting for keywords, operators, comments, and identifiers
- Distinct colors for definitions, relations, permissions, and caveats
- Error highlighting for undefined types and references
- Go to Definition (Ctrl+Click) for type references and arrow expressions
- Brace matching and comment support

## Installation

### From Release (doesn't work yet. TODO)

1. Download the latest `.zip` from Releases
2. In IntelliJ: **Settings → Plugins → ⚙️ → Install Plugin from Disk...**
3. Select the downloaded ZIP file
4. Restart IntelliJ

### From Source

```bash
# Using build script (recommended)
./build.sh

# Or manually:
mvn initialize    # Downloads IntelliJ SDK (~800MB, first time only)
mvn clean package
```

The IntelliJ SDK is cached in `.intellij-sdk/` and reused between builds.

**Install:** Settings → Plugins → ⚙️ → Install Plugin from Disk... → select `target/intellij-spicedb-plugin-1.0.0-SNAPSHOT.zip`

## Requirements

- IntelliJ IDEA 2024.3+
- Java 21+ (for building)

## Schema File Location

Go to Definition searches for schemas in:
- `./schema/schema.zed`
- `./schema/compose/*.zed`

## License

Apache License 2.0 - see [LICENSE](LICENSE)

# ResultSet Mapper

ResultSet Mapper is a lightweight Java library for mapping JDBC `ResultSet` objects to Java objects (POJOs). It simplifies the process of converting relational data into an object-oriented format, reducing boilerplate code and improving readability.

## Features

-   **Annotation-based mapping:** Define mappings using simple annotations (`@ColumnMapping`) on your POJO fields or record components.
-   **Supports Classes and Records:** Can map `ResultSet` data to both traditional Java classes and modern Java Records.
-   **Customizable type conversion:** Provides standard converters for common data types and allows for custom converter registration.
-   **Flexible naming strategies:** Supports different naming conventions for database columns and Java fields (e.g., snake_case to camelCase).
-   **Configurable and easy to use:** Allows configuration for case-insensitive column matching and ignoring unknown columns for quick integration.
-   **Builder pattern:** `RowMapper` instances are created using a fluent builder API.

## Getting Started

### Installation

Make sure to add the required repositories. For GitLab, if your package is in a public group repository or you have access, you can use the group URL.

**Gradle (Groovy DSL):**
```groovy
repositories {
    mavenCentral()
    maven {
        url = uri("https://gitlab.com/api/v4/groups/dev.bxlab.resultset.mapper/-/packages/maven")
    }
}

dependencies {
    implementation 'dev.bxlab.resultset.mapper:resultset-mapper:1.0-SNAPSHOT'
}
```

**Maven:**
```xml
<repositories>
    <repository>
        <id>gitlab-maven</id>
        <url>https://gitlab.com/api/v4/groups/dev.bxlab.resultset.mapper/-/packages/maven</url>
    </repository>
    <repository>
        <id>central</id>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.bxlab.resultset.mapper</groupId>
        <artifactId>resultset-mapper</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```
*Note: If your package is at the project level or the group is not public, the repository URL may be `https://gitlab.com/api/v4/projects/YOUR_PROJECT_ID/packages/maven`. Replace `YOUR_PROJECT_ID` with your GitLab project ID.*

### Usage

1.  **Define your POJO or Record:**

    Java class (POJO):
    ```java
    public class User {
        @ColumnMapping("user_id")
        private int id;

        @ColumnMapping("username")
        private String name;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
    ```

    Or using a Java Record (recommended for immutability and conciseness):
    ```java
    public record BasicUser(
        @ColumnMapping("user_id") Integer id,
        @ColumnMapping("username") String name,
        @ColumnMapping("is_active") boolean active
    ) {}
    ```

2.  **Create a `RowMapper` instance:**

    For the `User` class:
    ```java
    ResultSetMapper<User> userMapper = RowMapperBuilder.forType(User.class).build();
    ```

    For the `BasicUser` record:
    ```java
    ResultSetMapper<BasicUser> basicUserMapper = RowMapperBuilder.forType(BasicUser.class)
                                                     .caseInsensitiveColumns(true) // Optional: for case-insensitive mapping
                                                     .build();
    ```

3.  **Map a `ResultSet`:**

    ```java
    List<User> users = new ArrayList<>();
    // Assuming 'connection' is a valid JDBC connection
    try (PreparedStatement stmt = connection.prepareStatement("SELECT user_id, username FROM users_table");
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            users.add(userMapper.map(rs));
        }
    }
    // Now the 'users' list contains User objects mapped from the ResultSet
    ```

## Advanced Usage

### Custom Type Converters

If you need to map a database type to a custom Java type, or want to override the conversion for an existing type, you can register a converter.

You can implement the `TypeConverter<T>` interface for complex logic:
```java
public class MyCustomTypeConverter implements TypeConverter<MyCustomType> {
    @Override
    public MyCustomType convert(ResultSet rs, String columnLabel, Class<?> targetType, Map<String, Object> attributes) throws SQLException {
        String valueFromDb = rs.getString(columnLabel);
        if (valueFromDb == null) {
            return null;
        }
        return new MyCustomType(valueFromDb);
    }
}

ResultSetMapper<MyEntity> mapper = RowMapperBuilder.forType(MyEntity.class)
        .registerConverter(MyCustomType.class, new MyCustomTypeConverter())
        .build();
```

Or use a lambda for simpler converters (example for `LocalDate`):
```java
ResultSetMapper<UserWithDate> mapper = RowMapperBuilder.forType(UserWithDate.class)
        .registerConverter(LocalDate.class, (rs, columnLabel, targetType, attributes) -> {
            String dateStr = rs.getString(columnLabel);
            if (dateStr == null || dateStr.isEmpty()) {
                return null;
            }
            return LocalDate.parse(dateStr); // Assumes ISO format (YYYY-MM-DD)
        })
        .build();
```

### Naming Strategies

The library defaults to an identity naming strategy (column name matches field name). You can specify a different strategy, like `NamingStrategy.SNAKE_CASE`, which converts snake_case column names to camelCase field names:

```java
ResultSetMapper<Product> productMapper = RowMapperBuilder.forType(Product.class)
        .withNamingStrategy(NamingStrategy.SNAKE_CASE)
        .build();
```
In this case, a column named `product_id` would map to a field named `productId`, and `product_name` to `productName`.

### Case-Insensitive Column Mapping

You can control case sensitivity explicitly:

```java
ResultSetMapper<User> userMapper = RowMapperBuilder.forType(User.class)
        .caseInsensitiveColumns(true) // true to ignore case, false to be strict
        .build();
```

### Ignoring Unknown Columns

You can configure the mapper to ignore unknown columns:

```java
ResultSetMapper<User> userMapper = RowMapperBuilder.forType(User.class)
        .ignoreUnknownColumns(true) // true to ignore unmapped columns, false to throw exception
        .build();
```

### Field-specific Formatting

You can specify format attributes for data types like dates directly in the `@ColumnMapping` annotation:

```java
import java.util.Date;

public record UserWithFormattedDate(
    @ColumnMapping("ID") Integer id,
    @ColumnMapping(value = "CREATED_AT", format = "yyyy-MM-dd") Date createdAt
) {}

ResultSetMapper<UserWithFormattedDate> mapper = RowMapperBuilder.forType(UserWithFormattedDate.class).build();
```

## Building from Source

1.  Clone the repository (make sure the URL is correct for your project, this is an example):
    ```pwsh
    git clone https://gitlab.com/dev.bxlab.resultset.mapper/resultset-mapper.git
    # Or your specific GitLab repository URL
    ```
2.  Navigate to the project directory:
    ```pwsh
    cd resultset-mapper
    ```
3.  Build with Gradle Wrapper:
    ```pwsh
    .\gradlew.bat build
    # Or on Linux/macOS:
    # ./gradlew build
    ```
    You can use `.\gradlew.bat clean build` to clean before building.

## Contributing

Contributions are welcome! Please feel free to submit a pull request or open an issue.

_(Details on contribution guidelines can be added here.)_

## License

This project is licensed under the MIT License - see the `LICENSE` file for details.

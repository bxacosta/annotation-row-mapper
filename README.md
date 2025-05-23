# ResultSet Mapper

ResultSet Mapper is a lightweight Java library for mapping JDBC `ResultSet` objects to Java objects (POJOs). It simplifies the process of converting relational data into an object-oriented format, reducing boilerplate code and improving readability.

## Features

-   **Annotation-based mapping:** Define mappings using simple annotations on your POJO fields.
-   **Supports Classes and Records:** Can map `ResultSet` data to both traditional Java classes and modern Java Records.
-   **Customizable type conversion:** Provides standard converters for common data types and allows for custom converter registration.
-   **Flexible naming strategies:** Supports different naming conventions for database columns and Java fields (e.g., snake_case to camelCase).
-   **Easy to use:** Requires minimal setup, with defaults like case-insensitive matching and ignored unknown columns for quick integration.
-   **Builder pattern:** `RowMapper` instances are created using a fluent builder API.

## Getting Started

### Prerequisites

-   Java 8 or higher
-   A JDBC-compliant database driver

### Installation

_(Instructions on how to add the library as a dependency, e.g., Maven or Gradle, will be added here once the library is published.)_

### Basic Usage

1.  **Define your POJO:**

    ```java
    public class User {
        @Column("user_id")
        private int id;

        @Column("username")
        private String name;

        // Getters and setters
    }
    ```

2.  **Create a `RowMapper` instance:**

    ```java
    RowMapper<User> userMapper = RowMapper.builder(User.class)
            .build();
    ```

3.  **Map a `ResultSet`:**

    ```java
    List<User> users = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement("SELECT user_id, username FROM users");
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            users.add(userMapper.map(rs));
        }
    }
    ```

## Advanced Usage

### Custom Type Converters

If you need to map a database type to a custom Java type, you can implement the `TypeConverter` interface and register it with the `RowMapperBuilder`:

```java
public class MyCustomTypeConverter implements TypeConverter<MyCustomType> {
    @Override
    public MyCustomType convert(Object value) throws SQLException {
        // ... conversion logic ...
        return new MyCustomType(value.toString());
    }
}

RowMapper<MyEntity> mapper = RowMapper.builder(MyEntity.class)
        .registerConverter(MyCustomType.class, new MyCustomTypeConverter())
        .build();
```

### Naming Strategies

The library defaults to an identity naming strategy (column name matches field name). You can specify a different strategy, like `SnakeCaseNamingStrategy`, which converts snake_case column names to camelCase field names:

```java
RowMapper<Product> productMapper = RowMapper.builder(Product.class)
        .setNamingStrategy(new SnakeCaseNamingStrategy())
        .build();
```

In this case, a column named `product_name` would map to a field named `productName`.

## Building from Source

1.  Clone the repository:
    ```bash
    git clone https://github.com/your-username/resultset-mapper.git
    ```
2.  Navigate to the project directory:
    ```bash
    cd resultset-mapper
    ```
3.  Build with Maven:
    ```bash
    mvn clean install
    ```

## Contributing

Contributions are welcome! Please feel free to submit a pull request or open an issue.

_(Details on contribution guidelines can be added here.)_

## License

This project is licensed under the MIT License - see the `LICENSE` file for details.

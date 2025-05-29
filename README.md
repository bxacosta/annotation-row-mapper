# ResultSet Mapper

ResultSet Mapper is a lightweight Java library for mapping JDBC `ResultSet` objects to Java objects (POJOs). It
simplifies the process of converting relational data into an object-oriented format, reducing boilerplate code and
improving readability.

## Features

- **Annotation-based mapping:** Define mappings using simple annotations (`@ColumnMapping`) on your POJO fields or
  record components.
- **Customizable type conversion:** Provides standard converters for common data types and allows for custom converter
  registration.
- **Flexible naming strategies:** Supports different naming conventions for database columns and Java fields (e.g.,
  snake_case to camelCase).
- **Configurable and easy to use:** Allows configuration for case-insensitive column matching and ignoring unknown
  columns for quick integration.
- **Automatic Primitive Defaults**: Handles database NULLs by setting corresponding Java primitive fields
  to their default values.
- **Builder pattern:** `RowMapper` instances are created using a fluent builder API.

## Getting Started

### Java Compatibility

This library requires Java 17 or later.

### Installation

The resultset-mapper library is published on the [GitLab Package Registry](https://gitlab.com/bxacosta/libs/-/packages)
and can be consumed by configuring your project's Maven repository settings.

**Gradle (Groovy DSL):**

Include the GitLab repository and dependency in your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
    maven {
        name = "GitLab"
        url = uri("https://gitlab.com/api/v4/projects/70108742/packages/maven")
    }
}

dependencies {
    implementation 'dev.bxlab.libs:resultset-mapper:1.0-SNAPSHOT'
}
```

**Maven:**

Include the GitLab repository and dependency in your `pom.xml` file:

```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
    <repository>
        <id>gitlab-maven</id>
        <url>https://gitlab.com/api/v4/projects/70108742/packages/maven</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.bxlab.libs</groupId>
        <artifactId>resultset-mapper</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Usage

1. **Define your POJO or Record:**

   Java class (POJO):
   ```java
   public class User {
       @ColumnMapping("user_id")
       private int id;
       @ColumnMapping("username")
       private String name;
       @ColumnMapping
       private boolean active;

       // Getters and setters
   }
   ```

   Or using a Java Record:
   ```java
   public record User(
       @ColumnMapping("user_id") int id,
       @ColumnMapping("username") String name,
       @ColumnMapping boolean active
   ) {}
   ```

2. **Create a `RowMapper` instance:**

   For the `User` `class` or `record`:
   ```java
   ResultSetMapper<User> mapper = RowMapperBuilder.forType(User.class).build();
   ```

3. **Map a `ResultSet`:**

   ```java
    List<User> users = new ArrayList<>();
    // Assuming 'connection' is a valid JDBC connection
    try (PreparedStatement preparedStatement = connection
            .prepareStatement("SELECT user_id, username, is_active FROM users");
         ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
            users.add(mapper.map(resultSet));
        }
    }
   
    // Or map all rows at once:
    List<User> users;
    // Assuming 'connection' is a valid JDBC connection
    try (PreparedStatement preparedStatement = connection
            .prepareStatement("SELECT user_id, username, is_active FROM users");
         ResultSet resultSet = preparedStatement.executeQuery()) {
        users = mapper.mapAll(resultSet);
    }
    ```

## Advanced Usage

### Custom Type Converters

If you need to map a database type to a custom Java type or want to override the conversion for an existing type, you
can register a custom converter.

**Using class converter**

```java
public enum Status {
    ACTIVE, INACTIVE, PENDING
}

// Custom converter class for the Status enum
public class StatusEnumConverter implements TypeConverter<Status> {
    @Override
    public Status convert(
            ResultSet resultSet, 
            String columnName, 
            Map<String, Object> attributes
    ) throws SQLException {
        String value = resultSet.getString(columnName);
        if (value == null) return null;
        try {
            return Status.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // or throw, depending on your needs
        }
    }
}

// Option 1: Register converter globally for the Status type
ResultSetMapper<Product> mapper = RowMapperBuilder.forType(Product.class)
        .registerConverter(Status.class, new StatusEnumConverter())
        .build();


// Option 2: Register the converter for a specific field
public class Product {
    @ColumnMapping(value = "status", converter = StatusEnumConverter.class)
    private Status status;
}
```

**Using lambda syntax**

```java
ResultSetMapper<Product> mapper = RowMapperBuilder.forType(Product.class)
        .registerConverter(Boolean.class, (resultSet, columnName, attributes) -> {
            String value = resultSet.getString(columnName);
            if (value == null) return null;
            value = value.trim().toUpperCase();
            return value.equals("Y") 
                    || value.equals("YES") 
                    || value.equals("TRUE") 
                    || value.equals("1");
        })
        .build();
```

**Using lambda syntax with custom attributes**

You can add custom attributes to a field and use them in your lambda converter:

```java
ResultSetMapper<Product> mapper = RowMapperBuilder.forType(Product.class)
        .mapField("stock", config -> config
                .withAttribute("minValue", 0.0)
                .withAttribute("maxValue", 100.0)
                // Register the converter for this specific field
                .withConverter((resultSet, columnName, attributes) -> {
                    double value = resultSet.getDouble(columnName);
                    Double min = (Double) attributes.get("minValue");
                    Double max = (Double) attributes.get("maxValue");
                    if (value < min) 
                        throw new IllegalArgumentException("Value below minimum");
                    if (value > max) 
                        throw new IllegalArgumentException("Value above maximum");
                    return value;
                })
        )
        .build();
```

> [!NOTE]
> When you register a global TypeConverter for a specific data type (e.g., Boolean, Integer, LocalDate, etc.), it will 
> replace any existing default library converter or any previously registered global converter for that same data type.

### Naming Strategies

By default, the library uses `NamingStrategy.AS_IS`, which maps fields to columns using the field name.
You can check `NamingStrategy` enum to see the available naming strategies.

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
        // true to ignore 'case', false to be strict
        .caseInsensitiveColumns(true)
        .build();
```

### Ignoring Unknown Columns

You can configure the mapper to ignore unknown columns:

```java
ResultSetMapper<User> userMapper = RowMapperBuilder.forType(User.class)
        // true to ignore unmapped columns, false to throw exception
        .ignoreUnknownColumns(true)
        .build();
```

### Field-specific Formatting

You can specify format attributes for data types like dates directly in the `@ColumnMapping` annotation:

```java
public record UserWithFormattedDate(
        @ColumnMapping("ID") Integer id,
        @ColumnMapping(value = "CREATED_AT", format = "yyyy-MM-dd") LocalDate createdAt
) {
}
```

## Building from Source

1. **Clone the repository:**
   ```pwsh
   git clone https://github.com/bxacosta/resultset-mapper.git
   ```
2. **Navigate to the project directory:**
   ```pwsh
   cd resultset-mapper
   ```
3. **Publish to your local Maven repository:**
   ```pwsh
   ./gradlew publishToMavenLocal
   ```

## License

This project is licensed under the [MIT License](LICENSE).

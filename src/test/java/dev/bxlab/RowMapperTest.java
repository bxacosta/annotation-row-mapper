package dev.bxlab;

import dev.bxlab.configs.NamingStrategy;
import dev.bxlab.core.ColumnMapping;
import dev.bxlab.core.ResultSetMapper;
import dev.bxlab.core.RowMapperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RowMapperTest {

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData metaData;

    @BeforeEach
    void setUp() throws SQLException {
        when(resultSet.getMetaData()).thenReturn(metaData);
    }

    @Test
    void mapBasicTypes() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(3);
        when(metaData.getColumnLabel(1)).thenReturn("id");
        when(metaData.getColumnLabel(2)).thenReturn("name");
        when(metaData.getColumnLabel(3)).thenReturn("active");

        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Test User");
        when(resultSet.getBoolean("active")).thenReturn(true);

        ResultSetMapper<BasicUser> mapper = RowMapperBuilder.forType(BasicUser.class).build();

        BasicUser user = mapper.map(resultSet);

        assertNotNull(user);
        assertEquals(1, user.id());
        assertEquals("Test User", user.name());
        assertTrue(user.active());
    }

    @Test
    void mapWithCustomColumnNames() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(3);
        when(metaData.getColumnLabel(1)).thenReturn("user_id");
        when(metaData.getColumnLabel(2)).thenReturn("full_name");
        when(metaData.getColumnLabel(3)).thenReturn("is_active");

        when(resultSet.getInt("user_id")).thenReturn(2);
        when(resultSet.getString("full_name")).thenReturn("Custom User");
        when(resultSet.getBoolean("is_active")).thenReturn(true);

        ResultSetMapper<CustomColumnUser> mapper = RowMapperBuilder.forType(CustomColumnUser.class).build();

        CustomColumnUser user = mapper.map(resultSet);

        assertNotNull(user);
        assertEquals(2, user.id());
        assertEquals("Custom User", user.name());
        assertTrue(user.active());
    }

    @Test
    void mapWithCaseInsensitiveColumns() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(3);
        when(metaData.getColumnLabel(1)).thenReturn("USER_ID");
        when(metaData.getColumnLabel(2)).thenReturn("FULL_NAME");
        when(metaData.getColumnLabel(3)).thenReturn("IS_ACTIVE");

        when(resultSet.getInt("USER_ID")).thenReturn(2);
        when(resultSet.getString("FULL_NAME")).thenReturn("Case Insensitive");
        when(resultSet.getBoolean("IS_ACTIVE")).thenReturn(true);

        ResultSetMapper<CustomColumnUser> mapperCaseSensitive = RowMapperBuilder.forType(CustomColumnUser.class)
                .caseInsensitiveColumns(false)
                .build();

        CustomColumnUser userCaseSensitive = mapperCaseSensitive.map(resultSet);
        assertNotNull(userCaseSensitive);
        assertNull(userCaseSensitive.id());
        assertNull(userCaseSensitive.name());
        assertFalse(userCaseSensitive.active());


        ResultSetMapper<CustomColumnUser> mapperCaseInsensitive = RowMapperBuilder.forType(CustomColumnUser.class)
                .caseInsensitiveColumns(true)
                .build();

        CustomColumnUser userCaseInsensitive = mapperCaseInsensitive.map(resultSet);
        assertNotNull(userCaseInsensitive);
        assertEquals(2, userCaseInsensitive.id());
        assertEquals("Case Insensitive", userCaseInsensitive.name());
        assertTrue(userCaseInsensitive.active());
    }

    @Test
    void mapWithCustomConverter() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("ID");
        when(metaData.getColumnLabel(2)).thenReturn("BIRTH_DATE");

        when(resultSet.getInt("ID")).thenReturn(4);
        when(resultSet.getString("BIRTH_DATE")).thenReturn("1990-01-15");
        when(resultSet.wasNull()).thenReturn(false, false);

        ResultSetMapper<UserWithDate> mapper = RowMapperBuilder.forType(UserWithDate.class)
                .registerConverter(LocalDate.class, (resultSet, columnName, attributes) -> {
                    String stringDate = resultSet.getString(columnName);
                    return stringDate != null ? LocalDate.parse(stringDate) : null;
                })
                .build();

        UserWithDate user = mapper.map(resultSet);
        assertNotNull(user);
        assertEquals(4, user.id());
        assertEquals(LocalDate.of(1990, 1, 15), user.birthDate());
    }

    @Test
    void mapWithIgnoreUnknownColumns() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("ID");

        when(resultSet.getInt("ID")).thenReturn(5);
        when(resultSet.wasNull()).thenReturn(false);


        ResultSetMapper<BasicUser> mapperStrict = RowMapperBuilder.forType(BasicUser.class)
                .ignoreUnknownColumns(false)
                .build();
        Exception exception = assertThrows(IllegalStateException.class, () -> mapperStrict.map(resultSet));
        assertTrue(exception.getMessage().contains("Column not found"));


        ResultSetMapper<BasicUser> mapperIgnore = RowMapperBuilder.forType(BasicUser.class)
                .ignoreUnknownColumns(true)
                .build();
        BasicUser user = mapperIgnore.map(resultSet);
        assertNotNull(user);
        assertEquals(5, user.id());
        assertNull(user.name());
    }

    @Test
    void mapWithNamingStrategy() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("user_id");
        when(metaData.getColumnLabel(2)).thenReturn("user_name");

        when(resultSet.getInt("user_id")).thenReturn(6);
        when(resultSet.getString("user_name")).thenReturn("Strategy User");
        when(resultSet.wasNull()).thenReturn(false, false);

        ResultSetMapper<StrategyUser> mapper = RowMapperBuilder.forType(StrategyUser.class)
                .withNamingStrategy(NamingStrategy.SNAKE_CASE)
                .build();

        StrategyUser user = mapper.map(resultSet);

        assertNotNull(user);
        assertEquals(6, user.userId());
        assertEquals("Strategy User", user.userName());
    }

    @Test
    void mapWithInvalidColumnName() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("DIFFERENT_ID");

        ResultSetMapper<BasicUser> mapper = RowMapperBuilder.forType(BasicUser.class)
                .ignoreUnknownColumns(false)
                .build();

        Exception exception = assertThrows(IllegalStateException.class, () -> mapper.map(resultSet));
        assertTrue(exception.getMessage().contains("Column not found"));
    }

    @Test
    void mapWithFormatAttribute() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("ID");
        when(metaData.getColumnLabel(2)).thenReturn("CREATED_AT");

        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("CREATED_AT")).thenReturn("2023-05-15");

        ResultSetMapper<UserWithFormattedDate> mapper = RowMapperBuilder
                .forType(UserWithFormattedDate.class)
                .build();

        UserWithFormattedDate user = mapper.map(resultSet);

        assertNotNull(user);
        assertEquals(1, user.id());
        assertNotNull(user.createdAt());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals("2023-05-15", sdf.format(user.createdAt()));
    }

    @Test
    void mapWithCustomAttributes() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("ID");
        when(metaData.getColumnLabel(2)).thenReturn("SCORE");

        when(resultSet.getInt("ID")).thenReturn(2);
        when(resultSet.getDouble("SCORE")).thenReturn(75.5);

        ResultSetMapper<UserWithScore> mapper = RowMapperBuilder
                .forType(UserWithScore.class)
                .mapField("score", config -> config
                        .withAttribute("minValue", 0.0)
                        .withAttribute("maxValue", 100.0)
                        .withConverter((resultSet, columnName, attributes) -> {
                            double value = resultSet.getDouble(columnName);
                            if (resultSet.wasNull()) return null;

                            Double minValue = (Double) attributes.get("minValue");
                            Double maxValue = (Double) attributes.get("maxValue");

                            if (minValue != null && value < minValue) {
                                throw new IllegalArgumentException("Value below minimum: " + value + " < " + minValue);
                            }

                            if (maxValue != null && value > maxValue) {
                                throw new IllegalArgumentException("Value exceeds maximum: " + value + " > " + maxValue);
                            }

                            return value;
                        })
                )
                .build();

        UserWithScore user = mapper.map(resultSet);

        assertNotNull(user);
        assertEquals(2, user.id());
        assertEquals(75.5, user.score());

        when(resultSet.getDouble("SCORE")).thenReturn(150.0);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> mapper.map(resultSet));
        assertTrue(exception.getMessage().contains("Value exceeds maximum"));
    }

    @Test
    void mapWithCustomConverters() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("ID");
        when(metaData.getColumnLabel(2)).thenReturn("ROLE");

        when(resultSet.getString("ROLE")).thenReturn("ADMIN");

        ResultSetMapper<UserWithCustomProperty> mapper = RowMapperBuilder
                .forType(UserWithCustomProperty.class)
                .includeDefaultConverters(false)
                .registerConverter(UserWithCustomProperty.Role.class, (resultSet, columnName, attributes) -> {
                    String role = resultSet.getString(columnName);
                    return role != null ? UserWithCustomProperty.Role.valueOf(role.toUpperCase()) : null;
                })
                .build();

        UserWithCustomProperty user = mapper.map(resultSet);

        assertNotNull(user);
        assertNull(user.id());
        assertEquals(UserWithCustomProperty.Role.ADMIN, user.role());
    }

    @Test
    void mapWithFallbackConverter() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(3);
        when(metaData.getColumnLabel(1)).thenReturn("id");
        when(metaData.getColumnLabel(2)).thenReturn("name");
        when(metaData.getColumnLabel(3)).thenReturn("active");

        when(resultSet.getObject("id")).thenReturn(1);
        when(resultSet.getObject("name")).thenReturn("Test User");
        when(resultSet.getObject("active")).thenReturn(true);

        ResultSetMapper<BasicUser> mapper = RowMapperBuilder
                .forType(BasicUser.class)
                .includeDefaultConverters(false)
                .ignoreUnknownTypes(false)
                .build();

        BasicUser user = mapper.map(resultSet);

        assertNotNull(user);
        assertEquals(1, user.id());
        assertEquals("Test User", user.name());
        assertTrue(user.active());
    }

    @Test
    void mapWithProgrammaticFieldConfig() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("USER_CODE");
        when(metaData.getColumnLabel(2)).thenReturn("USER_NAME");

        when(resultSet.getInt("USER_CODE")).thenReturn(2);
        when(resultSet.getString("USER_NAME")).thenReturn("Programmatic User");

        ResultSetMapper<BasicUser> mapper = RowMapperBuilder.forType(BasicUser.class)
                .mapField("id", config -> config.toColumn("USER_CODE"))
                .mapField("name", config -> config.toColumn("USER_NAME"))
                .build();

        BasicUser user = mapper.map(resultSet);

        assertNotNull(user);
        assertEquals(2, user.id());
        assertEquals("Programmatic User", user.name());
    }

    @Test
    void mapWithNullValues() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(3);
        when(metaData.getColumnLabel(1)).thenReturn("ID");
        when(metaData.getColumnLabel(2)).thenReturn("NAME");
        when(metaData.getColumnLabel(3)).thenReturn("ACTIVE");

        when(resultSet.getInt("ID")).thenReturn(0);
        when(resultSet.getString("NAME")).thenReturn(null);
        when(resultSet.getBoolean("ACTIVE")).thenReturn(true);
        when(resultSet.wasNull()).thenReturn(true, true);

        ResultSetMapper<BasicUser> mapper = RowMapperBuilder.forType(BasicUser.class).build();

        BasicUser user = mapper.map(resultSet);

        assertNotNull(user);
        assertNull(user.id());
        assertNull(user.name());
        assertFalse(user.active());
    }

    @Test
    void mapWithFailingConverter() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("ID");
        when(metaData.getColumnLabel(2)).thenReturn("NAME");

        ResultSetMapper<BasicUser> mapper = RowMapperBuilder.forType(BasicUser.class)
                .registerConverter(Integer.class, (resultSet, columnName, attributes) -> {
                    throw new SQLException("Failed to convert column " + columnName + " to Integer");
                })
                .build();

        Exception exception = assertThrows(SQLException.class, () -> mapper.map(resultSet));
        assertTrue(exception.getMessage().contains("Failed to convert column"));
    }

    @Test
    void mapWithNoDefaultConstructor() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("ID");

        ResultSetMapper<UserWithoutDefaultConstructor> mapper = RowMapperBuilder
                .forType(UserWithoutDefaultConstructor.class)
                .build();

        Exception exception = assertThrows(IllegalStateException.class, () -> mapper.map(resultSet));
        assertTrue(exception.getMessage().contains("Error mapping to"));
    }


    public record BasicUser(
            @ColumnMapping Integer id,
            @ColumnMapping String name,
            @ColumnMapping boolean active
    ) {
    }

    public record CustomColumnUser(
            @ColumnMapping("user_id") Integer id,
            @ColumnMapping("full_name") String name,
            @ColumnMapping("is_active") boolean active
    ) {
    }

    public record UserWithDate(
            @ColumnMapping("ID") Integer id,
            @ColumnMapping("BIRTH_DATE") LocalDate birthDate
    ) {
    }

    public record StrategyUser(
            @ColumnMapping Integer userId,
            @ColumnMapping String userName
    ) {
    }

    public record UserWithFormattedDate(
            @ColumnMapping("ID") Integer id,
            @ColumnMapping(value = "CREATED_AT", format = "yyyy-MM-dd") Date createdAt
    ) {
    }

    public record UserWithScore(
            @ColumnMapping("ID") Integer id,
            @ColumnMapping("SCORE") Double score
    ) {
    }

    public record UserWithCustomProperty(
            @ColumnMapping Integer id,
            @ColumnMapping Role role
    ) {
        public enum Role {
            ADMIN, USER
        }
    }

    public static class UserWithoutDefaultConstructor {
        @ColumnMapping
        private Integer id;

        public UserWithoutDefaultConstructor(Integer id) {
            this.id = id;
        }
    }
}
package dev.bxlab.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;

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
        assertEquals(4, user.getId());
        assertEquals(LocalDate.of(1990, 1, 15), user.getBirthDate());
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
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            mapperStrict.map(resultSet);
        });
        assertTrue(exception.getMessage().contains("Column not found"));


        ResultSetMapper<BasicUser> mapperIgnore = RowMapperBuilder.forType(BasicUser.class)
                .ignoreUnknownColumns(true)
                .build();
        BasicUser user = mapperIgnore.map(resultSet);
        assertNotNull(user);
        assertEquals(5, user.id());
        assertNull(user.name());
    }
//
//    @Test
//    void mapWithNamingStrategy() throws SQLException {
//        // Configurar el ResultSet mock
//        when(metaData.getColumnCount()).thenReturn(2);
//        when(metaData.getColumnLabel(1)).thenReturn("user_id");
//        when(metaData.getColumnLabel(2)).thenReturn("user_name");
//
//        when(resultSet.getInt("user_id")).thenReturn(6);
//        when(resultSet.getString("user_name")).thenReturn("Strategy User");
//        when(resultSet.wasNull()).thenReturn(false, false);
//
//        // Crear el mapper con estrategia de nombres SNAKE_CASE
//        ResultSetMapper<StrategyUser> mapper = RowMapperBuilder.forType(StrategyUser.class)
//                .withNamingStrategy(NamingStrategy.SNAKE_CASE)
//                .build();
//
//        // Ejecutar el mapeo
//        StrategyUser user = mapper.map(resultSet);
//
//        // Verificar resultados
//        assertNotNull(user);
//        assertEquals(6, user.getUserId());
//        assertEquals("Strategy User", user.getUserName());
//    }

    // Clases de prueba
//    public static class BasicUser {
//        @ColumnMapping("ID")
//        private Integer id;
//
//        @ColumnMapping("NAME")
//        private String name;
//
//        @ColumnMapping("ACTIVE")
//        private boolean active;
//
//        public Integer getId() {
//            return id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public boolean isActive() {
//            return active;
//        }
//    }

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

    public static class UserWithDate {
        @ColumnMapping("ID")
        private Integer id;

        @ColumnMapping("BIRTH_DATE")
        private LocalDate birthDate;

        public Integer getId() {
            return id;
        }

        public LocalDate getBirthDate() {
            return birthDate;
        }
    }

    public static class StrategyUser {
        @ColumnMapping
        private Integer userId;

        @ColumnMapping
        private String userName;

        public Integer getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }
    }
}
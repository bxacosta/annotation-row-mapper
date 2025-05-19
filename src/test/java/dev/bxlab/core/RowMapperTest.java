package dev.bxlab.core;

import dev.bxlab.configs.NamingStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RowMapperTest {

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData metaData;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        when(resultSet.getMetaData()).thenReturn(metaData);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void mapBasicTypes() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(3);
        when(metaData.getColumnLabel(1)).thenReturn("ID");
        when(metaData.getColumnLabel(2)).thenReturn("NAME");
        when(metaData.getColumnLabel(3)).thenReturn("ACTIVE");

        when(resultSet.getInt("ID")).thenReturn(1);
        when(resultSet.getString("NAME")).thenReturn("Test User");
        when(resultSet.getBoolean("ACTIVE")).thenReturn(true);

        ResultSetMapper<BasicUser> mapper = RowMapperBuilder.forType(BasicUser.class).build();

        BasicUser user = mapper.map(resultSet);

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("Test User", user.getName());
        assertTrue(user.isActive());
    }

    @Test
    void mapWithCustomColumnNames() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(3);
        when(metaData.getColumnLabel(1)).thenReturn("USER_ID");
        when(metaData.getColumnLabel(2)).thenReturn("FULL_NAME");
        when(metaData.getColumnLabel(3)).thenReturn("IS_ACTIVE");

        when(resultSet.getInt("USER_ID")).thenReturn(2);
        when(resultSet.getString("FULL_NAME")).thenReturn("Custom User");
        when(resultSet.getBoolean("IS_ACTIVE")).thenReturn(true);
        when(resultSet.wasNull()).thenReturn(false, false, false);

        ResultSetMapper<CustomColumnUser> mapper = RowMapperBuilder.forType(CustomColumnUser.class).build();

        CustomColumnUser user = mapper.map(resultSet);

        assertNotNull(user);
        assertEquals(2, user.getId());
        assertEquals("Custom User", user.getName());
        assertTrue(user.isActive());
    }

    @Test
    void mapWithCaseInsensitiveColumns() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("id");
        when(metaData.getColumnLabel(2)).thenReturn("name");

        when(resultSet.getInt("id")).thenReturn(3);
        when(resultSet.getString("name")).thenReturn("Case Insensitive");

        ResultSetMapper<BasicUser> mapperCaseSensitive = RowMapperBuilder.forType(BasicUser.class)
                .caseInsensitiveColumns(false)
                .build();

        ResultSetMapper<BasicUser> mapperCaseInsensitive = RowMapperBuilder.forType(BasicUser.class)
                .caseInsensitiveColumns(true)
                .build();

        Exception exception = assertThrows(IllegalStateException.class, () -> mapperCaseSensitive.map(resultSet));
        assertTrue(exception.getMessage().contains("Column not found"));
        
        BasicUser user = mapperCaseInsensitive.map(resultSet);
        assertNotNull(user);
        assertEquals(3, user.getId());
        assertEquals("Case Insensitive", user.getName());
    }

    @Test
    void mapWithCustomConverter() throws SQLException {
        // Configurar el ResultSet mock
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("ID");
        when(metaData.getColumnLabel(2)).thenReturn("BIRTH_DATE");

        when(resultSet.getInt("ID")).thenReturn(4);
        when(resultSet.getString("BIRTH_DATE")).thenReturn("1990-01-15");
        when(resultSet.wasNull()).thenReturn(false, false);

        // Crear el mapper con un convertidor personalizado
        ResultSetMapper<UserWithDate> mapper = RowMapperBuilder.forType(UserWithDate.class)
                .registerConverter(LocalDate.class, (rs, colName, attrs) -> {
                    String dateStr = rs.getString(colName);
                    return dateStr != null ? LocalDate.parse(dateStr) : null;
                })
                .build();

        // Ejecutar el mapeo
        UserWithDate user = mapper.map(resultSet);

        // Verificar resultados
        assertNotNull(user);
        assertEquals(4, user.getId());
        assertEquals(LocalDate.of(1990, 1, 15), user.getBirthDate());
    }

    @Test
    void mapWithIgnoreUnknownColumns() throws SQLException {
        // Configurar el ResultSet mock
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("ID");

        when(resultSet.getInt("ID")).thenReturn(5);
        when(resultSet.wasNull()).thenReturn(false);

        // Crear el mapper con ignoreUnknownColumns = false
        ResultSetMapper<BasicUser> mapperStrict = RowMapperBuilder.forType(BasicUser.class)
                .ignoreUnknownColumns(false)
                .build();

        // Crear el mapper con ignoreUnknownColumns = true
        ResultSetMapper<BasicUser> mapperIgnore = RowMapperBuilder.forType(BasicUser.class)
                .ignoreUnknownColumns(true)
                .build();

        // Ejecutar el mapeo con strict (debería fallar al no encontrar la columna NAME)
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            mapperStrict.map(resultSet);
        });
        assertTrue(exception.getMessage().contains("Column not found"));

        // Ejecutar el mapeo con ignore (debería funcionar)
        BasicUser user = mapperIgnore.map(resultSet);
        assertNotNull(user);
        assertEquals(5, user.getId());
        assertNull(user.getName());
    }

    @Test
    void mapWithNamingStrategy() throws SQLException {
        // Configurar el ResultSet mock
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("user_id");
        when(metaData.getColumnLabel(2)).thenReturn("user_name");

        when(resultSet.getInt("user_id")).thenReturn(6);
        when(resultSet.getString("user_name")).thenReturn("Strategy User");
        when(resultSet.wasNull()).thenReturn(false, false);

        // Crear el mapper con estrategia de nombres SNAKE_CASE
        ResultSetMapper<StrategyUser> mapper = RowMapperBuilder.forType(StrategyUser.class)
                .withNamingStrategy(NamingStrategy.SNAKE_CASE)
                .build();

        // Ejecutar el mapeo
        StrategyUser user = mapper.map(resultSet);

        // Verificar resultados
        assertNotNull(user);
        assertEquals(6, user.getUserId());
        assertEquals("Strategy User", user.getUserName());
    }

    // Clases de prueba
    public static class BasicUser {
        @ColumnMapping("ID")
        private Integer id;

        @ColumnMapping("NAME")
        private String name;

        @ColumnMapping("ACTIVE")
        private boolean active;

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }
    }

    public static class CustomColumnUser {
        @ColumnMapping("USER_ID")
        private Integer id;

        @ColumnMapping("FULL_NAME")
        private String name;

        @ColumnMapping("IS_ACTIVE")
        private boolean active;

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }
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
package dev.bxlab.utils;

import dev.bxlab.core.AnnotationBasedRowMapper;
import dev.bxlab.core.ColumnMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnotationBasedRowMapperTest {
    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData metaData;

    @BeforeEach
    void setUp() throws SQLException {
        when(resultSet.getMetaData()).thenReturn(metaData);
    }

    @Test
    void testMapRow_SimpleMapping() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(3);
        List<String> columns = Arrays.asList("id", "date", "formatedDate");
        for (int i = 1; i <= columns.size(); i++) {
            when(metaData.getColumnLabel(i)).thenReturn(columns.get(i - 1));
        }

        when(resultSet.getObject("id")).thenReturn(100L);
        when(resultSet.getLong("id")).thenReturn(100L);
        when(resultSet.wasNull()).thenReturn(false);

        LocalDate localDate = LocalDate.of(2023, 10, 5);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        when(resultSet.getObject("date")).thenReturn(sqlDate);
        when(resultSet.getDate("date")).thenReturn(sqlDate);

        LocalDate formattedLocalDate = LocalDate.of(2024, 11, 20);
        String stringFormattedLocalDate = formattedLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        when(resultSet.getObject("formatedDate")).thenReturn(formattedLocalDate);
        when(resultSet.getString("formatedDate")).thenReturn(stringFormattedLocalDate);

        AnnotationBasedRowMapper<ParentDTO> mapper = new AnnotationBasedRowMapper<>(ParentDTO.class);
        ParentDTO dto = mapper.mapRow(resultSet);

        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertEquals(localDate, dto.getDate());
        assertEquals(formattedLocalDate, dto.getFormatedDate());
    }

    @Test
    void testMapRowWithInheritedDto() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(6);
        List<String> columns = Arrays.asList("id", "date", "formatedDate", "name", "amount", "active");
        for (int i = 1; i <= columns.size(); i++) {
            when(metaData.getColumnLabel(i)).thenReturn(columns.get(i - 1));
        }

        when(resultSet.getObject("id")).thenReturn(200L);
        when(resultSet.getLong("id")).thenReturn(200L);

        LocalDate localDate = LocalDate.of(2023, 9, 15);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        when(resultSet.getObject("date")).thenReturn(sqlDate);
        when(resultSet.getDate("date")).thenReturn(sqlDate);

        LocalDate formattedLocalDate = LocalDate.of(2024, 9, 2);
        String stringFormattedLocalDate = formattedLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        when(resultSet.getObject("formatedDate")).thenReturn(formattedLocalDate);
        when(resultSet.getString("formatedDate")).thenReturn(stringFormattedLocalDate);

        when(resultSet.getObject("name")).thenReturn("Test Name");
        when(resultSet.getString("name")).thenReturn("Test Name");

        BigDecimal amount = new BigDecimal("1234.56");
        when(resultSet.getObject("amount")).thenReturn(amount);
        when(resultSet.getBigDecimal("amount")).thenReturn(amount);

        when(resultSet.getObject("active")).thenReturn(true);
        when(resultSet.getBoolean("active")).thenReturn(true);
        when(resultSet.wasNull()).thenReturn(false);

        AnnotationBasedRowMapper<ChildDTO> mapper = new AnnotationBasedRowMapper<>(ChildDTO.class);
        ChildDTO dto = mapper.mapRow(resultSet);

        assertNotNull(dto);
        // Campos heredados de ParentDTO
        assertEquals(200L, dto.getId());
        assertEquals(localDate, dto.getDate());
        // Campos de ChildDTO
        assertEquals("Test Name", dto.getName());
        assertEquals(amount, dto.getAmount());
        assertTrue(dto.isActive());
    }

    @Test
    void testMapRow_MissingAndNullColumns() throws SQLException {
        // Simulamos 3 columnas: "id", "date" y "amount", "name" y "active" faltan o devuelven null.
        when(metaData.getColumnCount()).thenReturn(3);

        List<String> columns = Arrays.asList("id", "date", "amount");
        for (int i = 1; i <= columns.size(); i++) {
            when(metaData.getColumnLabel(i)).thenReturn(columns.get(i - 1));
        }

        when(resultSet.getObject("id")).thenReturn(300L);
        when(resultSet.getLong("id")).thenReturn(300L);
        when(resultSet.getObject("date")).thenReturn(null);
        when(resultSet.getObject("amount")).thenReturn(null);

        when(resultSet.wasNull()).thenReturn(false);

        AnnotationBasedRowMapper<ChildDTO> mapper = new AnnotationBasedRowMapper<>(ChildDTO.class);
        ChildDTO dto = mapper.mapRow(resultSet);

        assertNotNull(dto);
        assertEquals(300L, dto.getId());
        assertNull(dto.getDate());
        assertNull(dto.getAmount());
        assertFalse(dto.isActive());
        assertNull(dto.getName());
    }

    @SuppressWarnings("unused")
    public static class ParentDTO {
        @ColumnMapping("id")
        private Long id;
        @ColumnMapping("date")
        private LocalDate date;
        @ColumnMapping(value = "formatedDate", format = "dd/MM/yyyy")
        private LocalDate formatedDate;

        public Long getId() {
            return id;
        }

        public LocalDate getDate() {
            return date;
        }

        public LocalDate getFormatedDate() {
            return formatedDate;
        }
    }

    @SuppressWarnings("unused")
    public static class ChildDTO extends ParentDTO {
        @ColumnMapping("name")
        private String name;
        @ColumnMapping("amount")
        private BigDecimal amount;
        @ColumnMapping("active")
        private boolean active;

        public String getName() {
            return name;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public boolean isActive() {
            return active;
        }
    }
}
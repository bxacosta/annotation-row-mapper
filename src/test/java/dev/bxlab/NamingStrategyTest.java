package dev.bxlab;

import dev.bxlab.configs.NamingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NamingStrategyTest {
    @Test
    @DisplayName("AS_IS strategy should keep field name unchanged")
    void asIs_ShouldKeepFieldNameUnchanged() {
        NamingStrategy strategy = NamingStrategy.AS_IS;

        assertEquals("firstName", strategy.fieldToColumnName("firstName"));
        assertEquals("address", strategy.fieldToColumnName("address"));
        assertEquals("UserProfile", strategy.fieldToColumnName("UserProfile"));
        assertEquals("requestURL", strategy.fieldToColumnName("requestURL"));
        assertEquals("myHTTPRequest", strategy.fieldToColumnName("myHTTPRequest"));
        assertEquals("", strategy.fieldToColumnName(""));
        assertEquals("a", strategy.fieldToColumnName("a"));
    }

    @Test
    @DisplayName("SNAKE_CASE strategy should convert camelCase to snake_case")
    void snakeCase_ShouldConvertCamelCaseToSnakeCase() {
        NamingStrategy strategy = NamingStrategy.SNAKE_CASE;

        assertEquals("first_name", strategy.fieldToColumnName("firstName"));
        assertEquals("last_name", strategy.fieldToColumnName("lastName"));
        assertEquals("address", strategy.fieldToColumnName("address"));
        assertEquals("user_profile", strategy.fieldToColumnName("UserProfile"));
        assertEquals("request_url", strategy.fieldToColumnName("requestURL"));
        assertEquals("my_http_request", strategy.fieldToColumnName("myHTTPRequest"));
        assertEquals("user_data_entry", strategy.fieldToColumnName("userDataEntry"));
        assertEquals("", strategy.fieldToColumnName(""));
        assertEquals("a", strategy.fieldToColumnName("a"));
    }

    @Test
    @DisplayName("UPPER_SNAKE_CASE strategy should convert camelCase to UPPER_SNAKE_CASE")
    void upperSnakeCase_ShouldConvertCamelCaseToUpperSnakeCase() {
        NamingStrategy strategy = NamingStrategy.UPPER_SNAKE_CASE;

        assertEquals("FIRST_NAME", strategy.fieldToColumnName("firstName"));
        assertEquals("LAST_NAME", strategy.fieldToColumnName("lastName"));
        assertEquals("ADDRESS", strategy.fieldToColumnName("address"));
        assertEquals("USER_PROFILE", strategy.fieldToColumnName("UserProfile"));
        assertEquals("REQUEST_URL", strategy.fieldToColumnName("requestURL"));
        assertEquals("MY_HTTP_REQUEST", strategy.fieldToColumnName("myHTTPRequest"));
        assertEquals("USER_DATA_ENTRY", strategy.fieldToColumnName("userDataEntry"));
        assertEquals("", strategy.fieldToColumnName(""));
        assertEquals("A", strategy.fieldToColumnName("a"));
    }
}

package eu.cryptoeuro.bankgateway.services.common;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

/**
 * Abstract base class for JDBC RowMapper implementations. Provides null-safe methods for reading different datatypes.
 *
 * @author Erko Hansar
 */
public abstract class AbstractRowMapper<T> implements RowMapper<T> {

    protected String getString(ResultSet rs, String columnLabel) throws SQLException {
        return rs.getString(columnLabel);
    }

    protected Integer getInteger(ResultSet rs, String columnLabel) throws SQLException {
        Integer value = rs.getInt(columnLabel);
        return rs.wasNull() ? null : value;
    }

    protected Long getLong(ResultSet rs, String columnLabel) throws SQLException {
        Long value = rs.getLong(columnLabel);
        return rs.wasNull() ? null : value;
    }

    protected BigDecimal getBigDecimal(ResultSet rs, String columnLabel) throws SQLException {
        return rs.getBigDecimal(columnLabel);
    }

    protected Date getTimestamp(ResultSet rs, String columnLabel) throws SQLException {
        return rs.getTimestamp(columnLabel);
    }

    protected Date getDate(ResultSet rs, String columnLabel) throws SQLException {
        return rs.getDate(columnLabel);
    }

    protected Boolean getBoolean(ResultSet rs, String columnLabel) throws SQLException {
        boolean value = rs.getBoolean(columnLabel);
        return rs.wasNull() ? null : value;
    }

}

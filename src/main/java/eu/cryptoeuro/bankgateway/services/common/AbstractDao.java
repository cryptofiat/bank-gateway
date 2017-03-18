package eu.cryptoeuro.bankgateway.services.common;

import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Abstract base class for DAO implementations. Provides connectivity and helper functions.
 *
 * @author Erko Hansar
 */
public abstract class AbstractDao {

    private NamedParameterJdbcDaoSupport daoSupport = null;

    @Autowired
    protected void setDataSource(@Qualifier("dataSource") DataSource dataSource) {
        daoSupport = new NamedParameterJdbcDaoSupport();
        daoSupport.setDataSource(dataSource);
    }

    protected DataSource getDataSource() {
        return daoSupport.getDataSource();
    }

    protected JdbcTemplate getJdbcTemplate() {
        return daoSupport.getJdbcTemplate();
    }

    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return daoSupport.getNamedParameterJdbcTemplate();
    }

    protected long getNextSequenceValue(String sequence) {
        return getJdbcTemplate().queryForObject("SELECT nextval('" + sequence + "')", Long.class);
    }

    protected AdvancedParameterSource[] getBatchParameterSource(Collection<?> beans) {
        return beans.stream()
                .map(bean -> new AdvancedParameterSource().addBean(bean))
                .toArray(AdvancedParameterSource[]::new);
    }

}

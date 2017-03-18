package eu.cryptoeuro.bankgateway.services.common;

import java.beans.PropertyDescriptor;
import java.sql.Types;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Spring named parameter source implementation which combines the best from {@link BeanPropertySqlParameterSource} and {@link MapSqlParameterSource} to support
 * adding both beans (all their readable properties) and also individual values.
 *
 * Encodes Enum's as VARCHAR types Encodes Map's as VARCHAR types
 *
 * @author Erko Hansar
 */
public class AdvancedParameterSource implements SqlParameterSource {

    private final MapSqlParameterSource delegate = new MapSqlParameterSource();

    public AdvancedParameterSource addBean(Object bean) {
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        for (PropertyDescriptor pd : beanWrapper.getPropertyDescriptors()) {
            String paramName = pd.getName();
            if (beanWrapper.isReadableProperty(paramName)) {
                Object value = beanWrapper.getPropertyValue(paramName);
                addValue(paramName, value);
            }
        }
        return this;
    }

    @SuppressWarnings("rawtypes")
    public AdvancedParameterSource addValue(String paramName, Object value) {
        if (value instanceof Enum) {
            delegate.addValue(paramName, ((Enum)value).name(), Types.VARCHAR);
        } else {
            delegate.addValue(paramName, value);
        }
        return this;
    }

    @Override
    public boolean hasValue(String paramName) {
        return delegate.hasValue(paramName);
    }

    @Override
    public Object getValue(String paramName) {
        return delegate.getValue(paramName);
    }

    @Override
    public int getSqlType(String paramName) {
        return delegate.getSqlType(paramName);
    }

    @Override
    public String getTypeName(String paramName) {
        return delegate.getTypeName(paramName);
    }

}

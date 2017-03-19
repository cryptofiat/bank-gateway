package eu.cryptoeuro.bankgateway.services.slack.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Field implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String value;
    @JsonProperty("short")
    private boolean valueIsShort;

    public Field(String title, String value, boolean valueIsShort) {
        this.title = title;
        this.value = value;
        this.valueIsShort = valueIsShort;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isValueIsShort() {
        return valueIsShort;
    }

    public void setValueIsShort(boolean valueIsShort) {
        this.valueIsShort = valueIsShort;
    }

}

package eu.cryptoeuro.bankgateway.services.slack.json;

import java.io.Serializable;

public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String color;
    private String text;
    private Field[] fields;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

}

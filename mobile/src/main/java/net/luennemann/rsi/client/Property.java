package net.luennemann.rsi.client;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by patrick on 07.05.2017.
 */

public class Property {
    public boolean isChanged() {
        return changed;
    }

    public enum Type {
        STRING, URI, UUID
    }

    private String name = null;
    private String value = null;
    private Type type = null;
    private boolean isSet = false;
    private Element parent;
    private boolean changed = false;
    private boolean changeable = true;

    public Property(Element parent, String name, Type type) {
        this(parent, name, type, null);
    }

    public Property(Element parent, String name, Type type, String value) {
        this(parent, name, type, (Object) value);
    }

    public Property(Element parent, String name, Type type, Object value) {
        this.parent = parent;
        this.name = name;
        this.type = type;
        //TODO check if property type is compatible with object class type
        setValueInternal(value);
    }

    public boolean isSet() {
        return isSet;
    }

    public String getName() {
        return name;
    }

    public String getValueAsString() {
        if (!this.isSet) return "";
        return value;
    }

    public Object getValueAsType() {
        if (!this.isSet) return null;
        switch (type) {
            case STRING:
                return value;
            case URI:
                return URI.create(value);
            case UUID:
                return UUID.fromString(value);
        }
        return null;
    }

    public void unset() {
        isSet = false;
        value = null;
    }

    private void setValueInternal(Object value) {
        if (value == null) return;
        this.isSet = true;
        this.value = value.toString();
    }

    public void setValue(Object value) {
        if (!changeable) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Tried to change unchangeable property '"+getName()+"'.");
            return;
        }
        try {
            //TODO check if property type is compatible with object class type
            changed = true;

        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }

    }

}

package net.luennemann.rsi.client;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.R.attr.value;

/**
 * Created by patrick on 05.05.2017.
 */

public class Element extends XObject {
    private HashMap<String, Property> properties = new HashMap<>();
    private boolean nameChanged = false;

    public Element(URI uri) {
        super(Type.ELEMENT, uri);
        readElementRemote();
    }

    private void readElementRemote() {
        try {
            JSONObject json = Manager.getJSON(uri.toURL());
            if (json.getString("status").equalsIgnoreCase("ok")) {
                setID(UUID.fromString(json.getJSONObject("data").getString("id")));
                fillProperties(json.getJSONObject("data"));
            } else {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Cannot load URI for Element");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Cannot load load URI for Element");
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Cannot load parse json for Element");
        }
    }

    public Element(JSONObject json, String name, UUID id, XObject parent) {
        super(Type.ELEMENT, name, id, parent);
        fillProperties(json);
    }

    public Element(JSONObject json, String name, UUID id, URI uri) {
        super(Type.ELEMENT, name, id, uri);
        fillProperties(json);
    }

    public Element(List<Property> properties, String name, UUID id, XObject parent) {
        super(Type.ELEMENT, name, id, parent);
        fillProperties(properties);
    }

    public Element(List<Property> properties, String name, UUID id, URI uri) {
        super(Type.ELEMENT, name, id, uri);
        fillProperties(properties);
    }

    public Element(List<Property> properties, String name, XObject parent) {
        super(Type.ELEMENT, name, null, parent);
        fillProperties(properties);
    }

    public Element(List<Property> properties, String name, URI uri) {
        super(Type.ELEMENT, name, null, uri);
        fillProperties(properties);
    }

    private void fillProperties(List<Property> properties) {
        for (Property prop:properties) setProperty(prop);
    }

    private void fillProperties(JSONObject json) {
        try {
            Iterator<String> keys = json.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                if (!key.equalsIgnoreCase("id") && !key.equalsIgnoreCase("uri"))
                    setPropertyInternal(new Property(this, key, Property.Type.STRING, json.getString(key)));
                //TODO map on correct type
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public Property getProperty(String name) {
        if (name.equalsIgnoreCase("name")) {
            return new Property(this, "name", Property.Type.STRING, this.getName());
        }
        if (name.equalsIgnoreCase("id")) {
            return new Property(this, "id", Property.Type.UUID, this.getId().toString());
        }
        if (name.equalsIgnoreCase("uri")) {
            return new Property(this, "uri", Property.Type.URI, this.getUri().toString());
        }
        Property prop = properties.get(name);
        if (prop==null) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Property '"+name+"' not found in '"+this.getName()+"'");
            return null;
        }
        return prop;
    }

    public String getPropertyAsString(String name) {
        if (name.equalsIgnoreCase("name")) return this.getName();
        if (name.equalsIgnoreCase("id")) return this.getId().toString();
        if (name.equalsIgnoreCase("uri")) return this.getUri().toString();
        Property prop = properties.get(name);
        if (prop==null) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Property '"+name+"' not found in '"+this.getName()+"'");
            return "";
        }
        return prop.getValueAsString();
    }

    public void setProperty(String name, String value) {
        setProperty(new Property(this, name, Property.Type.STRING, value));
    }

    public void setProperty(String name, Property.Type type, String value) {
        setProperty(new Property(this, name, type, value));
    }

    public void setProperty(Property property) {
        setPropertyInternal(property);
        sendChanges();
    }

    public void setProperties(List<Property> properties) {
        for (Property prop: properties) {
            setPropertyInternal(prop);
        }
        sendChanges();
    }

    private void setPropertyInternal(Property prop) {
        if (prop.getName().equalsIgnoreCase("id") || prop.getName().equalsIgnoreCase("uri")) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Property 'id' and 'uri' are not changeable");
            return;
        }
        if (prop.getName().equalsIgnoreCase("name")) {
            setName(prop.getValueAsString());
            //TODO send name changes to server
        }
        else {
            Property old = properties.get(prop.getName());
            if (old==null) {
                //TODO property has to be marked as new/changed
                properties.put(prop.getName(), prop);
            } else {
                old.setValue(prop.getValueAsType());
            }
        }
    }

    private boolean sendChanges() {
        try {
            JSONObject json = new JSONObject();

            for (Property prop : properties.values()) {
                if (prop.isChanged()) json.put(prop.getName(), prop.getValueAsString());
            }

            Logger.getLogger(getClass().getName()).log(Level.INFO, "To server: "+json.toString());
            JSONObject ret = Manager.postJSON(getUri().toURL(), json);
            if (ret==null) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Updating server did not work.");
                return false;
            }
            Logger.getLogger(getClass().getName()).log(Level.INFO, "From server: "+ret.toString());
            readElementRemote();
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Set<String> getPropertyNames() {
        Set<String> ret = new HashSet<String>(Arrays.asList(XObject.getDefaults()));;
        ret.addAll(properties.keySet());
        return ret;
    }

    public String toString() {
        return getUri()+" (#"+(properties.size()+3)+")";
    }

}

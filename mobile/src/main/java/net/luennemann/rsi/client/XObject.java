package net.luennemann.rsi.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by patrick on 07.05.2017.
 */

public abstract class XObject {
    protected UUID id;
    protected String name;
    private XObject parent = null;
    protected URI uri = null;
    private Type type = Type.UNKNOWN;

    public enum Type { UNKNOWN, SERVICE, RESOURCE, ELEMENT }

    public static String[] getDefaults() {return new String[]{"name","id","uri"}; };

    public XObject(Type type, JSONObject json, URI uri) {
        this.uri = uri;
        this.type = type;
    }

    public XObject(Type type, URI uri) {
        this.type = type;
        this.uri = uri;
    }

    public XObject(Type type, String name, UUID id, XObject parent) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.name = name;
        this.parent = parent;
        if (parent==null) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Parent has to be set");
        }
    }

    public XObject(Type type, String name, UUID id, URI uri) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.name = name;
        this.uri = uri;
    }

    public XObject getParent() {
        if (parent != null) return parent;

        //remove last path part
        if (uri == null) return null;

        String uri = getUri().toASCIIString();
        uri = uri.charAt(uri.length()-1) == '/' ? uri.substring(0,uri.length()-2) : uri;

        uri = uri.substring(0, uri.lastIndexOf('/'));
        URI parentUri = URI.create(uri);
        if (parentUri.getPath().equalsIgnoreCase("")) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "No path left. Wrong structure.");
            return null;
        }

        switch (type) {
            case UNKNOWN:
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "No parents for unknown");
                return null;
            case SERVICE:
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "No parents for services");
                return null;
            case RESOURCE:
                parent = new Service(parentUri);
            case ELEMENT:
                parent = new Resource(parentUri);
        }
        return parent;
    }

    public URI getUri() {
        if (this.uri != null) {
            return this.uri;
        }
        if (getParent()==null) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Parent has to be set. Should never happen.");
            return null;
        }
        String base = this.getParent().getUri().toASCIIString();
        this.uri = URI.create(base.charAt(base.length()-1) == '/' ? base+this.name : base + "/" + this.name);
        return uri;
    }

    protected void setID(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return getUri().toString();
    }
}

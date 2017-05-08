package net.luennemann.rsi.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by patrick on 05.05.2017.
 */

public class Resource extends XObject {
    public Resource(String name, UUID id, XObject parent) {
        super(Type.RESOURCE, name, id, parent);
    }

    public Resource(String name, UUID id, URI uri) {
        super(Type.RESOURCE, name, id, uri);
    }

    public Resource(String name, XObject parent) {
        super(Type.RESOURCE, name, null, parent);
    }

    public Resource(String name, URI uri) {
        super(Type.RESOURCE, name, null, uri);
    }

    private HashMap<UUID, Element> elements = new HashMap<>();

    public Resource(URI uri) {
        super(Type.RESOURCE, uri);
    }

    public Collection<Element> getElements() {
        //TODO implement Filter

        try {
            JSONObject json = Manager.getJSON(getUri().toURL());
            if (json!=null && json.getString("status").equalsIgnoreCase("ok")) {
                elements = new HashMap<>();
                JSONArray data = json.getJSONArray("data");
                for(int i=0; i<data.length(); i++){
                    JSONObject jsonelement = data.getJSONObject(i);
                    String name = jsonelement.getString("name");
                    UUID id = jsonelement.has("id") ? UUID.fromString(jsonelement.getString("id")) : null;
                    addElement(jsonelement, name, id);
                }
                return elements.values();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "something went wrong while getting elements");
        return new LinkedList<Element>();

    }

    private void addElement(JSONObject json, String name, UUID id) {
        Element ele = new Element(json, name, id, this);
        if (elements.containsKey(ele.getId())) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Existing element with ID '"+ele.getId()+"' overwritten with given element.");
        }
        elements.put(ele.getId(), ele);
    }

    public void addElement(String name, UUID uuid, List<Property> properties) {
        Element ele = uuid==null ? new Element(properties, name, this) : new Element(properties, name, uuid, this);
        if (elements.containsKey(ele.getId())) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Existing element with ID '"+ele.getId()+"' overwritten with given element.");
        }
        elements.put(ele.getId(), ele);
    }

    public boolean removeElement(UUID uuid) {
        return elements.remove(uuid)!=null;
    }

    public String toString() {
        return getUri().toString();
    }

}

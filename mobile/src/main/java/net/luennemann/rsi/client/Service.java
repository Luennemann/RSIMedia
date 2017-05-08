package net.luennemann.rsi.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
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

public class Service extends XObject {

    public Service(String name, UUID id, URI uri) {
        super(Type.SERVICE, name, id, uri);
    }

    public Service(String name, URI uri) {
        super(Type.SERVICE, name, null, uri);
    }

    public Service(URI uri) {
        super(Type.SERVICE, uri);
    }

    public Service(JSONObject json, URI uri) {
        super(Type.SERVICE, json, uri);
    }

    HashMap<UUID,Resource> resources = null;

    public Collection<Resource> getResources() {
        if (resources !=null) return resources.values();

        try {
            JSONObject json = Manager.getJSON(getUri().toURL());
            if (json!=null && json.getString("status").equalsIgnoreCase("ok")) {
                resources = new HashMap<>();
                JSONArray data = json.getJSONArray("data");
                for(int i=0; i<data.length(); i++){
                    JSONObject jsonresource = data.getJSONObject(i);
                    String name = jsonresource.getString("name");
                    UUID id = jsonresource.has("id") ? UUID.fromString(jsonresource.getString("id")) : null;
                    addResource(name, id);
                }
                return resources.values();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "something went wrong while getting resources");
        return new LinkedList<Resource>();
    }

    public void addResource(String name, UUID uuid) {
        if (resources==null) resources = new HashMap<>();
        Resource resource = uuid==null ? new Resource(name, this) : new Resource(name, uuid, this);
        if (resources.containsKey(resource.getId())) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Existing resource with ID '"+resource.getId());
        } else {
            resources.put(resource.getId(), resource);
        }
    }

    public String toString() {
        if (resources==null) return getUri()+" (#?)";
        else return getUri()+" (#"+resources.size()+")";
    }
}

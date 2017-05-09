package net.luennemann.rsi.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by patrick on 05.05.2017.
 */

public class Manager {

    private HashMap<UUID, Service> services = null;
    private static final String TAG = "RSIClientManager";

    public Collection<Service> getServices(URL baseurl) {
        if (services !=null) return services.values();

        try {
            JSONObject json = getJSON(baseurl, 500);
            if (json!=null && json.getString("status").equalsIgnoreCase("ok")) {
                services = new HashMap<>();
                JSONArray data = json.getJSONArray("data");
                for(int i=0; i<data.length(); i++){
                    JSONObject jsonservice = data.getJSONObject(i);
                    String name = jsonservice.getString("name");
                    UUID id = UUID.fromString(jsonservice.getString("id"));
                    URI uri = URI.create(baseurl + jsonservice.getString("uri"));
                    services.put(id, new Service(name, id, uri));
                }
                return services.values();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "something went wrong while getting services");
        return new LinkedList<Service>();
    }

    public static JSONObject postJSON(URL url, JSONObject json) {
        return postJSON(url, json, 500);
    }

    public static JSONObject postJSON(URL url, JSONObject json, int timeout) {
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) url.openConnection();
            c.setReadTimeout(timeout);
            c.setConnectTimeout(timeout);
            c.setRequestMethod("POST");
            c.setDoInput(true);
            c.setDoOutput(true);
            c.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            OutputStream os = c.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(json.toString());

            writer.flush();
            writer.close();
            os.close();

            c.connect();

            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    try {
                        return new JSONObject(sb.toString());
                    } catch (JSONException ex) {
                        Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
                        return null;
                    }
                case 500:
                    return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }



    public static JSONObject getJSON(URL url) {
        return getJSON(url, 500);
    }

    public static JSONObject getJSON(URL url, int timeout) {
        //Logger.getLogger(TAG).log(Level.INFO, "Getting json from "+url.toString());
        //TODO reuse existing connections
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    try {
                        JSONObject json = new JSONObject(sb.toString());
                        return json;
                    } catch (JSONException ex) {
                        Logger.getLogger(TAG).log(Level.SEVERE, "", ex);
                        return null;
                    }
            }
        } catch (Exception ex) {
            Logger.getLogger(TAG).log(Level.SEVERE, "", ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(TAG).log(Level.SEVERE, "", ex);
                }
            }
        }
        return null;
    }
}

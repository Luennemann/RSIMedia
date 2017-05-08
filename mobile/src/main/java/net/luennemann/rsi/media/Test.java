package net.luennemann.rsi.media;

import android.util.Log;

import net.luennemann.rsi.client.Element;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by patrick on 07.05.2017.
 */

public class Test implements Runnable {
    final String tag = "testing";

    @Override
    public void run() {
        Log.i(tag, "testing");

        URL baseURL = null;
        try {
            baseURL = new URL("http://192.168.6.227:3000");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

/*
        Manager manager = new Manager();
        Collection<Service> services = manager.getServices(baseURL);
        for (Service service:services) {
            Log.i(tag, service.toString());

            Collection<Resource> resources = service.getResources();
            for (Resource resource:resources) {
                Log.i(tag, "  "+resource.toString());

                Collection<Element> elements = resource.getElements();
                for (Element element:elements) {
                    Log.i(tag, "    "+element.toString());

                    for (String propname:element.getPropertyNames()) {
                        Log.i(tag, "      " + propname+": "+element.getPropertyAsString(propname));
                    }
                }
            }
        }
*/
        Element netflux = new Element(URI.create("http://192.168.6.227:3000/media/renderers/d6ebfd90-d2c1-11e6-9376-df943f51f0d8"));
        showProperties(netflux);

        netflux.setProperty("state", "play");

    }

    static void showProperties(Element element) {
        for (String propname:element.getPropertyNames()) {
            Log.i("ShowProperties", "      " + propname+": "+element.getPropertyAsString(propname));
        }
    }
}
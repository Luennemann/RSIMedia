package net.luennemann.rsi.media;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import net.luennemann.rsi.client.Element;

import java.net.URI;
import java.util.logging.Level;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AndroidLoggingHandler.reset(new AndroidLoggingHandler());
        java.util.logging.Logger.getGlobal().setLevel(Level.FINEST);
    }

    Element renderer;

    public void onButton(View view) {
        switch (view.getId()) {
            case R.id.btnConnect:
                //new Thread(new Test()).start();
                final String baseUrl = ((EditText)findViewById(R.id.edtBaseURL)).getText().toString();
                Log.i("Main", "Connecting to "+baseUrl);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        renderer = new Element(URI.create(baseUrl));
                        showProperties(renderer);
                    }
                }).start();
                break;
            case R.id.btnPause:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        renderer.setProperty("state", "pause");
                        showProperties(renderer);
                    }
                }).start();
                break;
            case R.id.btnPlay:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        renderer.setProperty("state", "play");
                        showProperties(renderer);
                    }
                }).start();
                break;
        }
    }

    void showProperties(Element element) {
        for (String propname:element.getPropertyNames()) {
            Log.i("ShowProperties", "      " + propname+": "+element.getPropertyAsString(propname));
            final String value = element.getPropertyAsString(propname);
            if (propname.equalsIgnoreCase("offset")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.lblPosition)).setText(value);
                    }
                });
            }
        }
    }
}

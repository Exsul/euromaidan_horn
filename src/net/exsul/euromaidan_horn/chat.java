package net.exsul.euromaidan_horn;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by enela_000 on 21.02.14.
 */
public class chat extends Activity {
    static String[] names = {
            "Обновляем информацию с сервера"
    };
    static Boolean inited = false;
    static Integer last_id = 0;
    static Context c;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        c = getApplicationContext();

        UpdateChat(names);

        final Button button = (Button)findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText text = (EditText)findViewById(R.id.editText);
                String str = text.getText().toString();
                postData(str);
            }
        });

        if (!inited) {
            final Handler h = new Handler();
            final int delay = 10 * 60 * 1000;//milli seconds


            setOnetimeTimer(c);
            inited = true;
        }
    }

    public void setOnetimeTimer(Context context) {
        final int delay = 10 * 60 * 1000;//milli seconds
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("onetime", Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), delay, pi);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_MENU:
                getData();
                return true;
        }

        return super.onKeyDown(keycode, e);
    }

    public void UpdateChat( String[] arr ) {
        // находим список
        ListView lvMain = (ListView) findViewById(R.id.listView);

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arr);

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);
    }

    public void postData(String str) {
        new network(this, str).execute(null, null, null);
    }


    public void getData() {
        new network(this).execute(null, null, null);
    }
}
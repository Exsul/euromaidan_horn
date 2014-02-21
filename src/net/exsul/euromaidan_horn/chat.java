package net.exsul.euromaidan_horn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

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

            h.postDelayed(new Runnable(){
                public void run()   {
                    getData();
                    //do something
                    h.postDelayed(this,delay);
                }
            },
               100);
            inited = true;
        }
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
        // http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://push.maidan.exsul.net/");
        HttpResponse response;

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("version", "1"));
            nameValuePairs.add(new BasicNameValuePair("action", "push"));
            nameValuePairs.add(new BasicNameValuePair("stringdata", str));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            // Execute HTTP Post Request
            response = httpclient.execute(httppost);
            String res = ReadResponse(response);

        } catch (ClientProtocolException e) {
            Toast.makeText(this.getApplicationContext(), "ClientProtocolException:" + e.toString(), Toast.LENGTH_LONG).show();
            // TODO Auto-generated catch block
        } catch (IOException e) {
            Toast.makeText(this.getApplicationContext(), "IOExeption: " + e.toString(), Toast.LENGTH_LONG).show();
            // TODO Auto-generated catch block
        } finally {
            Toast.makeText(this.getApplicationContext(), "Успешно", Toast.LENGTH_LONG).show();
            ((EditText)findViewById(R.id.editText)).setText("");
            (new Handler()).postDelayed(new Runnable(){
                public void run()   {
                    getData();
                }
            }, 500);
        }
    }

    public String ReadResponse( final HttpResponse resp ) {
        try {
            HttpEntity ent = resp.getEntity();
            InputStream in = ent.getContent();
            String text = slurp(in, 1000);
            return text;
        } catch (IOException e) {
            return e.toString();
        }
    }

    public static String slurp(final InputStream is, final int bufferSize)
    {
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try {
            final Reader in = new InputStreamReader(is, "UTF-8");
            try {
                for (;;) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0)
                        break;
                    out.append(buffer, 0, rsz);
                }
            }
            finally {
                in.close();
            }
        }
        catch (UnsupportedEncodingException ex) {
    /* ... */
        }
        catch (IOException ex) {
      /* ... */
        }
        return out.toString();
    }

    public void getData() {
        // http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://push.maidan.exsul.net/");
        HttpResponse response = null;

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("version", "1"));
            nameValuePairs.add(new BasicNameValuePair("action", "read"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            Toast.makeText(this.getApplicationContext(), "ClientProtocolException:" + e.toString(), Toast.LENGTH_LONG).show();
            return;
            // TODO Auto-generated catch block
        } catch (IOException e) {
            Toast.makeText(this.getApplicationContext(), "IOExeption: " + e.toString(), Toast.LENGTH_LONG).show();
            return;
            // TODO Auto-generated catch block
        } finally {
            Toast.makeText(this.getApplicationContext(), "Успешно", Toast.LENGTH_LONG).show();
            ((EditText)findViewById(R.id.editText)).setText("");

            try {
                String text = ReadResponse(response);
                JSONObject obj = new JSONObject(text);

                ArrayList<String> stringArray = new ArrayList<String>();
                JSONArray jsonArray = obj.getJSONArray("chat");
                for(int i = 0, count = jsonArray.length(); i < count; i++)
                    try {
                        //JSONObject jsonObject = jsonArray.getJSONObject(i);
                        stringArray.add(jsonArray.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                String []arr = stringArray.toArray(new String[0]);
                names = arr;
                UpdateChat(arr);
            } catch (JSONException e) {

            }
        }
    }
}
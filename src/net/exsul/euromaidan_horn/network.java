package net.exsul.euromaidan_horn;

import android.os.AsyncTask;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;
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
public class network extends AsyncTask<Void, Void, String[]> {
    String send = null;
    String ToastMes = null;
    chat me;
    public network( chat _me, String _send ) {
        me = _me;
        send = _send;
    }

    public network( chat _me ) {
        me = _me;
    }

    @Override
    protected String[] doInBackground(Void... params) {
        // http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://push.maidan.exsul.net/");
        HttpResponse response;

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("version", "1"));
            if (send != null) {
                nameValuePairs.add(new BasicNameValuePair("action", "push"));
                nameValuePairs.add(new BasicNameValuePair("stringdata", send));
            } else
                nameValuePairs.add(new BasicNameValuePair("action", "read"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            // Execute HTTP Post Request
            response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            ToastMes = "ClientProtocolException:" + e.toString();
            return null;
        } catch (IOException e) {
            ToastMes = "IOExeption: " + e.toString();
            return null;
        }
        ToastMes = "Успешно";
        if (send != null)
            return null;
        String text = ReadResponse(response);
        try {
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
            return arr;
        } catch (JSONException e) {

        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(me.getApplicationContext(), "Начата передача данных", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String[] msg) {
        if (ToastMes != null)
            Toast.makeText(me.getApplicationContext(), ToastMes, Toast.LENGTH_LONG).show();
        ((EditText)me.findViewById(R.id.editText)).setText("");
        if (send != null) {
            (new Handler()).postDelayed(new Runnable(){
                public void run()   {
                    me.getData();
                }
            }, 500);
            return;
        }
        if (msg != null)
            me.names = msg;
        me.UpdateChat(me.names);

    }

    public String ReadResponse( final HttpResponse resp ) {
        try {
            if (resp == null)
                return "RESP IS NULL";
            HttpEntity ent = resp.getEntity();
            InputStream in = ent.getContent();
            String text = slurp(in, 1000);
            return text;
        } catch (IOException e) {
            return e.toString();
        } catch (IllegalStateException e) {
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
}

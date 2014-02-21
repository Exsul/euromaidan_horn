package net.exsul.euromaidan_horn;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
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
public class network extends AsyncTask<Void, Void, Integer> {
    String send = null;
    String ToastMes = null;
    chat me = null;

    public network( chat _me, String _send ) {
        me = _me;
        send = _send;
    }

    public network( chat _me ) {
        me = _me;
    }

    public network( ) {

    }

    @Override
    protected Integer doInBackground(Void... params) {
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
            Integer last_id = obj.getInt("last_id");
            for(int i = 0, count = jsonArray.length(); i < count; i++)
                try {
                    //JSONObject jsonObject = jsonArray.getJSONObject(i);
                    stringArray.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            String []arr = stringArray.toArray(new String[0]);
            me.names = arr;
            return last_id;
        } catch (JSONException e) {

        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        if (me != null)
            Toast.makeText(me.getApplicationContext(), "Начата передача данных", Toast.LENGTH_SHORT).show();
    }

    public static int NOTIFICATION_ID = 1;

    @Override
    protected void onPostExecute(Integer msg) {
        if (disclaimer.context == null)
            return;
        if (AlarmReceiver.GetLastId(disclaimer.context) < msg) {
            AlarmReceiver.SetLastId(disclaimer.context, msg);
            //We get a reference to the NotificationManager
            NotificationManager notificationManager = (NotificationManager)disclaimer.context.getSystemService(Context.NOTIFICATION_SERVICE);

            String MyText = "Рупор майдана";
            Notification mNotification = new Notification(R.drawable.icon, MyText, System.currentTimeMillis() );
            //The three parameters are: 1. an icon, 2. a title, 3. time when the notification appears

            String MyNotificationTitle = "Рупор майдана";
            String MyNotificationText  = "Новые сообщения";

            Intent MyIntent = new Intent(disclaimer.context, disclaimer.class);
            PendingIntent StartIntent = PendingIntent.getActivity(disclaimer.context, 0, MyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            //A PendingIntent will be fired when the notification is clicked. The FLAG_CANCEL_CURRENT flag cancels the pendingintent

            mNotification.defaults |= Notification.DEFAULT_SOUND;
            mNotification.defaults |= Notification.DEFAULT_VIBRATE;
            mNotification.setLatestEventInfo(disclaimer.context, MyNotificationTitle, MyNotificationText, StartIntent);

            notificationManager.notify(NOTIFICATION_ID , mNotification);
            //We are passing the notification to the NotificationManager with a unique id.
        }
        if (me == null)
            return;
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

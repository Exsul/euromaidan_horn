package net.exsul.euromaidan_horn;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by enela_000 on 21.02.14.
 */
public class chat extends Activity {
    String[] names = {
            "Тестовое длинное сообщение о пересечении каких то улиц с контактным телефоном 12323123232131",
            "Тестовое длинное сообщение о пересечении каких то улиц с контактным телефоном 12323123232131",
            "Тестовое длинное сообщение о пересечении каких то улиц с контактным телефоном 12323123232131"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        // находим список
        ListView lvMain = (ListView) findViewById(R.id.listView);

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names);

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);
    }
}
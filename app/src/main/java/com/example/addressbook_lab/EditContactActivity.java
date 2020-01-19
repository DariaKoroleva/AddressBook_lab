package com.example.addressbook_lab;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditContactActivity extends AppCompatActivity {
    public static final String CONTACT_INTENT_KEY = "CONTACT_INTENT_KEY";
    public static final String CONTACT_FLAG = "CONTACT_ADD_FLAG"; //edit, add, view

    Contact contact;
    String flag;

    PendingIntent pendingNotifyIntent;
    Intent notifyIntent;
    AlarmManager alarmManager;

    TextView f_first_name, f_last_name, f_patronymic, f_phone_number;
    Button bt_notify;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        f_first_name = findViewById(R.id.edit_first_name);
        f_last_name = findViewById(R.id.edit_last_name);
        f_patronymic = findViewById(R.id.edit_patronymic);
        f_phone_number = findViewById(R.id.edit_phone_number);

        bt_notify = findViewById(R.id.bt_notify);

        f_first_name.setEnabled(false);

        //получаем данные, переданные в активность
        contact = (Contact) getIntent().getSerializableExtra(CONTACT_INTENT_KEY);
        flag = (String) getIntent().getSerializableExtra(CONTACT_FLAG);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE); // инициализируем сервис
        checkFlag();

    }

    private void checkFlag(){
        //сверяем флаги
        switch (flag){
            case("edit"):
                editInflator();
                break;
            case("view"):
                viewInflator();
                break;
            case("add"):
                addInflator();
                break;
            default:
                Toast.makeText(this, "Ошибка флага. Сворачиваемся", Toast.LENGTH_LONG).show();
                this.finish();
        }
    }


    private void setNotifyReciever(){
        Toast.makeText(EditContactActivity.this, "Напоминание сработает через 30 секунд", Toast.LENGTH_SHORT).show();
        long time = System.currentTimeMillis() + 30000;

        notifyIntent = new Intent(getApplicationContext(), NotifyReceiver.class);


        notifyIntent.putExtra(NotifyActivity.NOTIFY_KEY, contact);

        pendingNotifyIntent = PendingIntent.getBroadcast(EditContactActivity.this, 0, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, time , pendingNotifyIntent);

    }


    public void setBtNotify(View v){
        setNotifyReciever();
    }

    /**
     * заполнение полей макета
     */
    private void fillFields(){
        f_first_name.setText(contact.getFirstName());
        f_last_name.setText(contact.getLastName());
        f_patronymic.setText(contact.getPatronymic());
        f_phone_number.setText(contact.getPhoneNumber());
    }


    private void setAllElementsEnabled(){
        f_first_name.setEnabled(true);
        f_last_name.setEnabled(true);
        f_patronymic.setEnabled(true);
        f_phone_number.setEnabled(true);
        bt_notify.setEnabled(true);
    }

    private void setBtNotifyDisabled(){
        bt_notify.setEnabled(false);
    }

    /**
     * инициализация элементов макета для дальнейшего изменения контакта
     */
    private void editInflator(){
        setAllElementsEnabled();
        setBtNotifyDisabled();
        fillFields();
    }

    /**
     * инициализация элементов макета для дальнейшего добавления контакта
     */
    private void addInflator(){
        setAllElementsEnabled();
        setBtNotifyDisabled();
    }

    /**
     * инициализация элементов макета для дальнейшего просмотра контакта
     */
    private void viewInflator(){
        f_first_name.setEnabled(false);
        f_last_name.setEnabled(false);
        f_patronymic.setEnabled(false);
        f_phone_number.setEnabled(false);
        bt_notify.setEnabled(true);
        fillFields();
    }
}

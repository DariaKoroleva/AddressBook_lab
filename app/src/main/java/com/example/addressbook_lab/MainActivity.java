package com.example.addressbook_lab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.io.text.VCardWriter;
import ezvcard.property.StructuredName;

public class MainActivity extends AppCompatActivity implements DataAdapter.Listener {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int CAMERA_REQUEST = 15;
    int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 1;


    List<Contact> contactList = new ArrayList<>();
    String vfile;


    /**
     * Метод, вызываемый при создании активности
     * @param savedInstanceState сохраненное состояние активности
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initContactsPermissions();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //указываем разметку


        DataAdapter adapter = new DataAdapter(contactList, this); //инициализируем адаптер для списка

        RecyclerView recyclerView = findViewById(R.id.rview_list_of_contacts);
        recyclerView.setAdapter(adapter); //задаём адаптер

    }

    /**
     * инициализация верхнего меню
     * @param menu экземпляр хранителя меню
     * @return инициализирует поведение элемента родительского метода
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_right_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * вешаем действия на верхние кнопки
     * @param item выбранный элемент
     * @return инициализирует поведение элемента родительского метода, в случае, если
     * не сработал ни один из case-вариантов (не выбрано ни одного пунка меню.
     * Если же сработал case-вариант, то вызывается привязанный к кнопке метод
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.bt_add_contact :
                addButtonClicked();
                return true;
            case R.id.bt_export_contacts:
                setvCardString();
                return true;
            case R.id.bt_geo:
                setGeoPermissions();
                getGeolocation();
            case R.id.bt_photo:
                setCameraPermissions();
                cameraCapture();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * получаем геолокацию
     */
    private void getGeolocation() {
        switch (setGeoPermissions()) {
            case 0:
                GPSTracker mGPS = new GPSTracker(this);
                waitingGeo(mGPS);
                break;
            case 2:
                Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
                break;
            default:
                break;

        }
    }

    /**
     * ожидаем, пока определится местоположение. Выводим окошко с данными о широте и долготе
     * @param mGPS
     */
    private void waitingGeo(GPSTracker mGPS){
        if(mGPS.canGetLocation ){
            mGPS.getLocation();
            Toast.makeText(this, "Ваше текущее местоположение: " + "Lat"+mGPS.getLatitude() + "Lon" + mGPS.getLongitude(), Toast.LENGTH_LONG).show();
            formatLocation(mGPS);
        }else{
            Toast.makeText(this, "Невозможно определить", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * открываем карты на телефоне
     * @param mGPS
     */
    private void formatLocation(GPSTracker mGPS) {
        String completeGeo = "geo:" + mGPS.getLatitude() + mGPS.getLongitude();
        Intent geoApp = new Intent(Intent.ACTION_VIEW, Uri.parse(completeGeo));
        startActivity(geoApp);
    }


    /**
     * действие на кнопку "добавить"
     */
    public void addButtonClicked() {
        Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra(EditContactActivity.CONTACT_INTENT_KEY, new Contact());
        intent.putExtra(EditContactActivity.CONTACT_FLAG, "add");
        startActivityForResult(intent, 1);
    }


    /**
     * установка имени для файла vcf и инициализация экспорта
     */
    public void setvCardString() {
        switch (setStoragePermissions()){
            case 0:
                vfile = "Contacts" + "_" + System.currentTimeMillis()+".vcf";
                getvCard();
                break;
            case 2:
                Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
                break;
        }
    }


    private void cameraCapture12(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CAMERA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_CAMERA));
        sendOrderedBroadcast(intent, null);
    }


    /**
     * включаем камеру
     */
    public void cameraCapture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }


    /**
     * проверка прав на чтение списка контактов
     * @return результат проверки прав
     */
    private int checkCameraPermissions(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
    }


    private Integer setCameraPermissions(){
        boolean permissions;

        permissions = checkCameraPermissions() == PackageManager.PERMISSION_GRANTED;

        if (!permissions) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA },
                    CAMERA_REQUEST);

            if (checkCameraPermissions() == PackageManager.PERMISSION_GRANTED){
                return 0;
            }
            else {
                return 2;
            }
        }
        else { return 0; }
    }


    /**
     * проверка прав при запуске приложения
     */
    public void initContactsPermissions() {
        switch (setContactsPermissions()){
            case 0:
                readContacts(this);
                break;
            case 2:
                Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
                this.finish();
                break;
            default: break;
        }
    }


    /**
     * проверка прав на чтение списка контактов
     * @return результат проверки прав
     */
    private int checkContactsPermissions(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
    }


    /**
     * установка прав на чтение контактов
     * @return код для инициализации дальнейшей реакции
     */
    private Integer setContactsPermissions() {
        boolean permissions;

        permissions = checkContactsPermissions() == PackageManager.PERMISSION_GRANTED;

        if (!permissions) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS },
                    PERMISSIONS_REQUEST_READ_CONTACTS);

            if (checkContactsPermissions() == PackageManager.PERMISSION_GRANTED){
                return 0;
            }
            else {
                return 2;
            }
        }
        else { return 0; }
    }


    /**
     * проверка прав на запись в хранилище
     * @return результат проверки прав
     */
    private int checkStoragePermissions(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
    }


    /**
     * установка прав на чтение внутреннего хранилища. Запрос их у пользователя
     * @return 0, если права выданы; 1, если не выдал права; 2 если пользователь повторно отклонил запрос
     *
     */
    private Integer setStoragePermissions(){
        boolean permissions;

        permissions = checkStoragePermissions() == PackageManager.PERMISSION_GRANTED;

        if (!permissions) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);

            if (checkStoragePermissions() == PackageManager.PERMISSION_GRANTED){
                return 0;
            }
            else {
                return 2;
            }
        }
        else { return 0; }
    }


    /**
     * проверка прав на доступ к геолокации
     * @return результат проверки прав
     */
    private int checkGeoPermissions(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }


    /**
     * установка прав на доступ к геолокации. Запрос их у пользователя
     * @return 0, если права выданы; 1, если не выдал права; 2 если пользователь повторно отклонил запрос
     *
     */
    private Integer setGeoPermissions(){
        boolean permissions;

        permissions = checkGeoPermissions() == PackageManager.PERMISSION_GRANTED;

        if (!permissions) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

            if (checkGeoPermissions() == PackageManager.PERMISSION_GRANTED){
                return 0;
            }
            else {
                return 2;
            }
        }
        else { return 0; }
    }


    /**
     * экспорт контактов
     */
    public void getvCard() {
        //указываем путь хранения
        String pathToFolder = Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName();
        String fullPath = pathToFolder + File.separator + vfile;
        File externalAppDir = new File(pathToFolder);
        if (!externalAppDir.exists()) {
            externalAppDir.mkdir();
        }
        List<VCard> vcards = new ArrayList<>();
        VCard vCard;

        //считываем все контакты из списка
        for (Contact item : contactList) {
            vCard = new VCard();
            StructuredName n = new StructuredName();
            //n.addParameter("Patronymic", item.getPatronymic());
            n.setFamily(item.getLastName());
            n.setGiven(item.getFirstName());
            vCard.setStructuredName(n);
            vCard.addTelephoneNumber(item.getPhoneNumber());

            vcards.add(vCard);
        }

        //пишем в файл
        File file = new File(fullPath);
        try {
            VCardWriter writer = new VCardWriter(file, VCardVersion.V4_0);
            try {
                for (VCard item : vcards) {
                    writer.write(item);
                }
            } catch (IOException io) {
                Log.e("IO", "ошибка");
            } finally {
                writer.close();
            }
        } catch (IOException io) {
            Log.e("IO", "ошибка");
        }

        Toast.makeText(this, "файл экспортирован по пути: " + fullPath, Toast.LENGTH_LONG).show();
    }


    /**
     * метод, вызываемый при закрытии дочерней активности (Не работает)
     * @param requestCode код запроса для конкретной активности
     * @param resultCode код результата, который возращает вызываемая активность
     * @param data входные данные для обработки
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2) {
            if (resultCode == RESULT_OK) {
                try {
                    if (data.getData() != null) {
                        Toast.makeText(this, "Папка выбрана!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException ex) {
                    Log.e("Err in file dialog", ex.toString());
                }
            }
        }

        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Фотка сделана, извлекаем картинку
                Intent preview = new Intent(this, ImagePreview.class);
                preview.putExtra(ImagePreview.PREVIEW_KEY, (Bitmap) data.getExtras().get("data"));
                startActivity(preview);
            }
        }
    }


    /**
     * обработчик события на нажатие кнопки удаления
     * @param contact список контактов
     */
    @Override
    public void onRmButtonClicked(@NonNull Contact contact) {
        /*String str = contactList.get(position);
        arrayList.remove(str);
        MyAdapter.this.notifyDataSetChanged();*/
        /*Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra(EditContactActivity.CONTACT_INTENT_KEY, contact);
        intent.putExtra(EditContactActivity.CONTACT_FLAG, "remove");
        startActivity(intent);*/
    }


    /**
     * обработчик события на нажатие кнопки изменения
     * @param contact список контактов
     */
    @Override
    public void onEdButtonClicked(@NonNull Contact contact) {
        //создаём новую активность и передаём в неё данные
        Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra(EditContactActivity.CONTACT_INTENT_KEY, contact);
        intent.putExtra(EditContactActivity.CONTACT_FLAG, "edit");
        startActivity(intent);
    }

    /**
     * обработчик события на нажатия на пункт списка
     * @param contact список контактов
     */
    @Override
    public void onContactClicked(@NonNull Contact contact) {
        Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra(EditContactActivity.CONTACT_INTENT_KEY, contact);
        intent.putExtra(EditContactActivity.CONTACT_FLAG, "view");
        startActivity(intent);
    }


    /**
     * считывание контактов из Contacts API Android в список для дальнейшей работы
     * @param context текущий контекст приложения
     * @throws NullPointerException в случае, если указатель на следующий элемент никуда не указывает
     */
    private void readContacts(@NonNull Context context) throws NullPointerException {
        Contact contact;
        String whereName = ContactsContract.Data.MIMETYPE.concat(" = ?");
        String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName , whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
        //Cursor cursor=context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);

        //делаем запросы к API
        if(cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                contact = new Contact();
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                contact.setId(id);

                //имя
                String first_name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.Data.DATA2));
                contact.setFirstName(first_name);

                //фамилия
                String last_name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.Data.DATA3));
                contact.setLastName(last_name);

                //отчество
                String patronymic = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.Data.DATA5));
                contact.setPatronymic(patronymic);

                String has_phone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (Integer.parseInt(has_phone) > 0) {
                    // извлекаем номер телефона
                    Cursor pCur;
                    pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null,
                            null);
                    while(pCur.moveToNext()) {
                        String phone = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contact.setPhoneNumber(phone);
                    }
                    pCur.close();
                }
                if(contact.getPhoneNumber() != null){
                    this.contactList.add(contact);
                }
            }
        }
        cursor.close();
    }
}

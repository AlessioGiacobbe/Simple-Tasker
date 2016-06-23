package com.example.alessio.myapplication;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.ArrayList;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.jorgecastilloprz.FABProgressCircle;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.Manifest;


public class MainActivity extends AppCompatActivity implements LocationListener {
    static final String TAG = "DevicePolicyDemoActivity";
    static final int ACTIVATION_REQUEST = 47;

    protected LocationManager LocationManager;
    protected static final int REQUEST_ENABLE = 0;
    protected LocationListener LocationListener;
    protected Context context;
    int currentSelect;
    ObjectItem[] myobj;
    public Cursor cursor;
    ListAdapter adapter;
    DevicePolicyManager devicePolicyManager;
    ComponentName demoDeviceAdmin;
    ComponentName adminComponent;
    TextView txtLat,txtLong;
    boolean animOpen = false, famopen = false;
    public String eventName = null;
    public int ActiveProfileID;
    public Double eventLat = null, eventLong = null;
    public Double globLat, globLong;
    public ListView mylist;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView cancel_menu = (ImageView) findViewById(R.id.cancel_menu);
        TextView delete_menu = (TextView) findViewById(R.id.delete_menu);
        TextView edit_menu = (TextView) findViewById(R.id.edit_menu);
        com.getbase.floatingactionbutton.FloatingActionButton myfab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.locationfab);
        com.getbase.floatingactionbutton.FloatingActionButton hourfab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.hourfab);
        FloatingActionsMenu myfam = (FloatingActionsMenu) findViewById(R.id.fab);
        setSupportActionBar(toolbar);

        myfam.performClick();

        getSupportActionBar().setTitle("Localizzatore");


        myfam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FamClick();
                Log.d("Ciaone", "bau");

            }
        });

        hourfab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NewHour();

            }
        });

        myfab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FabClick();
            }
        });
        delete_menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DeleteDb();
            }
        });
        cancel_menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                closeMenu();
            }
        });

        edit_menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editMenu();
            }
        });

       DBCreate();

        CheckPermissions();

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        demoDeviceAdmin = new ComponentName(this, Darclass.class);
    }

    void FamClick(){
        LinearLayout alphal = (LinearLayout) findViewById(R.id.whitealph);
        alphal.setVisibility(View.VISIBLE);
        if(!famopen) {
            Animation alphanim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_anim_white);
            alphal.setAnimation(alphanim);
            alphanim.start();
            famopen = true;
        }else {
            Animation alphanim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_anim_white_rev);
            alphal.setAnimation(alphanim);
            alphanim.start();
            famopen = false;
        }

        Log.d("fatto", "fattoh");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
        }
    }

    void NewHour(){
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.hourlayout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Crea un nuovo evento");
        builder.setView(dialoglayout);
        final EditText nameedit = (EditText)dialoglayout.findViewById(R.id.newName);
        final Button Actionbutton = (Button)dialoglayout.findViewById(R.id.ActionButton);
        final TimePicker tp = (TimePicker)dialoglayout.findViewById(R.id.hourselect);


        Actionbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActionSelectAct();
            }
        });
        builder.setPositiveButton("Salva", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(nameedit.getText().toString().matches("")){
                    Toast.makeText(MainActivity.this, "Riempi tutti i campi", Toast.LENGTH_SHORT).show();
                }else{
                    SQLiteDatabase myDatabase = openOrCreateDatabase("myDb", MODE_PRIVATE, null);
                    String Name = nameedit.getText().toString();
                    int max = getStringNumber();
                    String ROW1 = "INSERT INTO DataTable(nome, lat, long)"
                            + " VALUES('" + nameedit.getText().toString() + "' , " + tp.getHour() + "." + tp.getMinute() + ", " + "6565" + ");" ;
                    myDatabase.execSQL(ROW1);
                    setSettings();
                    PopulateList();
                    SharedPreferences sharedpreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("ID", getLastID());
                    editor.commit();
                }
            }
        });
        builder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.d("cancella", "cancella");

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }




    void InitializeLoc(){
        if (LocationManager != null) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                CheckPermissions();
            }
        }

        LocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       /* LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        LocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);*/

        if (LocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            LocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        if (LocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);



    }

    void ActionSelectAct(){
        Intent intent = new Intent(this, ActionSelect.class);
        startActivity(intent);

    }

    void getCurrentInfo(){
        SQLiteDatabase myDatabase = openOrCreateDatabase("myDb", MODE_PRIVATE, null);
        Cursor cursor = myDatabase.rawQuery("SELECT * FROM DataTable WHERE id = " + currentSelect,null);
        cursor.moveToFirst();
        eventName = cursor.getString(1);
        eventLat = Double.valueOf(cursor.getString(2));
        eventLong = Double.valueOf(cursor.getString(3));
    }

    void editMenu(){
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.mylayout, null);
        getCurrentInfo();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifica un evento");
        builder.setView(dialoglayout);
        final EditText longedit = (EditText)dialoglayout.findViewById(R.id.LongitudeEdit);
        final EditText latedit = (EditText)dialoglayout.findViewById(R.id.LatitudeEdit);
        final EditText nameedit = (EditText)dialoglayout.findViewById(R.id.newName);
        final Button Actionbutton = (Button)dialoglayout.findViewById(R.id.ActionButton);

        longedit.setText(eventLong.toString());
        latedit.setText(eventLat.toString());
        nameedit.setText(eventName.toString());

        Actionbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActionSelectAct();
            }
        });
        builder.setPositiveButton("Salva", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(longedit.getText().toString().matches("")|| latedit.getText().toString().matches("") || nameedit.getText().toString().matches("")){
                    Toast.makeText(MainActivity.this, "Riempi tutti i campi", Toast.LENGTH_SHORT).show();
                }else{
                    SQLiteDatabase myDatabase = openOrCreateDatabase("myDb", MODE_PRIVATE, null);
                    String current = String.valueOf(currentSelect);
                    String ROW1 = "UPDATE DataTable SET nome ='" + nameedit.getText().toString() + "' , lat =" + latedit.getText().toString() + " , long =" + longedit.getText().toString() + " WHERE id = " + current + ";";
                    myDatabase.execSQL(ROW1);



                    PopulateList();
                    closeMenu();
                }
            }
        });
        builder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.d("cancella", "cancella");
                closeMenu();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    void closeMenu(){
        final LinearLayout   view = (LinearLayout) findViewById(R.id.CircleView);
        int centerx = view.getRight();
        int centery = view.getTop();
        int startRadius = 0;
        int endRadius = (int) Math
                .hypot(view.getWidth(), view.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(view, centerx, centery, endRadius, startRadius);
        anim.setDuration(600);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                LinearLayout alphal = (LinearLayout) findViewById(R.id.alpha_layout);
                alphal.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        if (animOpen) {
            anim.start();
            animOpen = false;
            ListView mylist = (ListView) findViewById(R.id.myList);
            mylist.setEnabled(true);
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null){
            mylist = null;
            myobj = null;
            PopulateList();
        adapter.notifyDataSetChanged();}
    }

    public void openMenu(){
        LinearLayout   view = (LinearLayout) findViewById(R.id.CircleView);
        int centerx = view.getRight();
        int centery = view.getTop();

        int startRadius = 0;
        int endRadius = (int) Math
                .hypot(view.getWidth(), view.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(view, centerx, centery, startRadius, endRadius);
        anim.setDuration(600);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                LinearLayout alphal = (LinearLayout) findViewById(R.id.alpha_layout);
                alphal.setVisibility(View.VISIBLE);
                Animation alphanim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_anim);
                alphal.setAnimation(alphanim);
                alphanim.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        view.setVisibility(View.VISIBLE);

        if(!animOpen){
            anim.start();
            animOpen = true;
            ListView mylist = (ListView) findViewById(R.id.myList);
            mylist.setEnabled(false);
        }
    }

    void DeleteDb(){
        SQLiteDatabase myDatabase = openOrCreateDatabase("myDb", MODE_PRIVATE, null);
        int last = getStringNumber();
        String ROW1 = "DELETE FROM DataTable"
                + " WHERE id = " + currentSelect ;
        myDatabase.execSQL(ROW1);

        closeMenu();
        PopulateList();
        myDatabase.close();
    }



    void PopulateList(){
        SQLiteDatabase myDatabase = openOrCreateDatabase("myDb", MODE_PRIVATE, null);
        cursor = myDatabase.rawQuery("SELECT * FROM DataTable WHERE id IS NOT NULL",null);
        cursor.moveToFirst();

        int lastindex = cursor.getCount();
            myobj = new ObjectItem[lastindex];

            lastindex = 0;
            if (cursor.moveToFirst()) {
                do {
                    Log.d("id " + cursor.getString(0) , "nome " + cursor.getString(1));
                    myobj[lastindex] = new ObjectItem(Integer.valueOf(cursor.getString(0)), cursor.getString(1), Double.valueOf(cursor.getString(2)), Double.valueOf(cursor.getString(3)));
                    lastindex++;
                    
                } while (cursor.moveToNext());
            cursor.close();


            adapter = new ListAdapter(this, R.layout.listlayout, myobj);
            mylist = (ListView) findViewById(R.id.myList);

                View empty = findViewById(R.id.vuoto);
                mylist.setEmptyView(empty);

            mylist.setAdapter(adapter);


            mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Context context = view.getContext();

                    TextView item = ((TextView) view.findViewById(R.id.nomeEvento));
                    String mystring = item.getTag().toString();
                    currentSelect = Integer.valueOf(item.getTag().toString());
                    openMenu();
                }
            });

                myDatabase.close();
        }
    }


    void setBrightness(int brightness){

        if (!Settings.System.canWrite(this)){
            Toast.makeText(MainActivity.this, "Per garantire il corretto funzionamento di questa applicazione, abilita questa opzione, Grazie.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

            ContentResolver cResolver = getContentResolver();
            Window window = getWindow();
            cResolver = getContentResolver();
            Settings.System.putInt(cResolver,
                    Settings.System.SCREEN_BRIGHTNESS, ((brightness*255)/100));

    }

    void readActions(String id){
        if(ActiveProfileID != Integer.valueOf(id)) {
            final String xmlFile = getApplicationContext().getObbDir().toString() + "/Action" + id + ".xml";
            File file = new File(xmlFile);
            FileInputStream fli = null;
            try {
                fli = new FileInputStream(file);
                String code = new String();
                code = IOUtils.toString(fli, StandardCharsets.UTF_8);
                Log.d("fli ", code);
                XMLParser myparser = new XMLParser();
                Document doc = myparser.getDomElement(code);
                NodeList ndl = doc.getElementsByTagName("Actions");

                for (int i = 0; i < ndl.getLength(); i++) {
                    Element e = (Element) ndl.item(i);
                    String disturb = myparser.getValue(e, "disturb"); // name child value
                    String luminosit = myparser.getValue(e, "lumin"); // cost child value
                    String appstart = myparser.getValue(e, "appstart"); // description child value
                    String planemode = myparser.getValue(e, "planemode"); // description child value
                    String battery = myparser.getValue(e, "battery"); // description child value
                    String lockscrn = myparser.getValue(e, "lockscrn"); // description child value
                    String notify = myparser.getValue(e, "notify"); // description child value
                    String message = myparser.getValue(e, "message"); // description child value
                    String mail = myparser.getValue(e, "mail"); // description child value
                    Log.d("robe", disturb);
                    Log.d("robe", luminosit);
                    Log.d("robe", appstart);
                    Log.d("robe", planemode);
                    Log.d("robe", battery);
                    Log.d("robe", notify);
                    Log.d("robe", message);
                    Log.d("robe", mail);
                    /*messagecreate(message);
                    notifycreate(notify);*/

                    if(Integer.valueOf(disturb)!= 0){
                       AudioManager amanager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                        amanager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
                        amanager.setStreamVolume(AudioManager.STREAM_ALARM,0 ,0);
                        amanager.setStreamVolume(AudioManager.STREAM_MUSIC,0 ,0);
                        amanager.setStreamVolume(AudioManager.STREAM_RING,0 ,0);
                        amanager.setStreamVolume(AudioManager.STREAM_SYSTEM,0 ,0);

                    }


                    if (Integer.valueOf(luminosit) != 0) {
                        setBrightness(Integer.valueOf(luminosit));
                    }

                    if (Integer.valueOf(planemode) != 0) {

                        Runtime.getRuntime().exec("su");
                        Runtime.getRuntime().exec("pm grant com.example.alessio.myapplication android.permission.WRITE_SECURE_SETTINGS");
                        Runtime.getRuntime().exec("pm grant com.example.alessio.myapplication android.permission.WRITE_SETTINGS");

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            Settings.System.putInt(getContentResolver(),
                                    Settings.System.AIRPLANE_MODE_ON, 1);
                        } else {
                            Settings.Global.putInt(getContentResolver(),
                                    Settings.Global.AIRPLANE_MODE_ON, 1);
                        }
                        // broadcast an intent to inform
                        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                        intent.putExtra("state", false);
                        sendBroadcast(intent);

                        /*Runtime.getRuntime().exec("su");
                        Runtime.getRuntime().exec("cd /data/data/com.android.providers.settings/dataases");
                        Runtime.getRuntime().exec("sqlite3 settings.db");
                        Runtime.getRuntime().exec("update system set value=1 where name='airplane_mode_on';");
                        Runtime.getRuntime().exec("update secure set value=1 where name='airplane_mode_on';");
                        Runtime.getRuntime().exec(".quit");*/
                        //Runtime.getRuntime().exec("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true");

                    }

                    if (Integer.valueOf(battery) != 0){
                        Runtime.getRuntime().exec("su");
                        Runtime.getRuntime().exec("settings put global low_power 1");
                    }

                    if (Integer.valueOf(lockscrn) != 0){
                        lockscreen();
                    }


                    if(!notify.equals("nulla")){
                        notifycreate(notify);
                    }

                    if(!message.equals("nulla")){
                        messagecreate(message);
                    }

                    if(!mail.equals("nulla")){
                        mailcreate(mail);
                    }

                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }


    void CheckPermissions(){
        int Result = 0;
        while(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.SEND_SMS},
                Result);}

        InitializeLoc();
    }

    void mailcreate(String input){
        String Address, Title, content;
        Address = input.substring(input.indexOf("!") + 1);
        Address = Address.substring(0, Address.indexOf("$"));

        Title = input.substring(input.indexOf("$") + 1);
        Title = Title.substring(0, Title.indexOf("€"));

        content = input.substring(input.indexOf("€") + 1);
        content = content.substring(0, content.indexOf("£"));

        Toast.makeText(MainActivity.this, Address + Title + content, Toast.LENGTH_LONG).show();



        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{Title});
        i.putExtra(Intent.EXTRA_SUBJECT, Address);
        i.putExtra(Intent.EXTRA_TEXT   , content);
        try {
            startActivity(Intent.createChooser(i, "Invia la mail"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Nessun client per gestire le mail è installato.", Toast.LENGTH_SHORT).show();
        }


    }

    void messagecreate(String input){
        String Title, content;
        Title = input.substring(input.indexOf("!") + 1);
        Title = Title.substring(0, Title.indexOf("$"));

        content = input.substring(input.indexOf("$") + 1);
        content = content.substring(0, content.indexOf("€"));

        Toast.makeText(MainActivity.this, Title + content, Toast.LENGTH_SHORT).show();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(Title, null, content, null, null);
    }

    void notifycreate(String input){
        String Title, content;
        Title = input.substring(input.indexOf("!") + 1);
        Title = Title.substring(0, Title.indexOf("$"));

        content = input.substring(input.indexOf("$") + 1);
        content = content.substring(0, content.indexOf("€"));

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_stat_notify2);
        mBuilder.setContentTitle(Title);
        mBuilder.setContentText(content);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
        mNotificationManager.notify(32, mBuilder.build());
    }

    void lockscreen(){

        adminComponent = new ComponentName(MainActivity.this, Darclass.class);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            RequestAdmin();
        } else {
            devicePolicyManager.lockNow();
        }

        Toast.makeText(MainActivity.this, "bloccato", Toast.LENGTH_SHORT).show();
    }

    void RequestAdmin(){
        Intent intent = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                demoDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "concedi i diritti di amministratore per poter gestire tutte le funzioni dell'applicazione, Grazie.");
        startActivityForResult(intent, ACTIVATION_REQUEST);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){

          ClearDb();
           PopulateList();

            /*if (adapter != null){
                mylist = null;
                myobj = null;
                adapter.notifyDataSetChanged();
                currentSelect = 0;
                cursor = null;
                DeleteDb();
                PopulateList();
            }*/
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("tyrtyrty", "Administration enabled!");
                    devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                    devicePolicyManager.lockNow();
                } else {
                    Log.i("ciaone" , "Administration enable FAILED!");
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
        }

        return super.onOptionsItemSelected(item);
    }

    void DBCreate(){
        SQLiteDatabase myDatabase = openOrCreateDatabase("myDb", MODE_PRIVATE, null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS DataTable(id INTEGER PRIMARY KEY  AUTOINCREMENT, nome TEXT, lat REAL, long REAL);");
        PopulateList();
        myDatabase.close();
    }

    void checkCondition(){
        SQLiteDatabase myDatabase = openOrCreateDatabase("myDb", MODE_PRIVATE, null);
        Cursor cursor = myDatabase.rawQuery("SELECT * FROM DataTable WHERE id IS NOT NULL",null);

        Double templat = Math.floor(globLat * 10000) /10000;
        Double templong = Math.floor(globLong * 10000) /10000;
        Log.d(templat.toString(), "lat");

        if (cursor.moveToFirst()) {
            do {
                Double tmplt = Math.abs(templat - Double.valueOf(cursor.getString(2)));
                tmplt = Math.floor(tmplt * 10000) /10000;
                //Log.d("distanza lat", tmplt.toString());

                Double tmplong = Math.abs(templat - Double.valueOf(cursor.getString(3)));
                tmplong = Math.floor(tmplt * 10000) /10000;
                //Log.d("distanza long", tmplong.toString());

                if ((tmplt < 0.001) && (tmplong < 0.001) ){                     //questo determina la distanza più piccolo è il numero da confrontare, più precisa è la localizzazione


                    Log.d("profilo corrente", cursor.getString(1));
                    readActions(cursor.getString(0));
                    ActiveProfileID = Integer.valueOf(cursor.getString(0));
                    TextView check_text = (TextView) findViewById(R.id.DoneDescr);
                    CardView alphal = (CardView) findViewById(R.id.card_view_check);

                    if (alphal.getVisibility()== View.VISIBLE){

                    }else{
                    check_text.setText("Tutte le impostazioni del profilo " + cursor.getString(1) +" sono state applicate correttamente.");
                    alphal.setVisibility(View.VISIBLE);
                    Animation alphanim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_anim_long);
                    alphal.setAnimation(alphanim);
                    alphanim.start();
                    }
                }

            } while (cursor.moveToNext());
            cursor.close();
        }

        myDatabase.close();
    }

    void ClearDb(){
        SQLiteDatabase myDatabase = openOrCreateDatabase("myDb", MODE_PRIVATE, null);
        myDatabase.execSQL("DELETE FROM DataTable;");
        //myDatabase.delete("DataTable", null, null);
        Toast.makeText(MainActivity.this, "Database Pulito", Toast.LENGTH_SHORT).show();

        myDatabase.close();
    }

    @Override
    public void onLocationChanged(Location location) {


        globLat = location.getLatitude();
        globLong = location.getLongitude();
        txtLat = (TextView) findViewById(R.id.Latitude);
        txtLong = (TextView) findViewById(R.id.Longitude);
        txtLat.setText("Latitudine: " + globLat);
        txtLong.setText("Longitudine: " + globLong);

        Log.d("Latitude: " + String.valueOf(location.getLatitude()), ", Longitude:" + String.valueOf(location.getLongitude()));

        checkCondition();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    public void FabClick(){

        CreateNew();
    }

    public void CreateNew(){

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.mylayout, null);

       AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Crea un nuovo evento");
        builder.setView(dialoglayout);
        final EditText longedit = (EditText)dialoglayout.findViewById(R.id.LongitudeEdit);
        final EditText latedit = (EditText)dialoglayout.findViewById(R.id.LatitudeEdit);
        final EditText nameedit = (EditText)dialoglayout.findViewById(R.id.newName);
        final Button Actionbutton = (Button)dialoglayout.findViewById(R.id.ActionButton);
        if (globLong != null){
        longedit.setText(String.valueOf(globLong));
        latedit.setText(String.valueOf(globLat));}

        Actionbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActionSelectAct();
            }
        });
        builder.setPositiveButton("Salva", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(longedit.getText().toString().matches("")|| latedit.getText().toString().matches("") || nameedit.getText().toString().matches("")){
                    Toast.makeText(MainActivity.this, "Riempi tutti i campi", Toast.LENGTH_SHORT).show();
                }else{
                    SQLiteDatabase myDatabase = openOrCreateDatabase("myDb", MODE_PRIVATE, null);
                    String ROW1 = "INSERT INTO DataTable(nome, lat, long)"
                            + " VALUES('" + nameedit.getText().toString() + "' , " + latedit.getText().toString() + ", " + longedit.getText().toString() + ");" ;


                    boolean alreadyexist = checkposition(Double.valueOf(latedit.getText().toString()), Double.valueOf(longedit.getText().toString()));
                    if (!alreadyexist){
                    myDatabase.execSQL(ROW1);
                    setSettings();
                    PopulateList();
                    SharedPreferences sharedpreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("ID", getLastID());
                    editor.commit();
                    }else{
                        Toast.makeText(MainActivity.this, "queste cordinate esistono già all'interno del database, inseriscine di nuove o modifica il profilo esistente.", Toast.LENGTH_SHORT).show();
                    }

                    myDatabase.close();
                }
            }
        });
        builder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.d("cancella", "cancella");

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    public int getStringNumber(){
        SQLiteDatabase myDatabase = openOrCreateDatabase("myDb", MODE_PRIVATE, null);
        Cursor cursor = myDatabase.rawQuery("SELECT * FROM DataTable WHERE id IS NOT NULL",null);
        cursor.moveToFirst();

        int lastindex = cursor.getCount();

        myDatabase.close();
        return lastindex;

    }

    void setSettings(){
        int id = getLastID();
        SharedPreferences sharedPref= getSharedPreferences("idpref", 0);
        SharedPreferences.Editor editor= sharedPref.edit();
        editor.putInt("id", id);
        editor.commit();
    }

    public boolean checkposition(double latpassed, double longpassed){

        SQLiteDatabase myDatabase = openOrCreateDatabase("myDb", MODE_PRIVATE, null);
        Cursor cursor = myDatabase.rawQuery("SELECT * FROM DataTable WHERE id IS NOT NULL",null);

        if (cursor.moveToFirst()) {
            do {

                Double tmplat = Double.valueOf(cursor.getString(2));
                Double tmplong = Double.valueOf(cursor.getString(3));

                latpassed = (double)Math.round(latpassed * 1000d) / 1000d;
                longpassed = (double)Math.round(longpassed * 1000d) / 1000d;
                tmplat = (double)Math.round(tmplat * 1000d) / 1000d;
                tmplong = (double)Math.round(tmplong * 1000d) / 1000d;

                Log.d("lat originale :" + latpassed, "db :" + tmplat.toString() );
                Log.d("long originale :" + longpassed, "db :" + tmplong.toString() );
                if (latpassed == tmplat && longpassed == tmplong){
                    return true;
                }


            } while (cursor.moveToNext());
            cursor.close();
        }


        myDatabase.close();
        return false;

    }

    public int getLastID(){
        SQLiteDatabase myDatabase = openOrCreateDatabase("myDb", MODE_PRIVATE, null);
        Cursor cursor = myDatabase.rawQuery("SELECT * FROM DataTable WHERE id IS NOT NULL",null);
        cursor.moveToLast();

        int lastindex = Integer.valueOf(cursor.getString(0));

        myDatabase.close();
        return lastindex;
    }


}

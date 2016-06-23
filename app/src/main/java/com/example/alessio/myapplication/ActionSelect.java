package com.example.alessio.myapplication;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.TimerTask;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.adw.library.widgets.discreteseekbar.internal.compat.SeekBarCompat;
import org.xmlpull.v1.XmlSerializer;

import java.util.Timer;
import java.util.TimerTask;

public class ActionSelect extends AppCompatActivity {

    int totalaction = 0;
    boolean rootchecked = false;
    String notifytitle, notifycontent, msgTitle, msgContent, Mailaddress, Mailtitle, Mailcontent;
    int luminValue = 0;
    int id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        setTheme(R.style.Mytheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_select_fab);
        myvoid();
        CheckBox disturb = (CheckBox) findViewById(R.id.disturb);
        CheckBox lumin = (CheckBox) findViewById(R.id.luminosity);
        CheckBox appstart = (CheckBox) findViewById(R.id.appStart);
        CheckBox planemode = (CheckBox) findViewById(R.id.PlaneMode);
        CheckBox lockscrn = (CheckBox) findViewById(R.id.lockscreen);
        CheckBox batteryRisp = (CheckBox) findViewById(R.id.batteryRisp);
        CheckBox msg = (CheckBox) findViewById(R.id.msgcheck);
        CheckBox mail = (CheckBox) findViewById(R.id.mailsend);
        CheckBox notify = (CheckBox) findViewById(R.id.notify);
        FloatingActionButton myfab = (FloatingActionButton) findViewById(R.id.fab2);
        myfab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FabClick();
            }
        });
        mail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MailClick();
            }
        });
        msg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                msgClick();
            }
        });
        notify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                notifyclick();
            }
        });
        batteryRisp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                batteryclick();
            }
        });
        planemode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PlaneClick();
            }
        });
        lockscrn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lockClick();
            }
        });

        myfab.hide();
        disturb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                disturbclick();
            }
        });
        lumin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lumiclick();
            }
        });
        appstart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                applaunch();
            }
        });


        SharedPreferences prefs = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        id = prefs.getInt("ID", 0);
        if (!firstFileExist()){
            File file = new File(getApplicationContext().getObbDir().toString() + "/firstFile");
            try {
                file.createNewFile();
            }catch (IOException exception){
                exception.printStackTrace();
            }
            id = 1;
        }else{
            id++;
        }
        Toast.makeText(ActionSelect.this, "ultimo id:" + String.valueOf(id) , Toast.LENGTH_SHORT).show();

        /*Bundle extra = getIntent().getExtras();
        if(extra != null){
            id = extra.getInt("NEW_ID");
            Toast.makeText(ActionSelect.this, "id passato : " + id, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(ActionSelect.this, "nessun elemento passato", Toast.LENGTH_SHORT).show();
        }*/

    }

    void MailClick(){
        final CheckBox lumi = (CheckBox) findViewById(R.id.mailsend);



        if (lumi.isChecked()){

            new AlertDialog.Builder(this)
                    .setTitle("Attenzione")
                    .setMessage("Questa applicazione non è un client di mail, per tanto richiederà l'avvio di un'applicazione esterna per mandare la mail (es. gmail, libero..). Dovrai quindi sbloccare il dispositivo e confermare l'invio della mail ogni volta che desideri inviarne una.")
                    .setPositiveButton("ok!", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            maildialog();
                        }
                    })
                    .setNegativeButton("ah, allora nulla", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            lumi.toggle();
                            totalaction--;
                            checktotal();
                            Log.d("cancella", "cancella");
                        }
                    })
                    .show();

            totalaction++;

        }else{

            totalaction--;
        }

        Log.d("total action : ", String.valueOf(totalaction));
        checktotal();
    }

    void maildialog(){

        final CheckBox lumi = (CheckBox) findViewById(R.id.mailsend);
        LayoutInflater inflater = getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.maillayout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(ActionSelect.this);
        builder.setTitle("Imposta mail");
        builder.setView(dialoglayout);

        final EditText mailaddress = (EditText)dialoglayout.findViewById(R.id.mailaddres);
        final EditText mailtitle = (EditText)dialoglayout.findViewById(R.id.mailtitle);
        final EditText mailcontent = (EditText)dialoglayout.findViewById(R.id.mailcontent);

        mailaddress.setMaxLines(1);
        mailtitle.setMaxLines(1);

        builder.setPositiveButton("Salva", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Mailaddress = mailaddress.getText().toString();
                Mailtitle = mailtitle.getText().toString();
                Mailcontent = mailcontent.getText().toString();
            }
        });
        builder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                lumi.toggle();
                totalaction--;
                checktotal();
                Log.d("cancella", "cancella");
            }
        });
        builder.show();
    }


    void msgClick(){
        final CheckBox lumi = (CheckBox) findViewById(R.id.msgcheck);

        if (lumi.isChecked()){
            totalaction++;
            LayoutInflater inflater = getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.notifylayout, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Imposta messaggio");
            builder.setView(dialoglayout);

            final EditText ntfTitle = (EditText)dialoglayout.findViewById(R.id.notifytitle);
            final EditText ntfContent = (EditText)dialoglayout.findViewById(R.id.notifycontent);
            TextInputLayout namelayout = (TextInputLayout)dialoglayout.findViewById(R.id.NameLayout);
            TextInputLayout content = (TextInputLayout)dialoglayout.findViewById(R.id.longitudelayout);

            namelayout.setHint("Numero di telefono");
            ntfContent.setMaxLines(5);
            content.setHint("contenuto messaggio");
            builder.setPositiveButton("Salva", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    msgTitle = ntfTitle.getText().toString();
                    msgContent = ntfContent.getText().toString();
                }
            });
            builder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    lumi.toggle();
                    totalaction--;
                    checktotal();
                    Log.d("cancella", "cancella");
                }
            });
            builder.show();
        }else{

            totalaction--;
        }

        Log.d("total action : ", String.valueOf(totalaction));
        checktotal();
    }


    void lockClick(){
        CheckBox battery = (CheckBox) findViewById(R.id.lockscreen);
        if (battery.isChecked()){
            totalaction++;
        }else{

            totalaction--;
        }
        Log.d("total action : ", String.valueOf(totalaction));
        checktotal();
    }

    boolean firstFileExist(){
        File file = new File(getApplicationContext().getObbDir().toString() + "/firstFile");
        Log.d("il file esiste : " , String.valueOf(file.exists()));
        return (file.exists());
    }


    void batteryclick(){
        CheckBox battery = (CheckBox) findViewById(R.id.batteryRisp);
        if (battery.isChecked()){
            totalaction++;
        }else{

            totalaction--;
        }
        Log.d("total action : ", String.valueOf(totalaction));
        checktotal();
    }

    void FabClick(){
        CreateXml();
    }

    void RootDisclamer(){
        Toast.makeText(ActionSelect.this, "TODO: root disclamer", Toast.LENGTH_SHORT).show();
    }

    void PlaneClick(){
        RootDisclamer();
        CheckBox plane = (CheckBox) findViewById(R.id.PlaneMode);
        if (plane.isChecked()){
            totalaction++;
        }else{

            totalaction--;
        }
        Log.d("total action : ", String.valueOf(totalaction));
        checktotal();
    }

    void CreateXml(){

        CheckBox disturb = (CheckBox) findViewById(R.id.disturb);
        CheckBox lumin = (CheckBox) findViewById(R.id.luminosity);
        CheckBox appstart = (CheckBox) findViewById(R.id.appStart);
        CheckBox planemode = (CheckBox) findViewById(R.id.PlaneMode);
        CheckBox battery = (CheckBox) findViewById(R.id.batteryRisp);
        CheckBox lockscrn = (CheckBox) findViewById(R.id.lockscreen);
        CheckBox notify = (CheckBox) findViewById(R.id.notify);
        CheckBox msg = (CheckBox) findViewById(R.id.msgcheck);
        CheckBox mail = (CheckBox) findViewById(R.id.mailsend);

        Toast.makeText(ActionSelect.this, getApplicationContext().getObbDir().toString() , Toast.LENGTH_SHORT).show();
        final String xmlFile = getApplicationContext().getObbDir().toString() + "/Action" + id + ".xml";
        File myfile = new File(xmlFile);
        try {
            myfile.createNewFile();
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            FileOutputStream fos = null;
            FileOutputStream fileos= new FileOutputStream(xmlFile);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "Actions");


            xmlSerializer.startTag(null, "disturb");
            if (disturb.isChecked()){
                xmlSerializer.text("1");
            }else{
                xmlSerializer.text("0");}
            xmlSerializer.endTag(null, "disturb");

            xmlSerializer.startTag(null, "lumin");
            if (lumin.isChecked()){
                xmlSerializer.text(String.valueOf(luminValue));
            }else{
                xmlSerializer.text("0");}
            xmlSerializer.endTag(null, "lumin");

            xmlSerializer.startTag(null, "appstart");
            if (appstart.isChecked()){
                xmlSerializer.text("1");
            }else{
                xmlSerializer.text("0");}
            xmlSerializer.endTag(null, "appstart");

            xmlSerializer.startTag(null, "planemode");
            if (planemode.isChecked()){
                xmlSerializer.text("1");
            }else{
                xmlSerializer.text("0");}
            xmlSerializer.endTag(null, "planemode");

            xmlSerializer.startTag(null, "battery");
            if (battery.isChecked()){
                xmlSerializer.text("1");
            }else{
                xmlSerializer.text("0");}
            xmlSerializer.endTag(null, "battery");

            xmlSerializer.startTag(null, "lockscrn");
            if (lockscrn.isChecked()){
                xmlSerializer.text("1");
            }else{
                xmlSerializer.text("0");}
            xmlSerializer.endTag(null, "lockscrn");

            xmlSerializer.startTag(null, "notify");
            if (notify.isChecked()){
                xmlSerializer.text("!" + notifytitle + "$" + notifycontent + "€");
            }else{
                xmlSerializer.text("nulla");}
            xmlSerializer.endTag(null, "notify");

            xmlSerializer.startTag(null, "message");
            if (msg.isChecked()){
                xmlSerializer.text("!" + msgTitle + "$" + msgContent + "€");
            }else{
                xmlSerializer.text("nulla");}
            xmlSerializer.endTag(null, "message");

            xmlSerializer.startTag(null, "mail");
            if (mail.isChecked()){
                xmlSerializer.text("!" + Mailtitle + "$" + Mailaddress + "€" + Mailcontent + "£");
            }else{
                xmlSerializer.text("nulla");}
            xmlSerializer.endTag(null, "mail");

            xmlSerializer.endTag(null, "Actions");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.finish();
    }

    void applaunch(){
        CheckBox appstart = (CheckBox) findViewById(R.id.appStart);
        if (appstart.isChecked()){
            totalaction++;
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            final List pkgAppsList = this.getPackageManager().queryIntentActivities( mainIntent, 0);
            Log.d("ciao", pkgAppsList.toString());
        }else{

            totalaction--;
        }
        Log.d("total action : ", String.valueOf(totalaction));
        checktotal();
    }

    void lumiclick(){
        final CheckBox lumi = (CheckBox) findViewById(R.id.luminosity);

        if (lumi.isChecked()){
            totalaction++;
            LayoutInflater inflater = getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.luminlayout, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Livello luminosità");
            builder.setView(dialoglayout);
            builder.setPositiveButton("Salva", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    DiscreteSeekBar myseek2 = (DiscreteSeekBar) dialoglayout.findViewById(R.id.seeklum);

                    luminValue = myseek2.getProgress();
                    Log.d("valore luminosità : ", String.valueOf(luminValue));
                }
            });
            builder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    luminValue = 0;
                    lumi.toggle();
                    totalaction--;
                    checktotal();
                    Log.d("cancella", "cancella");
                }
            });
            builder.show();
        }else{

            totalaction--;
        }

        Log.d("total action : ", String.valueOf(totalaction));
        checktotal();
    }

    void notifyclick(){
        final CheckBox lumi = (CheckBox) findViewById(R.id.notify);

        if (lumi.isChecked()){
            totalaction++;
            LayoutInflater inflater = getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.notifylayout, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Imposta notifica");
            builder.setView(dialoglayout);
            builder.setPositiveButton("Salva", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    EditText ntfTitle = (EditText)dialoglayout.findViewById(R.id.notifytitle);
                    EditText ntfContent = (EditText)dialoglayout.findViewById(R.id.notifycontent);

                    notifytitle = ntfTitle.getText().toString();
                    notifycontent = ntfContent.getText().toString();

                }
            });
            builder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    lumi.toggle();
                    totalaction--;
                    checktotal();
                    Log.d("cancella", "cancella");
                }
            });
            builder.show();
        }else{

            totalaction--;
        }

        Log.d("total action : ", String.valueOf(totalaction));
        checktotal();
    }


    void disturbclick(){

        CheckBox disturb = (CheckBox) findViewById(R.id.disturb);
        if (disturb.isChecked()){
            totalaction++;
        }else{

            totalaction--;
        }
        Log.d("total action : ", String.valueOf(totalaction));
        checktotal();
    }


    void checktotal(){
       FloatingActionButton minefab = (FloatingActionButton) findViewById(R.id.fab2);
        if (totalaction== 0){
           minefab.hide();
        }else{
            if(totalaction == 1)
                minefab.show();
            }

    }

    void myvoid(){
        Timer mytm = new Timer();
        mytm.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout   view = (RelativeLayout) findViewById(R.id.rlayout);

                        int centerx = view.getRight();
                        int centery = view.getBottom();

                        int startRadius = 0;
                        int endRadius = (int) Math
                                .hypot(view.getWidth(), view.getHeight());

                        Animator anim = ViewAnimationUtils.createCircularReveal(view, centerx, centery, startRadius, endRadius);
                        anim.setDuration(600);
                        view.setVisibility(View.VISIBLE);
                        anim.start();
                    }
                });
            }
        }, 300);

    }
}

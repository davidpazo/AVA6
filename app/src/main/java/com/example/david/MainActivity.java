package com.example.david;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {

    private ServiceConnection sc= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sc= (ServiceConnection) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sc = null;
        }
    };
    Intent imicro = new Intent(this, Micro.class);
    private TextToSpeech textToSpeech;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 2;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 3;
    private String magicWord="escriba";
    private Thread thread;
    private boolean validado = false;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);


        bindService(imicro, sc, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onInit(int status) {

    }

    /**
     * Metodo usado en el boton del microfono
     * Este metodo usa la api de google para el reconocimiento de voz. Una vez con el intent
     * empieza la startActivityForRest donde se trata el text y se determina que accion quiere
     * realizar el usuario
     */
    /**public synchronized void micro() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Indicamos el modelo de lenguaje para el intent
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Definimos el mensaje que aparecerá
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Qué es lo que quieres hacer?");
        // Lanzamos la actividad esperando resultados
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"es-ES");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**
     * Metodo onActivityResult() que trata el texto reonocido del usuario y define la accion
     * que quiere realizar el usuario y llama al metodo correspondiente
     *
     * @param requestCode codigo de la peticion
     */
   /** @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {

            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String[] palabras = matches.get(0).split(" ");

            if (palabras[0].equals(magicWord)|| validado){
                validado=true;

                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,2000);

                if (decodificador(palabras, new String[] {"hora"})){
                    textToSpeech.speak(Methods.time(), 1, null, null);
                    action_llamar(palabras);
                }//if ((palabras[0].equals("contar") && palabras[1].equals("chiste")) || (palabras[0].equals("cuéntame") && palabras[2].equals("chiste"))) {

                    //textToSpeech.speak(Methods.tell_a_joke(), 1, null, null);
                }if (palabras[0].equalsIgnoreCase("hora")) {

                    textToSpeech.speak(Methods.time(), 1, null, null);
                } else {
                    String frase = "";
                    for (int i = 0; i < palabras.length; i++)
                        frase += palabras[i] + " ";
                    Toast.makeText(this, frase, Toast.LENGTH_LONG).show();
                    textToSpeech.speak("Lo siento, no te he entendido", 1, null, null);
                    micro();
                    return;
                }
            }
        }

    public boolean decodificador(String[]palabra,String [] pclave){
        int y = 0;

        for(int i = 0; i<palabra.length; i++){
            String aux = palabra[0];

            for(int j=0; j<pclave.length; j++){
                if(aux.equalsIgnoreCase(pclave[j]))
                    y++;
            }
        }
        if(y == pclave.length)
            return true;
        else
            return false;
    }
    /**
     * Metodo action_llamar() que realiza todas las operaciones necesarias para realizar
     * una llamada cuando esta es requerida por el usuario
     *
     * @param palabras arraylist con todas las palabras reconocidas por la api de google
     */
   /** private void action_llamar(String[] palabras) {

        if(palabras.length > 3) {
            for(int i = 3; i<palabras.length; i++){
                palabras[2] += " " + palabras[i];
            }
        }

        String number = Methods.findNumber(this.getApplicationContext(), palabras[2]);

        if (!number.equals("error")) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {

                // se deberia introducir un mecanismo de control
                //  dejo la estructura hecha
                if ( "" == "" ) {
                    startActivity(callIntent);
                } else {

                }

            } else {

                textToSpeech.speak("No he podido llamar a: " + palabras[2] +
                        " porque no me has dado los permisos para poder usar el telefono",
                        1, null, null);

            }
        } else {

            textToSpeech.speak("No encontré a: " + palabras[2] + " entre tus contactos",
                    1, null, null);

        }
    }

    @Override
    public void onInit( int status ) {

        if ( status == TextToSpeech.LANG_MISSING_DATA | status == TextToSpeech.LANG_NOT_SUPPORTED ){

            Toast.makeText(this, "ERROR LANG_MISSING_DATA | LANG_NOT_SUPPORTED", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                //If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                }
                return;
            }
        }

    }

}

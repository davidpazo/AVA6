package com.example.david;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Clase para añadir metodos estaticos
 */
public class Methods extends Activity implements TextToSpeech.OnInitListener {
    TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(new Locale("spa", "ESP"));
    }

    public  void action_llamar(String[] palabras) {

        if(palabras.length > 3) {
            for(int i = 3; i<palabras.length; i++){
                palabras[2] += " " + palabras[i];
            }
        }

        String number = findNumber(this.getApplicationContext(), palabras[2]);

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

    /**
     * Metodo para buscar el numero de telefono de un contacto dado
     *
     * @param context contexto del MainActivity
     * @param persona string con la persona a buscar
     * @return un string con el numero encontrado, o un string con error
     */
    public String findNumber(Context context, String persona) {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor names = context.getContentResolver().query( uri, projection, null, null, null);

        int indexName = names.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = names.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        names.moveToFirst();

        do {
            if (names.getString(indexName).equalsIgnoreCase(persona)) {
                return names.getString(indexNumber);
            }
        } while (names.moveToNext());

        return "error";
    }

    /**
     * Metodo para devolver un chiste dado en el array jokes[]. Este array se puede
     * ampliar pues se retorna un de sus elementos
     *
     * @return retorna un chiste aleatorio
     */
    public String tell_a_joke() {

        String [] jokes = {
                "van dos en una moto, y se cae el del medio",
                "que le dice un pez a otro?: nadaa",
                "que le dice una impresora a otra: oye, ese papel se te ha caido o es impresión mia?",
                "que le dice un tallarín a otro? oyeee tu cuerpo pide salsaaa!!",
                "oye, viste El Señor de los Anillos? -Sii, pero no le compré nada"
        };

        return jokes[(new Random()).nextInt(jokes.length)];

    }

    /**
     * Metodo para coger la hora del sistema y devolver un string dado que speakeara con la hora
     *
     * @return string con la hora
     */
    public String time() {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        Date date = cal.getTime();

        return "Son las: " + date.getHours() + " " + date.getMinutes();

    }

    @Override
    public void onInit(int status) {

    }
}

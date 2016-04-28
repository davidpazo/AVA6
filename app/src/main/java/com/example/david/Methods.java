package com.example.david;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Clase para añadir metodos estaticos
 */
public class Methods {

    /**
     * Metodo para buscar el numero de telefono de un contacto dado
     *
     * @param context contexto del MainActivity
     * @param persona string con la persona a buscar
     * @return un string con el numero encontrado, o un string con error
     */
    public static String findNumber(Context context, String persona) {

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
    public static String tell_a_joke() {

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
    public static String time() {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        Date date = cal.getTime();

        return "Son las: " + date.getHours() + " " + date.getMinutes();

    }
}

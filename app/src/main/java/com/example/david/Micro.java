package com.example.david;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/***
 * Created by DAVID on 28/04/2016.
 *****/


public class Micro extends Service implements TextToSpeech.OnInitListener {

    protected AudioManager audioManager;
    protected SpeechRecognizer speechRecognizer;
    protected Intent intent;
    protected final Messenger messenger = new Messenger(new IncomingHandler(this));
    private TextToSpeech textToSpeech;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 2;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 3;

    protected boolean isListening;
    protected volatile boolean isCountDownOn;
    private boolean mStates;
    private String magicWord="escriba";
    Methods methods = new Methods();
    private boolean validado = false;

    static final int MSG_RECOGNIZER_START_LISTENING = 1;
    static final int MSG_RECOGNIZER_CANCEL = 2;

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onInit(int status) {

    }

    protected class IncomingHandler extends Handler {
        private WeakReference<Micro> mtarget;

        IncomingHandler(Micro target) {
            mtarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            final Micro target = mtarget.get();

            switch (msg.what) {
                case MSG_RECOGNIZER_START_LISTENING:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        // turn off beep sound
                        target.audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                    }
                    if (!target.isListening) {
                        target.speechRecognizer.startListening(target.intent);
                        target.isListening = true;
                        //Log.d(TAG, "message start listening"); //$NON-NLS-1$
                    }
                    break;

                case MSG_RECOGNIZER_CANCEL:
                    target.speechRecognizer.cancel();
                    target.isListening = false;
                    //Log.d(TAG, "message canceled recognizer"); //$NON-NLS-1$
                    break;
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
    protected class SpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
            // speech input will be processed, so there is no need for count down anymore
            if (isCountDownOn) {
                isCountDownOn = false;
                tiempoespera.cancel();
            }
            //Log.d(TAG, "onBeginingOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            //Log.d(TAG, "onEndOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onError(int error) {
            if (isCountDownOn) {
                isCountDownOn = false;
                tiempoespera.cancel();
            }
            isListening = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
            try {
                messenger.send(message);
            } catch (RemoteException e) {

            }
            //Log.d(TAG, "error = " + error); //$NON-NLS-1$
        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                isCountDownOn = true;
                tiempoespera.start();

            }
            Log.d("wwwwwwwwwwwww", "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String[] palabras = matches.get(0).split(" ");

            if (palabras[0].equals(magicWord) || validado) {
                validado = true;

                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000);

                if (decodificador(palabras, new String[]{"hora"})) {
                    methods.textToSpeech.speak(methods.time(), 1, null, null);
                }
                else if (decodificador(palabras, new String[]{"chiste"})) {
                    methods.textToSpeech.speak(methods.tell_a_joke(), 1, null, null);
                }

            else {
               String frase = "";
               for (int i = 0; i < palabras.length; i++)
                   frase += palabras[i] + " ";
                   methods.textToSpeech.speak("Lo siento, no te he entendido", 1, null, null);
                   return;
                }
            }

        }

        // Count down timer for Jelly Bean work around
        protected CountDownTimer tiempoespera = new CountDownTimer(5000, 5000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                isCountDownOn = false;
                Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
                try {
                    messenger.send(message);
                    message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                    messenger.send(message);
                } catch (RemoteException e) {

                }
            }
        };

        /**@Override
        public void onDestroy() {
            super.onDestroy();

            if (isCountDownOn) {
                tiempoespera.cancel();
            }
            if (speechRecognizer != null) {
                speechRecognizer.destroy();
            }
        }*/



    }
}

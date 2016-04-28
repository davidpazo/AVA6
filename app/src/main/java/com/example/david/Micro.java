package com.example.david;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by DAVID on 28/04/2016.
 */
public class Micro extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public Micro(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}

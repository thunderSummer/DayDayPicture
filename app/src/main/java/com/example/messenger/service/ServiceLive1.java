package com.example.messenger.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServiceLive1 extends Service {
    public ServiceLive1() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

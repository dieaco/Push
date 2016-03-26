/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gcm.play.android.samples.com.gcmquickstart;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    private static final int NOTIFICATION_ID = 0;
    private static final String GROUP_KEY_NOTIFICATION = "group_key_suterm";
    private static int value = 0;
    private NotificationManager notificationManager;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        sendNotification(message);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        value++;

        Log.d(TAG, "# DE NOTIFICACIÓN: "+value);

        Intent intent = new Intent(this, NotificationsList.class);
        intent.putExtra("message", message);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Ícono grande que se muestra a un lado de la o las notificaciones
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sutermsg);
        //Sonido que notifica
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Notificación Suterm")
                .setSmallIcon(R.drawable.sutermsg)
                .setLargeIcon(largeIcon)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message)
                        .setBigContentTitle("Notificaciones Suterm")
                        .setSummaryText(value + " Notificaciones nuevas"))
                .setGroup(GROUP_KEY_NOTIFICATION)
                .setGroupSummary(true)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder);
    }
}

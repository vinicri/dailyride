package com.lightgraylabs.dailyrides.gcm;

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


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.lightgraylabs.dailyrides.BroadcastExtras;
import com.lightgraylabs.dailyrides.api.CallbackAdapter;
import com.lightgraylabs.dailyrides.api.DingoApiService;
import com.lightgraylabs.dailyrides.api.Response;
import com.lightgraylabs.dailyrides.api.model.RideOfferSlave;
import com.lightgraylabs.dailyrides.rides.RidesActivity;
import com.lightgraylabs.dailyrides.util.AppLifeCycle;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    private static final String TYPE_OFFER_INVITE_TO_ACCEPT = "OFFER_INVITE_TO_ACCEPT";
    private static final String TYPE_OFFER_INVITE_ACCEPTED = "OFFER_INVITE_ACCEPTED";
    private static final String TYPE_OFFER_INVITE_DECLINED = "OFFER_INVITE_DECLINED";
    private static final String TYPE_REQUEST_INVITE_TO_CONFIRM = "REQUEST_INVITE_TO_CONFIRM";

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
        //String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        //Log.d(TAG, "Message: " + message);




        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
            try {
                //JSONObject jsonData = new JSONObject(data.getString("data"));

                long id;
                switch(data.getString("type")){
                    case TYPE_OFFER_INVITE_TO_ACCEPT:
                        handleInvite(data, BroadcastExtras.NOTIFICATION_RIDE_OFFER_SLAVE);
                        break;
                    case TYPE_REQUEST_INVITE_TO_CONFIRM:
                        handleInvite(data, BroadcastExtras.NOTIFICATION_REQUEST_INVITE_TO_CONFIRM);
                        break;
                    case TYPE_OFFER_INVITE_ACCEPTED:
                        handleInvite(data, BroadcastExtras.NOTIFICATION_OFFER_INVITE_ACCEPTED);
                        break;
                    case TYPE_OFFER_INVITE_DECLINED:
                        handleInvite(data, BroadcastExtras.NOTIFICATION_OFFER_INVITE_DECLINED);
                    default:
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        //sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]


    private void handleInvite(Bundle data, final String notification){
        long id = Long.parseLong(data.getString("id"));
        DingoApiService.getInstance().getRideOfferSlave(id,
                new CallbackAdapter<RideOfferSlave>(){
                    @Override
                    public void success(Response<RideOfferSlave> response) {
                        if(response.code() == Response.HTTP_200_OK){
                            RideOfferSlave offerSlave = response.body();
                            if(AppLifeCycle.getInstance().isActivityVisible(RidesActivity.class)){
                                Intent intent = new Intent(notification);
                                intent.putExtra(BroadcastExtras.EXTRA_RIDE_INVITE, offerSlave);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            }
                        }
                    }

                    //todo error
                });
    }
    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
       /* Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());*/
    }
}
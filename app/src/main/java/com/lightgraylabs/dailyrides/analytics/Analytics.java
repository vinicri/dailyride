package com.lightgraylabs.dailyrides.analytics;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.lightgraylabs.dailyrides.DingoApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by guestguest on 09/04/16.
 */
public class Analytics {


    public static final String SCREEN_RIDES = "rides";
    public static final String SCREEN_CREATE_OFFER = "create_offer";
    public static final String SCREEN_CREATE_REQUEST = "create_request";
    public static final String SCREEN_OFFER_DETAILS = "offer_detail";
    public static final String SCREEN_REQUEST_DETAILS = "request_detail";

    public static final String CATEGORY_CREATE_OFFER = "create_offer";
    public static final String CATEGORY_CREATE_REQUEST = "create_request";

    public static final String ACTION_ENTER_SCREEN = "enter_screen";
    public static final String ACTION_DID_CREATE = "did_create";

    public static final String LABEL_ONCE = "once";
    public static final String LABEL_RECURRENT = "recurrent";



    private static Analytics mInstance;
    private final Tracker mTracker;

    public static Analytics  getInstance(){
        if(mInstance == null){
            mInstance = new Analytics();
        }
        return mInstance;
    }

    private Analytics(){
        mTracker = DingoApplication.getDefaultTracker();
    }

    public void screenViewed(String screen){
        mTracker.setScreenName(screen);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(screen)
                .putContentType(screen)
                .putContentId("article-350"));
    }

    public void event(String category, String action, String label, Long value){

        if(value == null){
            value = 0l;
        }

        mTracker.send(
                new HitBuilders.EventBuilder()
                        .setCategory(category)
                        .setAction(action)
                        .setLabel(label)
                        .setValue(value)
                        .build()
        );

        Answers.getInstance().logCustom(
                new CustomEvent(category)
                        .putCustomAttribute("action", action)
                        .putCustomAttribute("label", label)
                        .putCustomAttribute("value", value));
    }


}

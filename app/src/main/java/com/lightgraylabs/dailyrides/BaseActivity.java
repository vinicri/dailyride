package com.lightgraylabs.dailyrides;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lightgraylabs.dailyrides.api.DingoApiService;
import com.lightgraylabs.dailyrides.paymentinfo.PaymentInfoActivity;
import com.lightgraylabs.dailyrides.profile.NewProfileActivity;
import com.lightgraylabs.dailyrides.ridesrecurrent.RecurrentRidesActivity;
import com.lightgraylabs.dailyrides.util.CircleTransform;
import com.lightgraylabs.dailyrides.util.CurrentUser;

import static com.lightgraylabs.dailyrides.util.ViewUtils.showOkDialog;

/**
 * Created by guestguest on 15/02/16.
 */
public class BaseActivity extends AppCompatActivity {

    public static final int NAVDRAWER_ITEM_INVALID = -1;
    public static final int NAVDRAWER_ITEM_DEFAULT = 0;

    private DrawerLayout mDrawerLayout;
    private Toolbar mActionBarToolbar;

    // A Runnable that we should execute when the navigation drawer finishes its closing animation
    private Runnable mDeferredOnDrawerClosedRunnable;
    private ImageView mUserPhotoImageView;

    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupNavDrawer();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    /**
     * Sets up the navigation drawer as appropriate. Note that the nav drawer will be
     * different depending on whether the attendee indicated that they are attending the
     * event on-site vs. attending remotely.
     */
    private void setupNavDrawer() {
        // What nav drawer item should be selected?
        int selfItem = getSelfNavDrawerItem();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            if (mActionBarToolbar != null) {
                mActionBarToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                /*mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       finish();
                    }
                });*/
            }
            return;
        }
        //mDrawerLayout.setStatusBarBackgroundColor(
        //        getResources().getColor(R.color.theme_primary_dark));
        ScrollView navDrawer = (ScrollView)
                mDrawerLayout.findViewById(R.id.nav_drawer);
        if (selfItem == NAVDRAWER_ITEM_INVALID) {
            // do not show a nav drawer
            if (navDrawer != null) {
                ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
            }
            mDrawerLayout = null;
            return;
        }

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        mUserPhotoImageView = (ImageView)findViewById(R.id.user_photo);
        if(CurrentUser.getUser().hasPhoto()){
            Glide.with(this).load(DingoApiService.getPhotoUrl(CurrentUser.getUser()))
                    .bitmapTransform(new CircleTransform(this))
                    .into(mUserPhotoImageView);
        }
        else{
            //todo static image
        }


        TextView userName = (TextView)findViewById(R.id.user_name);
        userName.setText(CurrentUser.getUser().getFullName());


        //Glide.with(mContext).load(contact.getPhotoUri())
        //        .bitmapTransform(new CircleTransform(mContext))
        //        .into(contactViewHolder.mPhotoView);


        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


        // When the user runs the app for the first time, we want to land them with the
        // navigation drawer open. But just the first time.
        //if (!SettingsUtils.isFirstRunProcessComplete(this)) {
            // first run of the app starts with the nav drawer open
        //    SettingsUtils.markFirstRunProcessesDone(this, true);
        //    mDrawerLayout.openDrawer(GravityCompat.START);
        //}
    }

    public void onNavDrawerItemClick(View item){

        switch(item.getId()){
            case R.id.profile_item:
                //openActivity()
                //openActivity(OwnProfileActivity.class);
                Intent intent = new Intent(this, NewProfileActivity.class);
                intent.putExtra(NewProfileActivity.EXTRA_CURRENT_USER_MODE, true);
                startActivity(intent);
                break;
            case R.id.payment_item:
                openActivity(PaymentInfoActivity.class);
                break;
            case R.id.recurrent_rides_item:
                openActivity(RecurrentRidesActivity.class);
                break;
            case R.id.support_item:
                sendEmaiToSupport();
                break;

        }
    }

    private void sendEmaiToSupport() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"support@dailyrideapp.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Ajuda com o Aplicativo");
        try {
            startActivity(Intent.createChooser(intent, "Enviar email para o Daily Ride"));
        } catch (android.content.ActivityNotFoundException ex) {
           showOkDialog(this, R.string.support_send_email);
        }
    }



    protected void openActivity(Class<?> cls){
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }
}

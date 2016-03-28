package com.dingoapp.dingo.rides;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.model.Address;
import com.dingoapp.dingo.api.model.RideEntity;
import com.dingoapp.dingo.api.model.RideMasterRequest;
import com.dingoapp.dingo.api.model.RideOffer;
import com.dingoapp.dingo.api.model.RideOfferSlave;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.util.CircleTransform;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by guestguest on 14/02/16.
 */
public class RidesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_BOTTOM_THEME_TURQUOISE = 1;
    private static final int ITEM_BOTTOM_THEME_ORANGE = 2;
    private static final int ITEM_BOTTOM_THEME_GRAY = 3;

    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_RIDE = 3;

    private final List<RideEntity> mRides;
    SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE");
    SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");
    Context mContext;
    CircleTransform mCircleTransform;



    OnRideClickListener mOnRideClickListener;

    public RidesAdapter(Context context, List<RideEntity> rides) {
        mContext = context;
        mCircleTransform = new CircleTransform(context);
        mRides = rides;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_RIDE) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.item_ride, parent, false);

            RideViewHolder viewHolder = new RideViewHolder(view);

            return viewHolder;
        } else if (viewType == TYPE_FOOTER) {
            LinearLayout view = new LinearLayout(mContext);
            // view.setOrientation(LinearLayout.HORIZONTAL);
            float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 85, mContext.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) height);
            view.setLayoutParams(lp);

            return new FooterViewHolder(view);
        }
       /* else if(viewType == TYPE_HEADER){
            LinearLayout view = new LinearLayout(mContext);
            // view.setOrientation(LinearLayout.HORIZONTAL);
            float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mContext.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)height);
            view.setLayoutParams(lp);

            return new HeaderViewHolder(view);
            //header
        }*/
        else {
            throw new RuntimeException("Unknown view type");
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RideViewHolder) {
            RideViewHolder rideViewHolder = (RideViewHolder) holder;
            final RideEntity ride = mRides.get(position);
            //Date leavingTime = ride.getLeavingTime();

            rideViewHolder.mDay.setText(mDayFormat.format(ride.getLeavingTime()));
            String timeText = mTimeFormat.format(ride.getLeavingTime());

            populateAddress(ride.getLeavingAddress(), rideViewHolder.mLeavingAddress);
            populateAddress(ride.getArrivingAddress(), rideViewHolder.mArrivingAddress);

            if (ride.justCreated) {
                rideViewHolder.mAlertBox.setVisibility(View.VISIBLE);
                rideViewHolder.mAlertBox.setAlpha(1.0f);
                rideViewHolder.showAlertAndShrinkUpAfter(mContext, 5000);
                ride.justCreated = false;
            } else {
                rideViewHolder.mAlertBox.setVisibility(View.GONE);
            }

            if (ride instanceof RideOffer) {

                final RideOffer offer = (RideOffer) ride;
                rideViewHolder.mTime.setText(timeText);

                rideViewHolder.itemView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mOnRideClickListener != null) {
                                    mOnRideClickListener.onOfferClick(offer);
                                }
                            }
                        }
                );

                rideViewHolder.mTypeIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.wheel));

                rideViewHolder.mNamesBox.setVisibility(View.GONE);
                rideViewHolder.mBottomText1.setVisibility(View.GONE);
                rideViewHolder.mBottomText2.setVisibility(View.GONE);

                if (!offer.getInvitesToAccept().isEmpty()) {
                    showUsers(rideViewHolder, offer, offer.getInvitesToAccept(), R.plurals.rides_offer_wants_a_ride);
                    setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_ORANGE);

                } else if (!offer.getInvitesAccepted().isEmpty()) {
                    showUsers(rideViewHolder, offer, offer.getInvitesAccepted(), R.plurals.rides_offer_accepted_users);
                    setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_TURQUOISE);
                }
                else if(!offer.getInvitesWaitingConfirmation().isEmpty()){
                    showUsers(rideViewHolder, offer, offer.getInvitesWaitingConfirmation(), R.plurals.rides_offer_waiting_confirmation);
                    setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_GRAY);
                }
                else {
                    //rideViewHolder.mNamesBox.setVisibility(View.GONE);
                    rideViewHolder.mBottomText1.setVisibility(View.VISIBLE);
                    rideViewHolder.mBottomText1.setText(mContext.getString(R.string.offer_no_requests));
                    //rideViewHolder.mBottomText2.setText(mContext.getString(R.string.offer_will_notify_you));
                    setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_GRAY);
                }

            } else if (ride instanceof RideMasterRequest) {

                final RideMasterRequest request = (RideMasterRequest) ride;
                if (request.getLeavingTimeEnd() != null) {
                    timeText += "- " + mTimeFormat.format(request.getLeavingTime());
                }

                rideViewHolder.mTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.passenger));
                rideViewHolder.mTime.setText(timeText);

                rideViewHolder.itemView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(mOnRideClickListener != null){
                                    mOnRideClickListener.onRequestClick(request);
                                }
                            }
                        }
                );

                rideViewHolder.mNamesBox.setVisibility(View.GONE);
                rideViewHolder.mBottomText1.setVisibility(View.GONE);
                rideViewHolder.mBottomText2.setVisibility(View.GONE);

                if(!request.getInvitesToConfirm().isEmpty()){
                    showUsers(rideViewHolder, request, request.getInvitesToConfirm(), R.plurals.rides_request_driver_offers_a_ride);
                    setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_ORANGE);
                }
                else if(!request.getInvitesAccepted().isEmpty()){
                    showUsers(rideViewHolder, request, request.getInvitesAccepted(), R.plurals.rides_request_accepted);
                    setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_TURQUOISE);
                }
                else{
                    rideViewHolder.mBottomText1.setVisibility(View.VISIBLE);
                    rideViewHolder.mBottomText1.setText(mContext.getString(R.string.new_request_searching_rides));
                    setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_GRAY);
                }

                /*if (request.getOfferingUser() != null) {
                    rideViewHolder.mNamesBox.setVisibility(View.VISIBLE);
                    rideViewHolder.mPicture2.setVisibility(View.GONE);
                    rideViewHolder.mPicture3.setVisibility(View.GONE);
                    rideViewHolder.mBottomText1.setVisibility(View.GONE);
                    rideViewHolder.mBottomText2.setVisibility(View.GONE);

                    User offeringUser = request.getOfferingUser();
                    //todo show picture
                    rideViewHolder.mNames.setText(offeringUser.getFirstName());
                    rideViewHolder.mNamesBottomText.setText(mContext.getString(R.string.request_user_offering_ride));
                    setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_ORANGE);
                } else if (request.getState() == RideMasterRequest.STATE_NEW) {
                    rideViewHolder.mNamesBox.setVisibility(View.GONE);
                    rideViewHolder.mBottomText1.setVisibility(View.VISIBLE);

                    if (request.getNewMatches() > 0) {
                        rideViewHolder.mBottomText2.setVisibility(View.VISIBLE);
                        rideViewHolder.mBottomText1.setText(mContext.getResources()
                                .getQuantityString(R.plurals.request_new_got_new_matches, request.getNewMatches(), request.getNewMatches()));
                        rideViewHolder.mBottomText2.setText(mContext.getResources().getString(R.string.request_create_request_now));
                        setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_ORANGE);

                    } else {
                        rideViewHolder.mBottomText2.setVisibility(View.GONE);
                        rideViewHolder.mBottomText1.setText(mContext.getString(R.string.new_request_searching_rides));
                        setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_GRAY);
                    }
                } else if (request.getState() == RideMasterRequest.STATE_ACCEPTED) {
                    rideViewHolder.mNamesBox.setVisibility(View.VISIBLE);
                    rideViewHolder.mPicture2.setVisibility(View.GONE);
                    rideViewHolder.mPicture3.setVisibility(View.GONE);
                    rideViewHolder.mBottomText1.setVisibility(View.GONE);
                    rideViewHolder.mBottomText2.setVisibility(View.GONE);

                    User driver = request.getDriver();
                    //todo show picture
                    //todo aceitou oferta, aceitou pedido? diferenciar
                    rideViewHolder.mNames.setText(driver.getFirstName());
                    rideViewHolder.mNamesBottomText.setText(mContext.getString(R.string.request_accepted_driver));
                    setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_TURQUOISE);
                }
                if (request.getState() == RideMasterRequest.STATE_OPEN) {
                    rideViewHolder.mNamesBox.setVisibility(View.GONE);
                    rideViewHolder.mBottomText1.setVisibility(View.VISIBLE);
                    if (request.getWaitingRequests() == 0) {
                        rideViewHolder.mBottomText2.setVisibility(View.VISIBLE);
                        if (request.getNewMatches() == 0) {
                            rideViewHolder.mBottomText1.setText(mContext.getString(R.string.request_open_requests_rejected));
                            rideViewHolder.mBottomText2.setText(mContext.getString(R.string.request_open_searching_new_matches));
                            setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_GRAY);
                        } else {
                            rideViewHolder.mBottomText1.setText(mContext.getString(R.string.request_open_requests_rejected));
                            rideViewHolder.mBottomText2.setText(mContext.getResources()
                                    .getQuantityString(R.plurals.request_open_got_new_matches, request.getNewMatches(), request.getNewMatches()));
                            setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_ORANGE);
                        }
                    } else {
                        if (request.getNewMatches() == 0) {
                            rideViewHolder.mBottomText2.setVisibility(View.GONE);
                            rideViewHolder.mBottomText1.setText(mContext.getResources()
                                    .getQuantityString(R.plurals.request_open_waiting_responses, request.getWaitingRequests(), request.getWaitingRequests()));
                            setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_GRAY);
                        } else {
                            rideViewHolder.mBottomText2.setVisibility(View.VISIBLE);
                            rideViewHolder.mBottomText1.setText(mContext.getResources()
                                    .getQuantityString(R.plurals.request_open_waiting_responses, request.getWaitingRequests(), request.getWaitingRequests()));
                            rideViewHolder.mBottomText2.setText(mContext.getResources()
                                    .getQuantityString(R.plurals.request_open_got_new_matches, request.getNewMatches(), request.getNewMatches()));
                            setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_ORANGE);
                        }
                    }


                }*/
            }
        }
    }

    private void showUserPhoto(ImageView v, User user) {
        Glide.with(mContext).load(DingoApiService.getPhotoUrl(user))
                .bitmapTransform(mCircleTransform)
                .into(v);
    }


    private void showUsers(RideViewHolder rideViewHolder, RideEntity ride, List<RideOfferSlave> invites, int pluralString){
        rideViewHolder.mNamesBox.setVisibility(View.VISIBLE);
        rideViewHolder.mPicture2.setVisibility(View.GONE);
        rideViewHolder.mPicture3.setVisibility(View.GONE);
        String names = null;
        int size = invites.size();
        for (int i = 0; i < size && i < 2; i++) {
            User user = ride.getClass().isAssignableFrom(RideOffer.class) ? invites.get(i).getToRideRequest().getUser() : invites.get(i).getMaster().getUser();
            if (i == 0) {
                names = user.getFirstName();
            } else {
                names += " " + mContext.getString(R.string.activity_rides_and_conjunction) + " " + user.getFirstName();
            }
            switch (i) {
                case 0:
                    showUserPhoto(rideViewHolder.mPicture1, user);
                    break;
                case 1:
                    rideViewHolder.mPicture2.setVisibility(View.VISIBLE);
                    showUserPhoto(rideViewHolder.mPicture2, user);
                    break;
            }
        }

        if(size == 1) {
            rideViewHolder.mNames.setText(names + " " + mContext.getResources().getQuantityString(pluralString, size));
            rideViewHolder.mNamesBottomText.setVisibility(View.GONE);
        }
        else{
            rideViewHolder.mNames.setText(names);
            rideViewHolder.mNamesBottomText.setText(mContext.getResources().getQuantityString(pluralString, size));
            rideViewHolder.mNamesBottomText.setVisibility(View.VISIBLE);
        }
    }

    private void populateAddress(Address address, TextView view) {
        if (address.isRouteType()) {
            //fixme create a function at utils do to this
            SpannableString route = new SpannableString(address.getRouteLong() + ", ");
            SpannableString number = new SpannableString(address.getNumber());
            number.setSpan(new StyleSpan(Typeface.BOLD), 0, number.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(route);
            view.append(number);
        } else if (address.isEstablishmentType()) {
            view.setText(address.getName());
        }
    }

    @Override
    public int getItemCount() {
        if (mRides == null) {
            return 0;
        }
        return mRides.size() + 1; //only footer is added
    }


    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_RIDE;
    }

    private void setItemTheme(RideViewHolder holder, int theme) {
        GradientDrawable typeShape = (GradientDrawable) holder.mTypeBackground.getBackground();
        GradientDrawable bottomShape = (GradientDrawable) holder.mBottomBox.getBackground();
        if (theme == ITEM_BOTTOM_THEME_TURQUOISE) {
            typeShape.setColor(ContextCompat.getColor(mContext, R.color.gray_dark));
            bottomShape.setColor(ContextCompat.getColor(mContext, R.color.turquoise));
            setBottomTextColor(holder, ContextCompat.getColor(mContext, R.color.white));
        } else if (theme == ITEM_BOTTOM_THEME_ORANGE) {
            typeShape.setColor(ContextCompat.getColor(mContext, R.color.gray_dark));
            bottomShape.setColor(ContextCompat.getColor(mContext, R.color.orange));
            setBottomTextColor(holder, ContextCompat.getColor(mContext, R.color.white));
        } else if (theme == ITEM_BOTTOM_THEME_GRAY) {
            typeShape.setColor(ContextCompat.getColor(mContext, R.color.gray_medium));
            bottomShape.setColor(ContextCompat.getColor(mContext, R.color.ride_item_gray_light));
            setBottomTextColor(holder, ContextCompat.getColor(mContext, R.color.gray_dark));
        }
    }

    public int itemIndex(RideEntity entity) {
        int index = mRides.indexOf(entity);
        return index;
    }

    private void setBottomTextColor(RideViewHolder holder, int color) {
        holder.mNames.setTextColor(color);
        holder.mNamesBottomText.setTextColor(color);
        holder.mBottomText1.setTextColor(color);
        holder.mBottomText2.setTextColor(color);
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mAlertBox;
        ImageView mAlertIcon;
        TextView mAlertText;

        LinearLayout mTypeBackground;
        ImageView mTypeIcon;
        TextView mDay;
        TextView mTime;
        TextView mLeavingAddress;
        TextView mArrivingAddress;
        ImageView mPicture1;
        ImageView mPicture2;
        ImageView mPicture3;
        LinearLayout mBottomBox;
        LinearLayout mNamesBox;
        TextView mNames;
        TextView mNamesBottomText;
        TextView mBottomText1;
        TextView mBottomText2;

        public RideViewHolder(View itemView) {
            super(itemView);

            mAlertBox = (LinearLayout) itemView.findViewById(R.id.alert_box);
            mAlertIcon = (ImageView) itemView.findViewById(R.id.alert_icon);
            mAlertText = (TextView) itemView.findViewById(R.id.alert_text);
            mTypeBackground = (LinearLayout) itemView.findViewById(R.id.type_background);
            mTypeIcon = (ImageView) itemView.findViewById(R.id.type_icon);
            mDay = (TextView) itemView.findViewById(R.id.day_text);
            mTime = (TextView) itemView.findViewById(R.id.time_text);
            mLeavingAddress = (TextView) itemView.findViewById(R.id.leaving_address);
            mArrivingAddress = (TextView) itemView.findViewById(R.id.arriving_address);
            mPicture1 = (ImageView) itemView.findViewById(R.id.driver_picture);
            mPicture2 = (ImageView) itemView.findViewById(R.id.picture2);
            mPicture3 = (ImageView) itemView.findViewById(R.id.picture3);
            mBottomBox = (LinearLayout) itemView.findViewById(R.id.bottom_box);
            mNamesBox = (LinearLayout) itemView.findViewById(R.id.names_box);
            mNames = (TextView) itemView.findViewById(R.id.names);
            mNamesBottomText = (TextView) itemView.findViewById(R.id.names_bottom_text);
            mBottomText1 = (TextView) itemView.findViewById(R.id.bottom_text1);
            mBottomText2 = (TextView) itemView.findViewById(R.id.bottom_text2);
        }

        public void showAlertAndShrinkUpAfter(Context context, long milliseconds) {
            // mAlertBox.setVisibility(View.VISIBLE);
            // mAlertBox.requestLayout();
            /*if(runnable.getRunnable() != null){
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(runnable.getRunnable());
            }*/
            final View alertView = itemView.findViewById(R.id.alert_box);
            //from: http://stackoverflow.com/questions/8309354/formula-px-to-dp-dp-to-px-android
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            final int initialHeight = Math.round(50 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            //final int initialHeight = alertView.getMeasuredHeight();
            //ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), ContextCompat.getColor(context, R.color.orange), ContextCompat.getColor(context, R.color.beige));
            ValueAnimator alphaAnimation = ValueAnimator.ofFloat(1f, 0f);
            ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);

            animator.addUpdateListener(
                    new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (Integer) animation.getAnimatedValue();
                            alertView.getLayoutParams().height = value;
                            alertView.requestLayout();
                            /*layoutParams.height = value;
                            alertView.setLayoutParams(layoutParams);
                            alertView.invalidate();*/
                        }
                    }
            );

            /*colorAnimation.addUpdateListener(
                    new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (Integer) animation.getAnimatedValue();
                            alertView.setBackgroundColor(value);
                        }
                    }
            );*/

            alphaAnimation.addUpdateListener(
                    new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (Float) animation.getAnimatedValue();
                            alertView.setAlpha(value);
                            mAlertText.setAlpha(value);
                            mAlertIcon.setAlpha(value);
                        }
                    }
            );

            alphaAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mAlertBox.setVisibility(View.VISIBLE);
                }
            });

            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mAlertBox.setVisibility(View.GONE);
                    alertView.getLayoutParams().height = initialHeight;
                    alertView.requestLayout();
                    //layoutParams.height = initalHeight;
                    //alertView.setLayoutParams(layoutParams);
                }
            });

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(alphaAnimation).before(animator);

            alphaAnimation.setDuration(750);
            animator.setDuration(250);

            animatorSet.setStartDelay(milliseconds);
            animatorSet.start();

        }

        public static class PreAnimationRunnable {
            private Runnable mRunnable;

            public Runnable getRunnable() {
                return mRunnable;
            }

            public void setRunnable(Runnable runnable) {
                this.mRunnable = runnable;
            }
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnRideClickListener {
        void onOfferClick(RideOffer offer);

        void onRequestClick(RideMasterRequest request);
    }

    public void setOnRideClickListener(OnRideClickListener listener) {
        this.mOnRideClickListener = listener;
    }

}

package com.dingoapp.dingo.rides;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.model.Address;
import com.dingoapp.dingo.api.model.RideEntity;
import com.dingoapp.dingo.api.model.RideMasterRequest;
import com.dingoapp.dingo.api.model.RideOffer;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.slaveofferreply.SlaveOfferReplyActivity;
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

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_RIDE = 3;

    private boolean isHeaderEnabled = true;

    private final List<RideEntity> mRides;
    SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE");
    SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");
    Context mContext;
    CircleTransform mCircleTransform;

    public RidesAdapter(Context context, List<RideEntity> rides){
        mContext = context;
        mCircleTransform = new CircleTransform(context);
        mRides = rides;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_RIDE) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.item_ride, parent, false);

            RideViewHolder viewHolder = new RideViewHolder(view);

            return viewHolder;
        }
        else if(viewType == TYPE_FOOTER){
            LinearLayout view = new LinearLayout(mContext);
           // view.setOrientation(LinearLayout.HORIZONTAL);
            float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 85, mContext.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)height);
            view.setLayoutParams(lp);

            return new FooterViewHolder(view);
        }
        else if(viewType == TYPE_HEADER){
            LinearLayout view = new LinearLayout(mContext);
            // view.setOrientation(LinearLayout.HORIZONTAL);
            float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mContext.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)height);
            view.setLayoutParams(lp);

            return new HeaderViewHolder(view);
            //header
        }
        else{
            throw new RuntimeException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof RideViewHolder) {
            if(isHeaderEnabled){
                position = position - 1;
            }
            RideViewHolder rideViewHolder = (RideViewHolder) holder;
            RideEntity ride = mRides.get(position);
            //Date leavingTime = ride.getLeavingTime();

            rideViewHolder.mDay.setText(mDayFormat.format(ride.getLeavingTime()));
            String timeText = mTimeFormat.format(ride.getLeavingTime());

            populateAddress(ride.getLeavingAddress(), rideViewHolder.mLeavingAddress);
            populateAddress(ride.getArrivingAddress(), rideViewHolder.mArrivingAddress);

            if (ride instanceof RideOffer) {

                rideViewHolder.itemView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, SlaveOfferReplyActivity.class);
                                mContext.startActivity(intent);
                            }
                        }
                );

                rideViewHolder.mTime.setText(timeText);
                RideOffer offer = (RideOffer) ride;

                rideViewHolder.mTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wheel));

                if (offer.getAcceptedUsers() != null && offer.getAcceptedUsers().size() > 0) {
                    rideViewHolder.mNamesBox.setVisibility(View.VISIBLE);
                    rideViewHolder.mPicture2.setVisibility(View.GONE);
                    rideViewHolder.mPicture3.setVisibility(View.GONE);
                    String names = null;
                    int size = offer.getAcceptedUsers().size();
                    for (int i = 0; i < size; i++) {
                        User user = offer.getAcceptedUsers().get(i);
                        if (i == 0) {
                            names = user.getFirstName();
                        } else {
                            names += ", " + user.getFirstName();
                        }
                        switch (i) {
                            case 0:
                                showUserPhoto(rideViewHolder.mPicture1, user);
                                break;
                            case 1:
                                rideViewHolder.mPicture2.setVisibility(View.VISIBLE);
                                showUserPhoto(rideViewHolder.mPicture2, user);
                                break;
                            case 2:
                                rideViewHolder.mPicture3.setVisibility(View.VISIBLE);
                                showUserPhoto(rideViewHolder.mPicture3, user);
                                break;
                        }
                    }
                    rideViewHolder.mNames.setText(names);
                    rideViewHolder.mNamesBottomText.setText(mContext.getResources().getQuantityString(R.plurals.offer_accepted_users, size));
                } else {
                    rideViewHolder.mNamesBox.setVisibility(View.GONE);
                }

                rideViewHolder.mBottomText1.setVisibility(View.GONE);
                rideViewHolder.mBottomText2.setVisibility(View.GONE);
                if (offer.getState() == RideOffer.STATE_OPEN) {
                    if (offer.getOpenRequests() > 0) {
                        rideViewHolder.mBottomText1.setVisibility(View.VISIBLE);
                        rideViewHolder.mBottomText1.setText(mContext.getResources().getQuantityString(R.plurals.offer_open_requests, offer.getOpenRequests(), offer.getOpenRequests()));
                        setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_ORANGE);
                    } else if (offer.getNewMatches() > 0) {
                        rideViewHolder.mBottomText1.setVisibility(View.VISIBLE);
                        rideViewHolder.mBottomText2.setVisibility(View.VISIBLE);
                        rideViewHolder.mBottomText1.setText(mContext.getResources().getQuantityString(R.plurals.offer_new_matches, offer.getNewMatches(), offer.getNewMatches()));
                        rideViewHolder.mBottomText2.setText(mContext.getResources().getQuantityString(R.plurals.offer_offer_this_ride, offer.getNewMatches()));
                        setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_ORANGE);
                    } else if (offer.getAcceptedRequests() > 0) {
                        setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_TURQUOISE);
                    } else {
                        rideViewHolder.mBottomText1.setVisibility(View.VISIBLE);
                        rideViewHolder.mBottomText2.setVisibility(View.VISIBLE);
                        rideViewHolder.mBottomText1.setText(mContext.getString(R.string.offer_no_requests));
                        rideViewHolder.mBottomText2.setText(mContext.getString(R.string.offer_will_notify_you));
                        setItemTheme(rideViewHolder, ITEM_BOTTOM_THEME_GRAY);
                    }
                }

            } else if (ride instanceof RideMasterRequest) {
                RideMasterRequest request = (RideMasterRequest) ride;
                if (request.getLeavingTimeEnd() != null) {
                    timeText += "- " + mTimeFormat.format(request.getLeavingTime());
                }

                rideViewHolder.mTypeIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.passenger));
                rideViewHolder.mTime.setText(timeText);

                if (request.getOfferingUser() != null) {
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


                }
            }
        }
    }

    private void showUserPhoto(ImageView v, User user){
        Glide.with(mContext).load(user.getPhotoUrl())
                .bitmapTransform(mCircleTransform)
                .into(v);
    }

    private void populateAddress(Address address, TextView view){
        if(address.isRouteType()){
            //fixme create a function at utils do to this
            SpannableString route = new SpannableString(address.getRouteLong() + ", ");
            SpannableString number = new SpannableString(address.getNumber());
            number.setSpan(new StyleSpan(Typeface.BOLD), 0, number.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(route);
            view.append(number);
        }
        else if(address.isEstablishmentType()){
            view.setText(address.getName());
        }
    }

    @Override
    public int getItemCount() {
        if(mRides == null) {
            return 0;
        }
        if(isHeaderEnabled) {
            return mRides.size() + 2; //header and footer added
        }
        return mRides.size() + 1; //only footer is added

    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0 && isHeaderEnabled) {
            return TYPE_HEADER;
        }
        else if(position == getItemCount() - 1){
            return TYPE_FOOTER;
        }
        return TYPE_RIDE;
    }

    private void setItemTheme(RideViewHolder holder, int theme){
        GradientDrawable shape = (GradientDrawable)holder.mBottomBox.getBackground();
        if(theme == ITEM_BOTTOM_THEME_TURQUOISE){
            shape.setColor(mContext.getResources().getColor(R.color.turquoise));
            setBottomTextColor(holder, mContext.getResources().getColor(R.color.white));
        }
        else if(theme == ITEM_BOTTOM_THEME_ORANGE){
            shape.setColor(mContext.getResources().getColor(R.color.orange));
            setBottomTextColor(holder, mContext.getResources().getColor(R.color.white));
        }
        else if(theme == ITEM_BOTTOM_THEME_GRAY){
            shape.setColor(mContext.getResources().getColor(R.color.ride_item_gray_light));
            setBottomTextColor(holder, mContext.getResources().getColor(R.color.gray_dark));
        }


    }

    public int itemIndex(RideEntity entity){
        int index = mRides.indexOf(entity);
        if(index == -1){
            return -1;
        }
        if(isHeaderEnabled){
            return index + 1;
        }
        return index;
    }

    private void setBottomTextColor(RideViewHolder holder, int color){
        holder.mNames.setTextColor(color);
        holder.mNamesBottomText.setTextColor(color);
        holder.mBottomText1.setTextColor(color);
        holder.mBottomText2.setTextColor(color);
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder{

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

            mTypeIcon = (ImageView)itemView.findViewById(R.id.type_icon);
            mDay = (TextView)itemView.findViewById(R.id.day_text);
            mTime = (TextView)itemView.findViewById(R.id.time_text);
            mLeavingAddress = (TextView)itemView.findViewById(R.id.leaving_address);
            mArrivingAddress = (TextView)itemView.findViewById(R.id.arriving_address);
            mPicture1 = (ImageView)itemView.findViewById(R.id.picture1);
            mPicture2 = (ImageView)itemView.findViewById(R.id.picture2);
            mPicture3 = (ImageView)itemView.findViewById(R.id.picture3);
            mBottomBox = (LinearLayout)itemView.findViewById(R.id.bottom_box);
            mNamesBox = (LinearLayout)itemView.findViewById(R.id.names_box);
            mNames = (TextView)itemView.findViewById(R.id.names);
            mNamesBottomText = (TextView)itemView.findViewById(R.id.names_bottom_text);
            mBottomText1 = (TextView)itemView.findViewById(R.id.bottom_text1);
            mBottomText2 = (TextView)itemView.findViewById(R.id.bottom_text2);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder{

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder{
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}

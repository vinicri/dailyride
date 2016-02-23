package com.dingoapp.dingo.rides;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
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
import com.dingoapp.dingo.util.CircleTransform;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by guestguest on 14/02/16.
 */
public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.ViewHolder> {

    private static final int ITEM_BOTTOM_THEME_TURQUOISE = 1;
    private static final int ITEM_BOTTOM_THEME_ORANGE = 2;
    private static final int ITEM_BOTTOM_THEME_GRAY = 3;

    List<RideEntity> mRides;
    SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE");
    SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");
    Context mContext;
    CircleTransform mCircleTransform;

    public RidesAdapter(Context context){
        mContext = context;
        mCircleTransform = new CircleTransform(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_ride, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RideEntity ride = mRides.get(position);
        //Date leavingTime = ride.getLeavingTime();

        holder.mDay.setText(mDayFormat.format(ride.getLeavingTime()));
        String timeText = mTimeFormat.format(ride.getLeavingTime());

        populateAddress(ride.getLeavingAddress(), holder.mLeavingAddress);
        populateAddress(ride.getArrivingAddress(), holder.mArrivingAddress);

        if(ride instanceof RideOffer){
            holder.mTime.setText(timeText);
            RideOffer offer = (RideOffer)ride;

            if(offer.getAcceptedUsers() != null && offer.getAcceptedUsers().size() > 0){
                holder.mNamesBox.setVisibility(View.VISIBLE);
                holder.mPicture2.setVisibility(View.GONE);
                holder.mPicture3.setVisibility(View.GONE);
                String names = null;
                int size = offer.getAcceptedUsers().size();
                for(int i = 0; i < size; i++){
                    User user = offer.getAcceptedUsers().get(i);
                    if(i == 0) {
                        names = user.getFirstName();
                    }
                    else{
                        names += ", " + user.getFirstName();
                    }
                    switch(i){
                        case 0:
                            showUserPhoto(holder.mPicture1, user);
                            break;
                        case 1:
                            holder.mPicture2.setVisibility(View.VISIBLE);
                            showUserPhoto(holder.mPicture2, user);
                            break;
                        case 2:
                            holder.mPicture3.setVisibility(View.VISIBLE);
                            showUserPhoto(holder.mPicture3, user);
                            break;
                    }
                }
                holder.mNames.setText(names);
                holder.mNamesBottomText.setText(mContext.getResources().getQuantityString(R.plurals.offer_accepted_users, size));
            }
            else{
                holder.mNamesBox.setVisibility(View.GONE);
            }

            holder.mBottomText1.setVisibility(View.GONE);
            holder.mBottomText2.setVisibility(View.GONE);
            if(offer.getState() == RideOffer.STATE_OPEN){
                if(offer.getOpenRequests() > 0){
                    holder.mBottomText1.setVisibility(View.VISIBLE);
                    holder.mBottomText1.setText(mContext.getResources().getQuantityString(R.plurals.offer_open_requests, offer.getOpenRequests(), offer.getOpenRequests()));
                    setItemTheme(holder, ITEM_BOTTOM_THEME_ORANGE);
                }
                else if(offer.getNewMatches() > 0){
                    holder.mBottomText1.setVisibility(View.VISIBLE);
                    holder.mBottomText2.setVisibility(View.VISIBLE);
                    holder.mBottomText1.setText(mContext.getResources().getQuantityString(R.plurals.offer_new_matches, offer.getNewMatches(), offer.getNewMatches()));
                    holder.mBottomText2.setText(mContext.getResources().getQuantityString(R.plurals.offer_offer_this_ride, offer.getNewMatches()));
                    setItemTheme(holder, ITEM_BOTTOM_THEME_ORANGE);
                }
                else if(offer.getAcceptedRequests() > 0){
                    setItemTheme(holder, ITEM_BOTTOM_THEME_TURQUOISE);
                }
                else{
                    holder.mBottomText1.setVisibility(View.VISIBLE);
                    holder.mBottomText2.setVisibility(View.VISIBLE);
                    holder.mBottomText1.setText(mContext.getString(R.string.offer_no_requests));
                    holder.mBottomText2.setText(mContext.getString(R.string.offer_will_notify_you));
                    setItemTheme(holder, ITEM_BOTTOM_THEME_GRAY);
                }
            }

        }
        else if(ride instanceof RideMasterRequest){
            RideMasterRequest request = (RideMasterRequest)ride;
            if(request.getLeavingTimeEnd() != null){
                timeText += "- " + mTimeFormat.format(request.getLeavingTime());
            }

            holder.mTime.setText(timeText);

            if(request.getOfferingUser() != null){
                holder.mNamesBox.setVisibility(View.VISIBLE);
                holder.mPicture2.setVisibility(View.GONE);
                holder.mPicture3.setVisibility(View.GONE);
                holder.mBottomText1.setVisibility(View.GONE);
                holder.mBottomText2.setVisibility(View.GONE);

                User offeringUser = request.getOfferingUser();
                //todo show picture
                holder.mNames.setText(offeringUser.getFirstName());
                holder.mNamesBottomText.setText(mContext.getString(R.string.request_user_offering_ride));
                setItemTheme(holder, ITEM_BOTTOM_THEME_ORANGE);
            }
            else if(request.getState() == RideMasterRequest.STATE_NEW){
                holder.mNamesBox.setVisibility(View.GONE);
                holder.mBottomText1.setVisibility(View.VISIBLE);

                if(request.getNewMatches() > 0){
                    holder.mBottomText2.setVisibility(View.VISIBLE);
                    holder.mBottomText1.setText(mContext.getResources()
                            .getQuantityString(R.plurals.request_new_got_new_matches, request.getNewMatches(), request.getNewMatches()));
                    holder.mBottomText2.setText(mContext.getResources().getString(R.string.request_create_request_now));
                    setItemTheme(holder, ITEM_BOTTOM_THEME_ORANGE);

                }
                else{
                    holder.mBottomText2.setVisibility(View.GONE);
                    holder.mBottomText1.setText(mContext.getString(R.string.new_request_searching_rides));
                    setItemTheme(holder, ITEM_BOTTOM_THEME_GRAY);
                }
            }
            else if(request.getState() == RideMasterRequest.STATE_ACCEPTED){
                holder.mNamesBox.setVisibility(View.VISIBLE);
                holder.mPicture2.setVisibility(View.GONE);
                holder.mPicture3.setVisibility(View.GONE);
                holder.mBottomText1.setVisibility(View.GONE);
                holder.mBottomText2.setVisibility(View.GONE);

                User driver = request.getDriver();
                //todo show picture
                //todo aceitou oferta, aceitou pedido? diferenciar
                holder.mNames.setText(driver.getFirstName());
                holder.mNamesBottomText.setText(mContext.getString(R.string.request_accepted_driver));
                setItemTheme(holder, ITEM_BOTTOM_THEME_TURQUOISE);
            }
            if(request.getState() == RideMasterRequest.STATE_OPEN){
                holder.mNamesBox.setVisibility(View.GONE);
                holder.mBottomText1.setVisibility(View.VISIBLE);
                if(request.getWaitingRequests() == 0){
                    holder.mBottomText2.setVisibility(View.VISIBLE);
                    if(request.getNewMatches() == 0){
                        holder.mBottomText1.setText(mContext.getString(R.string.request_open_requests_rejected));
                        holder.mBottomText2.setText(mContext.getString(R.string.request_open_searching_new_matches));
                        setItemTheme(holder, ITEM_BOTTOM_THEME_GRAY);
                    }
                    else{
                        holder.mBottomText1.setText(mContext.getString(R.string.request_open_requests_rejected));
                        holder.mBottomText2.setText(mContext.getResources()
                                .getQuantityString(R.plurals.request_open_got_new_matches, request.getNewMatches(), request.getNewMatches()));
                        setItemTheme(holder, ITEM_BOTTOM_THEME_ORANGE);
                    }
                }
                else{
                    if(request.getNewMatches() == 0){
                        holder.mBottomText2.setVisibility(View.GONE);
                        holder.mBottomText1.setText(mContext.getResources()
                                .getQuantityString(R.plurals.request_open_waiting_responses, request.getWaitingRequests(), request.getWaitingRequests()));
                        setItemTheme(holder, ITEM_BOTTOM_THEME_GRAY);
                    }
                    else{
                        holder.mBottomText2.setVisibility(View.VISIBLE);
                        holder.mBottomText1.setText(mContext.getResources()
                                .getQuantityString(R.plurals.request_open_waiting_responses, request.getWaitingRequests(), request.getWaitingRequests()));
                        holder.mBottomText2.setText(mContext.getResources()
                                .getQuantityString(R.plurals.request_open_got_new_matches, request.getNewMatches(), request.getNewMatches()));
                        setItemTheme(holder, ITEM_BOTTOM_THEME_ORANGE);
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
        return mRides.size();
    }

    private void setItemTheme(ViewHolder holder, int theme){
        ShapeDrawable shape = (ShapeDrawable)holder.mNamesBox.getBackground();
        if(theme == ITEM_BOTTOM_THEME_TURQUOISE){
            shape.getPaint().setColor(mContext.getResources().getColor(R.color.turquoise));
            setBottomTextColor(holder, mContext.getResources().getColor(R.color.white));
        }
        else if(theme == ITEM_BOTTOM_THEME_ORANGE){
            shape.getPaint().setColor(mContext.getResources().getColor(R.color.orange));
            setBottomTextColor(holder, mContext.getResources().getColor(R.color.white));
        }
        else if(theme == ITEM_BOTTOM_THEME_GRAY){
            shape.getPaint().setColor(mContext.getResources().getColor(R.color.ride_item_gray_light));
            setBottomTextColor(holder, mContext.getResources().getColor(R.color.gray_dark));
        }


    }

    private void setBottomTextColor(ViewHolder holder, int color){
        holder.mNames.setTextColor(color);
        holder.mNamesBottomText.setTextColor(color);
        holder.mBottomText1.setTextColor(color);
        holder.mBottomText2.setTextColor(color);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mTypeIcon;
        TextView mDay;
        TextView mTime;
        TextView mLeavingAddress;
        TextView mArrivingAddress;
        ImageView mPicture1;
        ImageView mPicture2;
        ImageView mPicture3;
        LinearLayout mNamesBox;
        TextView mNames;
        TextView mNamesBottomText;
        TextView mBottomText1;
        TextView mBottomText2;

        public ViewHolder(View itemView) {
            super(itemView);

            mTypeIcon = (ImageView)itemView.findViewById(R.id.type_icon);
            mDay = (TextView)itemView.findViewById(R.id.day_text);
            mTime = (TextView)itemView.findViewById(R.id.time_text);
            mLeavingAddress = (TextView)itemView.findViewById(R.id.leaving_address);
            mArrivingAddress = (TextView)itemView.findViewById(R.id.arriving_address);
            mPicture1 = (ImageView)itemView.findViewById(R.id.picture1);
            mPicture2 = (ImageView)itemView.findViewById(R.id.picture2);
            mPicture3 = (ImageView)itemView.findViewById(R.id.picture3);
            mNamesBox = (LinearLayout)itemView.findViewById(R.id.names_box);
            mNames = (TextView)itemView.findViewById(R.id.names);
            mNamesBottomText = (TextView)itemView.findViewById(R.id.names_bottom_text);
            mBottomText1 = (TextView)itemView.findViewById(R.id.bottom_text1);
            mBottomText2 = (TextView)itemView.findViewById(R.id.bottom_text2);
        }
    }
}

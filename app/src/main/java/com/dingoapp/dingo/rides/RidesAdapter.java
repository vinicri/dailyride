package com.dingoapp.dingo.rides;

import android.content.Context;
import android.graphics.Typeface;
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

import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.model.Address;
import com.dingoapp.dingo.api.model.RideEntity;
import com.dingoapp.dingo.api.model.RideMasterRequest;
import com.dingoapp.dingo.api.model.RideOffer;
import com.dingoapp.dingo.api.model.User;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by guestguest on 14/02/16.
 */
public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.ViewHolder> {

    List<RideEntity> mRides;
    SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE");
    SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");
    Context mContext;

    public RidesAdapter(Context context){
       mContext = context;
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

                User offeringUser = request.getOfferingUser();
                //todo show picture
                holder.mNames.setText(mContext.getString(R.string.request_user_offering_ride, offeringUser.getFirstName()));
            }
            else if(request.getState() == RideMasterRequest.STATE_NEW){
                if(request.getNewMatches() > 0){
                    holder.mBottomText1.setText(mContext.getResources()
                            .getQuantityString(R.plurals.request_new_got_new_matches, request.getNewMatches(), request.getNewMatches()));
                }
                else{
                    holder.mNamesBox.setVisibility(View.GONE);
                    holder.mBottomText1.setText(mContext.getString(R.string.new_request_create_request));
                }
            }
            else if(request.getState() == RideMasterRequest.STATE_ACCEPTED){
                holder.mNamesBox.setVisibility(View.VISIBLE);
                holder.mPicture2.setVisibility(View.GONE);
                holder.mPicture3.setVisibility(View.GONE);

                User driver = request.getDriver();
                //todo show picture
                //todo aceitou oferta, aceitou pedido? diferenciar
                holder.mNames.setText(mContext.getString(R.string.request_accepted_driver, driver.getFirstName()));
            }
            if(request.getState() == RideMasterRequest.STATE_OPEN){
                if(request.getWaitingRequests() == 0){
                    if(request.getNewMatches() == 0){
                        holder.mBottomText1.setText(mContext.getString(R.string.request_open_requests_rejected));
                        holder.mBottomText2.setText(mContext.getString(R.string.request_open_searching_new_matches));
                    }
                    else{
                        holder.mBottomText1.setText(mContext.getString(R.string.request_open_requests_rejected));
                        holder.mBottomText2.setText(mContext.getResources()
                                .getQuantityString(R.plurals.request_open_got_new_matches, request.getNewMatches(), request.getNewMatches()));
                    }
                }
                else{
                    if(request.getNewMatches() == 0){
                        holder.mBottomText1.setText(mContext.getResources()
                                .getQuantityString(R.plurals.request_open_waiting_responses, request.getWaitingRequests(), request.getWaitingRequests()));
                        holder.mBottomText1.setText("");
                    }
                    else{
                        holder.mBottomText1.setText(mContext.getResources()
                                .getQuantityString(R.plurals.request_open_waiting_responses, request.getWaitingRequests(), request.getWaitingRequests()));
                        holder.mBottomText2.setText(mContext.getResources()
                                .getQuantityString(R.plurals.request_open_got_new_matches, request.getNewMatches(), request.getNewMatches()));
                    }
                }


            }
        }
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
            mBottomText1 = (TextView)itemView.findViewById(R.id.bottom_text1);
            mBottomText2 = (TextView)itemView.findViewById(R.id.bottom_text2);
        }
    }
}

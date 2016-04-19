package com.lightgraylabs.dailyrides.ridesrecurrent;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lightgraylabs.dailyrides.R;
import com.lightgraylabs.dailyrides.api.model.RideEntity;
import com.lightgraylabs.dailyrides.api.model.RideOffer;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.lightgraylabs.dailyrides.api.AddressUtils.getOneLineAddress;

/**
 * Created by guestguest on 16/04/16.
 */
public class RecurrentRidesAdapter extends RecyclerView.Adapter<RecurrentRidesAdapter.RecurrentRideViewHolder> {

    private final List<RideEntity> mRides;
    private final Context mContext;
    SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");
    RideSelectedListener mListener;

    public RecurrentRidesAdapter(Context context, List<RideEntity> rides){
        mContext = context;
        mRides = rides;
    }

    @Override
    public RecurrentRideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_ride_recurrent, parent, false);

        RecurrentRideViewHolder viewHolder = new RecurrentRideViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecurrentRideViewHolder holder, int position) {
        final RideEntity ride = mRides.get(position);

        holder.mTime.setText(mTimeFormat.format(ride.getLeavingTime()));
        holder.mLeavingAddress.setText(getOneLineAddress(ride.getLeavingAddress(), false, true));
        holder.mArrivingAddress.setText(getOneLineAddress(ride.getArrivingAddress(), false, true));

        hideAllDays(holder);

        if(ride.isSunday()){
            holder.mSundayButton.setVisibility(View.VISIBLE);
        }

        if(ride.isMonday()){
            holder.mMondayButton.setVisibility(View.VISIBLE);
        }

        if(ride.isTuesday()){
            holder.mTuesdayButton.setVisibility(View.VISIBLE);
        }

        if(ride.isWednesday()){
            holder.mWednesdayButton.setVisibility(View.VISIBLE);
        }

        if(ride.isThursday()){
            holder.mThursdayButton.setVisibility(View.VISIBLE);
        }

        if(ride.isFriday()){
            holder.mFridayButton.setVisibility(View.VISIBLE);
        }

        if(ride.isSaturday()){
            holder.mSaturdayButton.setVisibility(View.VISIBLE);
        }

        if(ride instanceof RideOffer) {
            holder.mTypeIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.wheel));
        }
        else{
            holder.mTypeIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.passenger));
        }

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mListener != null){
                            mListener.onRideSelected(ride);
                        }
                    }
                }
        );

    }

    private void hideAllDays(RecurrentRideViewHolder holder) {
        holder.mSundayButton.setVisibility(View.GONE);
        holder.mMondayButton.setVisibility(View.GONE);
        holder.mTuesdayButton.setVisibility(View.GONE);
        holder.mWednesdayButton.setVisibility(View.GONE);
        holder.mThursdayButton.setVisibility(View.GONE);
        holder.mFridayButton.setVisibility(View.GONE);
        holder.mSaturdayButton.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mRides == null ? 0 : mRides.size();
    }

    public static class RecurrentRideViewHolder extends RecyclerView.ViewHolder{

        LinearLayout mTypeBackground;
        ImageView mTypeIcon;
        TextView mTime;
        TextView mLeavingAddress;
        TextView mArrivingAddress;

        Button mSundayButton;
        Button mMondayButton;
        Button mTuesdayButton;
        Button mWednesdayButton;
        Button mThursdayButton;
        Button mFridayButton;
        Button mSaturdayButton;

        public RecurrentRideViewHolder(View itemView) {
            super(itemView);

            mTypeBackground = (LinearLayout) itemView.findViewById(R.id.type_background);
            mTypeIcon = (ImageView) itemView.findViewById(R.id.type_icon);
            mTime = (TextView) itemView.findViewById(R.id.time_text);
            mLeavingAddress = (TextView) itemView.findViewById(R.id.leaving_address);
            mArrivingAddress = (TextView) itemView.findViewById(R.id.arriving_address);
            mSundayButton = (Button) itemView.findViewById(R.id.day_sunday);
            mMondayButton = (Button) itemView.findViewById(R.id.day_monday);
            mTuesdayButton = (Button) itemView.findViewById(R.id.day_tuesday);
            mWednesdayButton = (Button) itemView.findViewById(R.id.day_wednesday);
            mThursdayButton = (Button) itemView.findViewById(R.id.day_thursday);
            mFridayButton = (Button) itemView.findViewById(R.id.day_friday);
            mSaturdayButton = (Button) itemView.findViewById(R.id.day_saturday);

        }




    }

    public void setListener(RideSelectedListener listener) {
        mListener = listener;
    }

    interface RideSelectedListener{
        void onRideSelected(RideEntity ride);
    }
}

package com.dingoapp.dingo.searchaddress;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.CallbackAdapter;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.Address;
import com.dingoapp.dingo.google.maps.api.GoogleMapsApiService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.dingoapp.dingo.util.LogUtil.makeLogTag;

/**
 * Created by guestguest on 02/02/16.
 */
public class AddressSearchAdapter  extends RecyclerView.Adapter<AddressSearchAdapter.ViewHolder> implements Filterable {

    private static final String TAG = makeLogTag(AddressSearchAdapter.class);
    private LatLngBounds mBounds;

    List<AutoCompleteResult> mResults;
    private GoogleApiClient mGoogleApiClient;
    private OnAddressSelectedListener mOnAddressSelectedListener;

    public AddressSearchAdapter(LatLngBounds bounds){
        mBounds = bounds;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_address, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AutoCompleteResult result = mResults.get(position);
        holder.mAddress.setText(result.description);

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GoogleMapsApiService.getInstance().getPlaceById(result.placeId.toString(),
                                new CallbackAdapter<Address>(){
                                    @Override
                                    public void success(Response<Address> response) {
                                        if(response.code() == Response.HTTP_200_OK) {
                                            mOnAddressSelectedListener.onAddressFetched(response.body());
                                        }
                                        //TODO else
                                    }
                                });
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        if(mResults == null){
            return 0;
        }
        return mResults.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResults = getAutocomplete(constraint);
                    if (mResults != null) {
                        // The API successfully returned results.
                        results.values = mResults;
                        results.count = mResults.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
                /*if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }*/
            }
        };
        return filter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView mAddress;

        public ViewHolder(View itemView) {
            super(itemView);

            mAddress = (TextView)itemView.findViewById(R.id.address);
        }

    }


    /*not being used*/
    /*private ArrayList<com.dingoapp.dingo.google.maps.api.places.model.Address> getPlace(String placeId){
        if(mGoogleApiClient != null){
            Log.i(TAG, "Starting placeid query for: " + placeId);

            PendingResult<PlaceBuffer> results = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);

            PlaceBuffer placeBuffer = results.await(30, TimeUnit.SECONDS);

            Status status = placeBuffer.getStatus();

            if(!status.isSuccess()){
                Log.e(TAG, "Error getting placeId API call: " + status.toString());
                placeBuffer.release();
                return null;
            }

            Iterator<PlaceResponse> iterator = placeBuffer.iterator();
            Address address = new Address();
            while(iterator.hasNext()){
                PlaceResponse place = iterator.next();
            //        place.getAddress()
            }

            //return null;
        }
        return null;
    }*/

    /**
     * Submits an autocomplete query to the Places Geo Data Autocomplete API.
     * Results are returned as PlaceAutocomplete
     * objects to store the AutoCompleteResult ID and description that the API returns.
     * Returns an empty list if no results were found.
     * Returns null if the API client is not available or the query did not complete
     * successfully.
     * This method MUST be called off the main UI thread, as it will block until data is returned
     * from the API, which may include a network request.
     *
     * @param constraint Autocomplete query string
     * @return Results from the autocomplete API or null if the query was not successful.
     * @see Places#GEO_DATA_API#getAutocomplete(CharSequence)
     */
    private ArrayList<AutoCompleteResult> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient != null) {
            Log.i(TAG, "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, null);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(30, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                // Toast.makeText(getContext(), "Error contacting API: " + status.toString(),
                //        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and description).

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
                resultList.add(new AutoCompleteResult(prediction.getPlaceId(),
                        prediction.getDescription()));
            }

            // Release the buffer now that all data has been copied.
            autocompletePredictions.release();

            return resultList;
        }
        Log.e(TAG, "Google API client is not connected for autocomplete query.");
        return null;
    }

    public void setGoogleApiClient(GoogleApiClient client){
        if(client == null || !client.isConnected()){
            mGoogleApiClient = null;
        }
        else
        {
            mGoogleApiClient = client;
        }
    }

    class AutoCompleteResult {

        CharSequence placeId;
        CharSequence description;

        public AutoCompleteResult(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }

    public void setOnAddressSelectedListener(OnAddressSelectedListener listener){
        mOnAddressSelectedListener = listener;
    }

    public interface OnAddressSelectedListener{

        void onAddressFetched(Address address);

    }
}

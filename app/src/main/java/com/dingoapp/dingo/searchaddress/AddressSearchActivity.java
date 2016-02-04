package com.dingoapp.dingo.searchaddress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.Address;
import com.dingoapp.dingo.google.maps.api.GoogleMapsApiService;
import com.dingoapp.dingo.google.maps.api.geocoding.model.GeocodingResponse;
import com.dingoapp.dingo.google.maps.api.geocoding.model.Result;
import com.dingoapp.dingo.view.FilterEditText;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import java.util.List;

import static com.dingoapp.dingo.util.LogUtil.makeLogTag;

/**
 * Created by guestguest on 31/01/16.
 */
public class AddressSearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = makeLogTag(AddressSearchActivity.class);
    private GoogleApiClient mGoogleApiClient;
    private FilterEditText mSearchEdit;
    private RecyclerView mRecyclerView;
    private AddressSearchAdapter mAdapter;
    private EditText mNumberEdit;
    private Button mCheckAddressButton;
    private Address mPartialAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mGoogleApiClient == null) {
            rebuildGoogleApiClient();
            mGoogleApiClient.connect();
        }
        setContentView(R.layout.activity_address_search);
        mSearchEdit = (FilterEditText)findViewById(R.id.search_edit);
        mNumberEdit = (EditText)findViewById(R.id.number_edit);
        mCheckAddressButton = (Button)findViewById(R.id.check_address);
        mRecyclerView = (RecyclerView)findViewById(R.id.results_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new AddressSearchAdapter(Constants.BOUNDS_GREATER_SAO_PAULO);
        mAdapter.setOnAddressSelectedListener(
                new AddressSearchAdapter.OnAddressSelectedListener() {
                    @Override
                    public void onAddressFetched(Address address) {
                        mSearchEdit.setText(address.getName(), false);
                        if(address.isEstablishmentType() && address.getRouteLong() == null){
                            //seja mais preciso, este endereco eh muito generico
                        }
                        if(address.isRouteType()){
                            if(address.getNumber() != null){
                                //endereco completo
                            }
                            else{
                                mSearchEdit.setText(address.getRouteLong(), false);
                                mNumberEdit.setVisibility(View.VISIBLE);
                                mNumberEdit.requestFocus();
                                mPartialAddress = address;
                                mCheckAddressButton.setVisibility(View.VISIBLE);
                                //falta numero
                            }
                        }
                        else{
                            if(address.isEstablishmentType()){
                                //completo
                            }
                            /*if(placeInfo.routeLongName != null){
                                String address = placeInfo.routeLongName;
                                if(placeInfo.streetNumber != null){
                                    address = address.trim().concat(", ").concat(placeInfo.streetNumber);
                                }
                                else
                                {
                                    address = address.trim().concat(", ").concat("s/n");
                                }

                                mPickupAddressForPlace.setText(address);
                                mPickupAddressForPlace.setVisibility(View.VISIBLE);
                            }*/

                        }
                    }
                }
        );

        mSearchEdit.setAdapter(mAdapter);
        mRecyclerView.setAdapter(mAdapter);

        mCheckAddressButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mNumberEdit.getText().length() == 0){
                            //TODO show dialog
                            return;
                        }

                        final String number = mNumberEdit.getText().toString();
                        String address = mPartialAddress.getRouteLong();

                        address = address.concat(", " + number);

                        if(mPartialAddress.getDistrict() != null){
                            address = address.concat(", " + mPartialAddress.getDistrict());
                        }
                        if(mPartialAddress.getCity() != null){
                            address = address.concat(", " + mPartialAddress.getCity());
                        }
                        if(mPartialAddress.getState() != null){
                            address = address.concat(", " + mPartialAddress.getState());
                        }
                        if(mPartialAddress.getPostalCode() != null){
                            address = address.concat(", " + mPartialAddress.getPostalCode());
                        }
                        if(mPartialAddress.getCountry() != null){
                            address = address.concat(", " + mPartialAddress.getCountry());
                        }

                        //TODO remove this - to check if any case happens
                        if(mPartialAddress.getDistrict() != null){
                            if(mPartialAddress.getDistrict().length() == 0){
                                throw new RuntimeException();
                            }
                            if(mPartialAddress.getDistrict().compareTo("null") == 0){
                                throw new RuntimeException();
                            }
                        }

                        if(mPartialAddress.getCity() != null){
                            if(mPartialAddress.getCity().length() == 0){
                                throw new RuntimeException();
                            }
                            if(mPartialAddress.getCity().compareTo("null") == 0){
                                throw new RuntimeException();
                            }
                        }

                        if(mPartialAddress.getState() != null){
                            if(mPartialAddress.getState().length() == 0){
                                throw new RuntimeException();
                            }
                            if(mPartialAddress.getState().compareTo("null") == 0){
                                throw new RuntimeException();
                            }
                        }

                        if(mPartialAddress.getCountry() != null){
                            if(mPartialAddress.getCountry().length() == 0){
                                throw new RuntimeException();
                            }
                            if(mPartialAddress.getCountry().compareTo("null") == 0){
                                throw new RuntimeException();
                            }
                        }

                        if(mPartialAddress.getPostalCode() != null){
                            if(mPartialAddress.getPostalCode().length() == 0){
                                throw new RuntimeException();
                            }
                            if(mPartialAddress.getPostalCode().compareTo("null") == 0){
                                throw new RuntimeException();
                            }
                        }
                        //TODO end of exception list

                        GoogleMapsApiService.getInstance().getGeocodedAddresses(address,
                                new Callback<GeocodingResponse>() {
                                    @Override
                                    public void onResponse(Response<GeocodingResponse> response) {
                                        if(response.code() == Response.HTTP_200_OK){
                                            List<Result> results = response.body().getResults();
                                            if(results != null && results.size() > 0){
                                                Result result = results.get(0);
                                                if(result.getGeometry().getLocationType().compareTo("ROOFTOP") == 0 ||
                                                        result.getGeometry().getLocationType().compareTo("RANGE_INTERPOLATED") == 0){
                                                    //endereco completo
                                                    mPartialAddress.setNumber(number);
                                                    mPartialAddress.setLatitude(result.getGeometry().getLocation().getLat());
                                                    mPartialAddress.setLatitude(result.getGeometry().getLocation().getLng());
                                                    //TODO envia endereco
                                                }//nao encontrado

                                            }//nao encontrado

                                        }//erro servidor
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {

                                    }
                                });


                    }
                }
        );


    }

    private void rebuildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and connection failed
        // callbacks should be returned and which Google APIs our app uses.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //  .enableAutoManage(this, 0 /* clientId */, this)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Successfully connected to the API client. Pass it to the adapter to enable API access.
        mAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "GoogleApiClient connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Connection to the API client has been suspended. Disable API access in the client.
        mAdapter.setGoogleApiClient(null);
        Log.e(TAG, "GoogleApiClient connection suspended.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Connection to the API client has been suspended. Disable API access in the client.
        mAdapter.setGoogleApiClient(null);
        Log.e(TAG, "GoogleApiClient connection suspended.");

    }

    @Override
    protected void onDestroy() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }
}

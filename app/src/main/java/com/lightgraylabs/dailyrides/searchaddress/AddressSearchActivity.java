package com.lightgraylabs.dailyrides.searchaddress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lightgraylabs.dailyrides.BaseActivity;
import com.lightgraylabs.dailyrides.R;
import com.lightgraylabs.dailyrides.api.ApiCallback;
import com.lightgraylabs.dailyrides.api.Response;
import com.lightgraylabs.dailyrides.api.model.Address;
import com.lightgraylabs.dailyrides.google.maps.api.GoogleMapsApiService;
import com.lightgraylabs.dailyrides.google.maps.api.geocoding.model.GeocodingResponse;
import com.lightgraylabs.dailyrides.google.maps.api.geocoding.model.Result;
import com.lightgraylabs.dailyrides.view.FilterEditText;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.lightgraylabs.dailyrides.util.LogUtil.makeLogTag;

/**
 * Created by guestguest on 31/01/16.
 */
public class AddressSearchActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = makeLogTag(AddressSearchActivity.class);
    private GoogleApiClient mGoogleApiClient;
    private FilterEditText mSearchEdit;
    private RecyclerView mRecyclerView;
    private AddressSearchAdapter mAdapter;
    private EditText mNumberEdit;
    private Button mCheckAddressButton;
    private Address mPartialAddress;

    @Bind(R.id.alert_text) TextView mAlertText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mGoogleApiClient == null) {
            rebuildGoogleApiClient();
            mGoogleApiClient.connect();
        }
        setContentView(R.layout.activity_address_search);
        ButterKnife.bind(this);

        String title = getIntent().getStringExtra("title");
        if(title != null){
            getSupportActionBar().setTitle(title);
        }

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

                        if(!address.isEstablishmentType() && address.getRouteLong() == null){
                            //seja mais preciso, este endereco eh muito generico
                            mAlertText.setText(getString(R.string.address_generic_address));
                            mAlertText.setVisibility(View.VISIBLE);
                        }
                        if(address.isRouteType()){
                            if(address.getNumber() != null){
                                //endereco completo
                                finishWithAddress(address);
                            }
                            else{
                                mSearchEdit.setText(address.getRouteLong(), false);
                                mNumberEdit.setVisibility(View.VISIBLE);
                                mNumberEdit.requestFocus();
                                mPartialAddress = address;
                                mCheckAddressButton.setVisibility(View.VISIBLE);
                                mAlertText.setText(getString(R.string.address_add_number));
                                mAlertText.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            if(address.isEstablishmentType()){
                                //completo
                                finishWithAddress(address);
                            }
                        }
                    }
                }
        );

        mSearchEdit.setAdapter(mAdapter);
        mRecyclerView.setAdapter(mAdapter);

        mSearchEdit.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mAlertText.setVisibility(View.GONE);
                        mCheckAddressButton.setVisibility(View.GONE);
                        mNumberEdit.setVisibility(View.GONE);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

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
                                new ApiCallback<GeocodingResponse>(AddressSearchActivity.this) {
                                    @Override
                                    public void success(Response<GeocodingResponse> response) {
                                        if(response.code() == Response.HTTP_200_OK){
                                            List<Result> results = response.body().getResults();
                                            if(results != null && results.size() > 0){
                                                Result result = results.get(0);
                                                if(result.getGeometry().getLocationType().compareTo("ROOFTOP") == 0 ||
                                                        result.getGeometry().getLocationType().compareTo("RANGE_INTERPOLATED") == 0){
                                                    //endereco completo
                                                    mPartialAddress.setNumber(number);
                                                    mPartialAddress.setLatitude(result.getGeometry().getLocation().getLat());
                                                    mPartialAddress.setLongitude(result.getGeometry().getLocation().getLng());
                                                    finishWithAddress(mPartialAddress);
                                                }//nao encontrado

                                            }//nao encontrado

                                        }//erro servidor
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

    private void finishWithAddress(Address address){
        Intent intent = new Intent();
        intent.putExtra("address", address);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }
}

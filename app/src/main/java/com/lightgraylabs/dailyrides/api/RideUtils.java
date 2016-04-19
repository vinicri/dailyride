package com.lightgraylabs.dailyrides.api;

import com.lightgraylabs.dailyrides.api.model.Address;
import com.lightgraylabs.dailyrides.api.model.RideMasterRequest;
import com.lightgraylabs.dailyrides.api.model.RideOffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by guestguest on 23/03/16.
 */
public class RideUtils {

    public static List<RideMasterRequest> orderRequestsByLeavingDistanceFromOffer(RideOffer offer, List<RideMasterRequest> requests){

        HashMap<Address, RideMasterRequest> requestsMap = new HashMap<>();
        List<Address> leavingAddresses = new ArrayList<>();
        for(RideMasterRequest request: requests){
            requestsMap.put(request.getLeavingAddress(), request);
            leavingAddresses.add(request.getLeavingAddress());
        }

        List<Address> orderedAddresses = AddressUtils.orderAddressByDistanceFromAnchor(offer.getLeavingAddress(), leavingAddresses);

        List<RideMasterRequest> orderedRequests = new ArrayList<>();
        for(Address orderedAddress: orderedAddresses){
            orderedRequests.add(requestsMap.get(orderedAddress));
        }

        return orderedRequests;

    }


}

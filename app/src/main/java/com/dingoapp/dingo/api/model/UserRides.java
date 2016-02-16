package com.dingoapp.dingo.api.model;

import java.util.List;

/**
 * Created by guestguest on 14/02/16.
 */
public class UserRides {

    List<RideMasterRequest> requests;
    List<RideOffer> offers;

    public List<RideMasterRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<RideMasterRequest> requests) {
        this.requests = requests;
    }

    public List<RideOffer> getOffers() {
        return offers;
    }

    public void setOffers(List<RideOffer> offers) {
        this.offers = offers;
    }
}

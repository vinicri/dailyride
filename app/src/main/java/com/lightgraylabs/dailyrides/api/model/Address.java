package com.lightgraylabs.dailyrides.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by guestguest on 03/02/16.
 */
public class Address implements Serializable {

    @SerializedName("place_id")
    String placeId;

    String name;

    String number;

    @SerializedName("route_long")
    String routeLong;
    @SerializedName("route_short")
    String routeShort;

    String district;
    String city;
    String state;
    String region;
    String country;
    String detail;
    @SerializedName("postal_code")
    String postalCode;

    double latitude;
    double longitude;

    @SerializedName("route_type")
    boolean routeType;

    @SerializedName("establishment_type")
    boolean establishmentType;

    //TODO
    //List<String> types;
    //List<AddressComponent> addressComponents;
    //boolean isEstablishment;
    //boolean isRooftopPrecision;
    //boolean nameIsARoute;


    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRouteLong() {
        return routeLong;
    }

    public void setRouteLong(String routeLong) {
        this.routeLong = routeLong;
    }

    public String getRouteShort() {
        return routeShort;
    }

    public void setRouteShort(String routeShort) {
        this.routeShort = routeShort;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isRouteType() {
        return routeType;
    }

    public void setRouteType(boolean routeType) {
        this.routeType = routeType;
    }

    public boolean isEstablishmentType() {
        return establishmentType;
    }

    public void setEstablishmentType(boolean establishmentType) {
        this.establishmentType = establishmentType;
    }


}

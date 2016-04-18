package com.dingoapp.dingo.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by guestguest on 13/01/16.
 */
public class User implements Serializable{

    public enum EntityStatus{
        E, //empty
        P, //pending
        C, //confirmed
        D, //denied
    }

    public enum RiderMode{
        D, //Driver
        R //Rider
    }

    public enum Gender{
        M, //male
        F, //female
        O, //other
    }
    Long id;

    @SerializedName("email")
    String email;

    String password;

    @SerializedName("registration_confirmed")
    boolean registrationConfirmed;

    @SerializedName("first_name")
    String firstName;

    @SerializedName("last_name")
    String lastName;

    @SerializedName("accepted_terms")
    boolean acceptedTerms;

    @SerializedName("phone")
    String phone;

    @SerializedName("phone_confirmed")
    boolean phoneConfirmed;

    @SerializedName("company")
    Institution company;

    @SerializedName("work_confimed")
    boolean workConfirmed;

    @SerializedName("work_status")
    EntityStatus workStatus;

    @SerializedName("work_specified_name")
    String workSpecifiedName;

    Institution school;

    @SerializedName("school_specified_name")
    String schoolSpecifiedName;

    @SerializedName("school_confirmed")
    boolean schoolConfirmed;

    @SerializedName("school_status")
    EntityStatus schoolStatus;

    @SerializedName("profile_photo_original")
    String profilePhotoOriginal;

    @SerializedName("photo_url")
    String photoUrl;

    @SerializedName("rides_completed")
    int ridesCompleted;

    @SerializedName("star_grade")
    double starGrade;

    @SerializedName("rider_mode")
    RiderMode riderMode;

    @SerializedName("vacation_mode")
    boolean vacationMode;
    //write only fields for registration

    Gender gender;

    @SerializedName("date_joined")
    Date dateJoined;

    @SerializedName("fb_access_token")
    String fbAccessToken;

    @SerializedName("fb_user_id")
    String fbUserId;

    @SerializedName("fb_total_friends")
    int fbTotalFriends;

    @SerializedName("auth_token")
    OAuthToken authToken;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName(){
        String fullName = firstName;
        if(lastName != null && lastName.length() > 0){
            fullName += " " + lastName;
        }
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRegistrationConfirmed() {
        return registrationConfirmed;
    }

    public void setRegistrationConfirmed(boolean registrationConfirmed) {
        this.registrationConfirmed = registrationConfirmed;
    }

    public boolean getAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isPhoneConfirmed() {
        return phoneConfirmed;
    }

    public void setPhoneConfirmed(boolean phoneConfirmed) {
        this.phoneConfirmed = phoneConfirmed;
    }

    public Institution getCompany() {
        return company;
    }

    public void setCompany(Institution company) {
        this.company = company;
    }

    public String getWorkSpecifiedName() {
        return workSpecifiedName;
    }

    public void setWorkSpecifiedName(String workSpecifiedName) {
        this.workSpecifiedName = workSpecifiedName;
    }

    public boolean isWorkConfirmed() {
        return workConfirmed;
    }

    public void setWorkConfirmed(boolean workConfirmed) {
        this.workConfirmed = workConfirmed;
    }

    public Institution getSchool() {
        return school;
    }

    public void setSchool(Institution school) {
        this.school = school;
    }

    public String getSchoolSpecifiedName() {
        return schoolSpecifiedName;
    }

    public void setSchoolSpecifiedName(String schoolSpecifiedName) {
        this.schoolSpecifiedName = schoolSpecifiedName;
    }

    public boolean isSchoolConfirmed() {
        return schoolConfirmed;
    }

    public void setSchoolConfirmed(boolean schoolConfirmed) {
        this.schoolConfirmed = schoolConfirmed;
    }

    public int getRidesCompleted() {
        return ridesCompleted;
    }

    public void setRidesCompleted(int ridesCompleted) {
        this.ridesCompleted = ridesCompleted;
    }

    public double getStarGrade() {
        return starGrade;
    }

    public void setStarGrade(double starGrade) {
        this.starGrade = starGrade;
    }

    public boolean isVacationMode() {
        return vacationMode;
    }

    public void setVacationMode(boolean vacationMode) {
        this.vacationMode = vacationMode;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getFbAccessToken() {
        return fbAccessToken;
    }

    public void setFbAccessToken(String fbAccessToken) {
        this.fbAccessToken = fbAccessToken;
    }

    public String getFbUserId() {
        return fbUserId;
    }

    public void setFbUserId(String fbUserId) {
        this.fbUserId = fbUserId;
    }

    public int getFbTotalFriends() {
        return fbTotalFriends;
    }

    public void setFbTotalFriends(int fbTotalFriends) {
        this.fbTotalFriends = fbTotalFriends;
    }

    public OAuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(OAuthToken authToken) {
        this.authToken = authToken;
    }

    public String getProfilePhotoOriginal() {
        return profilePhotoOriginal;
    }

    public void setProfilePhotoOriginal(String profilePhotoOriginal) {
        this.profilePhotoOriginal = profilePhotoOriginal;
    }

    public RiderMode getRiderMode() {
        return riderMode;
    }

    public void setRiderMode(RiderMode riderMode) {
        this.riderMode = riderMode;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean hasPhoto(){
        return profilePhotoOriginal != null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EntityStatus getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(EntityStatus workStatus) {
        this.workStatus = workStatus;
    }

    public EntityStatus getSchoolStatus() {
        return schoolStatus;
    }

    public void setSchoolStatus(EntityStatus schoolStatus) {
        this.schoolStatus = schoolStatus;
    }

    public static class OAuthToken implements Serializable{

        @SerializedName("expires_in")
        int expiresIn;

        @SerializedName("access_token")
        String accessToken;

        @SerializedName("refresh_token")
        String refreshToken;

        @SerializedName("scope")
        String scope;

        @SerializedName("token_type")
        String tokenType;

        public int getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }
    }


}

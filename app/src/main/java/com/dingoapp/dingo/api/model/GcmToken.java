package com.dingoapp.dingo.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by guestguest on 07/03/16.
 */
public class GcmToken {

    @SerializedName("installation_uuid")
    String installationUuid;

    @SerializedName("gcm_token")
    String gcmToken;

    public String getInstallationUuid() {
        return installationUuid;
    }

    public void setInstallationUuid(String installationUuid) {
        this.installationUuid = installationUuid;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }
}
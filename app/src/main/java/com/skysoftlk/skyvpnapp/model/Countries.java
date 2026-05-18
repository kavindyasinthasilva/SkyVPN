package com.skysoftlk.skyvpnapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Countries implements Parcelable {
    private String country;
    private String flagUrl;
    private String ovpn;
    private String ovpnUserName;
    private String ovpnUserPassword;
    private int ping = 0;

    public Countries() {
    }

    public Countries(String country, String flagUrl, String ovpn) {
        this.country = country;
        this.flagUrl = flagUrl;
        this.ovpn = ovpn;
    }

    public Countries(String country, String flagUrl, String ovpn, String ovpnUserName, String ovpnUserPassword) {
        this.country = country;
        this.flagUrl = flagUrl;
        this.ovpn = ovpn;
        this.ovpnUserName = ovpnUserName;
        this.ovpnUserPassword = ovpnUserPassword;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    public String getOvpn() {
        return ovpn;
    }

    public void setOvpn(String ovpn) {
        this.ovpn = ovpn;
    }

    public String getOvpnUserName() {
        return ovpnUserName;
    }

    public void setOvpnUserName(String ovpnUserName) {
        this.ovpnUserName = ovpnUserName;
    }

    public String getOvpnUserPassword() {
        return ovpnUserPassword;
    }

    public void setOvpnUserPassword(String ovpnUserPassword) {
        this.ovpnUserPassword = ovpnUserPassword;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public static final Creator<Countries> CREATOR
            = new Creator<Countries>() {
        public Countries createFromParcel(Parcel in) {
            return new Countries(in);
        }

        public Countries[] newArray(int size) {
            return new Countries[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(country);
        dest.writeString(flagUrl);
        dest.writeString(ovpn);
        dest.writeString(ovpnUserName);
        dest.writeString(ovpnUserPassword);
        dest.writeInt(ping);
    }

    private Countries(Parcel in ) {
        country = in.readString();
        flagUrl = in.readString();
        ovpn = in.readString();
        ovpnUserName = in.readString();
        ovpnUserPassword = in.readString();
        ping = in.readInt();
    }

    public String getServerHost() {
        if (ovpn == null || ovpn.isEmpty()) return null;
        String[] lines = ovpn.split("\\\\n|\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("remote ")) {
                String[] parts = trimmed.split("\\s+");
                for (String part : parts) {
                    if (part.matches("^(\\d{1,3}\\.){3}\\d{1,3}$") || part.contains(".")) {
                        if (!part.equals("remote") && !part.equals("tcp") && !part.equals("udp")) {
                            return part;
                        }
                    }
                }
            }
        }
        return null;
    }
}

package com.jzj.weatherlearn.model.gson_city;

import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("location")
    public LocationInfo location;

    @SerializedName("formatted_address")
    public String address;

    public String business;

    public AddressComponent addressComponent;

    public Integer cityCode;

}

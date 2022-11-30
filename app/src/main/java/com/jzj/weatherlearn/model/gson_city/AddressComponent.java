package com.jzj.weatherlearn.model.gson_city;

import com.google.gson.annotations.SerializedName;

public class AddressComponent {

    public String city;
    public String direction;
    public String distance;
    public String district;
    public String province;
    public String street;
    @SerializedName("street_number")
    public String streetNumber;

}

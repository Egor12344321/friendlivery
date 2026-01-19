package com.globallogix.delivery.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.globallogix.delivery.entity.UserLocations;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpApiResponse {

    private String status;
    private String message;

    private String continent;
    private String continentCode;

    private String country;
    private String countryCode;

    private String region;
    private String regionName;

    private String city;
    private String district;
    private String zip;

    private Double lat;
    private Double lon;

    private String timezone;
    private Integer offset;
    private String currency;

    private String ip;
    private String org;
    private String as;
    private String asname;

    private String reverse;

    @JsonProperty("mobile")
    private Boolean isMobile;

    @JsonProperty("proxy")
    private Boolean isProxy;

    @JsonProperty("hosting")
    private Boolean isHosting;

    private String query;

    public boolean isSuccess() {
        return "success".equals(status);
    }

    public static UserLocations mapFromResponseToEntity(IpApiResponse response){
        return UserLocations.builder()
                .city(response.city)
                .country(response.country)
                .ip(response.ip)
                .build();
    }


}
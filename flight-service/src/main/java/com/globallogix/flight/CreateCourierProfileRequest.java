package com.globallogix.flight;

import lombok.Data;

@Data
public class CreateCourierProfileRequest {
    private Double maxWeight;
    private String preferredAirlines;
    private boolean notificationEnabled;
    private String contactPreference;

}

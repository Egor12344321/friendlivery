package com.globallogix.delivery.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_locations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class UserLocations{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip")
    private String ip;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

}

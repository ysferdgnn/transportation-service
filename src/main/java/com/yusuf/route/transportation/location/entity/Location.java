package com.yusuf.route.transportation.location.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locations", indexes = {
        @Index(name = "idx_location_code", columnList = "locationCode", unique = true)
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String locationCode;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 120)
    private String city;

    @Column(nullable = false, length = 120)
    private String country;
}

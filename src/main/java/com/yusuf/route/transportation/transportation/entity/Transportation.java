package com.yusuf.route.transportation.transportation.entity;


import com.yusuf.route.transportation.location.entity.Location;
import com.yusuf.route.transportation.transportation.enums.TransportationType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "transportations",
        indexes = {
                @Index(name = "ix_transportations_origin", columnList = "origin_location_id"),
                @Index(name = "ix_transportations_destination", columnList = "destination_location_id"),
                @Index(name = "ix_transportations_type", columnList = "transportation_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transportation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "origin_location_id", nullable = false)
    private Location origin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_location_id", nullable = false)
    private Location destination;

    @Enumerated(EnumType.STRING)
    @Column(name = "transportation_type", nullable = false, length = 20)
    private TransportationType type;


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "transportation_operating_days",
            joinColumns = @JoinColumn(name = "transportation_id", nullable = false)
    )
    @Column(name = "day_of_week", nullable = false)
    private Set<Integer> operatingDays = new HashSet<>();
}

package com.yusuf.route.transportation;

import com.yusuf.route.transportation.location.entity.Location;
import com.yusuf.route.transportation.location.enums.LocationType;
import com.yusuf.route.transportation.transportation.entity.Transportation;
import com.yusuf.route.transportation.transportation.enums.TransportationType;

import java.util.Set;

/**
 * Test data factory for Location and Transportation entities (e.g. route finder tests).
 */
public final class TestDataHelper {

    public static Location location(Long id, String code, LocationType type) {
        Location loc = new Location();
        loc.setId(id);
        loc.setLocationCode(code);
        loc.setName(code + " Name");
        loc.setCity("City");
        loc.setCountry("Country");
        loc.setType(type);
        return loc;
    }

    public static Transportation transportation(Long id, Location origin, Location destination, TransportationType type) {
        Transportation t = new Transportation();
        t.setId(id);
        t.setOrigin(origin);
        t.setDestination(destination);
        t.setType(type);
        t.setOperatingDays(Set.of(1, 2, 3, 4, 5, 6, 7));
        return t;
    }
}

package com.yusuf.route.transportation.location.service;

import com.yusuf.route.transportation.common.exception.IATAFormatException;
import com.yusuf.route.transportation.location.dto.LocationCreateRequest;
import com.yusuf.route.transportation.location.dto.LocationResponse;
import com.yusuf.route.transportation.location.entity.Location;
import com.yusuf.route.transportation.location.enums.LocationType;
import com.yusuf.route.transportation.location.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    @Test
    void create_throwsWhenLocationCodeAlreadyExists() {
        LocationCreateRequest req = new LocationCreateRequest("OTH", "Other", "City", "Country", LocationType.OTHER);
        when(locationRepository.existsByLocationCode("OTH")).thenReturn(true);

        assertThatThrownBy(() -> locationService.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("locationCode already exists");

        verify(locationRepository).existsByLocationCode("OTH");
    }

    @Test
    void create_throwsIATAFormatExceptionWhenAirportWithInvalidCode() {
        LocationCreateRequest req = new LocationCreateRequest("IN", "Airport", "City", "Country", LocationType.AIRPORT);
        when(locationRepository.existsByLocationCode("IN")).thenReturn(false);

        assertThatThrownBy(() -> locationService.create(req))
                .isInstanceOf(IATAFormatException.class);
    }

    @Test
    void create_savesAndReturnsResponseForValidOtherType() {
        LocationCreateRequest req = new LocationCreateRequest("OTH", "Other Place", "Istanbul", "Turkey", LocationType.OTHER);
        when(locationRepository.existsByLocationCode("OTH")).thenReturn(false);
        Location saved = new Location();
        saved.setId(1L);
        saved.setLocationCode("OTH");
        saved.setName("Other Place");
        saved.setCity("Istanbul");
        saved.setCountry("Turkey");
        saved.setType(LocationType.OTHER);
        when(locationRepository.save(any(Location.class))).thenReturn(saved);

        LocationResponse response = locationService.create(req);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.locationCode()).isEqualTo("OTH");
        assertThat(response.name()).isEqualTo("Other Place");
        assertThat(response.type()).isEqualTo(LocationType.OTHER);
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void create_savesAndReturnsResponseForValidAirportWithIATACode() {
        LocationCreateRequest req = new LocationCreateRequest("IST", "Istanbul Airport", "Istanbul", "Turkey", LocationType.AIRPORT);
        when(locationRepository.existsByLocationCode("IST")).thenReturn(false);
        Location saved = new Location();
        saved.setId(1L);
        saved.setLocationCode("IST");
        saved.setName("Istanbul Airport");
        saved.setCity("Istanbul");
        saved.setCountry("Turkey");
        saved.setType(LocationType.AIRPORT);
        when(locationRepository.save(any(Location.class))).thenReturn(saved);

        LocationResponse response = locationService.create(req);

        assertThat(response.locationCode()).isEqualTo("IST");
        assertThat(response.type()).isEqualTo(LocationType.AIRPORT);
    }

    @Test
    void list_returnsAllLocations() {
        Location loc = new Location();
        loc.setId(1L);
        loc.setLocationCode("IST");
        loc.setName("Istanbul");
        loc.setCity("Istanbul");
        loc.setCountry("Turkey");
        loc.setType(LocationType.AIRPORT);
        when(locationRepository.findAll()).thenReturn(List.of(loc));

        List<LocationResponse> result = locationService.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).locationCode()).isEqualTo("IST");
    }

    @Test
    void get_throwsWhenNotFound() {
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.get(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("location not found");
    }

    @Test
    void get_returnsLocationWhenFound() {
        Location loc = new Location();
        loc.setId(1L);
        loc.setLocationCode("IST");
        loc.setName("Istanbul");
        loc.setCity("Istanbul");
        loc.setCountry("Turkey");
        loc.setType(LocationType.AIRPORT);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(loc));

        LocationResponse response = locationService.get(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.locationCode()).isEqualTo("IST");
    }

    @Test
    void delete_callsRepositoryDelete() {
        locationService.delete(1L);
        verify(locationRepository).deleteById(1L);
    }
}

package com.yusuf.route.transportation.transportation.service;

import com.yusuf.route.transportation.common.exception.FlightRequiresAirportException;
import com.yusuf.route.transportation.common.exception.LocationNotFoundException;
import com.yusuf.route.transportation.common.exception.OperationDaysRangeException;
import com.yusuf.route.transportation.location.entity.Location;
import com.yusuf.route.transportation.location.enums.LocationType;
import com.yusuf.route.transportation.location.repository.LocationRepository;
import com.yusuf.route.transportation.transportation.dto.TransportationCreateRequest;
import com.yusuf.route.transportation.transportation.dto.TransportationResponse;
import com.yusuf.route.transportation.transportation.entity.Transportation;
import com.yusuf.route.transportation.transportation.enums.TransportationType;
import com.yusuf.route.transportation.transportation.repository.TransportationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransportationServiceTest {

    @Mock
    private TransportationRepository transportationRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private TransportationService transportationService;

    private static Location airport(String code) {
        Location loc = new Location();
        loc.setId(1L);
        loc.setLocationCode(code);
        loc.setName(code);
        loc.setCity("City");
        loc.setCountry("Country");
        loc.setType(LocationType.AIRPORT);
        return loc;
    }

    private static Location other(String code) {
        Location loc = new Location();
        loc.setId(2L);
        loc.setLocationCode(code);
        loc.setName(code);
        loc.setCity("City");
        loc.setCountry("Country");
        loc.setType(LocationType.OTHER);
        return loc;
    }

    @Test
    void create_throwsLocationNotFoundWhenOriginMissing() {
        TransportationCreateRequest req = new TransportationCreateRequest(
                "ORIGIN", "ADB", TransportationType.FLIGHT, Set.of(1, 2, 3));
        when(locationRepository.findByLocationCode("ORIGIN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transportationService.create(req))
                .isInstanceOf(LocationNotFoundException.class);
    }

    @Test
    void create_throwsLocationNotFoundWhenDestinationMissing() {
        TransportationCreateRequest req = new TransportationCreateRequest(
                "IST", "DEST", TransportationType.FLIGHT, Set.of(1, 2, 3));
        when(locationRepository.findByLocationCode("IST")).thenReturn(Optional.of(airport("IST")));
        when(locationRepository.findByLocationCode("DEST")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transportationService.create(req))
                .isInstanceOf(LocationNotFoundException.class);
    }

    @Test
    void create_throwsFlightRequiresAirportWhenOriginNotAirport() {
        TransportationCreateRequest req = new TransportationCreateRequest(
                "OTH", "IST", TransportationType.FLIGHT, Set.of(1, 2, 3));
        when(locationRepository.findByLocationCode("OTH")).thenReturn(Optional.of(other("OTH")));
        when(locationRepository.findByLocationCode("IST")).thenReturn(Optional.of(airport("IST")));

        assertThatThrownBy(() -> transportationService.create(req))
                .isInstanceOf(FlightRequiresAirportException.class);
    }

    @Test
    void create_throwsFlightRequiresAirportWhenDestinationNotAirport() {
        TransportationCreateRequest req = new TransportationCreateRequest(
                "IST", "OTH", TransportationType.FLIGHT, Set.of(1, 2, 3));
        when(locationRepository.findByLocationCode("IST")).thenReturn(Optional.of(airport("IST")));
        when(locationRepository.findByLocationCode("OTH")).thenReturn(Optional.of(other("OTH")));

        assertThatThrownBy(() -> transportationService.create(req))
                .isInstanceOf(FlightRequiresAirportException.class);
    }

    @Test
    void create_throwsOperationDaysRangeWhenDayOutOfRange() {
        TransportationCreateRequest req = new TransportationCreateRequest(
                "IST", "ADB", TransportationType.FLIGHT, Set.of(1, 8));
        when(locationRepository.findByLocationCode("IST")).thenReturn(Optional.of(airport("IST")));
        when(locationRepository.findByLocationCode("ADB")).thenReturn(Optional.of(airport("ADB")));

        assertThatThrownBy(() -> transportationService.create(req))
                .isInstanceOf(OperationDaysRangeException.class);
    }

    @Test
    void create_savesAndReturnsResponseForValidFlightBetweenAirports() {
        TransportationCreateRequest req = new TransportationCreateRequest(
                "IST", "ADB", TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5));
        Location ist = airport("IST");
        Location adb = airport("ADB");
        when(locationRepository.findByLocationCode("IST")).thenReturn(Optional.of(ist));
        when(locationRepository.findByLocationCode("ADB")).thenReturn(Optional.of(adb));
        Transportation saved = new Transportation();
        saved.setId(100L);
        saved.setOrigin(ist);
        saved.setDestination(adb);
        saved.setType(TransportationType.FLIGHT);
        saved.setOperatingDays(Set.of(1, 2, 3, 4, 5));
        when(transportationRepository.save(any(Transportation.class))).thenReturn(saved);

        TransportationResponse response = transportationService.create(req);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.originCode()).isEqualTo("IST");
        assertThat(response.destinationCode()).isEqualTo("ADB");
        assertThat(response.type()).isEqualTo(TransportationType.FLIGHT);
        assertThat(response.operatingDays()).containsExactlyInAnyOrder(1, 2, 3, 4, 5);

        ArgumentCaptor<Transportation> captor = ArgumentCaptor.forClass(Transportation.class);
        verify(transportationRepository).save(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo(TransportationType.FLIGHT);
    }

    @Test
    void create_allowsNonFlightBetweenAnyLocations() {
        TransportationCreateRequest req = new TransportationCreateRequest(
                "A", "B", TransportationType.BUS, Set.of(1));
        when(locationRepository.findByLocationCode("A")).thenReturn(Optional.of(other("A")));
        when(locationRepository.findByLocationCode("B")).thenReturn(Optional.of(other("B")));
        Transportation saved = new Transportation();
        saved.setId(1L);
        saved.setOrigin(other("A"));
        saved.setDestination(other("B"));
        saved.setType(TransportationType.BUS);
        saved.setOperatingDays(Set.of(1));
        when(transportationRepository.save(any(Transportation.class))).thenReturn(saved);

        TransportationResponse response = transportationService.create(req);

        assertThat(response.type()).isEqualTo(TransportationType.BUS);
    }

    @Test
    void get_throwsWhenNotFound() {
        when(transportationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transportationService.get(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transportation not found");
    }

    @Test
    void delete_callsRepositoryDelete() {
        transportationService.delete(1L);
        verify(transportationRepository).deleteById(1L);
    }
}

package com.example.tsp_rest_api;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.databaseCore.Entities.Maps.*;
import com.example.databaseCore.Entities.User.User;
import com.example.databaseCore.Pojos.Maps.Req.Coordinates_Req;
import com.example.databaseCore.Pojos.Maps.Req.SavedTrips.SavedTripReq;
import com.example.databaseCore.Pojos.Maps.Req.SavedTrips.TripReq;
import com.example.databaseCore.Pojos.Maps.Res.SummaryOfTrips;
import com.example.databaseCore.Repositories.Maps.CoordinatesRepository;
import com.example.databaseCore.Repositories.Maps.TripsRepository;
import com.example.databaseCore.Repositories.User.UserRepository;
import com.example.tsp_rest_api.Controller.MainController;
import com.example.tsp_rest_api.Service.TspRestApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class MapsTests {

    @Mock
    private CoordinatesRepository coordinatesRepository;

    @Mock
    private TripsRepository tripsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TspRestApiService tspRestApiService;


//    Integracyjne

    @Test
    void UpdateAndThenGetClickedCoordinates() {
        Long user_id = 1L;
        Coordinates_Req coordinates_req = new Coordinates_Req();
        coordinates_req.setLatitude(50.0F);
        coordinates_req.setLongitude(19.0F);
        User user = new User();
        user.setId(user_id);
        user.setCoordinates(new ArrayList<>());
        user.setVerifyClickedCoordinates(null);
        when(userRepository.findById(user_id)).thenReturn(Optional.of(user));
        ResponseEntity<VerifyClickedCoordinates> updateResponse = tspRestApiService.updateClickedCoordinates(user_id, coordinates_req);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(user.getVerifyClickedCoordinates()).isNotNull();
        assertThat(user.getVerifyClickedCoordinates().getLatitude()).isEqualTo(coordinates_req.getLatitude());
        assertThat(user.getVerifyClickedCoordinates().getLongitude()).isEqualTo(coordinates_req.getLongitude());
        when(userRepository.findById(user_id)).thenReturn(Optional.of(user));
        ResponseEntity<List<Coordinates>> getResponse = tspRestApiService.getClickedCoordinates(user_id, coordinates_req);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).hasSize(1);
        Coordinates saved = getResponse.getBody().getFirst();
        assertThat(saved.getLatitude()).isEqualTo(coordinates_req.getLatitude());
        assertThat(saved.getLongitude()).isEqualTo(coordinates_req.getLongitude());
        verify(userRepository, times(2)).findById(user_id);
        verify(userRepository, times(2)).save(any(User.class));
    }

//    Jednostkowe

    @Test
    public void shouldDeleteCoordinatesAndReturnUpdatedList() {

        Long userId = 1L;
        Coordinates coord1 = new Coordinates();
        coord1.setId(1L);
        coord1.setLatitude(50.0F);
        coord1.setLongitude(20.0F);

        Coordinates coord2 = new Coordinates();
        coord2.setId(2L);
        coord2.setLatitude(51.0F);
        coord2.setLongitude(21.0F);

        User user = new User();
        user.setId(userId);
        user.setCoordinates(new ArrayList<>(List.of(coord1, coord2)));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<List<Coordinates>> response = tspRestApiService.deleteCoordinates(userId, List.of(1L));

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().getId()).isEqualTo(2L);

        verify(userRepository).save(user);
    }

    @Test
    void shouldReturnCoordinatesList_WhenPresent() {
        Long userId = 1L;

        Coordinates coord1 = new Coordinates();
        coord1.setId(1L);
        coord1.setLatitude(50.0F);
        coord1.setLongitude(20.0F);

        Coordinates coord2 = new Coordinates();
        coord2.setId(2L);
        coord2.setLatitude(51.0F);
        coord2.setLongitude(21.0F);

        List<Coordinates> coordinates = List.of(coord1, coord2);
        Optional<List<Coordinates>> optionalCoordinates = Optional.of(coordinates);

        when(coordinatesRepository.findByUserId(userId)).thenReturn(optionalCoordinates);

        ResponseEntity<List<Coordinates>> response = tspRestApiService.getAllCoordinates(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(coordinates);
    }

    @Test
    void shouldReturnNotFound_WhenCoordinatesListIsEmpty() {
        Long userId = 2L;

        Optional<List<Coordinates>> emptyOptional = Optional.empty();

        when(coordinatesRepository.findByUserId(userId)).thenReturn(emptyOptional);

        ResponseEntity<List<Coordinates>> response = tspRestApiService.getAllCoordinates(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }


//    Trip wszystkie testy

    //    Add trip

    @Test
    void shouldReturnNotFound_WhenUserDoesNotExist() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        SavedTripReq savedTripReq = mock(SavedTripReq.class);
        ResponseEntity<Trips> response = tspRestApiService.addTrip(userId, savedTripReq);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnConflict_WhenStartDateIsInvalid() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        SavedTripReq savedTripReq = mock(SavedTripReq.class);
        when(savedTripReq.getStartDate()).thenReturn(LocalDate.now().minusDays(1));

        ResponseEntity<Trips> response = tspRestApiService.addTrip(userId, savedTripReq);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldReturnNotAcceptable_WhenTripCoordinatesConflict() {
        Long userId = 1L;
        User user = new User();
        Trips existingTrip = new Trips();
        IsFinished isFinished = new IsFinished();
        isFinished.setFinished(false);
        existingTrip.setIsFinished(isFinished);
        CoordinatesForTrips coord = new CoordinatesForTrips();
        coord.setLatitude(10.0F);
        coord.setLongitude(20.0F);
        existingTrip.addCoordinatesForTrips(coord);
        user.addTrips(existingTrip);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        SavedTripReq savedTripReq = mock(SavedTripReq.class);
        TripReq tripReq = mock(TripReq.class);
        Coordinates_Req coordinatesReq = new Coordinates_Req();
        coordinatesReq.setLatitude(10.0F);
        coordinatesReq.setLongitude(20.0F);
        List<Coordinates_Req> coordsList = List.of(coordinatesReq);
        when(savedTripReq.getStartDate()).thenReturn(LocalDate.now().plusDays(1));
        when(savedTripReq.getTrip()).thenReturn(tripReq);
        when(tripReq.getCoordinates()).thenReturn(coordsList);
        when(tripReq.getDuration()).thenReturn(10000L);
        when(tripReq.getDistance()).thenReturn(1000L);
        ResponseEntity<Trips> response = tspRestApiService.addTrip(userId, savedTripReq);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    void shouldAddTripSuccessfully() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        SavedTripReq savedTripReq = mock(SavedTripReq.class);
        TripReq tripReq = mock(TripReq.class);
        Coordinates_Req coordinatesReq = new Coordinates_Req();
        coordinatesReq.setLatitude(10.0F);
        coordinatesReq.setLongitude(20.0F);
        List<Coordinates_Req> coordsList = List.of(coordinatesReq);
        long duration = 10L * 3600 * 1000L;
        long drivingLimitPerDayInSeconds = 9 * 3600 * 1000;
        when(savedTripReq.getStartDate()).thenReturn(LocalDate.now().plusDays(1));
        when(savedTripReq.getTrip()).thenReturn(tripReq);
        when(tripReq.getCoordinates()).thenReturn(coordsList);
        when(tripReq.getDuration()).thenReturn(duration);
        when(tripReq.getDistance()).thenReturn(1000L);
        ResponseEntity<Trips> response = tspRestApiService.addTrip(userId, savedTripReq);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(user.getTrips()).contains(response.getBody());
        Long expectedDays = duration / drivingLimitPerDayInSeconds;
        Long actualDays = tspRestApiService.calculateEndDate(duration);
        assertThat(actualDays).isEqualTo(expectedDays);
        Trips createdTrip = response.getBody();
        LocalDate expectedEndDate = savedTripReq.getStartDate().plusDays(expectedDays);
        assertThat(createdTrip.getEndDate()).isEqualTo(expectedEndDate);
    }


    //    Get unfinished trips

    @Test
    void shouldReturnOkWithTripsWhenTripsFound() {
        Long userId = 1L;
        List<Trips> trips = List.of(new Trips(), new Trips());

        when(tripsRepository.getAllByFinishedCondition(userId, false)).thenReturn(Optional.of(trips));

        ResponseEntity<List<Trips>> response = tspRestApiService.getAllUnfinishedTrips(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(trips);
    }

    @Test
    void shouldReturnNotFoundWhenNoTripsFound() {
        Long userId = 1L;

        when(tripsRepository.getAllByFinishedCondition(userId, false)).thenReturn(Optional.empty());

        ResponseEntity<List<Trips>> response = tspRestApiService.getAllUnfinishedTrips(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    //    Delete trips

    @Test
    void shouldDeleteTripAndReturnUpdatedTrips_realUser() {
        Long userId = 1L;
        Long tripId = 10L;


        User user = new User();
        Trips trip1 = new Trips();
        trip1.setId(10L);
        Trips trip2 = new Trips();
        trip2.setId(20L);
        user.setTrips(new ArrayList<>(List.of(trip1, trip2)));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));


        ResponseEntity<List<Trips>> response = tspRestApiService.deleteTrip(userId, tripId);

        verify(userRepository).save(user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).doesNotContain(trip1);
        assertThat(user.getTrips()).doesNotContain(trip1);
    }

    @Test
    void shouldReturnNotFoundWhenUserNotFound() {
        Long userId = 1L;
        Long tripId = 10L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<List<Trips>> response = tspRestApiService.deleteTrip(userId, tripId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    //    Start/Stop driving and measuring time

    @Test
    void shouldStartAndStopDrivingAndUpdateTimeCorrectly() {
        Long userId = 1L;
        UUID mapName = UUID.randomUUID();
        MeasuringTime measuringTime = new MeasuringTime();
        measuringTime.setDriving(false);
        measuringTime.setTotal(1000L);
        Trips trip = new Trips();
        trip.setMeasuringTime(measuringTime);
        when(tripsRepository.findByNameAndUserId(mapName, userId)).thenReturn(Optional.of(trip));
        when(tripsRepository.save(any())).thenReturn(trip);
        ResponseEntity<Boolean> startResponse = tspRestApiService.startDriving(userId, mapName);
        assertThat(startResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(startResponse.getBody()).isTrue();
        assertThat(trip.getMeasuringTime().getTotal()).isEqualTo(1000L);
        trip.getMeasuringTime().setChangingStatus(LocalDateTime.now().minusSeconds(2));
        ResponseEntity<Boolean> stopResponse = tspRestApiService.startDriving(userId, mapName);
        assertThat(stopResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(stopResponse.getBody()).isFalse();
        long updatedTotal = trip.getMeasuringTime().getTotal();
        assertThat(updatedTotal).isGreaterThanOrEqualTo(2950L);
        assertThat(updatedTotal).isLessThanOrEqualTo(3100L);
        ResponseEntity<Boolean> statusAfterStart = tspRestApiService.measuringTimeStatus(userId, mapName);
        assertThat(statusAfterStart.getBody()).isFalse();
        verify(tripsRepository, times(2)).save(trip);
    }

    //    Finish trips

    @Test
    void shouldFinishTripWhenDrivingIsFalseAndReturnUnfinishedTrips() {
        Long userId = 1L;
        Long tripId = 100L;
        MeasuringTime measuringTime = new MeasuringTime();
        measuringTime.setDriving(false);
        IsFinished isFinished = new IsFinished();
        User user = new User();
        user.setId(userId);
        Trips trip = new Trips();
        trip.setId(tripId);
        trip.setUser(user);
        trip.setMeasuringTime(measuringTime);
        trip.setIsFinished(isFinished);
        List<Trips> unfinishedTrips = List.of(new Trips(), new Trips());
        when(tripsRepository.findByIdAndUserId(tripId, userId)).thenReturn(Optional.of(trip));
        when(tripsRepository.getAllByFinishedCondition(userId, false)).thenReturn(Optional.of(unfinishedTrips));
        when(tripsRepository.save(any(Trips.class))).thenReturn(trip);
        ResponseEntity<List<Trips>> response = tspRestApiService.finishTrip(userId, tripId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(unfinishedTrips);
        assertThat(trip.getIsFinished().getFinished()).isTrue();
        assertThat(trip.getIsFinished().getFinishedAt()).isEqualTo(LocalDate.now());
        verify(tripsRepository).save(trip);
        verify(tripsRepository).getAllByFinishedCondition(userId, false);
    }

    @Test
    void shouldReturnNotAcceptableWhenDrivingIsTrue() {
        Long userId = 1L;
        Long tripId = 100L;

        MeasuringTime measuringTime = new MeasuringTime();
        measuringTime.setDriving(true);

        IsFinished isFinished = new IsFinished();
        User user = new User();
        user.setId(userId);

        Trips trip = new Trips();
        trip.setId(tripId);
        trip.setUser(user);
        trip.setMeasuringTime(measuringTime);
        trip.setIsFinished(isFinished);

        when(tripsRepository.findByIdAndUserId(tripId, userId)).thenReturn(Optional.of(trip));

        ResponseEntity<List<Trips>> response = tspRestApiService.finishTrip(userId, tripId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        verify(tripsRepository, never()).save(any());
    }

    //    Get all finished trips

    @Test
    void shouldReturnListOfFinishedTrips() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        LocalDate startDate = LocalDate.of(2024, 8, 1);
        LocalDate endDate = LocalDate.of(2024, 8, 1);
        LocalDate finishedAt = LocalDate.of(2024, 8, 1);
        Trips finishedTrip = new Trips();
        finishedTrip.setStartDate(startDate);
        finishedTrip.setEndDate(endDate);
        finishedTrip.setDistance(1000L);
        finishedTrip.setDuration(1000L);
        IsFinished isFinished = new IsFinished();
        isFinished.setFinishedAt(finishedAt);
        finishedTrip.setIsFinished(isFinished);
        MeasuringTime measuringTime = new MeasuringTime();
        measuringTime.setTotal(500L);
        finishedTrip.setMeasuringTime(measuringTime);
        List<Trips> trips = List.of(finishedTrip);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(tripsRepository.getAllByFinishedCondition(userId, true)).thenReturn(Optional.of(trips));
        ResponseEntity<List<SummaryOfTrips>> response = tspRestApiService.getAllFinishedTrips(userId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        SummaryOfTrips result = response.getBody().getFirst();
        assertThat(result.getStartDateOfTrip()).isEqualTo(startDate);
        assertThat(result.getEndDateOfTrip()).isEqualTo(endDate);
        assertThat(result.getRealisedEndDateOfTrip()).isEqualTo(finishedAt);
        assertThat(result.getDistance()).isEqualTo(1000L);
        assertThat(result.getDuration()).isEqualTo(1000L);
        assertThat(result.getRealisedDuration()).isEqualTo(500L);
    }



}

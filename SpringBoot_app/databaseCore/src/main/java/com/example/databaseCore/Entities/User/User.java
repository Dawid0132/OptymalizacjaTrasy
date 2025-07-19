package com.example.databaseCore.Entities.User;

import com.example.databaseCore.Entities.Maps.Coordinates;
import com.example.databaseCore.Entities.Maps.Trips;
import com.example.databaseCore.Entities.Maps.VerifyClickedCoordinates;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQuery(name = "User.findUserByEmail", query = "select u from User u where u.email=?1")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank
    @Size(min = 1, max = 50)
    private String firstname;

    @Column
    @NotBlank
    @Size(min = 1, max = 50)
    private String lastname;

    @Column(unique = true)
    @NotBlank
    @Size(min = 5, max = 50)
    @Email
    private String email;

    @Column
    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^(?=.*[A-Z].*[A-Z])(?=.*[!@#$&*])(?=.*[0-9].*[0-9])(?=.*[a-z].*[a-z].*[a-z]).{8,}$")
    private String password;

    @Column
    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @NotNull
    private LocalDateTime lastLogin = LocalDateTime.now();

    @Column
    @NotNull
    private Boolean isLoggedIn = Boolean.FALSE;

    @Column
    @NotNull
    private Boolean passwordChanged = Boolean.FALSE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Trips> trips = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Coordinates> coordinates = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private VerifyClickedCoordinates verifyClickedCoordinates;

    public User(Long id, String firstname, String lastname, String email, String password, LocalDateTime createdAt, LocalDateTime lastLogin, Boolean isLoggedIn, Boolean passwordChanged, List<Trips> trips, List<Coordinates> coordinates, VerifyClickedCoordinates verifyClickedCoordinates) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.isLoggedIn = isLoggedIn;
        this.passwordChanged = passwordChanged;
        this.trips = trips;
        this.coordinates = coordinates;
        this.verifyClickedCoordinates = verifyClickedCoordinates;
    }

    public User() {
    }

    public List<Trips> addTrips(Trips t) {
        t.setUser(this);
        trips.add(t);
        return trips;
    }

    public List<Coordinates> addCoordinates(Coordinates coordinates1) {
        coordinates1.setUser(this);
        coordinates.add(coordinates1);
        return coordinates;
    }

    public List<Coordinates> removeCoordinates(List<Long> ids) {
        for (Long i : ids) {
            coordinates.removeIf(coordinates1 -> coordinates1.getId().equals(i));
        }
        return coordinates;
    }

    public List<Trips> removeTrips(Long id) {
        trips.removeIf(trips1 -> trips1.getId().equals(id));
        return trips;
    }

    public List<Trips> updateTrips(Long id) {
        for (Trips t : trips) {
            if (t.getId().equals(id)) {
                t.setFinished(Boolean.TRUE);
            }
        }
        return trips;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public Boolean getPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(Boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

    public List<Trips> getTrips() {
        return trips;
    }

    public void setTrips(List<Trips> trips) {
        this.trips = trips;
    }

    public List<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }

    public VerifyClickedCoordinates getVerifyClickedCoordinates() {
        return verifyClickedCoordinates;
    }

    public void setVerifyClickedCoordinates(VerifyClickedCoordinates verifyClickedCoordinates) {
        this.verifyClickedCoordinates = verifyClickedCoordinates;
    }
}

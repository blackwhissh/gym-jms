package com.epam.hibernate.dto.trainer;

import com.epam.hibernate.entity.TrainingType;

public class TrainerListInfo {
    private String username;
    private String firstName;
    private String lastName;
    private TrainingType specialization;

    public TrainerListInfo(String username, String firstName,
                           String lastName, TrainingType specialization) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }
    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }
    @Override
    public String toString() {
        return  "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", specialization=" + specialization;
    }
}

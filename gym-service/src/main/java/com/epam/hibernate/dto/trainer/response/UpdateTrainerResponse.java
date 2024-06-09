package com.epam.hibernate.dto.trainer.response;

import com.epam.hibernate.dto.trainee.TraineeListInfo;
import com.epam.hibernate.entity.TrainingType;

import java.util.Set;

public class UpdateTrainerResponse {
    private String username;
    private String firstName;
    private String lastName;
    private TrainingType specialization;
    private Boolean isActive;
    private Set<TraineeListInfo> traineeList;

    public UpdateTrainerResponse(String username, String firstName, String lastName,
                                 TrainingType specialization, Boolean isActive, Set<TraineeListInfo> traineeList) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.isActive = isActive;
        this.traineeList = traineeList;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Set<TraineeListInfo> getTraineeList() {
        return traineeList;
    }

    public void setTraineeList(Set<TraineeListInfo> traineeList) {
        this.traineeList = traineeList;
    }

    @Override
    public String toString() {
        return  "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", specialization=" + specialization +
                ", isActive=" + isActive +
                ", traineeList=" + traineeList;
    }
}

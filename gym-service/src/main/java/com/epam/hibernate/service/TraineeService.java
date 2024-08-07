package com.epam.hibernate.service;

import com.epam.hibernate.dto.trainee.request.TraineeRegisterRequest;
import com.epam.hibernate.dto.trainee.request.TraineeTrainingsRequest;
import com.epam.hibernate.dto.trainee.request.UpdateTraineeRequest;
import com.epam.hibernate.dto.trainee.request.UpdateTrainersListRequest;
import com.epam.hibernate.dto.trainee.response.*;
import com.epam.hibernate.dto.trainer.TrainerListInfo;
import com.epam.hibernate.entity.*;
import com.epam.hibernate.exception.UserInactiveException;
import com.epam.hibernate.repository.TraineeRepository;
import com.epam.hibernate.repository.TrainerRepository;
import com.epam.hibernate.repository.UserJpaRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.epam.hibernate.Utils.generateUsername;

@Service
public class TraineeService {
    private final Timer timer;
    private final TraineeRepository traineeRepository;
    private final UserJpaRepository userRepository;
    private final TrainerRepository trainerRepository;

    @Autowired
    public TraineeService(MeterRegistry meterRegistry, TraineeRepository traineeRepository, UserJpaRepository userRepository, TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.timer = Timer.builder("trainee_register_timer").description("Times Registering Trainee").register(meterRegistry);
    }

    public ResponseEntity<TraineeRegisterResponse> createProfile(@NotNull TraineeRegisterRequest request) {
        User traineeUser = new User(request.getFirstName(), request.getLastName(), true, RoleEnum.TRAINEE);
        timer.record(() -> {
            if (userRepository.existsByUsername(generateUsername(traineeUser.getFirstName(), traineeUser.getLastName(), false))) {
                traineeUser.setUsername(generateUsername(traineeUser.getFirstName(), traineeUser.getLastName(), false));
            } else {
                traineeUser.setUsername(generateUsername(traineeUser.getFirstName(), traineeUser.getLastName(), true));
            }
            Trainee trainee = new Trainee(request.getDob(), request.getAddress(), traineeUser);
            traineeRepository.save(trainee);
        });

        return ResponseEntity.ok().body(new TraineeRegisterResponse(traineeUser.getUsername(), traineeUser.getTempPass()));
    }

    public ResponseEntity<TraineeProfileResponse> selectTraineeProfile(String username) {
        Trainee trainee = traineeRepository.selectByUsername(username);

        return ResponseEntity.ok().body(new TraineeProfileResponse(trainee.getUser().getFirstName(), trainee.getUser().getLastName(), trainee.getDob(), trainee.getAddress(), trainee.getUser().getActive(), trainee.getTrainers().stream().map(trainer -> new TrainerListInfo(trainer.getUser().getUsername(), trainer.getUser().getFirstName(), trainer.getUser().getLastName(), trainer.getSpecialization())).collect(Collectors.toSet())));
    }

    public ResponseEntity<UpdateTraineeResponse> updateTrainee(@NotNull String username, @NotNull UpdateTraineeRequest request) {
        Trainee trainee = traineeRepository.updateTrainee(username, request.getDob(), request.getAddress(), request.getFirstName(), request.getLastName(), request.getActive());

        return ResponseEntity.ok().body(new UpdateTraineeResponse(trainee.getUser().getUsername(), trainee.getUser().getFirstName(), trainee.getUser().getLastName(), trainee.getDob(), trainee.getAddress(), trainee.getUser().getActive(), trainee.getTrainers().stream().map(trainer -> new TrainerListInfo(trainer.getUser().getUsername(), trainer.getUser().getFirstName(), trainer.getUser().getLastName(), trainer.getSpecialization())).collect(Collectors.toSet())));
    }

    @Transactional
    public void deleteTrainee(@NotNull String username) {
        traineeRepository.deleteTrainee(username);
    }

    @Transactional
    public ResponseEntity<List<TraineeTrainingsResponse>> getTrainingList(@NotNull String username, @NotNull TraineeTrainingsRequest request) {
        List<Training> trainingList = traineeRepository.getTrainingList(username, request.getFrom(), request.getTo(), request.getTrainerName(), request.getTrainingType());
        return ResponseEntity.ok(trainingList.stream().map(training -> new TraineeTrainingsResponse(training.getTrainingName(), training.getTrainingDate(), training.getTrainingType(), training.getTrainingDuration(), training.getTrainer().getUser().getFirstName())).toList());
    }

    @Transactional
    public ResponseEntity<List<NotAssignedTrainer>> notAssignedTrainersList(@NotNull String username) {
        Trainee trainee = traineeRepository.selectByUsername(username);
        List<Trainer> allTrainers = trainerRepository.getAllTrainers();
        List<Trainer> notAssignedTrainers = new ArrayList<>();
        for (Trainer trainer : allTrainers) {
            if (!trainer.getTrainees().contains(trainee) && trainer.getUser().getActive()) {
                notAssignedTrainers.add(trainer);
            }
        }
        return ResponseEntity.ok().body(notAssignedTrainers.stream().map(trainer -> new NotAssignedTrainer(trainer.getUser().getUsername(), trainer.getUser().getFirstName(), trainer.getUser().getLastName(), trainer.getSpecialization())).toList());
    }

    public List<Trainer> assignedTrainersList(@NotNull String traineeUsername) {
        Trainee trainee = traineeRepository.selectByUsername(traineeUsername);
        List<Trainer> allTrainers = trainerRepository.getAllTrainers();
        List<Trainer> assignedTrainers = new ArrayList<>();
        for (Trainer trainer : allTrainers) {
            if (trainer.getTrainees().contains(trainee)) {
                assignedTrainers.add(trainer);
            }
        }
        return assignedTrainers;
    }

    @Transactional
    public ResponseEntity<List<TrainerListInfo>> updateTrainersList(String username, UpdateTrainersListRequest request) {
        Trainee trainee = traineeRepository.selectByUsername(username);

        Set<String> trainersSet = request.getTrainers();
        Set<Trainer> trainers = trainersSet.stream().map(trainerRepository::selectByUsername).collect(Collectors.toSet());
        trainers.forEach(trainer -> {
            if (!trainer.getUser().getActive()) throw new UserInactiveException("Trainer is inactive");
        });
        trainee.setTrainers(trainers);
        traineeRepository.save(trainee);

        return ResponseEntity.ok().body(trainers.stream().map(trainer -> new TrainerListInfo(trainer.getUser().getUsername(), trainer.getUser().getFirstName(), trainer.getUser().getLastName(), trainer.getSpecialization())).toList());
    }
}

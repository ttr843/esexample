package ru.itis.example.es.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.example.es.model.entity.Student;
import ru.itis.example.es.model.entity.Subject;
import ru.itis.example.es.repository.StudentRepository;
import ru.itis.example.es.repository.SubjectRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InsertDataService {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public void insertData() {
        log.info("start save content");
        Subject subject = Subject.builder()
                .title("Java")
                .build();
        Subject subject2 = Subject.builder()
                .title("Python")
                .build();
        Subject subject3 = Subject.builder()
                .title("JS")
                .build();
        subjectRepository.saveAll(Arrays.asList(subject, subject2, subject3));
        List<Subject> subjectList = subjectRepository.findAll();
        long m = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Student studentOne = Student.builder()
                    .firstName("user")
                    .gender("male")
                    .isWorking(true)
                    .subjects(Arrays.asList(subjectList.get(0), subjectList.get(1)))
                    .university("KFU")
                    .startStudyYear((int) (Math.random() * 18 + 2000))
                    .build();
            Student studentTwo = Student.builder()
                    .firstName("user")
                    .gender("male")
                    .isWorking(false)
                    .subjects(Arrays.asList(subjectList.get(1), subjectList.get(2)))
                    .university("ITMO")
                    .startStudyYear((int) (Math.random() * 18 + 2000))
                    .build();
            Student studentThree = Student.builder()
                    .firstName("user")
                    .gender("male")
                    .isWorking(true)
                    .subjects(Arrays.asList(subjectList.get(0), subjectList.get(2)))
                    .university("MGU")
                    .startStudyYear((int) (Math.random() * 18 + 2000))
                    .build();
            Student studentFour = Student.builder()
                    .firstName("user")
                    .gender("female")
                    .isWorking(false)
                    .subjects(Arrays.asList(subjectList.get(0), subjectList.get(2)))
                    .university("KFU")
                    .startStudyYear((int) (Math.random() * 18 + 2000))
                    .build();
            Student studentFive = Student.builder()
                    .firstName("user")
                    .gender("female")
                    .isWorking(false)
                    .subjects(Arrays.asList(subjectList.get(0), subjectList.get(1), subjectList.get(2)))
                    .university("ITMO")
                    .startStudyYear((int) (Math.random() * 18 + 2000))
                    .build();
            Student studentSix = Student.builder()
                    .firstName("user")
                    .gender("male")
                    .isWorking(false)
                    .subjects(Arrays.asList(subjectList.get(0), subjectList.get(1)))
                    .university("MGU")
                    .startStudyYear((int) (Math.random() * 18 + 2000))
                    .build();
            Student studentSeven = Student.builder()
                    .firstName("user")
                    .gender("male")
                    .isWorking(true)
                    .subjects(Arrays.asList(subjectList.get(0), subjectList.get(1), subjectList.get(2)))
                    .university("KFU")
                    .startStudyYear((int) (Math.random() * 18 + 2000))
                    .build();
            studentRepository.saveAll(Arrays.asList(studentOne, studentTwo, studentThree, studentFour, studentFive, studentSix, studentSeven));
        }
        log.info("save all content");
        log.info("time: {}", (double) (System.currentTimeMillis() - m));
    }
}

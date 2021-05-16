package ru.itis.example.es.model.mapper;

import org.springframework.stereotype.Component;
import ru.itis.example.es.model.entity.Student;
import ru.itis.example.es.model.entity.Subject;
import ru.itis.example.es.model.index.StudentIndex;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Component
public class StudentIndexMapper {


    private List<String> getSubjectIds(Student student) {
        return student.getSubjects().stream().map(Subject::getId).map(id -> String.valueOf(id)).collect(Collectors.toList());
    }

    private List<String> getSubjectTitles(Student student) {
        return student.getSubjects().stream().map(Subject::getTitle).collect(Collectors.toList());
    }

    public StudentIndex entityToIndex(Student entity) {
        StudentIndex studentIndex = new StudentIndex();
        studentIndex.setId(entity.getId());
        studentIndex.setFirstName(entity.getFirstName());
        studentIndex.setGender(entity.getGender());
        studentIndex.setUniversity(entity.getUniversity());
        studentIndex.setStartStudyYear(String.valueOf(entity.getStartStudyYear()));
        studentIndex.setWorking(entity.isWorking());
        studentIndex.setSubjectIds(getSubjectIds(entity));
        studentIndex.setSubjectTitles(getSubjectTitles(entity));
        return studentIndex;
    }
}

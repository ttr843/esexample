package ru.itis.example.es.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Data
@Builder
public class FilterDTO {
    private String university;
    private String gender;
    private String startStudyYear;
    private boolean isWorking;
    private List<String> subjectTitles;
    private List<Long> subjectIds;
}

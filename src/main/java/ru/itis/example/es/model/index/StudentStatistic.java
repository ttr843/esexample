package ru.itis.example.es.model.index;

import lombok.Builder;
import lombok.Data;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */

@Data
@Builder
public class StudentStatistic {
    private String university;
    private String startStudyYear;
    private String subject;
    private Long totalCount;

}

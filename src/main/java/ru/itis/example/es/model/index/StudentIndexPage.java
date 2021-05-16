package ru.itis.example.es.model.index;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;


/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class StudentIndexPage extends PageImpl<StudentIndex> {
    public StudentIndexPage(List<StudentIndex> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
}

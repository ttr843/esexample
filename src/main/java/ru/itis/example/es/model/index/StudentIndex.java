package ru.itis.example.es.model.index;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Data
@Document(indexName = "student")
@Setting(settingPath = "/settings/lowercase-normalizer.json")
public class StudentIndex {
    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String firstName;
    @Field(type = FieldType.Keyword)
    private String university;
    @Field(type = FieldType.Keyword)
    private String gender;

    @Field(type = FieldType.Keyword)
    private String startStudyYear;

    private boolean isWorking = false;

    @Field(type = FieldType.Keyword)
    private List<String> subjectTitles = new ArrayList<>();

    private List<String> subjectIds = new ArrayList<>();
}

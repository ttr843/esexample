package ru.itis.example.es.utils.elastic;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
public interface ElasticSearchAggregationConstantsNames {
    String GLOBAL_AGGREGATION_NAME = "global_result_agg";
    String FILTER_AGGREGATION_NAME = "filter_agg";
    String MAIN_AGGREGATION_NAME = "main_student_result";
    String SUB_ONE_MAIN_AGGREGATION_NAME = "sub_one_main_agg";
    String SUB_TWO_MAIN_AGGREGATION_NAME = "sub_two_main_agg";
    String IS_WORKING_FIELD_NAME = "isWorking";
    String START_STUDY_YEAR_FIELD_NAME = "startStudyYear";
    String UNIVERSITY_FIELD_NAME = "university";
    String SUBJECT_TITLE_FIELD_NAME = "subjectTitles";
    String GENDER_FIELD_NAME = "gender";


    int SIZE_OF_REQUEST = 10_000_000;
}

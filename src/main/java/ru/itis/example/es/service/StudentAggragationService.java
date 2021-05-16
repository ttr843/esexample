package ru.itis.example.es.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.stereotype.Component;
import ru.itis.example.es.model.dto.FilterDTO;
import ru.itis.example.es.model.index.StudentStatistic;
import ru.itis.example.es.utils.elastic.ElasticSearchAggregationBuilder;
import ru.itis.example.es.utils.elastic.ElasticsearchAggregationHandler;

import java.util.ArrayList;
import java.util.List;

import static ru.itis.example.es.utils.elastic.ElasticSearchAggregationConstantsNames.*;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StudentAggragationService {

    private final ElasticsearchAggregationHandler elasticsearchAggregationHandler;
    private AggregationBuilder lastSubAgg;

    /**
     * Создаём агрегацию
     */
    protected AggregationBuilder getAggregationBuilder() {
        AggregationBuilder mainAgg = AggregationBuilders
                .terms(MAIN_AGGREGATION_NAME)
                .field(UNIVERSITY_FIELD_NAME)
                .size(SIZE_OF_REQUEST);
        AggregationBuilder subOneAgg = AggregationBuilders
                .terms(SUB_ONE_MAIN_AGGREGATION_NAME)
                .field(START_STUDY_YEAR_FIELD_NAME)
                .size(SIZE_OF_REQUEST);
        lastSubAgg = AggregationBuilders
                .terms(SUB_TWO_MAIN_AGGREGATION_NAME)
                .field(SUBJECT_TITLE_FIELD_NAME)
                .size(SIZE_OF_REQUEST);
        return mainAgg.subAggregation(subOneAgg.subAggregation(lastSubAgg));
    }

    /**
     * Добавляем необходимые агрегации
     */
    protected ElasticSearchAggregationBuilder getElasticsearchAggregationBuilder(ElasticSearchAggregationBuilder elasticsearchAggregationBuilder,
                                                                                 FilterDTO filter) {
        return elasticsearchAggregationBuilder
                .initGlobalAggregation()
                .addBasicFilters(filter)
                .addMainAggregationWithTail(getAggregationBuilder(), lastSubAgg);
    }

    /**
     * Получаем статистику по всем агрегациям
     */
    protected List<StudentStatistic> getStatistics(Terms mainAggregation) {
        List<StudentStatistic> studentStatistics = new ArrayList<>();
        for (Terms.Bucket mainEntry : mainAggregation.getBuckets()) {
            Terms subMainAggregation = mainEntry.getAggregations().get(SUB_ONE_MAIN_AGGREGATION_NAME);
            for (Terms.Bucket subEntry : subMainAggregation.getBuckets()) {
                Terms subTwo = subEntry.getAggregations().get(SUB_TWO_MAIN_AGGREGATION_NAME);
                for (Terms.Bucket subTwoEntry : subTwo.getBuckets()) {
                    StudentStatistic studentStatistic = StudentStatistic.builder()
                            .university(mainEntry.getKeyAsString())
                            .startStudyYear(subEntry.getKeyAsString())
                            .subject(subTwoEntry.getKeyAsString())
                            .totalCount(subEntry.getDocCount())
                            .build();
                    studentStatistics.add(studentStatistic);
                }
            }
        }
        return studentStatistics;
    }

    @SneakyThrows
    public List<StudentStatistic> createStatistic(FilterDTO filterDTO) {
        log.info("start create statistic");
        ElasticSearchAggregationBuilder elasticsearchAggregationBuilder =
                this.getElasticsearchAggregationBuilder(new ElasticSearchAggregationBuilder(), filterDTO);
        return getStatistics(elasticsearchAggregationHandler.makeRequestAndGetMainAggregationFromResponse(elasticsearchAggregationBuilder.build()));
    }

}

package ru.itis.example.es.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import ru.itis.example.es.model.dto.FilterDTO;
import ru.itis.example.es.model.dto.ListQuery;
import ru.itis.example.es.model.dto.PageRequestDTO;
import ru.itis.example.es.model.index.StudentIndex;
import ru.itis.example.es.model.index.StudentIndexPage;
import ru.itis.example.es.model.index.StudentStatistic;
import ru.itis.example.es.utils.elastic.ElasticSearchQueryBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static ru.itis.example.es.utils.elastic.ElasticSearchAggregationConstantsNames.*;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentIndexService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final StudentAggragationService studentAggragationService;

    public StudentIndexPage search(FilterDTO filterDTO, ListQuery listQuery) {
        ElasticSearchQueryBuilder builder = getCommonBuilder(filterDTO);
        if (builder != null) {
            return getSortedAndPagedStudentIndexes(builder, listQuery);
        }
        return new StudentIndexPage(null, null, 0);
    }

    public List<StudentStatistic> createAllAggregation(FilterDTO filterDTO) {
        return studentAggragationService.createStatistic(filterDTO);
    }

    private StudentIndexPage getSortedAndPagedStudentIndexes(ElasticSearchQueryBuilder builder, ListQuery listQuery) {
        PageRequestDTO pageable = listQuery.toPageRequest();
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withFilter(builder.getFilterBuilder())
                .withPageable(PageRequest.of(pageable.getPage(), pageable.getSize()));
        NativeSearchQuery searchQuery = queryBuilder.build();
        SearchHits<StudentIndex> searchHits = elasticsearchOperations.search(searchQuery, StudentIndex.class);

        NativeSearchQuery totalCountQuey = new NativeSearchQueryBuilder()
                .withFilter(builder.getFilterBuilder())
                .build();
        long totalCount = elasticsearchOperations.count(totalCountQuey, StudentIndex.class);
        List<StudentIndex> studentIndexList = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        return new StudentIndexPage(studentIndexList, searchQuery.getPageable(), totalCount);
    }

    private ElasticSearchQueryBuilder getCommonBuilder(FilterDTO filterDTO) {
        ElasticSearchQueryBuilder builder = new ElasticSearchQueryBuilder();
        if (filterDTO == null) {
            return null;
        } else {
            if (filterDTO.getStartStudyYear() != null) {
                builder.addToQuery(ElasticSearchQueryBuilder.TermType.EQ, START_STUDY_YEAR_FIELD_NAME, filterDTO.getStartStudyYear());
            }
            if (filterDTO.getUniversity() != null) {
                builder.addToQuery(ElasticSearchQueryBuilder.TermType.LIKE, UNIVERSITY_FIELD_NAME, filterDTO.getUniversity());
            }
            if (filterDTO.getGender() != null) {
                builder.addToQuery(ElasticSearchQueryBuilder.TermType.NOT_EQ, GENDER_FIELD_NAME, filterDTO.getGender());
            }
            return builder;
        }
    }
}

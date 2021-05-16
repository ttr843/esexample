package ru.itis.example.es.utils.elastic;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import ru.itis.example.es.model.dto.FilterDTO;

import static ru.itis.example.es.utils.elastic.ElasticSearchAggregationConstantsNames.*;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */

@Data
@RequiredArgsConstructor
public class ElasticSearchAggregationBuilder {

    private AggregationBuilder aggregationBuilder;
    private AggregationBuilder filtersHead;
    private AggregationBuilder currentAggregation;
    private BoolQueryBuilder boolQueryBuilder;

    /**
     * Создаём глобальную агрегацию для elasticsearch
     */
    public ElasticSearchAggregationBuilder initGlobalAggregation() {
        aggregationBuilder = AggregationBuilders.global(GLOBAL_AGGREGATION_NAME);
        currentAggregation = aggregationBuilder;
        boolQueryBuilder = QueryBuilders.boolQuery();
        return this;
    }

    /**
     * Добавляем фильтры для перед агрегацией
     */
    public ElasticSearchAggregationBuilder addBasicFilters(FilterDTO filter) {
        addFilterByUniversity(filter);
        addFilterByGender(filter);
        addFilterByIsWorking(filter);
        return this;
    }

    private void addFilterByIsWorking(FilterDTO filter) {
        if (BooleanUtils.isTrue(filter.isWorking())) {
            addFilterToTheEndOfAggregationFilter(QueryBuilders.termQuery(IS_WORKING_FIELD_NAME, true));
        } else if (BooleanUtils.isFalse(filter.isWorking())) {
            addFilterToTheEndOfAggregationFilter(QueryBuilders.termQuery(IS_WORKING_FIELD_NAME, false));
        }
    }

    private void addFilterByGender(FilterDTO filter) {
        if (filter.getGender() != null) {
            addFilterToTheEndOfAggregationFilter(QueryBuilders.termQuery(GENDER_FIELD_NAME, filter.getGender()));
        }
    }

    private void addFilterByUniversity(FilterDTO filter) {
        if (filter.getUniversity() != null) {
            addFilterToTheEndOfAggregationFilter(QueryBuilders.termQuery(UNIVERSITY_FIELD_NAME, filter.getUniversity()));
        }
    }

    public void addFilterToTheEndOfAggregationFilter(QueryBuilder queryBuilder) {
        boolQueryBuilder.must().add(queryBuilder);
    }


    /**
     * Добавляем основную агрегация состояющую из нескольких условий
     */
    public ElasticSearchAggregationBuilder addMainAggregationWithTail(AggregationBuilder mainHeaderAgg, AggregationBuilder lastMainAgg) {
        if (filtersHead == null) {
            this.filtersHead = mainHeaderAgg;
            this.currentAggregation = lastMainAgg;
            return this;
        }
        this.currentAggregation.subAggregation(mainHeaderAgg);
        this.currentAggregation = lastMainAgg;
        return this;
    }

    public AggregationBuilder build() {
        return this.aggregationBuilder.subAggregation(AggregationBuilders.filter(FILTER_AGGREGATION_NAME, boolQueryBuilder).subAggregation(filtersHead));
    }
}

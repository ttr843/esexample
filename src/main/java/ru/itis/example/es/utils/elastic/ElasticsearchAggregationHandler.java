package ru.itis.example.es.utils.elastic;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.global.Global;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */

@Service
@RequiredArgsConstructor
public class ElasticsearchAggregationHandler {

    private static final String STUDENT_INDEX_NAME = "student";
    private final RestHighLevelClient restHighLevelClient;

    /**
     * Делаем запрос в elasticsearch и получаем главную результат главной агрегации
     */
    public <T> T makeRequestAndGetMainAggregationFromResponse(AggregationBuilder aggregationBuilder) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0).aggregation(aggregationBuilder);
        return getResponseByAggregation(searchSourceBuilder);
    }


    /**
     * Делаем запрос в индекс кнм на основе агрегации
     */
    private <T> T getResponseByAggregation(SearchSourceBuilder searchSourceBuilder) throws IOException {
        SearchRequest searchRequest = new SearchRequest().indices(STUDENT_INDEX_NAME).source(searchSourceBuilder);
        return skipHeadOfResponseAndGetMainAggregation(restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT));
    }

    /**
     * Пропускаем все фильтрый и возвращаем главную часть ответа
     */
    public <T> T skipHeadOfResponseAndGetMainAggregation(SearchResponse response) {
        Global globalAgg = response.getAggregations().get(ElasticSearchAggregationConstantsNames.GLOBAL_AGGREGATION_NAME);
        Filter filterAgg = skipFilters(globalAgg);
        return filterAgg.getAggregations().get(ElasticSearchAggregationConstantsNames.MAIN_AGGREGATION_NAME);
    }

    /**
     * Пропускаем агрегационные фильтры
     */
    public Filter skipFilters(Global globalAgg) {
        Filter filter = globalAgg.getAggregations().get(ElasticSearchAggregationConstantsNames.FILTER_AGGREGATION_NAME);
        while (filter.getAggregations().get(ElasticSearchAggregationConstantsNames.FILTER_AGGREGATION_NAME) != null) {
            filter = filter.getAggregations().get(ElasticSearchAggregationConstantsNames.FILTER_AGGREGATION_NAME);
        }
        return filter;
    }
}

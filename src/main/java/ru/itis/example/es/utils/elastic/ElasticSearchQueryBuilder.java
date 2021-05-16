package ru.itis.example.es.utils.elastic;

import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Getter
public class ElasticSearchQueryBuilder {
    private final BoolQueryBuilder filterBuilder = boolQuery().must(matchAllQuery());
    private int termCount = 0;

    public void addToQuery(TermType type, String fieldName, Object value) {
        if (isEmpty(value) || type == null) {
            return;
        }

        switch (type) {
            case LIKE:
                filterBuilder.must(wildcardQuery(fieldName, "*" + ((String) value) + "*"));
                break;
            case IN:
                filterBuilder.must(termsQuery(fieldName, (Collection<?>) value));
                break;
            case MATCH:
                filterBuilder.must(matchPhraseQuery(fieldName, value));
                break;
            case EXISTS:
                boolean exists = BooleanUtils.isTrue((Boolean) value);
                if (exists) {
                    filterBuilder.must(existsQuery(fieldName));
                } else {
                    filterBuilder.mustNot(existsQuery(fieldName));
                }
                break;
            case FULL_LIKE: {
                String[] values = ((String) value).split(" ");
                for (String v : values) {
                    filterBuilder.should(wildcardQuery(fieldName, "*" + v.toLowerCase() + "*"));
                }
                filterBuilder.should(matchPhraseQuery(fieldName, value));
                filterBuilder.minimumShouldMatch(values.length);
            }
            case NOT_EQ:
                filterBuilder.mustNot(termQuery(fieldName, value));
                break;
            default:
                filterBuilder.must(termQuery(fieldName, value));
        }
        this.termCount++;
    }

    private boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }

        boolean isEmpty = false;
        if (value instanceof String) {
            isEmpty = StringUtils.isEmpty((String) value);
        } else if (value instanceof Collection) {
            isEmpty = CollectionUtils.isEmpty((Collection<?>) value);
        }

        return isEmpty;
    }

    public enum TermType {
        EQ, LIKE, IN, MATCH, EXISTS, NOT_EQ, FULL_LIKE
    }
}

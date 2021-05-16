package ru.itis.example.es.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import static java.lang.Integer.parseInt;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListQuery {

    private static final int DEFAULT_PAGE_SIZE = 25;

    private String order;

    private String page;
    private String size;

    public static ListQuery empty() {
        return new ListQuery(null, null, null);
    }

    public PageRequestDTO toPageRequest() {
        PageRequestDTO pageRequest = createPageRequest();
        pageRequest.setSort(toSort());
        return pageRequest;
    }

    private PageRequestDTO createPageRequest() {
        if (page != null && !page.matches("\\d+")) {
            throw new IllegalStateException("The 'page' parameter should be a number: " + page);
        }
        if (size != null && !size.matches("\\d+")) {
            throw new IllegalStateException("The 'size' parameter should be a number: " + page);
        }
        return PageRequestDTO.builder()
                .page(page != null ? parseInt(page) : 0)
                .size(size != null ? parseInt(size) : DEFAULT_PAGE_SIZE)
                .build();
    }

    public Sort toSort() {
        if (order == null || order.isEmpty()) {
            return Sort.unsorted();
        }
        String[] orders = order.split(";");
        Sort sort = parseSort(orders[0]);
        for (int i = 1; i < orders.length; i++) {
            sort = sort.and(parseSort(orders[i]));
        }
        return sort;
    }

    private static Sort parseSort(String sort) {
        String[] parts = sort.split(",", 2);
        if (parts.length == 2) {
            Sort.Direction direction;
            switch (parts[1]) {
                case "asc":
                    direction = Sort.Direction.ASC;
                    break;
                case "desc":
                    direction = Sort.Direction.DESC;
                    break;
                default:
                    direction = Sort.DEFAULT_DIRECTION;
            }
            return Sort.by(direction, parts[0]);
        } else {
            return Sort.by(Sort.DEFAULT_DIRECTION, parts[0]);
        }
    }
}

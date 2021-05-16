package ru.itis.example.es.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageRequestDTO implements Pageable {
    private int page;
    private int size;
    private Sort sort;

    @Override
    public int getPageNumber() {
        return page;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return size * page;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return PageRequestDTO.builder().page(page + 1).size(size).build();
    }

    @Override
    public Pageable previousOrFirst() {
        return page > 0 ? PageRequestDTO.builder().page(page - 1).size(size).build() : null;
    }

    @Override
    public Pageable first() {
        return PageRequestDTO.builder().page(0).size(size).build();
    }

    @Override
    public boolean hasPrevious() {
        return page > 0;
    }

    public void setDefaultSortIfUnsorted(Sort.Direction direction, String property) {
        if (this.getSort().equals(Sort.unsorted())) {
            this.setSort(Sort.by(direction, property));
        }
    }

    public PageRequestDTO with(Sort sort) {
        setSort(getSort().and(sort));
        return this;
    }
}

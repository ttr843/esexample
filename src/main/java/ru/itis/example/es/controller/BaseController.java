package ru.itis.example.es.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itis.example.es.model.dto.FilterDTO;
import ru.itis.example.es.model.dto.ListQuery;
import ru.itis.example.es.model.index.StudentIndexPage;
import ru.itis.example.es.model.index.StudentStatistic;
import ru.itis.example.es.service.InsertDataService;
import ru.itis.example.es.service.ReindexService;
import ru.itis.example.es.service.StudentIndexService;

import java.util.List;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("es/api")
public class BaseController {

    private final ReindexService reindexService;
    private final InsertDataService insertDataService;
    private final StudentIndexService studentIndexService;

    @Operation(
            summary = "Insert data into database",
            description = "insert data into database"
    )
    @GetMapping("insert-data")
    public void insertData() {
        insertDataService.insertData();
    }

    @Operation(
            summary = "Insert data into index",
            description = "insert data into UserIndex from user table"
    )
    @GetMapping("reindex")
    public void reindex() {
        reindexService.reindex();
    }

    @PostMapping("search")
    @Operation(
            summary = "Search",
            description = "search content from index"
    )
    public StudentIndexPage searchIndex(@Parameter(description = "Параметры поиска") @RequestBody(required = false) FilterDTO filterDTO,
                                        ListQuery listQuery) {
        return studentIndexService.search(filterDTO, listQuery);
    }

    @PostMapping("aggregation")
    @Operation(
            summary = "create agg",
            description = "create  Aggregation"
    )
    public List<StudentStatistic> allAgg(@Parameter(description = "Параметры поиска") @RequestBody(required = false) FilterDTO filterDTO) {
        return studentIndexService.createAllAggregation(filterDTO);
    }

}

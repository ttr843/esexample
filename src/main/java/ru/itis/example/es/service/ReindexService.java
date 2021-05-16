package ru.itis.example.es.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Service;
import ru.itis.example.es.config.ReindexProperties;
import ru.itis.example.es.model.index.StudentIndex;
import ru.itis.example.es.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReindexService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final StudentRepository studentRepository;
    private final ReindexProperties reindexProperties;
    private final SaveStudentIndexService saveStudentIndexService;

    private AtomicInteger saved;

    @SneakyThrows
    public void reindex() {
        log.info("Rebuild student search index is started");
        long m = System.currentTimeMillis();
        this.saved = new AtomicInteger(reindexProperties.getStudentSavedBefore());

        IndexOperations indexOperations = elasticsearchOperations.indexOps(StudentIndex.class);
        if (reindexProperties.getStudentStartingPage() == 0 && indexOperations.exists()) {
            indexOperations.delete();
            log.info("Previous student search index was deleted");
        }

        if (!indexOperations.exists()) {
            Document mapping = indexOperations.createMapping(StudentIndex.class);
            indexOperations.create();
            indexOperations.putMapping(mapping);
            log.info("New student search index was created");
        }

        long count = studentRepository.count();
        int pageCount = (int) Math.ceil((float) count / reindexProperties.getRebuildPageSize());

        ExecutorService executorService = Executors.newFixedThreadPool(reindexProperties.getThreadPoolSize());

        List<? extends Future<?>> futures = new ArrayList<>();
        try {
            futures = IntStream.range(reindexProperties.getStudentStartingPage(), pageCount)
                    .mapToObj(page -> executorService.submit(() -> saveStudentIndexService.saveToIndex(page, saved)))
                    .collect(Collectors.toList());
        } catch (UncategorizedDataAccessException e) {
            log.error("ElasticSearch Exception: ", e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Future<?> future : futures) {
            future.get();
        }

        log.info("Rebuild student search index is finished");
        log.info("time: {}", (double) (System.currentTimeMillis() - m));
    }

}

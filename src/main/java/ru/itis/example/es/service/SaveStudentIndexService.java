package ru.itis.example.es.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.example.es.config.ReindexProperties;
import ru.itis.example.es.model.entity.Student;
import ru.itis.example.es.model.index.StudentIndex;
import ru.itis.example.es.model.mapper.StudentIndexMapper;
import ru.itis.example.es.repository.StudentRepository;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SaveStudentIndexService {

    private final StudentRepository studentRepository;
    private final ReindexProperties reindexProperties;
    private final StudentIndexMapper studentIndexMapper;
    private final ElasticsearchOperations elasticsearchOperations;

    @Transactional
    public void saveToIndex(int i, AtomicInteger saved) {
        List<Student> batch = studentRepository.findAllByOrderById(PageRequest.of(i, reindexProperties.getRebuildPageSize()));
        List<StudentIndex> collect = batch.stream()
                .map(this::toIndex)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!collect.isEmpty()) {
            elasticsearchOperations.save(collect);
        }
        log.info("Saved: {}, page: {}", saved.addAndGet(collect.size()), i);
    }

    private StudentIndex toIndex(Student student) {
        try {
            return studentIndexMapper.entityToIndex(student);
        } catch (Exception ex) {
            log.error("Exception by reindex knm_id: " + student.getId());
            log.error(ex.getMessage(), ex);
            return null;
        }
    }
}

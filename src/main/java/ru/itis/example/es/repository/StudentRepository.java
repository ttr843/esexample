package ru.itis.example.es.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itis.example.es.model.entity.Student;

import java.util.List;

/**
 * @author <a href="mailto:ruslan.pashin@waveaccess.ru">Ruslan Pashin</a>
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query(value = "select s from Student as s order by s.id DESC")
    List<Student> findAllByOrderById(@Param("pageable") Pageable pageable);
}

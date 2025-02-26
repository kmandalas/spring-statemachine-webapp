package com.example.demolition.repository;

import com.example.demolition.entity.FormData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormDataRepository extends JpaRepository<FormData, Long> {

    List<FormData> findByProcessIdAndStep(Long processId, String step);

    @Query("SELECT f FROM FormData f WHERE f.process.id = :processId AND f.id IN " +
            "(SELECT MAX(f2.id) FROM FormData f2 WHERE f2.process.id = :processId GROUP BY f2.step)")
    List<FormData> findLatestFormDataByProcess(@Param("processId") Long processId);

}

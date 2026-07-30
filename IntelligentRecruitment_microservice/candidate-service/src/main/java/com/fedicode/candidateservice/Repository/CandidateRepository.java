package com.fedicode.candidateservice.Repository;

import com.fedicode.candidateservice.Entity.Candidate;
import com.fedicode.candidateservice.Entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate,Integer>, JpaSpecificationExecutor<Candidate> {

    Optional<Candidate> findByEmail(String email);

    Boolean existsByEmail(String email);

    Page<Candidate> findAllByStatus(Status status, Pageable pageable);

    Page<Candidate> findAllByOrderByCreatedAtDesc(Pageable pageable);


    long countByStatus(Status status);

    long countByCreatedAtAfter(LocalDateTime localDateTime);

    @Query(value = """
        SELECT TO_CHAR(c.created_at,'FMMonth') as month_name,count (c.id)
                from Candidate c
                        GROUP BY extract (month  from c.created_at),
                                TO_CHAR(c.created_at ,'FMMonth')
                                        ORDER BY extract(month from c.created_at)
        """,
            nativeQuery=true
    )
    List<Object[]> countGroupedByMonth();
}

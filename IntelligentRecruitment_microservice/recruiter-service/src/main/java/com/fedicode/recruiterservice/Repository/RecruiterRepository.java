package com.fedicode.recruiterservice.Repository;

import com.fedicode.recruiterservice.Entity.Recruiter;
import com.fedicode.recruiterservice.Entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecruiterRepository extends JpaRepository<Recruiter,Integer>, JpaSpecificationExecutor<Recruiter> {

    Optional<Recruiter> findByEmail(String email);

    Boolean existsByEmail(String email);

    Page<Recruiter> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Recruiter> findAllByStatus(Status status, Pageable pageable);


    long countByStatus(Status status);

    long countByCreatedAtAfter(LocalDateTime createdAt);


    @Query(value = """
        SELECT TO_CHAR(r.created_at,'FMMonth') as month_name,count (r.id)
                from Recruiter r
                        GROUP BY extract (month  from r.created_at),
                                TO_CHAR(r.created_at ,'FMMonth')
                                        ORDER BY extract(month from r.created_at)
        """,
            nativeQuery=true
    )
    List<Object[]> countGroupedByMonth();

}

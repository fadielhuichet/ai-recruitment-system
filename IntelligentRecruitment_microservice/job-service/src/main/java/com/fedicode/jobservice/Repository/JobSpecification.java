package com.fedicode.jobservice.Repository;

import com.fedicode.jobservice.Entity.Job;
import com.fedicode.jobservice.Entity.JobCategory;
import com.fedicode.jobservice.Entity.JobStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


// JobSpecification.java
public class JobSpecification {
    public static Specification<Job> withFilters2(
            String status,
            List<Integer> recruiterIds, // ← list now
            String query) {

        return (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isBlank()) {
                try {
                    predicates.add(cb.equal(
                            root.get("status"),
                            JobStatus.valueOf(status)
                    ));
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (recruiterIds != null && !recruiterIds.isEmpty()) {
                predicates.add(root.get("recruiterId").in(recruiterIds)); // ← IN clause
            }

            if (query != null && !query.isBlank()) {
                String pattern = "%" + query.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("company")), pattern),
                        cb.like(cb.lower(root.get("location")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Job> withFilters(
            String title,
            String location,
            JobCategory category) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isBlank()) {
                System.out.println("Searching for: " + title);

                String search = "%" + title.toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("title")), search),
                                cb.like(cb.lower(root.get("company")), search),
                                cb.like(cb.lower(root.get("description")), search)
                        )
                );
            }
            if (location != null && !location.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("location")),
                        "%" + location.toLowerCase() + "%"
                ));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

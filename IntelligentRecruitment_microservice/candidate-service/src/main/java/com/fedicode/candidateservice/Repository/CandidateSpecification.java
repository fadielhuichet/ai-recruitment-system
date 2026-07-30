package com.fedicode.candidateservice.Repository;

import com.fedicode.candidateservice.Entity.Candidate;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CandidateSpecification {

    public static Specification<Candidate> withFilters(
            String firstName,
            String lastName,
            String email) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (firstName != null && !firstName.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("firstName")),
                        "%" + firstName.toLowerCase() + "%"
                ));
            }
            if (lastName != null && !lastName.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("lastName")),
                        "%" + lastName.toLowerCase() + "%"
                ));
            }
            if (email != null && !email.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%"
                ));
            }


            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}


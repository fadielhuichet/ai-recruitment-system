package com.fedicode.recruiterservice.Repository;

import com.fedicode.recruiterservice.Entity.Recruiter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RecruiterSpecification {

    public static Specification<Recruiter> withFilters(
            String firstName,
            String lastName,
            String email,
            String companyName) {

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

            if (companyName != null && !companyName.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("companyName")),
                        "%" + companyName.toLowerCase() + "%"
                ));
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}

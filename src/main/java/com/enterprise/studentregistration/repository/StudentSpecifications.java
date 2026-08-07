package com.enterprise.studentregistration.repository;

import com.enterprise.studentregistration.entity.Student;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a combinable Specification<Student> from optional search/filter
 * criteria. Any null/blank parameter is simply skipped, allowing the
 * Student list page to support keyword search + course/branch/semester
 * filters simultaneously (Search & Filtering modules).
 */
public final class StudentSpecifications {

    private StudentSpecifications() {
    }

    public static Specification<Student> withFilters(String keyword, String course,
                                                       String branch, Integer semester) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate keywordPredicate = cb.or(
                        cb.like(cb.lower(root.get("firstName")), likePattern),
                        cb.like(cb.lower(root.get("lastName")), likePattern),
                        cb.like(cb.lower(root.get("studentId")), likePattern),
                        cb.like(cb.lower(root.get("email")), likePattern)
                );
                predicates.add(keywordPredicate);
            }

            if (StringUtils.hasText(course)) {
                predicates.add(cb.equal(cb.lower(root.get("course")), course.toLowerCase()));
            }

            if (StringUtils.hasText(branch)) {
                predicates.add(cb.equal(cb.lower(root.get("branch")), branch.toLowerCase()));
            }

            if (semester != null) {
                predicates.add(cb.equal(root.get("semester"), semester));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

package com.enterprise.studentregistration.repository;

import com.enterprise.studentregistration.entity.Gender;
import com.enterprise.studentregistration.entity.Student;
import com.enterprise.studentregistration.entity.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Student entity. Extends JpaSpecificationExecutor so the
 * service layer can build dynamic, combinable filters (course + branch +
 * semester + keyword) for the search/filter module.
 */
public interface StudentRepository extends JpaRepository<Student, Long>,
        JpaSpecificationExecutor<Student> {

    Optional<Student> findByEmailIgnoreCase(String email);

    Optional<Student> findByStudentId(String studentId);

    boolean existsByEmailIgnoreCase(String email);

    long countByStatus(StudentStatus status);

    long countByGender(Gender gender);

    @Query("SELECT s FROM Student s ORDER BY s.createdAt DESC")
    Page<Student> findRecentlyRegistered(Pageable pageable);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.studentId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Student> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Finds the last generated studentId for a given admission year so the
     * next sequential id can be computed (see StudentIdGenerator).
     */
    @Query("SELECT s.studentId FROM Student s WHERE s.studentId LIKE CONCAT('STU', :year, '%') " +
           "ORDER BY s.studentId DESC")
    List<String> findLastStudentIdForYear(@Param("year") String year, Pageable pageable);
}

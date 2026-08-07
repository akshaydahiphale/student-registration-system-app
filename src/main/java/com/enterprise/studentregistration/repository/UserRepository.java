package com.enterprise.studentregistration.repository;

import com.enterprise.studentregistration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndEmailIgnoreCase(String username, String email);

    Optional<User> findByResetToken(String resetToken);

    boolean existsByUsername(String username);

    boolean existsByStudentId(Long studentId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.student WHERE u.username = :username")
    Optional<User> findByUsernameWithStudent(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.student WHERE u.enabled = false")
    List<User> findByEnabledFalseWithStudent();
}
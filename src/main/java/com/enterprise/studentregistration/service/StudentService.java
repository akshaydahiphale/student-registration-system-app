package com.enterprise.studentregistration.service;

import com.enterprise.studentregistration.dto.StudentDTO;
import com.enterprise.studentregistration.dto.StudentProfileUpdateDTO;
import com.enterprise.studentregistration.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

    Student createStudent(StudentDTO dto);

    Student updateStudent(Long id, StudentDTO dto);

    void deleteStudent(Long id);

    Student getStudentById(Long id);

    Student getStudentByStudentId(String studentId);
    Student updateOwnProfile(Long studentId, StudentProfileUpdateDTO dto);
    Page<Student> getAllStudents(Pageable pageable);

    /** Combined search + filter + sort + pagination used by the student list page. */
    Page<Student> searchStudents(String keyword, String course, String branch,
                                  Integer semester, Pageable pageable);
}

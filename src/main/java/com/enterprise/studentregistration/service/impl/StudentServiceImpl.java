package com.enterprise.studentregistration.service.impl;

import com.enterprise.studentregistration.dto.StudentDTO;
import com.enterprise.studentregistration.dto.StudentProfileUpdateDTO;
import com.enterprise.studentregistration.entity.Student;
import com.enterprise.studentregistration.entity.StudentStatus;
import com.enterprise.studentregistration.exception.DuplicateEmailException;
import com.enterprise.studentregistration.exception.ResourceNotFoundException;
import com.enterprise.studentregistration.repository.StudentRepository;
import com.enterprise.studentregistration.repository.StudentSpecifications;
import com.enterprise.studentregistration.service.StudentService;
import com.enterprise.studentregistration.util.FileStorageUtil;
import com.enterprise.studentregistration.util.StudentIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentIdGenerator studentIdGenerator;
    private final FileStorageUtil fileStorageUtil;

    @Override
    @Transactional
    public Student createStudent(StudentDTO dto) {
        if (studentRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateEmailException("A student with email '" + dto.getEmail() + "' already exists");
        }

        Student student = Student.builder()
                .studentId(studentIdGenerator.generate())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .gender(dto.getGender())
                .dateOfBirth(dto.getDateOfBirth())
                .mobileNumber(dto.getMobileNumber())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .pinCode(dto.getPinCode())
                .course(dto.getCourse())
                .branch(dto.getBranch())
                .semester(dto.getSemester())
                .admissionDate(dto.getAdmissionDate())
                .status(dto.getStatus() != null ? dto.getStatus() : StudentStatus.ACTIVE)
                .build();

        if (dto.getPhoto() != null && !dto.getPhoto().isEmpty()) {
            student.setPhotoPath(fileStorageUtil.storePhoto(dto.getPhoto()));
        }

        Student saved = studentRepository.save(student);
        log.info("Created new student: {}", saved.getStudentId());
        return saved;
    }

    @Override
    @Transactional
    public Student updateStudent(Long id, StudentDTO dto) {
        Student student = getStudentById(id);

        studentRepository.findByEmailIgnoreCase(dto.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateEmailException("A student with email '" + dto.getEmail() + "' already exists");
            }
        });

        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setGender(dto.getGender());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setMobileNumber(dto.getMobileNumber());
        student.setEmail(dto.getEmail());
        student.setAddress(dto.getAddress());
        student.setCity(dto.getCity());
        student.setState(dto.getState());
        student.setPinCode(dto.getPinCode());
        student.setCourse(dto.getCourse());
        student.setBranch(dto.getBranch());
        student.setSemester(dto.getSemester());
        student.setAdmissionDate(dto.getAdmissionDate());
        if (dto.getStatus() != null) {
            student.setStatus(dto.getStatus());
        }

        if (dto.getPhoto() != null && !dto.getPhoto().isEmpty()) {
            String oldPhoto = student.getPhotoPath();
            student.setPhotoPath(fileStorageUtil.storePhoto(dto.getPhoto()));
            fileStorageUtil.deletePhoto(oldPhoto);
        }

        Student updated = studentRepository.save(student);
        log.info("Updated student: {}", updated.getStudentId());
        return updated;
    }

    /**
     * Self-service update: a logged-in STUDENT edits only their own
     * contact-related fields. Deliberately does NOT touch studentId,
     * firstName, lastName, gender, dateOfBirth, course, branch,
     * semester, admissionDate, or status - those remain admin-only
     * via updateStudent().
     */
    @Override
    @Transactional
    public Student updateOwnProfile(Long studentId, StudentProfileUpdateDTO dto) {
        Student student = getStudentById(studentId);

        studentRepository.findByEmailIgnoreCase(dto.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(studentId)) {
                throw new DuplicateEmailException("A student with email '" + dto.getEmail() + "' already exists");
            }
        });

        student.setMobileNumber(dto.getMobileNumber());
        student.setEmail(dto.getEmail());
        student.setAddress(dto.getAddress());
        student.setCity(dto.getCity());
        student.setState(dto.getState());
        student.setPinCode(dto.getPinCode());

        Student updated = studentRepository.save(student);
        log.info("Student {} updated own profile", updated.getStudentId());
        return updated;
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        fileStorageUtil.deletePhoto(student.getPhotoPath());
        studentRepository.delete(student);
        log.info("Deleted student: {}", student.getStudentId());
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Override
    public Student getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with student ID: " + studentId));
    }

    @Override
    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @Override
    public Page<Student> searchStudents(String keyword, String course, String branch,
                                         Integer semester, Pageable pageable) {
        return studentRepository.findAll(
                StudentSpecifications.withFilters(keyword, course, branch, semester), pageable);
    }
}
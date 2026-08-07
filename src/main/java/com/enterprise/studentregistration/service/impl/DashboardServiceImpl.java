package com.enterprise.studentregistration.service.impl;

import com.enterprise.studentregistration.dto.DashboardStatsDTO;
import com.enterprise.studentregistration.entity.Gender;
import com.enterprise.studentregistration.entity.StudentStatus;
import com.enterprise.studentregistration.repository.StudentRepository;
import com.enterprise.studentregistration.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;

    @Override
    public DashboardStatsDTO getStats() {
        long total = studentRepository.count();
        long active = studentRepository.countByStatus(StudentStatus.ACTIVE);
        long inactive = studentRepository.countByStatus(StudentStatus.INACTIVE);
        long male = studentRepository.countByGender(Gender.MALE);
        long female = studentRepository.countByGender(Gender.FEMALE);

        return DashboardStatsDTO.builder()
                .totalStudents(total)
                .activeStudents(active)
                .inactiveStudents(inactive)
                .maleStudents(male)
                .femaleStudents(female)
                .recentlyRegistered(studentRepository.findRecentlyRegistered(PageRequest.of(0, 5)).getContent())
                .build();
    }
}

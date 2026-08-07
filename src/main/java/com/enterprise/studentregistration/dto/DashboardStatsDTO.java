package com.enterprise.studentregistration.dto;

import com.enterprise.studentregistration.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalStudents;
    private long activeStudents;
    private long inactiveStudents;
    private long maleStudents;
    private long femaleStudents;
    private List<Student> recentlyRegistered;
}

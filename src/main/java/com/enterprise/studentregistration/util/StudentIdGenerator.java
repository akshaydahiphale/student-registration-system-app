package com.enterprise.studentregistration.util;

import com.enterprise.studentregistration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.List;

/**
 * Generates sequential, human-readable student IDs of the form
 * STU<year><3-digit sequence>, e.g. STU2026001, STU2026002...
 * The sequence resets every calendar year.
 */
@Component
@RequiredArgsConstructor
public class StudentIdGenerator {

    private final StudentRepository studentRepository;

    public synchronized String generate() {
        String year = String.valueOf(Year.now().getValue());
        List<String> last = studentRepository.findLastStudentIdForYear(year, PageRequest.of(0, 1));

        int nextSequence = 1;
        if (!last.isEmpty()) {
            String lastId = last.get(0); // e.g. STU2026007
            String sequencePart = lastId.substring(("STU" + year).length());
            nextSequence = Integer.parseInt(sequencePart) + 1;
        }

        return String.format("STU%s%03d", year, nextSequence);
    }
}

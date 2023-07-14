package com.securitydemo.student;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagementController {

    private static final List<Student> STUDENTS = Arrays.asList(
            new Student(1, "James Bond"),
            new Student(2, "Maria Jones"),
            new Student(3, "Anna Smith")
    );

    /**
     * @PreAuthorize is used for permission based auth on method level
     * It is generated from the AppSecConfig class.
     * it is a best practise to create the annotations after code refactoring
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINTRAINEE')")
    public void registerNewStudent(@RequestBody Student student) {
        //debugging
        System.out.println("registerNewStudent");
        System.out.println(student);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('student:write')")
    public List<Student> getAllStudents() {
        //debugging
        System.out.println("getAllStudents");
        return STUDENTS;
    }

    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public void deleteStudent(@PathVariable("studentId") Integer studentId) {
        System.out.println("deleteStudent");
        System.out.println(studentId);
    }

    @PutMapping("/{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public void updateStudent(@PathVariable("studentId") Integer studentId, @RequestBody Student student) {
        System.out.println("updateStudent");
        System.out.println(String.format("%s %s", student, student));
    }

}

package com.example.demo.student;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping(path = "api/v1/students")
@AllArgsConstructor
public class StudentController {

    // private final StudentService studentService;

    @GetMapping
    public List<String> getAllStudents() {
        return new ArrayList<>(){
            {
                add("John Doe");
            }
        };
    }

    // @PostMapping
    // public void addStudent(@Valid @RequestBody Student student) {
    //     studentService.addStudent(student);
    // }

    // @DeleteMapping(path = "{studentId}")
    // public void deleteStudent(
    //         @PathVariable("studentId") Long studentId) {
    //     studentService.deleteStudent(studentId);
    // }
}

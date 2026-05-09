package com.example.hometwin.controller;

import com.example.hometwin.dto.response.ApiResponse;
import com.example.hometwin.dto.response.PersonResponse;
import com.example.hometwin.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public ApiResponse<List<PersonResponse>> getAllPersons() {
        List<PersonResponse> persons = personService.getAllPersons();
        return ApiResponse.success(persons);
    }

    @GetMapping("/{id}")
    public ApiResponse<PersonResponse> getPersonById(@PathVariable String id) {
        PersonResponse person = personService.getPersonById(id);
        return ApiResponse.success(person);
    }
}

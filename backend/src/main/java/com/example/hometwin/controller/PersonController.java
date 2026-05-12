package com.example.hometwin.controller;

import com.example.hometwin.dto.request.PersonCreateRequest;
import com.example.hometwin.dto.request.PersonUpdateRequest;
import com.example.hometwin.dto.response.ApiResponse;
import com.example.hometwin.dto.response.PersonResponse;
import com.example.hometwin.service.PersonService;
import jakarta.validation.Valid;
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

    @PostMapping
    public ApiResponse<PersonResponse> createPerson(@Valid @RequestBody PersonCreateRequest request) {
        PersonResponse person = personService.createPerson(request);
        return ApiResponse.success("创建成功", person);
    }

    @PutMapping("/{id}")
    public ApiResponse<PersonResponse> updatePerson(@PathVariable String id, @RequestBody PersonUpdateRequest request) {
        PersonResponse person = personService.updatePerson(id, request);
        return ApiResponse.success("更新成功", person);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePerson(@PathVariable String id) {
        personService.deletePerson(id);
        return ApiResponse.success("删除成功", null);
    }
}

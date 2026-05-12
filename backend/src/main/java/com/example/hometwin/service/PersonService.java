package com.example.hometwin.service;

import com.example.hometwin.dto.request.PersonCreateRequest;
import com.example.hometwin.dto.request.PersonUpdateRequest;
import com.example.hometwin.dto.response.PersonResponse;
import com.example.hometwin.entity.Person;
import com.example.hometwin.repository.ItemRepository;
import com.example.hometwin.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ItemRepository itemRepository;

    public List<PersonResponse> getAllPersons() {
        List<Person> persons = personRepository.findAllByOrderByNameAsc();
        
        List<Object[]> counts = itemRepository.countActiveItemsByPerson();
        Map<String, Long> countMap = counts.stream()
            .collect(Collectors.toMap(
                arr -> (String) arr[0],
                arr -> (Long) arr[1]
            ));

        return persons.stream()
            .map(person -> PersonResponse.fromEntity(person, countMap.getOrDefault(person.getId(), 0L)))
            .collect(Collectors.toList());
    }

    public PersonResponse getPersonById(String id) {
        Person person = personRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("人物不存在: " + id));
        
        long count = itemRepository.countByPersonId(id);
        return PersonResponse.fromEntity(person, count);
    }

    public PersonResponse createPerson(PersonCreateRequest request) {
        Person person = new Person();
        person.setId(UUID.randomUUID().toString());
        person.setName(request.getName());
        person.setRelation(request.getRelation());
        person.setAvatar(request.getAvatar());
        
        Person savedPerson = personRepository.save(person);
        return PersonResponse.fromEntity(savedPerson, 0L);
    }

    public PersonResponse updatePerson(String id, PersonUpdateRequest request) {
        Person person = personRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("人物不存在: " + id));
        
        if (request.getName() != null) {
            person.setName(request.getName());
        }
        if (request.getRelation() != null) {
            person.setRelation(request.getRelation());
        }
        if (request.getAvatar() != null) {
            person.setAvatar(request.getAvatar());
        }
        
        Person savedPerson = personRepository.save(person);
        long count = itemRepository.countByPersonId(id);
        return PersonResponse.fromEntity(savedPerson, count);
    }

    public void deletePerson(String id) {
        if (!personRepository.existsById(id)) {
            throw new RuntimeException("人物不存在: " + id);
        }
        personRepository.deleteById(id);
    }
}

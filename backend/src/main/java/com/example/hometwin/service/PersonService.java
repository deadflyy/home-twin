package com.example.hometwin.service;

import com.example.hometwin.dto.response.PersonResponse;
import com.example.hometwin.entity.Person;
import com.example.hometwin.repository.ItemRepository;
import com.example.hometwin.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
}

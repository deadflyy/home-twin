package com.example.hometwin.dto.response;

import com.example.hometwin.entity.Person;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse {

    private String id;
    private String name;
    private String relation;
    private String avatar;
    private Long itemCount;

    public static PersonResponse fromEntity(Person person) {
        return new PersonResponse(
            person.getId(),
            person.getName(),
            person.getRelation(),
            person.getAvatar(),
            null
        );
    }

    public static PersonResponse fromEntity(Person person, Long itemCount) {
        return new PersonResponse(
            person.getId(),
            person.getName(),
            person.getRelation(),
            person.getAvatar(),
            itemCount
        );
    }
}

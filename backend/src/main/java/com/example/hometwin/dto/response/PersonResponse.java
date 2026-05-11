package com.example.hometwin.dto.response;

import com.example.hometwin.entity.Person;

public class PersonResponse {

    private String id;
    private String name;
    private String relation;
    private String avatar;
    private Long itemCount;

    public PersonResponse() {
    }

    public PersonResponse(String id, String name, String relation, String avatar, Long itemCount) {
        this.id = id;
        this.name = name;
        this.relation = relation;
        this.avatar = avatar;
        this.itemCount = itemCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getItemCount() {
        return itemCount;
    }

    public void setItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }

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

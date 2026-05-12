package com.example.hometwin.dto.request;

import jakarta.validation.constraints.Size;

public class PersonUpdateRequest {

    @Size(max = 50, message = "姓名最多50个字符")
    private String name;

    @Size(max = 50, message = "关系描述最多50个字符")
    private String relation;

    private String avatar;

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
}
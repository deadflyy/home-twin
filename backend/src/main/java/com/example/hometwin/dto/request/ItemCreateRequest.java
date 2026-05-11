package com.example.hometwin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ItemCreateRequest {

    @NotBlank(message = "物品名称不能为空")
    @Size(max = 100, message = "物品名称不能超过100个字符")
    private String name;

    @Size(max = 500, message = "物品描述不能超过500个字符")
    private String description;

    @NotBlank(message = "所属房间不能为空")
    private String roomId;

    private String personId;

    @Size(max = 200, message = "位置信息不能超过200个字符")
    private String location;

    private String photo;

    public ItemCreateRequest() {
    }

    public ItemCreateRequest(String name, String description, String roomId, String personId, String location, String photo) {
        this.name = name;
        this.description = description;
        this.roomId = roomId;
        this.personId = personId;
        this.location = location;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}

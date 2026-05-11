package com.example.hometwin.dto.response;

import com.example.hometwin.entity.Item;

import java.time.LocalDateTime;

public class ItemResponse {

    private String id;
    private String name;
    private String description;
    private String photo;
    private String roomId;
    private String roomName;
    private String personId;
    private String personName;
    private String location;
    private String status;
    private LocalDateTime addedAt;
    private LocalDateTime cleanedAt;

    public ItemResponse() {
    }

    public ItemResponse(String id, String name, String description, String photo, String roomId, String roomName, String personId, String personName, String location, String status, LocalDateTime addedAt, LocalDateTime cleanedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.roomId = roomId;
        this.roomName = roomName;
        this.personId = personId;
        this.personName = personName;
        this.location = location;
        this.status = status;
        this.addedAt = addedAt;
        this.cleanedAt = cleanedAt;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public LocalDateTime getCleanedAt() {
        return cleanedAt;
    }

    public void setCleanedAt(LocalDateTime cleanedAt) {
        this.cleanedAt = cleanedAt;
    }

    public static ItemResponse fromEntity(Item item, String roomName, String personName) {
        return new ItemResponse(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getPhoto(),
            item.getRoomId(),
            roomName,
            item.getPersonId(),
            personName,
            item.getLocation(),
            item.getStatus(),
            item.getAddedAt(),
            item.getCleanedAt()
        );
    }
}

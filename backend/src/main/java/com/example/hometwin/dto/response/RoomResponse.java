package com.example.hometwin.dto.response;

import com.example.hometwin.entity.Room;

public class RoomResponse {

    private String id;
    private String name;
    private String icon;
    private String color;
    private Long itemCount;

    public RoomResponse() {
    }

    public RoomResponse(String id, String name, String icon, String color, Long itemCount) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.color = color;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getItemCount() {
        return itemCount;
    }

    public void setItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }

    public static RoomResponse fromEntity(Room room) {
        return new RoomResponse(
            room.getId(),
            room.getName(),
            room.getIcon(),
            room.getColor(),
            null
        );
    }

    public static RoomResponse fromEntity(Room room, Long itemCount) {
        return new RoomResponse(
            room.getId(),
            room.getName(),
            room.getIcon(),
            room.getColor(),
            itemCount
        );
    }
}

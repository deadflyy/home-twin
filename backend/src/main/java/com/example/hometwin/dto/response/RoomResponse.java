package com.example.hometwin.dto.response;

import com.example.hometwin.entity.Room;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private String id;
    private String name;
    private String icon;
    private String color;
    private Long itemCount;

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

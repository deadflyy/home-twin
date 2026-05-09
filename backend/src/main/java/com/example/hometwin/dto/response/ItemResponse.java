package com.example.hometwin.dto.response;

import com.example.hometwin.entity.Item;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

package com.example.hometwin.service;

import com.example.hometwin.dto.response.RoomResponse;
import com.example.hometwin.entity.Room;
import com.example.hometwin.repository.ItemRepository;
import com.example.hometwin.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ItemRepository itemRepository;

    public List<RoomResponse> getAllRooms() {
        List<Room> rooms = roomRepository.findAllByOrderByNameAsc();
        
        List<Object[]> counts = itemRepository.countActiveItemsByRoom();
        Map<String, Long> countMap = counts.stream()
            .collect(Collectors.toMap(
                arr -> (String) arr[0],
                arr -> (Long) arr[1]
            ));

        return rooms.stream()
            .map(room -> RoomResponse.fromEntity(room, countMap.getOrDefault(room.getId(), 0L)))
            .collect(Collectors.toList());
    }

    public RoomResponse getRoomById(String id) {
        Room room = roomRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("房间不存在: " + id));
        
        long count = itemRepository.countByRoomId(id);
        return RoomResponse.fromEntity(room, count);
    }
}

package com.example.hometwin.service;

import com.example.hometwin.dto.request.ItemCreateRequest;
import com.example.hometwin.dto.request.ItemUpdateRequest;
import com.example.hometwin.dto.response.ItemResponse;
import com.example.hometwin.dto.response.PageResponse;
import com.example.hometwin.entity.Item;
import com.example.hometwin.entity.Person;
import com.example.hometwin.entity.Room;
import com.example.hometwin.repository.ItemRepository;
import com.example.hometwin.repository.PersonRepository;
import com.example.hometwin.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PersonRepository personRepository;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public PageResponse<ItemResponse> getItems(String roomId, String personId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> itemPage;

        if (roomId != null && personId != null) {
            itemPage = itemRepository.findByRoomIdAndPersonId(roomId, personId, pageable);
        } else if (roomId != null) {
            itemPage = itemRepository.findByRoomId(roomId, pageable);
        } else if (personId != null) {
            itemPage = itemRepository.findByPersonId(personId, pageable);
        } else {
            itemPage = itemRepository.findActiveItems(pageable);
        }

        return buildItemPageResponse(itemPage);
    }

    public ItemResponse getItemById(String id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("物品不存在: " + id));
        
        return buildItemResponse(item);
    }

    public ItemResponse createItem(ItemCreateRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new RuntimeException("房间不存在: " + request.getRoomId()));

        Person person = null;
        if (request.getPersonId() != null && !request.getPersonId().isEmpty()) {
            person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new RuntimeException("人物不存在: " + request.getPersonId()));
        }

        Item item = new Item();
        item.setId(UUID.randomUUID().toString());
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setRoomId(request.getRoomId());
        item.setPersonId(request.getPersonId());
        item.setLocation(request.getLocation());
        item.setStatus("active");
        item.setAddedAt(LocalDateTime.now());

        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            String photoUrl = savePhoto(request.getPhoto());
            item.setPhoto(photoUrl);
        }

        Item savedItem = itemRepository.save(item);
        return buildItemResponse(savedItem);
    }

    public ItemResponse updateItem(String id, ItemUpdateRequest request) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("物品不存在: " + id));

        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getRoomId() != null) {
            roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("房间不存在: " + request.getRoomId()));
            item.setRoomId(request.getRoomId());
        }
        if (request.getPersonId() != null) {
            personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new RuntimeException("人物不存在: " + request.getPersonId()));
            item.setPersonId(request.getPersonId());
        }
        if (request.getLocation() != null) {
            item.setLocation(request.getLocation());
        }
        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            String photoUrl = savePhoto(request.getPhoto());
            item.setPhoto(photoUrl);
        }

        Item savedItem = itemRepository.save(item);
        return buildItemResponse(savedItem);
    }

    public void deleteItem(String id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("物品不存在: " + id));
        itemRepository.delete(item);
    }

    public ItemResponse cleanItem(String id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("物品不存在: " + id));
        
        item.setStatus("cleaned");
        item.setCleanedAt(LocalDateTime.now());
        
        Item savedItem = itemRepository.save(item);
        return buildItemResponse(savedItem);
    }

    public PageResponse<ItemResponse> searchItems(String keyword, String roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> itemPage = itemRepository.searchItems(keyword, roomId, pageable);
        return buildItemPageResponse(itemPage);
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalItems = itemRepository.count();
        long activeItems = itemRepository.countByStatus("active");
        long cleanedItems = itemRepository.countByStatus("cleaned");
        
        stats.put("totalItems", totalItems);
        stats.put("activeItems", activeItems);
        stats.put("cleanedItems", cleanedItems);
        
        List<Object[]> roomStats = itemRepository.countActiveItemsByRoom();
        Map<String, Long> roomCountMap = new HashMap<>();
        for (Object[] row : roomStats) {
            roomCountMap.put((String) row[0], (Long) row[1]);
        }
        stats.put("roomStats", roomCountMap);
        
        List<Object[]> personStats = itemRepository.countActiveItemsByPerson();
        Map<String, Long> personCountMap = new HashMap<>();
        for (Object[] row : personStats) {
            personCountMap.put((String) row[0], (Long) row[1]);
        }
        stats.put("personStats", personCountMap);
        
        return stats;
    }

    private PageResponse<ItemResponse> buildItemPageResponse(Page<Item> itemPage) {
        List<ItemResponse> content = itemPage.getContent().stream()
            .map(this::buildItemResponse)
            .toList();
        
        return new PageResponse<>(
            content,
            itemPage.getTotalElements(),
            itemPage.getTotalPages(),
            itemPage.getNumber()
        );
    }

    private ItemResponse buildItemResponse(Item item) {
        String roomName = roomRepository.findById(item.getRoomId())
            .map(Room::getName)
            .orElse("未知房间");
        
        String personName = personRepository.findById(item.getPersonId())
            .map(Person::getName)
            .orElse(null);
        
        return ItemResponse.fromEntity(item, roomName, personName);
    }

    private String savePhoto(String base64Photo) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        return minioUrl + "/" + bucketName + "/" + fileName;
    }
}

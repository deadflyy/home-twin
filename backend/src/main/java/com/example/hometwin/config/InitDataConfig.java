package com.example.hometwin.config;

import com.example.hometwin.entity.Person;
import com.example.hometwin.entity.Room;
import com.example.hometwin.repository.PersonRepository;
import com.example.hometwin.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitDataConfig {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PersonRepository personRepository;

    @PostConstruct
    public void init() {
        initRooms();
        initPersons();
    }

    private void initRooms() {
        if (roomRepository.count() == 0) {
            List<Room> rooms = List.of(
                new Room("room-001", "主卧(卧室B)", "🏠", "#4A90D9", null, null),
                new Room("room-002", "次卧(卧室A)", "🛏️", "#67C23A", null, null),
                new Room("room-003", "客厅", "🛋️", "#E6A23C", null, null),
                new Room("room-004", "餐厅", "🥘", "#F56C6C", null, null),
                new Room("room-005", "厨房", "🍳", "#909399", null, null),
                new Room("room-006", "卫生间", "🚽", "#B37FEB", null, null),
                new Room("room-007", "阳台", "🌳", "#30C5FF", null, null)
            );
            roomRepository.saveAll(rooms);
        }
    }

    private void initPersons() {
        if (personRepository.count() == 0) {
            List<Person> persons = List.of(
                new Person("person-001", "小女孩", "4岁", "👧", null, null),
                new Person("person-002", "父亲", "小女孩父亲", "👨", null, null),
                new Person("person-003", "母亲", "小女孩母亲", "👩", null, null),
                new Person("person-004", "外婆", "小女孩外婆", "👵", null, null)
            );
            personRepository.saveAll(persons);
        }
    }
}

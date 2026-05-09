package com.example.hometwin.controller;

import com.example.hometwin.dto.response.ApiResponse;
import com.example.hometwin.dto.response.RoomResponse;
import com.example.hometwin.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public ApiResponse<List<RoomResponse>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms();
        return ApiResponse.success(rooms);
    }

    @GetMapping("/{id}")
    public ApiResponse<RoomResponse> getRoomById(@PathVariable String id) {
        RoomResponse room = roomService.getRoomById(id);
        return ApiResponse.success(room);
    }
}

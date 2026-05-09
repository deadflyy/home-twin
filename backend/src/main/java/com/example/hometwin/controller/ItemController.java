package com.example.hometwin.controller;

import com.example.hometwin.dto.request.ItemCreateRequest;
import com.example.hometwin.dto.request.ItemUpdateRequest;
import com.example.hometwin.dto.response.ApiResponse;
import com.example.hometwin.dto.response.ItemResponse;
import com.example.hometwin.dto.response.PageResponse;
import com.example.hometwin.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ApiResponse<PageResponse<ItemResponse>> getItems(
            @RequestParam(required = false) String roomId,
            @RequestParam(required = false) String personId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ItemResponse> items = itemService.getItems(roomId, personId, page, size);
        return ApiResponse.success(items);
    }

    @GetMapping("/{id}")
    public ApiResponse<ItemResponse> getItemById(@PathVariable String id) {
        ItemResponse item = itemService.getItemById(id);
        return ApiResponse.success(item);
    }

    @PostMapping
    public ApiResponse<ItemResponse> createItem(@Valid @RequestBody ItemCreateRequest request) {
        ItemResponse item = itemService.createItem(request);
        return ApiResponse.success("添加成功", item);
    }

    @PutMapping("/{id}")
    public ApiResponse<ItemResponse> updateItem(@PathVariable String id, @RequestBody ItemUpdateRequest request) {
        ItemResponse item = itemService.updateItem(id, request);
        return ApiResponse.success("更新成功", item);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteItem(@PathVariable String id) {
        itemService.deleteItem(id);
        return ApiResponse.success("删除成功", null);
    }

    @PutMapping("/{id}/clean")
    public ApiResponse<ItemResponse> cleanItem(@PathVariable String id) {
        ItemResponse item = itemService.cleanItem(id);
        return ApiResponse.success("清理成功", item);
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<ItemResponse>> searchItems(
            @RequestParam String keyword,
            @RequestParam(required = false) String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ItemResponse> items = itemService.searchItems(keyword, roomId, page, size);
        return ApiResponse.success(items);
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = itemService.getStatistics();
        return ApiResponse.success(stats);
    }
}

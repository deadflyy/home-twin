package com.example.hometwin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}

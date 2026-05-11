package com.example.hometwin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MimoService {

    private static final Logger log = LoggerFactory.getLogger(MimoService.class);

    @Value("${ai.mimo.api-key}")
    private String apiKey;

    @Value("${ai.mimo.base-url}")
    private String baseUrl;

    @Value("${ai.mimo.model}")
    private String model;

    private static final String SYSTEM_PROMPT = """
        你是一个家庭物品识别助手。用户会给你一张家庭物品的照片，你需要识别并返回以下信息：
        1. name: 物品名称（简短，如"乐高积木"、"遥控器"）
        2. category: 物品类别（如"玩具"、"电子产品"、"衣物"、"书籍"、"食品"、"日用品"、"家具"、"其他"）
        3. description: 物品简要描述（一句话）
        4. personHint: 推断该物品最可能属于谁（如"小女孩"、"父亲"、"母亲"、"外婆"、"家庭共用"）
        5. locationHint: 推断该物品最可能放在哪个房间（如"主卧"、"次卧"、"客厅"、"餐厅"、"厨房"、"卫生间"、"阳台"）

        家庭成员信息：小女孩（4岁）、父亲、母亲、外婆。

        请以JSON格式返回，不要包含markdown代码块标记。示例：
        {"name":"乐高积木","category":"玩具","description":"一套彩色塑料拼装积木","personHint":"小女孩","locationHint":"次卧"}
        """;

    public JSONObject recognizeItem(String imageBase64) {
        RestTemplate restTemplate = new RestTemplate();

        JSONObject requestBody = new RequestBodyBuilder(model)
            .addSystemMessage(SYSTEM_PROMPT)
            .addUserImageMessage(imageBase64)
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toJSONString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
            );

            JSONObject responseJson = JSON.parseObject(response.getBody());
            JSONArray choices = responseJson.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                String content = choices.getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

                int start = content.indexOf('{');
                int end = content.lastIndexOf('}');
                if (start != -1 && end != -1) {
                    return JSON.parseObject(content.substring(start, end + 1));
                }
            }

            throw new RuntimeException("AI 返回格式异常");
        } catch (Exception e) {
            log.error("Mimo API 调用失败", e);
            throw new RuntimeException("AI 识别失败: " + e.getMessage());
        }
    }

    private static class RequestBodyBuilder {
        private final JSONObject body;
        private final JSONArray messages;

        public RequestBodyBuilder(String model) {
            this.body = new JSONObject();
            this.messages = new JSONArray();
            body.put("model", model);
            body.put("max_completion_tokens", 1024);
        }

        public RequestBodyBuilder addSystemMessage(String content) {
            JSONObject msg = new JSONObject();
            msg.put("role", "system");
            msg.put("content", content);
            messages.add(msg);
            return this;
        }

        public RequestBodyBuilder addUserImageMessage(String imageBase64) {
            JSONObject msg = new JSONObject();
            msg.put("role", "user");

            JSONArray content = new JSONArray();

            JSONObject imageUrl = new JSONObject();
            JSONObject urlObj = new JSONObject();
            urlObj.put("url", "data:image/jpeg;base64," + imageBase64);
            imageUrl.put("image_url", urlObj);
            imageUrl.put("type", "image_url");
            content.add(imageUrl);

            JSONObject textPart = new JSONObject();
            textPart.put("type", "text");
            textPart.put("text", "请识别这张图片中的物品，返回JSON格式结果。");
            content.add(textPart);

            msg.put("content", content);
            messages.add(msg);
            return this;
        }

        public JSONObject build() {
            body.put("messages", messages);
            return body;
        }
    }
}

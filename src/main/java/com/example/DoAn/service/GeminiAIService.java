package com.example.DoAn.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class GeminiAIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient webClient;

    public GeminiAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<String> generateProductDescription(String productName, String categoryName, String brand) {
        if (apiKey == null || apiKey.equals("YOUR_GEMINI_API_KEY") || apiKey.trim().isEmpty()) {
            return Mono.just("<p><i>Lỗi: Chưa cấu hình Gemini API Key trong hệ thống. Vui lòng cập nhật application.properties.</i></p>");
        }

        String prompt = String.format(
            "Bạn là một chuyên gia viết bài PR sản phẩm công nghệ. Hãy viết một bài mô tả chi tiết, hấp dẫn và chuẩn SEO cho sản phẩm sau:\n" +
            "- Tên sản phẩm: %s\n" +
            "- Danh mục: %s\n" +
            "- Thương hiệu: %s\n\n" +
            "Yêu cầu định dạng:\n" +
            "1. Chỉ trả về mã HTML sạch (không bao gồm ```html ở đầu hoặc cuối).\n" +
            "2. Sử dụng các thẻ <h3>, <h4> cho tiêu đề phụ.\n" +
            "3. Sử dụng thẻ <ul>, <li> để liệt kê các tính năng nổi bật.\n" +
            "4. Sử dụng thẻ <p> cho các đoạn văn.\n" +
            "5. Không viết phần giới thiệu thừa thãi, đi thẳng vào nội dung bài PR.",
            productName, categoryName, (brand != null && !brand.isEmpty()) ? brand : "Chưa xác định"
        );

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt)
                ))
            )
        );

        return webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    try {
                        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        String text = (String) parts.get(0).get("text");
                        
                        // Clean up markdown markers if AI still includes them
                        text = text.replaceAll("(?i)^```html\\s*", "");
                        text = text.replaceAll("(?i)\\s*```$", "");
                        
                        return text.trim();
                    } catch (Exception e) {
                        return "<p><i>Lỗi xử lý kết quả từ AI. Vui lòng thử lại.</i></p>";
                    }
                })
                .onErrorResume(e -> Mono.just("<p><i>Lỗi kết nối tới AI: " + e.getMessage() + "</i></p>"));
    }
}

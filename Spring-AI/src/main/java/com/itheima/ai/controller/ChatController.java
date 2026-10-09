package com.itheima.ai.controller;

import com.itheima.ai.repository.ChatHistoryRepository;
import com.itheima.ai.service.LessonService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.model.Media;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatHistoryRepository chatHistoryRepository;
    private final LessonService lessonService;

    // 定义一个内部类，用于接收 AI 提取的元数据
    @Data
    public static class LessonMetaDTO {
        private String title;
        private Integer grade;
    }

    @RequestMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(@RequestParam("prompt") String prompt,
                             @RequestParam("chatId") String chatId,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files,
                             @RequestParam("teacherId") Integer teacherId){
        // 1.保存会话ID
        chatHistoryRepository.save("chat", chatId);

        // 2.请求模型
        if (files == null || files.isEmpty()) {
            return textChat(prompt, chatId, teacherId);
        } else {
            return multiModalChat(prompt, chatId, files, teacherId);
        }
    }

    /**
     * 核心修改：
     * 不再返回 Mono<Void>，而是直接返回 Flux<String> (原始流)。
     * 我们在内部订阅这个流，收集内容后提取元数据并保存。
     */
    private void processAndSave(Flux<String> contentFlux) {
        String newLessonId = UUID.randomUUID().toString();
        Path path = Paths.get("lessons", newLessonId + ".md");
        String filePath = path.toString();

        // 收集流中的所有文本块
        contentFlux
                .collectList()
                .subscribe(list -> {
                    String fullContent = String.join("", list);

                    // 1. 写入本地文件
                    try {
                        Files.createDirectories(path.getParent());
                        Files.write(path, fullContent.getBytes(StandardCharsets.UTF_8));
                        System.out.println("✅ 文件已保存: " + path);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return; // 文件保存失败则终止
                    }

                    // 2. 使用 AI 提取元数据 (Title & Grade)
                    LessonMetaDTO meta = extractMetadata(fullContent);

                    // 3. 调用 Service 保存数据库
                    lessonService.saveLessonMeta(filePath, meta.getTitle(), newLessonId, String.valueOf(meta.getGrade()), "1001")
                            .subscribe(
                                    lesson -> System.out.println("✅ 数据库记录已创建: " + lesson.getTitle()),
                                    error -> System.err.println("❌ 数据库保存失败: " + error.getMessage())
                            );
                });
    }

    /**
     * 使用 Spring AI 的 BeanOutputConverter 智能提取信息
     */
    private LessonMetaDTO extractMetadata(String content) {
        try {
            // 1. 定义转换器
            BeanOutputConverter<LessonMetaDTO> converter = new BeanOutputConverter<>(LessonMetaDTO.class);

            // 2. 构造提示词：必须明确告诉 AI 返回 JSON
            String extractionPrompt = """
                请分析以下课程内容，提取出课程标题和适用年级。
                如果无法确定年级，默认为 1 年级。
                请直接返回 JSON 格式数据，不要包含其他废话。
                内容如下：
                %s
                """.formatted(content);

            // 3. 调用 AI 进行提取
            // 修复点：使用 .call().entity() 方式，这是 Spring AI 新版本推荐的做法
            LessonMetaDTO result = chatClient.prompt()
                    .user(extractionPrompt) // 直接传入文本
                    .call()
                    .entity(LessonMetaDTO.class); // 自动转换

            // 兜底处理
            if (result == null || result.getTitle() == null) {
                result = new LessonMetaDTO();
                result.setTitle("未命名课程-" + System.currentTimeMillis());
                result.setGrade(1);
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            // 发生错误时返回默认值
            LessonMetaDTO fallback = new LessonMetaDTO();
            fallback.setTitle("未命名课程-" + System.currentTimeMillis());
            fallback.setGrade(1);
            return fallback;
        }
    }

    private Flux<String> multiModalChat(String prompt, String chatId, List<MultipartFile> files, Integer teacherId) {
        List<Media> medias = files.stream()
                .map(file -> new Media(MimeType.valueOf(file.getContentType()), file.getResource()))
                .toList();

        Flux<String> multiClientResponse = chatClient.prompt()
                .user(p -> p.text(prompt).media(medias.toArray(Media[]::new)))
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content()
                .publish()
                .autoConnect(2);

        processAndSave(multiClientResponse);

        return multiClientResponse;
    }

    private Flux<String> textChat(String prompt, String chatId, Integer teacherId) {
        Flux<String> chatClientResponse = chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content()
                .publish()
                .autoConnect(2);

        processAndSave(chatClientResponse);

        return chatClientResponse;
    }
}
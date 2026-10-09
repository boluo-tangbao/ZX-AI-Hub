package com.itheima.ai.service;

import com.itheima.ai.entity.Lesson;
import com.itheima.ai.mapper.LessonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

// 1. 添加缺少的导入
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonMapper lessonMapper;

    public Mono<Lesson> saveLessonMeta(String filePath, String title, String lessonID, String gradeStr, String teacherIdStr) {

        Lesson lesson = new Lesson();
        lesson.setLessonId(lessonID);
        lesson.setTitle(title);

        // 2. 修复类型不匹配：将 String 转换为 Integer
        try {
            lesson.setGrade(Integer.valueOf(gradeStr));
        } catch (NumberFormatException e) {
            lesson.setGrade(0); // 或者抛出自定义异常
        }

        lesson.setFilePath(filePath);

        // 3. 修复类型不匹配：将 String 转换为 Integer
        try {
            lesson.setCreatedBy(Integer.valueOf(teacherIdStr));
        } catch (NumberFormatException e) {
            lesson.setCreatedBy(0);
        }

        lesson.setCreatedAt(LocalDateTime.now());

        // 4. 确保 lessonMapper 能调用 insert
        // MyBatis-Plus 的 BaseMapper 自带 insert 方法
        return Mono.fromRunnable(() -> lessonMapper.insert(lesson))
                .thenReturn(lesson);
    }
}
package com.itheima.ai.dto;

import lombok.Data;

@Data
public class LessonMetaDTO {
    /**
     * 课程标题
     */
    private String title;
    /**
     * 年级 (例如: 1, 2, 3)
     */
    private Integer grade;
}
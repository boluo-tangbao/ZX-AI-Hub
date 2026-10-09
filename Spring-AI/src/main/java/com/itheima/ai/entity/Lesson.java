package com.itheima.ai.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("lesson")
public class Lesson {

    @TableId
    private String lessonId;

    private String title;

    private Integer grade;

    private String filePath;

    private Integer createdBy;

    private LocalDateTime createdAt;
}
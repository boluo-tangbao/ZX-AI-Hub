package com.itheima.ai.mapper;

import com.itheima.ai.entity.Lesson;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LessonMapper extends BaseMapper<Lesson>{

    int insertLesson(Lesson lesson);

    Lesson selectById(@Param("lessonId") String lessonId);

    List<Lesson> selectAll();

}
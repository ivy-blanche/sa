package com.gendml.entity;

import com.gendml.entity.Teacher;
import com.gendml.entity.Student;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Transient;


@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Accessors(chain = true)
@TableName("courseplan")
public class CoursePlan {
    private String Cname;
    @TableId
    private String Cid;
    private String Tid;
    private String Sid;
    private String Monday;
    private String Tuesday;
    private String Wednesday;
    private String Thursday;
    private String Friday;
    private String Saturday;
    private String Sunday;

    @TableField(exist = false)
    @Transient
    private Course course;

    @TableField(exist = false)
    @Transient
    private Teacher teacher;

    @TableField(exist = false)
    @Transient
    private Student student;
}

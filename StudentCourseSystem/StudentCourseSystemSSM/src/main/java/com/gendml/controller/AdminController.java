package com.gendml.controller;

import com.gendml.entity.Admin;
import com.gendml.entity.ResponseResult;
import com.gendml.service.AdminService;
import com.gendml.service.CourseService;
import com.gendml.service.StudentService;
import com.gendml.service.TeacherService;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Зөндөө
 * @create 2022-05-29 0:10
 */
@Slf4j
@RestController
@RequestMapping("admin")
public class   AdminController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseResult<Void> login(@RequestParam("account") String account,
                                      @RequestParam("password") String password){
        log.info("登录请求：account={}, password={}", account, "******");
        Admin admin = adminService.queryByAid(account);
        if (admin == null) {
            log.warn("登录失败：账号不存在 account={}", account);
            return ResponseResult.error("账号不存在");
        }

        log.info("数据库返回的 admin={}", admin);

        if (password.equals(admin.getApassword())) {
            log.info("登录成功 account={}", account);
            return ResponseResult.success(UUID.randomUUID().toString());
        }

        log.warn("登录失败：密码错误 account={}", account);
        return ResponseResult.error("密码错误");
    }

    @GetMapping("/userInfo")
    public ResponseResult<Map<String,String>> login(@RequestParam("id") String id){
        log.info("获取用户信息：id={}", id);
        Admin admin = adminService.queryByAid(id);
        Map<String,String> res = new HashMap<>();
        if(admin != null){
            res.put("name",admin.getAname());
            return ResponseResult.success(res);
        }
        res.put("name","");
        return ResponseResult.error(res);
    }


}

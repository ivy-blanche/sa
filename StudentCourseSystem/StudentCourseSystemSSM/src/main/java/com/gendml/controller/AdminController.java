package com.gendml.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gendml.entity.Admin;
import com.gendml.entity.Audit;
import com.gendml.entity.Course;
import com.gendml.entity.ResponseResult;
import com.gendml.mapper.AuditMapper;
import com.gendml.mapper.CourseMapper;
import com.gendml.service.AdminService;
import com.gendml.service.CourseService;
import com.gendml.service.StudentService;
import com.gendml.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private CourseMapper courseMapper;

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

    /*
    * 课程审核相关接口
    * */

    // 获取待审核课程列表
    @GetMapping("/audit/pending")
    public ResponseResult<List<Audit>> getPendingAudits() {
        QueryWrapper<Audit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "pending");
        List<Audit> audits = auditMapper.selectList(queryWrapper);
        return ResponseResult.success(audits);
    }

    // 批准课程
    @PostMapping("/audit/approve/{id}")
    public ResponseResult<Void> approveAudit(@PathVariable("id") Long id, 
                                            @RequestParam("adminId") String adminId) {
        Audit audit = auditMapper.selectById(id);
        if (audit == null) {
            return ResponseResult.error("审核记录不存在");
        }

        // 创建课程对象并插入到course表
        Course course = new Course();
        course.setCid(audit.getCid());
        course.setTid(audit.getTid());
        course.setCname(audit.getCname());
        course.setCcredit(audit.getCcredit());
        course.setCtype(audit.getCtype());
        course.setCbelongcoll(audit.getCbelongcoll());
        course.setCbelongpro(audit.getCbelongpro());
        course.setMonday(audit.getMonday());
        course.setTuesday(audit.getTuesday());
        course.setWednesday(audit.getWednesday());
        course.setThursday(audit.getThursday());
        course.setFriday(audit.getFriday());
        course.setSaturday(audit.getSaturday());
        course.setSunday(audit.getSunday());
        course.setCsemester(audit.getCsemester());
        course.setCourseweek(audit.getCourseweek());
        course.setCclassroom(audit.getCclassroom());
        course.setCteachbuilding(audit.getCteachbuilding());
        course.setCmodtime(audit.getCmodtime());

        int insertResult = courseMapper.insert(course);
        if (insertResult > 0) {
            // 更新审核状态为已批准
            audit.setStatus("approved");
            audit.setAuditTime(new java.util.Date());
            audit.setAuditAdmin(adminId);
            auditMapper.updateById(audit);
            return ResponseResult.success();
        } else {
            return ResponseResult.error("课程添加失败");
        }
    }

    // 拒绝课程
    @PostMapping("/audit/reject/{id}")
    public ResponseResult<Void> rejectAudit(@PathVariable("id") Long id,
                                           @RequestParam("adminId") String adminId,
                                           @RequestParam(value = "reason", required = false) String reason) {
        Audit audit = auditMapper.selectById(id);
        if (audit == null) {
            return ResponseResult.error("审核记录不存在");
        }

        audit.setStatus("rejected");
        audit.setAuditTime(new java.util.Date());
        audit.setAuditAdmin(adminId);
        audit.setRejectReason(reason);
        auditMapper.updateById(audit);

        return ResponseResult.success();
    }


}

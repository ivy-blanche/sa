package com.gendml.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gendml.entity.*;
import com.gendml.mapper.AuditMapper;
import com.gendml.mapper.CourseMapper;
import com.gendml.mapper.CoursePlanMapper;
import com.gendml.mapper.SCMapper;
import com.gendml.service.CourseService;
import com.gendml.service.StudentService;
import com.gendml.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Зөндөө
 * @create 2022-05-29 0:10
 */
@RestController
@RequestMapping("teacher")
@Slf4j
public class TeacherController {
    @Autowired
    private CoursePlanMapper coursePlanMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private SCMapper scMapper;

    @Autowired
    private AuditMapper auditMapper;
    

    @PostMapping("/login")
    public ResponseResult<Void> login(@RequestParam("account") String account,
                                      @RequestParam("password") String password) {
        Teacher teacher = teacherService.queryOneTeacherByTid(account);
        if (teacher != null && password.equals(teacher.getTpassword())) {
            return ResponseResult.success(UUID.randomUUID().toString());
        }
        return ResponseResult.error("");
    }

    @GetMapping("/userInfo")
    public ResponseResult<Map<String, String>> login(@RequestParam("id") String id) {
        Teacher teacher = teacherService.queryOneTeacherByTid(id);
        Map<String, String> res = new HashMap<>();
        if (teacher != null) {
            res.put("name", teacher.getTname());
            return ResponseResult.success(res);
        }
        res.put("name", "");
        return ResponseResult.error(res);
    }

    /*
     *
     * ******************************************************************************************************************
     * 教师管理课程
     *
     * */
    @PostMapping("insertCourse")
    public ResponseResult<Void> insertCourse(Course Course){
        try {
            log.info("开始处理课程审核申请，课程ID: {}", Course.getCid());
            
            // 创建审核记录
            Audit audit = new Audit();
            // 复制课程字段
            audit.setCid(Course.getCid());
            audit.setTid(Course.getTid());
            audit.setCname(Course.getCname());
            audit.setCcredit(Course.getCcredit());
            audit.setCtype(Course.getCtype());
            audit.setCbelongcoll(Course.getCbelongcoll());
            audit.setCbelongpro(Course.getCbelongpro());
            audit.setMonday(Course.getMonday());
            audit.setTuesday(Course.getTuesday());
            audit.setWednesday(Course.getWednesday());
            audit.setThursday(Course.getThursday());
            audit.setFriday(Course.getFriday());
            audit.setSaturday(Course.getSaturday());
            audit.setSunday(Course.getSunday());
            audit.setCsemester(Course.getCsemester());
            audit.setCourseweek(Course.getCourseweek());
            audit.setCclassroom(Course.getCclassroom());
            audit.setCteachbuilding(Course.getCteachbuilding());
            audit.setCmodtime(Course.getCmodtime());
            // 设置审核状态为待审核
            audit.setStatus("pending");
            audit.setSubmitTime(new Date());
            
            log.debug("审核记录对象: {}", audit);
            
            int res = auditMapper.insert(audit);
            if(res != 0){
                log.info("课程审核申请提交成功，课程ID: {}", Course.getCid());
                return ResponseResult.success();
            } else {
                log.warn("课程审核申请插入失败，课程ID: {}", Course.getCid());
                return ResponseResult.error("审核申请提交失败");
            }
        } catch (Exception e) {
            log.error("插入审核记录时发生异常，课程ID: {}", Course.getCid(), e);
            return ResponseResult.error("服务器内部错误: " + e.getMessage());
        }
    }

    @PostMapping("deleteCourse")
    public ResponseResult<Void> deleteCourse(@RequestParam("Cid") String Cid){
        int res = courseService.deleteCourseByCid(Cid);
        if(res != 0){
            return ResponseResult.success();
        }
        return ResponseResult.error();
    }

    @PostMapping("updateCourse")
    public ResponseResult<Void> updateCourse(Course Course){
        int res = courseService.updateCourse(Course);
        if(res != 0){
            return ResponseResult.success();
        }
        return ResponseResult.error();
    }

    //根据id模糊查询
    @GetMapping("getCourse/{Cid}")
    public ResponseResult<List<Course>> getCourse(@PathVariable("Cid") String Cid){
        List<Course> res = courseService.queryCourseByCid(Cid);
        if(res != null){
            for(Course c : res){
                Teacher resT = teacherService.queryOneTeacherByTid(c.getTid());
                c.setTeacher(resT);
            }
            return ResponseResult.success(res);
        }
        return ResponseResult.error();
    }

    //根据id查询
    @GetMapping("getOneCourse/{Cid}")
    public ResponseResult<Course> getOneCourse(@PathVariable("Cid") String Cid){
        Course res = courseService.queryOneCourseByCid(Cid);
        if(res != null){
            res.setTeacher(teacherService.queryOneTeacherByTid(res.getTid()));
            return ResponseResult.success(res);
        }
        return ResponseResult.error();
    }

    //根据id分页查询全部
    @GetMapping("getPageCourse/{currentPage}/{size}")
    public ResponseResult<List<Course>> getPageCourse(@PathVariable("currentPage") int currentPage,
                                                      @PathVariable("size") int size){
        List<Course> res = courseService.queryAllCourse(currentPage, size);
        if(res != null){
            for(Course c : res){
                Teacher resT = teacherService.queryOneTeacherByTid(c.getTid());
                c.setTeacher(resT);
            }
            return ResponseResult.success(res);
        }
        return ResponseResult.error();
    }

    // 查询教师课程表
    @GetMapping("getSchedule/{Tid}")
    public ResponseResult<List<Course>> getSchedule(@PathVariable("Tid") String Tid) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("Tid", Tid);
        List<Course> courses = courseMapper.selectList(queryWrapper);
        
        if (courses != null && !courses.isEmpty()) {
            for (Course course : courses) {
                Teacher teacher = teacherService.queryOneTeacherByTid(course.getTid());
                course.setTeacher(teacher);
            }
            return ResponseResult.success(courses);
        }
        List<Course> emptyList = new ArrayList<>();
        return ResponseResult.<List<Course>>error("该教师暂无课程", emptyList);
    }



/*
*
* ******************************************************************************************************************
* 教师管理个人信息
* */
    @PostMapping("insertTeacher")
    public ResponseResult<Void> insertTeacher(Teacher Teacher){
        int res = teacherService.insertTeacher(Teacher);
        if(res != 0){
            return ResponseResult.success();
        }
        return ResponseResult.error();
    }

    //删除教师->删除教师信息、教师开课信息、教师课程对应的学生成绩、学生已选的课表
    @PostMapping("deleteTeacher")
    public ResponseResult<Void> deleteTeacher(@RequestParam("Tid") String Tid){
        //删除教师信息
        int res = teacherService.deleteTeacherByTid(Tid);
        //删除教师开课信息
        QueryWrapper<Course> qw1 = new QueryWrapper<>();
        qw1.eq("Tid",Tid);
        courseMapper.delete(qw1);
        //删除教师课程对应的学生成绩
        QueryWrapper<SC> qw2 = new QueryWrapper<>();
        qw1.eq("Tid",Tid);
        scMapper.delete(qw2);
        //删除学生已选的课表
        QueryWrapper<CoursePlan> qw3 = new QueryWrapper<>();
        qw1.eq("Tid",Tid);
        coursePlanMapper.delete(qw3);
        if(res != 0){
            return ResponseResult.success();
        }
        return ResponseResult.error();
    }

    @PostMapping("updateTeacher")
    public ResponseResult<Void> updateTeacher(Teacher Teacher){
        int res = teacherService.updateTeacher(Teacher);
        if(res != 0){
            return ResponseResult.success();
        }
        return ResponseResult.error();
    }

    //根据id模糊查询
    @GetMapping("getTeacher/{Tid}")
    public ResponseResult<List<Teacher>> getTeacher(@PathVariable("Tid") String Tid){
        List<Teacher> res = teacherService.queryTeacherByTid(Tid);
        if(res != null){
            return ResponseResult.success(res);
        }
        return ResponseResult.error();
    }

    //根据id查询
    @GetMapping("getOneTeacher/{Tid}")
    public ResponseResult<Teacher> getOneTeacher(@PathVariable("Tid") String Tid){
        Teacher res = teacherService.queryOneTeacherByTid(Tid);
        if(res != null){
            return ResponseResult.success(res);
        }
        return ResponseResult.error();
    }

    //根据id分页查询全部
    @GetMapping("getPageTeacher/{currentPage}/{size}")
    public ResponseResult<List<Teacher>> getPageTeacher(@PathVariable("currentPage") int currentPage,
                                                        @PathVariable("size") int size){
        List<Teacher> res = teacherService.queryAllTeacher(currentPage, size);
        if(res != null){
            return ResponseResult.success(res);
        }
        return ResponseResult.error();
    }

    //给学生打成绩
    @PostMapping("setGrade")
    public ResponseResult<Void> setGrade(SC sc){
        QueryWrapper<SC> qw = new QueryWrapper<>();
        qw.eq("Sid",sc.getSid());
        qw.eq("Cid",sc.getCid());
        qw.eq("Tid",sc.getTid());
        int res = scMapper.update(sc,qw);
        if(res != 0){
            return ResponseResult.success();
        }
        return ResponseResult.error();
    }

    //查成绩 根据教师id和课程id
    @GetMapping("queryGrade/{Tid}/{Cid}")
    public ResponseResult<List<SC>> queryGrade(@PathVariable("Tid") String tid,
                                               @PathVariable("Cid") String cid) {
        QueryWrapper<SC> qw = new QueryWrapper<>();
        qw.eq("Tid",tid);
        qw.eq("Cid",cid);
        List<SC> res = scMapper.selectList(qw);
        if(res != null){
            for(SC sc : res){
                sc.setCourse(courseService.queryOneCourseByCid(sc.getCid()));
                sc.setStudent(studentService.queryOneStudentBySid(sc.getSid()));
            }
            return ResponseResult.success(res);
        }
        return ResponseResult.error();
    }

    // 条件分页查询教师
    @GetMapping("getPageTeacherByCondition")
    public ResponseResult<Map<String, Object>> getPageTeacherByCondition(
            @RequestParam(value = "tid", required = false) String tid,
            @RequestParam(value = "tinstitution", required = false) String tinstitution,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        Teacher teacher = new Teacher();
        teacher.setTid(tid);
        teacher.setTinstitution(tinstitution);
        
        List<Teacher> teachers = teacherService.queryTeachersByCondition(teacher, page, size);
        int total = teacherService.countTeachersByCondition(teacher);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", teachers);
        result.put("total", total);
        
        return ResponseResult.success(result);
    }
}

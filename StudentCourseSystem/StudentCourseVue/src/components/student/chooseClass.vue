<template>
  <div id="chooseClass">
    <div class="main">
      <el-row class="course_box card" :gutter="20">
        <div class="title">
          当前可选课程范围：{{ studentInfo.sinstitution }} |
          {{ studentInfo.sprofession }}专业课程
        </div>
        <div class="course-list">
          <div
            v-for="item in courseData"
            :key="item.cid"
            class="course-list-item"
          >
            <span class="course-name">{{ item.cname }}</span>
            <el-button
              @click="choose(item)"
              :type="selectedCourses[item.cid] ? 'success' : 'primary'"
              size="small"
              :disabled="selectedCourses[item.cid]"
            >
              {{ selectedCourses[item.cid] ? "已选" : "选课" }}
            </el-button>
          </div>
        </div>
      </el-row>
      <div class="table">
        <div class="class_box card" style="overflow: visible">
          <div class="tableTitle">我的课程表</div>
          <div class="header">
            <div class="day">周一</div>
            <div class="day">周二</div>
            <div class="day">周三</div>
            <div class="day">周四</div>
            <div class="day">周五</div>
          </div>
          <div class="choose_card">
            <div class="class" v-for="(item, index) in classTable" :key="index">
              <div class="class_inner" v-if="item.index == ''"></div>
              <div
                class="ban"
                :class="{ choosed: true }"
                v-if="item.index != ''"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      obj: {
        cname: "",
        cid: "",
        tid: "",
        monday: "",
        tuesday: "",
        wednesday: "",
        thursday: "",
        friday: "",
      },
      studentInfo: {},
      courseData: [],
      classTable: [],
      selectedCourses: {}, // 跟踪已选课程
    };
  },
  methods: {
    getStudentClass() {
      this.axios
        .get("student/getSchedule/" + this.$store.state.id)
        .then((res) => {
          if (res.data.code == 200) {
            this.classTable = this.parseData(res.data.data, false);
            // console.log(this.classTable)
          }
        })
        .catch((err) => {
          console.log(err);
          if (err.response) {
            const status = err.response.status;
            if (status === 404) {
              this.$message.error("请求的资源不存在");
            } else if (status === 500) {
              this.$message.error("服务器内部错误，请联系管理员");
            } else {
              this.$message.error(`服务器错误: ${status}`);
            }
          } else if (err.request) {
            if (err.code === "ECONNABORTED") {
              this.$message.error("请求超时，请检查网络连接");
            } else {
              this.$message.error("网络错误，请检查网络连接");
            }
          } else {
            this.$message.error("发生未知错误");
          }
        });
    },
    getWeekDay(weekday) {
      switch (weekday) {
        case "monday":
          return 1;
        case "tuesday":
          return 2;
        case "wednesday":
          return 3;
        case "thursday":
          return 4;
        case "friday":
          return 5;
        default:
          return 0;
      }
    },
    getCourseData() {
      this.axios
        .get("teacher/getPageCourse/1/100")
        .then((res) => {
          if (res.data.code == 200) {
            // //去除某个课程无开课老师的情况，逻辑上不需要从此处更改，需要在管理员删除教师后直接清除其所有数据，这里我比较懒，直接改的这一模块
            // var temp = res.data.data;
            // var result = [];
            // for (var i in temp) {
            //   if (temp[i].teacher != null) {
            //     result.push(temp[i]);
            //   }
            // }
            this.courseData = res.data.data;
            // console.log(this.courseData)
          }
        })
        .catch((err) => {
          console.log(err);
          if (err.response) {
            const status = err.response.status;
            if (status === 404) {
              this.$message.error("请求的资源不存在");
            } else if (status === 500) {
              this.$message.error("服务器内部错误，请联系管理员");
            } else {
              this.$message.error(`服务器错误: ${status}`);
            }
          } else if (err.request) {
            if (err.code === "ECONNABORTED") {
              this.$message.error("请求超时，请检查网络连接");
            } else {
              this.$message.error("网络错误，请检查网络连接");
            }
          } else {
            this.$message.error("发生未知错误");
          }
        });
    },
    // parseData:将获取到的classData（学生已选课节）填充数据，转存到classTable中
    parseData(data, isOne) {
      let finalData = [];
      var scheduleList = [];
      for (let i = 0; i < data.length; i++) {
        var schedule = {
          cname: data[i].cname,
          tname: data[i].teacher ? data[i].teacher.tname : "",
          schedule: {
            monday: data[i].monday == null ? "" : data[i].monday,
            tuesday: data[i].tuesday == null ? "" : data[i].tuesday,
            wednesday: data[i].wednesday == null ? "" : data[i].wednesday,
            thursday: data[i].thursday == null ? "" : data[i].thursday,
            friday: data[i].friday == null ? "" : data[i].friday,
          },
          index: "",
          courseweek: data[i].courseweek,
          cclassroom: isOne
            ? data[i].cclassroom
            : data[i].course
            ? data[i].course.cclassroom
            : "",
          cteachbuilding: isOne
            ? data[i].cteachbuilding
            : data[i].course
            ? data[i].course.cteachbuilding
            : "",
        };

        for (var k in schedule.schedule) {
          var timeList = schedule.schedule[k].split(",");
          // console.log(schedule.schedule)
          for (var j of timeList) {
            var tmp = {
              cname: data[i].cname,
              tname: data[i].teacher ? data[i].teacher.tname : "",
              schedule: {
                monday: data[i].monday == null ? "" : data[i].monday,
                tuesday: data[i].tuesday == null ? "" : data[i].tuesday,
                wednesday: data[i].wednesday == null ? "" : data[i].wednesday,
                thursday: data[i].thursday == null ? "" : data[i].thursday,
                friday: data[i].friday == null ? "" : data[i].friday,
              },
              index: (this.getWeekDay(k) - 1) * 4 + parseInt(j),
              courseweek: data[i].courseweek,
              cclassroom: isOne
                ? data[i].cclassroom
                : data[i].course
                ? data[i].course.cclassroom
                : "",
              cteachbuilding: isOne
                ? data[i].cteachbuilding
                : data[i].course
                ? data[i].course.cteachbuilding
                : "",
            };
            scheduleList.push(tmp);
          }
        }
      }
      //创建20个课程 填满
      for (let i = 0; i < 20; i++) {
        finalData.push({
          cname: "",
          tname: "",
          schedule: {
            monday: "",
            tuesday: "",
            wednesday: "",
            thursday: "",
            friday: "",
          },
          index: "",
          courseweek: "",
          cclassroom: "",
          cteachbuilding: "",
        });
      }
      //把已有的课程放入finaData
      for (var ele of scheduleList) {
        finalData[ele.index - 1] = ele;
      }
      return finalData;
    },
    // 直接提交选课请求
    choose(course) {
      this.$confirm(`确定要选择《${course.cname}》课程吗？`, "确认选课", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          // 获取课程详细信息
          this.axios
            .get("teacher/getOneCourse/" + course.cid)
            .then((res) => {
              if (res.data.code == 200) {
                var r = res.data.data;
                // 填充课程信息
                for (var i in this.obj) {
                  this.obj[i] = r[i] == null ? "" : r[i];
                }
                // 直接提交选课请求
                this.submit();
              }
            })
            .catch((err) => {
              console.log(err);
              if (err.response) {
                const status = err.response.status;
                if (status === 404) {
                  this.$message.error("课程信息不存在");
                } else if (status === 500) {
                  this.$message.error("服务器内部错误，请联系管理员");
                } else {
                  this.$message.error(`服务器错误: ${status}`);
                }
              } else if (err.request) {
                if (err.code === "ECONNABORTED") {
                  this.$message.error("请求超时，请检查网络连接");
                } else {
                  this.$message.error("网络错误，请检查网络连接");
                }
              } else {
                this.$message.error("发生未知错误");
              }
            });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消选课",
          });
        });
    },

    // 初始化已选课程状态
    initializeSelectedCourses() {
      this.axios
        .get("student/getSchedule/" + this.$store.state.id)
        .then((res) => {
          if (res.data.code == 200) {
            res.data.data.forEach((coursePlan) => {
              this.selectedCourses[coursePlan.cid] = true;
            });
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    submit() {
      const coursePlanData = {
        Cname: this.obj.cname,
        Sid: this.$store.state.id,
        Cid: this.obj.cid,
        Tid: this.obj.tid,
        Monday: this.obj.monday,
        Tuesday: this.obj.tuesday,
        Wednesday: this.obj.wednesday,
        Thursday: this.obj.thursday,
        Friday: this.obj.friday,
      };

      this.axios
        .post("student/insertCoursePlan", coursePlanData)
        .then((res) => {
          if (res.data.code == 200) {
            // 更新已选课程状态
            this.selectedCourses[this.obj.cid] = true;
            this.$message({
              showClose: true,
              message: "选课成功",
              type: "success",
            });
            // 立即刷新课表显示
            this.getStudentClass();
            // 不跳转，留在当前页面继续选课
          } else {
            // 处理后端返回的错误信息
            this.$message.error(res.data.message || "选课失败");
          }
        })
        .catch((err) => {
          console.log(err);
          if (err.response) {
            const status = err.response.status;
            if (status === 404) {
              this.$message.error("请求的资源不存在");
            } else if (status === 500) {
              this.$message.error("服务器内部错误，请联系管理员");
            } else {
              this.$message.error(`服务器错误: ${status}`);
            }
          } else if (err.request) {
            if (err.code === "ECONNABORTED") {
              this.$message.error("请求超时，请检查网络连接");
            } else {
              this.$message.error("网络错误，请检查网络连接");
            }
          } else {
            this.$message.error("发生未知错误");
          }
        });
    },
  },
  mounted() {
    this.axios
      .get("student/getOneStudent/" + this.$store.state.id)
      .then((res) => {
        if (res.data.code == 200) {
          this.studentInfo = res.data.data;
          // console.log(this.studentInfo)

          // 检查学生选课状态
          if (this.studentInfo.status !== "active") {
            this.$message.warning(
              "对不起，当前您不处于选课阶段，如有需要，请与管理员联系！"
            );
            // 清空课程数据，防止显示选课界面
            this.courseData = [];
            return;
          }

          // 状态正常，继续加载选课数据
          this.getStudentClass(); //连带执行parseData
          this.getCourseData();
          this.initializeSelectedCourses(); // 初始化已选课程状态
        }
      })
      .catch((err) => {
        console.log(err);
        if (err.response) {
          const status = err.response.status;
          if (status === 404) {
            this.$message.error("学生信息不存在");
          } else if (status === 500) {
            this.$message.error("服务器内部错误，请联系管理员");
          } else {
            this.$message.error(`服务器错误: ${status}`);
          }
        } else if (err.request) {
          if (err.code === "ECONNABORTED") {
            this.$message.error("请求超时，请检查网络连接");
          } else {
            this.$message.error("网络错误，请检查网络连接");
          }
        } else {
          this.$message.error("发生未知错误");
        }
      });
  },
};
</script>

<style lang="scss" scoped>
.choose {
  background-color: #67c23a !important;
  border-color: #67c23a !important;
  color: white !important;
}

.choosed {
  background-color: #409eff !important;
  border-color: #409eff !important;
  color: white !important;
}

.error {
  background-color: #f56c6c !important;
  border-color: #f56c6c !important;
  color: white !important;
}

.main {
  position: relative;
  display: flex;

  .course_box {
    flex: 1 1 auto;
    margin-right: 20px !important;
    margin-left: 0px !important;
    padding-left: 10px;
    padding-right: 10px;
    padding-bottom: 10px;

    .title {
      color: white;
      background-color: #409eff;
      margin: -20px -10px;
      margin-bottom: 20px;
      padding: 20px;
      border-radius: 10px 10px 0 0;
    }

    .course {
      margin-bottom: 21px;
      padding: 25px;
      border-radius: 10px;
      box-sizing: border-box;
      border: 3px solid rgba(0, 0, 0, 0.12);
      cursor: pointer;
      font-size: 14px;
      color: #606266;

      .group {
        display: flex;
        line-height: 1.7;

        .name {
          font-size: 20px;
          font-weight: bold;
        }

        .credit {
          margin-right: 10px;
        }
      }
    }

    .el-button.is-circle {
      width: 50px;
      height: 50px;
      position: absolute;
      bottom: -4px;
      right: 30px;
    }
  }

  .class_box {
    width: 310px;
    height: 267px;

    .tableTitle {
      text-align: center;
      font-weight: bold;
      padding-bottom: 10px;
    }

    .header {
      display: flex;

      .day {
        width: 20%;
        font-size: 14px;
        text-align: center;
      }
    }

    .choose_card {
      display: flex;
      flex-direction: column;
      flex-wrap: wrap;
      height: 220px;

      .class {
        width: 20%;
        height: 55px;
        padding: 10px;
        box-sizing: border-box;

        .class_inner {
          height: 100%;
          border-radius: 5px;
          border: 2px solid rgba(0, 0, 0, 0.2);
        }

        .ban {
          height: 100%;
          border-radius: 5px;
          border: 2px solid rgba(0, 0, 0, 0.1);
          background: rgba(0, 0, 0, 0.2);
        }
      }
    }
  }

  .tag {
    margin: 360px -20px;
    color: white;
  }

  .course-list {
    .course-list-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      border-bottom: 1px solid #ebeef5;
      transition: background-color 0.3s;

      &:hover {
        background-color: #f5f7fa;
      }

      .course-name {
        font-size: 16px;
        font-weight: 500;
        color: #606266;
      }

      .course-list-item .el-button {
        margin-left: auto; /* 将按钮推到最右边 */
        flex-shrink: 0; /* 防止按钮被压缩 */
      }
    }
  }

  .conflict-prompt {
    position: fixed;
    top: 30%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 1000;
    padding: 20px;
    border-radius: 8px;
    text-align: center;
    font-size: 16px;
    font-weight: bold;
    color: white;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}
</style>

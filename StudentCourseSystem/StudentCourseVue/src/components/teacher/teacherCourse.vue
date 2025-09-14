<template>
  <div id="teacherCourse">
    <div class="table">
      <div class="table_head">
        <div class="day">周一</div>
        <div class="day">周二</div>
        <div class="day">周三</div>
        <div class="day">周四</div>
        <div class="day">周五</div>
        <div class="day">周六</div>
        <div class="day">周日</div>
      </div>
      <div class="table_main">
        <div class="table_aside">
          <div class="time">1</div>
          <div class="time">2</div>
          <div class="time">3</div>
          <div class="time">4</div>
          <div class="time">5</div>
        </div>
        <div class="table_content">
          <div class="class" v-for="(item, index) in data" :key="index">
            <div class="class_inner" v-if="item.index != ''">
              <div class="course">{{ item.cname }}</div>
              <div class="class_info">
                <div class="Teacher">
                  <i class="el-icon-info"></i>
                  {{ $store.state.name }}
                </div>
                <div class="classroom">
                  <i class="el-icon-location"></i>
                  {{ item.cteachbuilding }}{{ item.cclassroom }}
                </div>
              </div>
            </div>
            <div class="no_class" v-if="item.index == ''"></div>
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
      data: [],
    };
  },
  methods: {
    parseData(data) {
      let finalData = [];
      var scheduleList = [];
      for (let i = 0; i < data.length; i++) {
        var schedule = {
          cname: data[i].cname,
          schedule: {
            monday: data[i].monday == null ? "" : data[i].monday,
            tuesday: data[i].tuesday == null ? "" : data[i].tuesday,
            wednesday: data[i].wednesday == null ? "" : data[i].wednesday,
            thursday: data[i].thursday == null ? "" : data[i].thursday,
            friday: data[i].friday == null ? "" : data[i].friday,
            saturday: data[i].saturday == null ? "" : data[i].saturday,
            sunday: data[i].sunday == null ? "" : data[i].sunday,
          },
          index: "",
          courseweek: data[i].courseweek,
          cclassroom: data[i].cclassroom,
          cteachbuilding: data[i].cteachbuilding,
        };
        for (var k in schedule.schedule) {
          var timeList = schedule.schedule[k].split(",");
          // console.log(schedule.schedule)
          for (var j of timeList) {
            var tmp = {
              cname: data[i].cname,
              index: (this.getWeekDay(k) - 1) * 5 + parseInt(j),
              courseweek: data[i].courseweek,
              cclassroom: data[i].cclassroom,
              cteachbuilding: data[i].cteachbuilding,
            };
            scheduleList.push(tmp);
          }
        }
      }
      //创建35个课程 填满 (7天 * 5节课)
      for (let i = 0; i < 35; i++) {
        finalData.push({
          cname: "",
          index: "",
          courseweek: "",
          cclassroom: "",
          cteachbuilding: "",
        });
      }
      //把已有的课程放入finalData
      for (var ele of scheduleList) {
        finalData[ele.index - 1] = ele;
      }
      return finalData;
    },
    getTeacherClass() {
      this.axios
        .get("teacher/getSchedule/" + this.$store.state.id)
        .then((res) => {
          if (res.data.code == 200) {
            this.data = this.parseData(res.data.data);
            // console.log(this.data)
            if (res.data.data.length == 0) {
              this.notify();
            }
          }
        })
        .catch((err) => {
          console.log(err);
          this.$message("服务器无法连接");
        });
    },
    notify() {
      this.$notify.info({
        title: "提示",
        message: "添加课程并设置上课时间后即可显示教师课表内容",
        customClass: "notifys",
        offset: 100,
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
        case "saturday":
          return 6;
        case "sunday":
          return 7;
        default:
          return 0;
      }
    },
  },
  mounted() {
    this.getTeacherClass();
  },
};
</script>

<style lang="scss" scoped>
.table_head {
  padding: 20px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
  margin-bottom: 20px;
  display: flex;
  margin-left: 85px;

  .day {
    width: 14.28%; /* 100% / 7 days */
    text-align: center;
  }
}

.table_main {
  display: flex;

  .table_aside {
    width: 50px;
    height: 500px; /* 5 time slots * 100px */
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
    padding: 8px;
    margin-right: 15px;

    .time {
      line-height: 100px;
      text-align: center;
      font-size: 1.2rem;
    }
  }

  .table_content {
    height: 500px; /* 5 time slots * 100px */
    background-color: #fff;
    border-radius: 10px;
    padding: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
    position: relative;
    display: flex;
    flex-direction: column;
    flex-wrap: wrap;
    width: 100%;

    .class {
      height: 100px;
      width: 14.28%; /* 100% / 7 days */
      padding: 8px;
      box-sizing: border-box;

      @media screen and (max-width: 1540px) {
        font-size: 12px;

        .course {
          min-height: 40px !important;
          font-size: 0.9rem !important;
        }

        .class_info {
          display: block !important;
          line-height: 16px !important;
        }
      }

      .class_inner {
        background-color: #409eff;
        height: 100%;
        border-radius: 8px;
        padding: 12px;
        box-sizing: border-box;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);

        .course {
          color: white;
          font-size: 1rem;
          font-weight: bold;
          min-height: 45px;
        }

        .class_info {
          display: -webkit-box;
          line-height: 20px;
          color: white;
          font-size: 0.85rem;

          .Teacher {
            margin-right: 8px;
          }
        }
      }

      .no_class {
        background-color: rgba(0, 0, 0, 0.05);
        height: 100%;
        border-radius: 8px;
        padding: 15px;
        box-sizing: border-box;
        line-height: 60px;
        text-align: center;
        font-size: 1rem;
        font-weight: lighter;
        color: rgba(0, 0, 0, 0.3);
      }
    }
  }
}

.notifys {
  background-color: #409eff !important;
  color: #fff !important;
}
</style>

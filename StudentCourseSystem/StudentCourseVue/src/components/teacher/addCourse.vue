<template>
  <div id="manageCourse">
    <h1>添加课程</h1>
    <div class="main">
      <div class="title">填写课程信息：</div>
      <hr />
      <el-form :model="form" ref="form" :rules="rules" label-position="top">
        <!-- 第一排: 年级, 院系, 课程类别 -->
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="年级" prop="Csemester">
              <el-select v-model="form.Csemester" placeholder="请选择年级">
                <el-option label="2022" value="2022"></el-option>
                <el-option label="2023" value="2023"></el-option>
                <el-option label="2024" value="2024"></el-option>
                <el-option label="2025" value="2025"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="院系" prop="Cbelongcoll">
              <el-select v-model="form.Cbelongcoll" placeholder="请选择院系">
                <el-option
                  v-for="item in Faculty"
                  :key="item.id"
                  :label="item.fname"
                  :value="item.fname"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="课程类别" prop="Ctype">
              <el-select v-model="form.Ctype" placeholder="请选择课程类别">
                <el-option label="必修" value="必修"></el-option>
                <el-option label="选修" value="选修"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第二排: 课程ID, 课程名称, 学分 -->
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="课程ID" prop="Cid">
              <el-input
                v-model="form.Cid"
                placeholder="请填写课程ID"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="课程名称" prop="Cname">
              <el-input
                v-model="form.Cname"
                placeholder="请填写课程名称"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="学分" prop="Ccredit">
              <el-input
                v-model="form.Ccredit"
                placeholder="请填写学分"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第三排: 周次, 教学楼, 教室 -->
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="周次" prop="Courseweek">
              <el-input
                v-model="form.Courseweek"
                placeholder="例如：1-10周"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="教学楼" prop="Cteachbuilding">
              <el-input
                v-model="form.Cteachbuilding"
                placeholder="请填写教学楼"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="教室" prop="Cclassroom">
              <el-input
                v-model="form.Cclassroom"
                placeholder="请填写教室"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-button
          type="success"
          round
          icon="el-icon-circle-plus"
          style="margin-top: 20px"
          @click="submit"
          >添加课程
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        Tid: "",
        Cid: "",
        Cname: "",
        Ccredit: "",
        Cintroduction: "",
        Ctype: "",
        Cbelongcoll: "",
        Cbelongpro: "",
        Csemester: "",
        Courseweek: "",
        Cclassroom: "",
        Cteachbuilding: "",
        Cmodtime: "",
      },
      Faculty: [],
      Semester: [
        {
          value: 1,
          label: "大一上学期",
        },
        {
          value: 2,
          label: "大一下学期",
        },
        {
          value: 3,
          label: "大二上学期",
        },
        {
          value: 4,
          label: "大二下学期",
        },
        {
          value: 5,
          label: "大三上学期",
        },
        {
          value: 6,
          label: "大三下学期",
        },
        {
          value: 7,
          label: "大四上学期",
        },
        {
          value: 8,
          label: "大四下学期",
        },
      ],
      rules: {
        Cid: [
          {
            required: true,
            message: "请输入课程ID",
            trigger: ["blur", "choose"],
          },
        ],
        Cname: [
          {
            required: true,
            message: "请输入课程名称",
            trigger: ["blur", "choose"],
          },
        ],
        Ccredit: [
          {
            required: true,
            message: "请输入学分",
            trigger: ["blur", "choose"],
          },
        ],
        Cbelongpro: [
          {
            required: true,
            message: "请输入课程所属专业",
            trigger: ["blur", "choose"],
          },
        ],
        Courseweek: [
          {
            required: true,
            message: "请输入课程周次",
            trigger: ["blur", "choose"],
          },
        ],
        Cteachbuilding: [
          {
            required: true,
            message: "请输入课程开课教学楼",
            trigger: ["blur", "choose"],
          },
        ],
        Cclassroom: [
          {
            required: true,
            message: "请输入课程教室",
            trigger: ["blur", "choose"],
          },
        ],
        Csemester: [
          {
            required: true,
            message: "请选择教授年级",
            trigger: ["blur", "choose"],
          },
        ],
        Cbelongcoll: [
          {
            required: true,
            message: "请选择教授院系",
            trigger: ["blur", "choose"],
          },
        ],
        Ctype: [
          {
            required: true,
            message: "请选择课程类别",
            trigger: ["blur", "choose"],
          },
        ],
      },
    };
  },
  methods: {
    submit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.$confirm("您确定要执行此操作吗?", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          })
            .then(() => {
              this.addCourse();
            })
            .catch(() => {
              this.$message({
                type: "info",
                message: "操作已取消",
              });
            });
        } else {
          return false;
        }
      });
    },
    addCourse() {
      this.form.Tid = this.$store.state.id;
      this.form.Cmodtime = new Date().toLocaleString();
      var param = this.$qs.stringify(this.form);
      this.axios
        .post("teacher/insertCourse", param)
        .then((res) => {
          if (res.data.code == 200) {
            this.$message.success("提交成功");
            this.$refs.form.resetFields();
          } else {
            // 处理后端返回的错误消息
            const errorMsg = res.data.message || "提交失败";
            this.$message.error(errorMsg);
          }
        })
        .catch((err) => {
          console.log(err);
          if (err.response && err.response.data && err.response.data.message) {
            // 显示后端返回的具体错误信息
            this.$message.error(err.response.data.message);
          } else {
            this.$message.error("提交失败，请检查网络连接或联系管理员");
          }
        });
    },
    getfaculty() {
      this.axios
        .get("/queryFaculty")
        .then((res) => {
          if (res.data.code == 200) {
            this.Faculty = res.data.data;
          }
        })
        .catch((err) => {
          console.log(err);
          this.$message("服务器无法连接，无法获取院系列表");
        });
    },
  },
  mounted() {
    this.getfaculty();
  },
};
</script>

<style lang="scss" scoped>
.main {
  background-color: #fff;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);

  .el-select {
    width: 90%;
  }
}
</style>

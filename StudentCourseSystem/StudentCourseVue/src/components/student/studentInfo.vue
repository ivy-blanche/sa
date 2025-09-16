<template>
  <div id="studentInfo">
    <div class="container">
      <h1>重置密码</h1>
      <div class="card semester">
        <el-descriptions title="" :column="2" border>
          <!-- 第一行：学号与姓名 -->
          <el-descriptions-item label="学号">{{
            studentInfo.sid
          }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{
            studentInfo.sname
          }}</el-descriptions-item>

          <!-- 第二行：学院与专业 -->
          <el-descriptions-item label="学院">{{
            studentInfo.sinstitution
          }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{
            studentInfo.sprofession
          }}</el-descriptions-item>

          <!-- 第三行：年级与班级 -->
          <el-descriptions-item label="年级">{{
            studentInfo.sgrade
          }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{
            studentInfo.sclass
          }}</el-descriptions-item>

          <!-- 第四行：性别与年龄 -->
          <el-descriptions-item label="性别">{{
            studentInfo.ssex
          }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{
            studentInfo.sage
          }}</el-descriptions-item>

          <!-- 第五行：账号类型与密码 -->
          <el-descriptions-item label="账号类型">
            <el-tag size="small">{{
              this.$store.state.identity | identity
            }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="密码">******</el-descriptions-item>
        </el-descriptions>
        <el-row style="margin-top: 10px">
          <el-button
            type="primary"
            size=""
            style="float: right"
            @click="dialogFormVisible = true"
            >修改密码
          </el-button>
        </el-row>

        <el-dialog title="修改密码" :visible.sync="dialogFormVisible">
          <el-form
            :model="passwordForm"
            :rules="rules"
            label-width="100px"
            :label-position="'left'"
            ref="passwordForm"
          >
            <el-form-item label="学号">
              <el-input v-model="studentInfo.sid" disabled></el-input>
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="studentInfo.sname" disabled></el-input>
            </el-form-item>
            <el-form-item label="当前密码" prop="currentPassword">
              <el-input
                v-model="passwordForm.currentPassword"
                type="password"
                show-password
                @blur="validateCurrentPassword"
              ></el-input>
              <div
                v-if="currentPasswordError"
                style="color: red; font-size: 12px; margin-top: 5px"
              >
                {{ currentPasswordError }}
              </div>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
              ></el-input>
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
              ></el-input>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button
              @click="
                dialogFormVisible = false;
                resetForm();
              "
              >取 消</el-button
            >
            <el-button type="primary" @click="validateAndSubmit"
              >确 定</el-button
            >
          </div>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      studentInfo: {
        sid: "",
        sname: "",
        ssex: "",
        sage: "",
        sgrade: "",
        sprofession: "",
        sclass: "",
        sinstitution: "",
        spassword: "",
      },
      passwordForm: {
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      },
      currentPasswordValid: false,
      currentPasswordError: "",
      dialogFormVisible: false,
      rules: {
        currentPassword: [
          { required: true, message: "请输入当前密码", trigger: "blur" },
        ],
        newPassword: [
          { required: true, message: "请输入新密码", trigger: "blur" },
          { min: 6, message: "密码长度不能少于6位", trigger: "blur" },
        ],
        confirmPassword: [
          { required: true, message: "请确认新密码", trigger: "blur" },
          {
            validator: (rule, value, callback) => {
              if (value !== this.passwordForm.newPassword) {
                callback(new Error("两次输入密码不一致"));
              } else {
                callback();
              }
            },
            trigger: "blur",
          },
        ],
      },
    };
  },
  methods: {
    validateAndSubmit() {
      this.$refs.passwordForm.validate((valid) => {
        if (valid) {
          // 首先验证当前密码是否正确
          if (
            this.passwordForm.currentPassword !== this.studentInfo.spassword
          ) {
            this.$message.error("当前密码输入错误");
            return;
          }

          // 验证新密码和确认密码是否一致
          if (
            this.passwordForm.newPassword !== this.passwordForm.confirmPassword
          ) {
            this.$message.error("两次输入的新密码不一致");
            return;
          }

          // 准备更新数据
          const updateData = {
            Sid: this.studentInfo.sid,
            Spassword: this.passwordForm.newPassword,
            Smodtime: new Date().toLocaleString(),
          };

          // 发送更新请求
          this.axios
            .post("student/updateStudent", this.$qs.stringify(updateData))
            .then((res) => {
              if (res.data.code == 200) {
                this.$message.success("密码修改成功，请重新登录");
                this.dialogFormVisible = false;
                this.resetForm();
                // 更新本地存储的密码信息
                this.studentInfo.spassword = this.passwordForm.newPassword;

                // 自动退出当前账户
                // 清除Cookie
                document.cookie = "=;";
                // 清除session
                sessionStorage.clear();
                // 跳转到登录页面
                window.location.href = "/login.html";
              } else {
                this.$message.error("密码修改失败");
              }
            })
            .catch((err) => {
              console.log(err);
              this.$message.error("接口异常，无法连接服务器");
            });
        } else {
          this.$message.error("请填写完整的密码信息");
          return false;
        }
      });
    },
    validateCurrentPassword() {
      if (this.passwordForm.currentPassword) {
        if (this.passwordForm.currentPassword === this.studentInfo.spassword) {
          this.currentPasswordValid = true;
          this.currentPasswordError = "";
        } else {
          this.currentPasswordValid = false;
          this.currentPasswordError = "当前密码输入错误";
        }
      } else {
        this.currentPasswordValid = false;
        this.currentPasswordError = "请输入当前密码";
      }
    },
    resetForm() {
      this.passwordForm = {
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      };
      this.currentPasswordValid = false;
      this.currentPasswordError = "";
    },
    updateStudentArray() {
      this.axios
        .get("student/getOneStudent/" + this.$store.state.id)
        .then((res) => {
          if (res.data.code == 200) {
            // 保留所有字段用于显示
            this.studentInfo = res.data.data;
          }
        })
        .catch((err) => {
          console.log(err);
          this.$message("无法连接到服务器，请检查接口");
        });
    },
  },
  mounted() {
    this.updateStudentArray();
  },
};
</script>

<style lang="scss" scoped>
.container {
  width: 550px;
}

.semester {
  .title {
    padding-bottom: 20px;
    margin-bottom: 10px;
    // border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  }
}
</style>

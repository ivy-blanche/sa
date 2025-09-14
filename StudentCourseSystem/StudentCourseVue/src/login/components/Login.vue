<template>
  <div id="login">
    <div class="title">学生选课系统</div>
    <hr />
    <el-form
      :model="loginForm"
      :rules="rules"
      label-width="50px"
      :label-position="'left'"
      ref="loginForm"
    >
      <el-form-item label="" prop="account">
        <el-input
          v-model="loginForm.account"
          placeholder="学号/职工号"
        ></el-input>
      </el-form-item>
      <el-form-item label="" prop="password">
        <el-input
          type="password"
          v-model="loginForm.password"
          placeholder="密码"
        ></el-input>
      </el-form-item>
      <el-form-item label="">
        <el-radio-group v-model="loginForm.identity" size="medium">
          <el-radio label="学生"></el-radio>
          <el-radio label="教师"></el-radio>
          <el-radio label="管理员"></el-radio>
        </el-radio-group>
      </el-form-item>
      <el-checkbox v-model="rememberPwd" class="rememberPwd"
        >记住密码</el-checkbox
      >
      <el-form-item class="btn_box">
        <el-button type="primary" @click="loginClick">登 录</el-button>
        <el-button type="success" @click="$router.push('/register')"
          >注 册</el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
//设置系统标题
document.title = "学生选课系统|登录";
export default {
  data() {
    return {
      loginForm: {
        account: "",
        password: "",
        identity: "",
      },
      rules: {
        account: [{ required: true, message: "请输入账号", trigger: "blur" }],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }],
      },
      rememberPwd: true,
    };
  },
  methods: {
    loginClick() {
      var obj = {
        account: this.loginForm.account,
        password: this.loginForm.password,
      };
      var param = this.$qs.stringify(obj);
      // alert(param)
      //表单验证
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          // 检查是否选择了身份
          if (!this.loginForm.identity) {
            this.$message({
              showClose: true,
              message: "请选择身份",
              type: "warning",
            });
            return false;
          }

          // TODO: 接入登录
          this.axios
            .post(
              this.identityCtoE(this.loginForm.identity) + "/login",
              param,
              {
                headers: {
                  "Content-Type": "application/x-www-form-urlencoded",
                },
              }
            )
            .then((res) => {
              console.log(res.data.code);
              if (res.data.code == 200) {
                this.$message({
                  showClose: true,
                  message: "登陆成功,正在跳转...",
                  type: "success",
                });
                // 记住用户名、密码和登陆状态
                if (this.rememberPwd) {
                  this.setCookie("account", this.loginForm.account);
                  this.setCookie("password", this.loginForm.password);
                } else {
                  this.setCookie("account", "");
                  this.setCookie("password", "");
                }
                // 保存token
                sessionStorage.setItem("token", res.data.message);
                // 保存用户基本信息至session
                var state = {
                  id: this.loginForm.account,
                  identity: this.identityCtoE(this.loginForm.identity),
                  name: "",
                };
                sessionStorage.setItem("state", JSON.stringify(state));
                setTimeout(() => {
                  window.location.href = this.COMMON.index_location;
                }, 1000);
              } else {
                this.$message({
                  showClose: true,
                  message: "账号或密码错误!",
                  type: "error",
                });
              }
            })
            .catch(() => {
              this.$message({
                showClose: true,
                message: "服务器连接失败",
              });
            });
        } else {
          return false;
        }
      });
    },

    setCookie(cname, cvalue) {
      var cookie = cname + "=" + cvalue + "; ";
      console.log("cooke:", cookie);
      document.cookie = cookie;
    },
    getCookie(cname) {
      var name = cname + "=";
      // console.log(document.cookie);
      var ca = document.cookie.split(";");
      for (var i = 0; i < ca.length; i++) {
        var c = ca[i].trim();
        if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
      }
      return "";
    },
    identityCtoE(str) {
      switch (str) {
        case "学生":
          return "student";
        case "教师":
          return "teacher";
        case "管理员":
          return "admin";
      }
    },
  },
  mounted() {
    this.loginForm.account = this.getCookie("account");
    this.loginForm.password = this.getCookie("password");
  },
};
</script>

<style lang="scss" scoped>
#login {
  .title {
    font-size: 1.5rem;
  }

  .btn_box {
    float: right;

    @media screen and (max-width: 560px) {
      float: initial;
      display: flex;
      justify-content: center;
      margin-left: -50px;
      margin-top: 10px;
    }
  }

  .rememberPwd {
    margin-left: 50px;
    margin-bottom: 20px;
  }

  ::v-deep .el-button--primary,
  ::v-deep .el-button--success {
    background-color: #9b0000;
    border-color: #9b0000;
  }

  ::v-deep .el-button--primary:hover,
  ::v-deep .el-button--success:hover,
  ::v-deep .el-button--primary:focus,
  ::v-deep .el-button--success:focus {
    background-color: #b30000;
    border-color: #b30000;
  }

  .title {
    text-align: center;
    margin-bottom: 20px;
  }

  /* 输入框只保留下边框样式 */
  ::v-deep .el-input__inner {
    border: none;
    border-bottom: 1px solid #dcdfe6;
    border-radius: 0;
    padding-left: 0;
    background: transparent;
  }

  /* 输入框聚焦时下边框变深红色 */
  ::v-deep .el-input__inner:focus {
    border-bottom: 2px solid #9b0000;
    outline: none;
    box-shadow: none;
  }

  /* 移除输入框的默认阴影效果 */

  /* 设置placeholder颜色，提高可读性 */
  ::v-deep .el-input__inner::placeholder {
    color: #666;
    opacity: 1;
  }

  /* 单选框选中样式 */
  ::v-deep .el-radio__input.is-checked .el-radio__inner {
    border-color: #9b0000;
    background: #9b0000;
  }

  ::v-deep .el-radio__input.is-checked + .el-radio__label {
    color: #9b0000;
  }

  /* 复选框选中样式 */
  ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
    border-color: #9b0000;
    background: #9b0000;
  }

  ::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
    color: #9b0000;
  }
}
</style>

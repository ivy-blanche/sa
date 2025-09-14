<template>
  <div id="manageStudent">
    <h1>管理学生</h1>
    <div class="search-form">
      <el-form :model="queryForm" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="学号">
              <el-input
                v-model="queryForm.sid"
                placeholder="请输入学号"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="班级">
              <el-input
                v-model="queryForm.sclass"
                placeholder="请输入班级"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="学院">
              <el-input
                v-model="queryForm.sinstitution"
                placeholder="请输入学院"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-button type="primary" @click="handleQuery">查询</el-button>
              <el-button @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="studentData" stripe style="width: 100%">
        <el-table-column prop="sid" label="学号" width="120"></el-table-column>
        <el-table-column
          prop="sname"
          label="姓名"
          width="100"
        ></el-table-column>
        <el-table-column prop="sinstitution" label="学院"></el-table-column>
        <el-table-column prop="sprofession" label="专业"></el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button
              type="danger"
              size="mini"
              @click="deleteUser(scope.row.sid, scope.$index)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      studentData: [],
      queryForm: {
        sid: "",
        sclass: "",
        sinstitution: "",
      },
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
    };
  },
  methods: {
    fetchStudents() {
      this.loading = true;
      const params = {
        page: this.currentPage,
        size: this.pageSize,
        ...this.queryForm,
      };

      // 移除空值参数
      Object.keys(params).forEach((key) => {
        if (params[key] === "") {
          delete params[key];
        }
      });

      this.axios
        .get("student/getPageStudentByCondition", { params })
        .then((res) => {
          if (res.data.code == 200) {
            this.studentData = res.data.data.list;
            this.total = res.data.data.total;
          }
        })
        .catch((err) => {
          console.log(err);
          this.$message.error("获取学生数据失败");
        })
        .finally(() => {
          this.loading = false;
        });
    },
    handleQuery() {
      this.currentPage = 1;
      this.fetchStudents();
    },
    resetQuery() {
      this.queryForm = {
        sid: "",
        sclass: "",
        sinstitution: "",
      };
      this.currentPage = 1;
      this.fetchStudents();
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.fetchStudents();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchStudents();
    },
    deleteUser(id, index) {
      this.$confirm("您确定要删除该学生吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.axios
            .post("student/deleteStudent?Sid=" + id)
            .then((res) => {
              if (res.data.code == 200) {
                this.$message({
                  showClose: true,
                  message: "删除成功！",
                  type: "success",
                });
                this.fetchStudents(); // 重新加载数据
              }
            })
            .catch(() => {
              this.$message.error("服务器无法连接");
            });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "操作已取消",
          });
        });
    },
  },
  mounted() {
    this.fetchStudents();
  },
};
</script>

<style lang="scss" scoped>
.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.table-container {
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  .pagination {
    padding: 20px;
    text-align: right;
  }
}
</style>

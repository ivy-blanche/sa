<template>
  <div id="manageTeacher">
    <h1>管理教师</h1>
    <div class="search-form">
      <el-form :model="queryForm" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="职工号">
              <el-input
                v-model="queryForm.tid"
                placeholder="请输入职工号"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="学院">
              <el-input
                v-model="queryForm.tinstitution"
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
      <el-table :data="teacherData" stripe style="width: 100%">
        <el-table-column
          prop="tid"
          label="职工号"
          width="120"
        ></el-table-column>
        <el-table-column
          prop="tname"
          label="姓名"
          width="100"
        ></el-table-column>
        <el-table-column prop="tsex" label="性别" width="80"></el-table-column>
        <el-table-column prop="tinstitution" label="学院"></el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button
              type="danger"
              size="mini"
              @click="deleteUser(scope.row.tid, scope.$index)"
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
      teacherData: [],
      queryForm: {
        tid: "",
        tinstitution: "",
      },
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
    };
  },
  methods: {
    fetchTeachers() {
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
        .get("teacher/getPageTeacherByCondition", { params })
        .then((res) => {
          if (res.data.code == 200) {
            this.teacherData = res.data.data.list;
            this.total = res.data.data.total;
          }
        })
        .catch((err) => {
          console.log(err);
          this.$message.error("获取教师数据失败");
        })
        .finally(() => {
          this.loading = false;
        });
    },
    handleQuery() {
      this.currentPage = 1;
      this.fetchTeachers();
    },
    resetQuery() {
      this.queryForm = {
        tid: "",
        tinstitution: "",
      };
      this.currentPage = 1;
      this.fetchTeachers();
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.fetchTeachers();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchTeachers();
    },
    deleteUser(id, index) {
      this.$confirm("您确定要删除该教师吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.axios
            .post("teacher/deleteTeacher?Tid=" + id)
            .then((res) => {
              if (res.data.code == 200) {
                this.$message({
                  showClose: true,
                  message: "删除成功！",
                  type: "success",
                });
                this.fetchTeachers(); // 重新加载数据
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
    this.fetchTeachers();
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

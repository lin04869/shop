<template>
  <div>
    <manager-header>
      <template v-slot:search>
        <el-input placeholder="请输入用户名查询" style="width: 200px" v-model="username"></el-input>
        <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      </template>
    </manager-header>

    <div class="table">
      <el-table :data="tableData" strip>
        <el-table-column label="序号" width="70" align="center">
          <template v-slot="scope">
            {{ (pageNum - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column label="加入时间">
          <template v-slot="scope">
            <div>{{ scope.row.memberSince ? new Date(scope.row.memberSince).toLocaleString() : '' }}</div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import ManagerHeader from '@/components/manager/ManagerHeader.vue'
export default {
  name: "Member",
  components: { ManagerHeader },
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      username: null,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}')
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/business/member/list', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.username
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    }
  }
}
</script>

<style scoped>

</style>

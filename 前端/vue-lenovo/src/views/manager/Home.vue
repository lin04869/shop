<template>
  <div>
    <div class="card" style="padding: 15px">
      您好，{{ user?.name }}！欢迎使用联想后台管理系统！
    </div>

    <div style="display: flex; margin: 10px 0">
      <div style="width: 50%;" class="card">
        <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom: 30px;">
          <div style="font-size:20px; font-weight:bold">公告列表 <span style="font-size:12px;color:#888;margin-left:8px">({{notices.length}})</span></div>
          <div>
            <!-- 显示新增按钮（管理员/商家都可见），用于直接在首页新增公告 -->
            <el-button type="primary" plain size="small" @click="openAddNotice" v-if="user && (user.role === 'ADMIN' || user.role === 'BUSINESS')">新增公告</el-button>
          </div>
        </div>
        <div >
          <el-timeline  reverse slot="reference">
            <template v-if="notices && notices.length">
              <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time">
              <el-popover
                  placement="right"
                  width="260"
                  trigger="hover">
                <div style="font-weight:600">{{ item.title }}</div>
                <div style="margin-top:6px">{{ item.content }}</div>
                <div style="margin-top:8px; color:#888; font-size:12px">发布者：{{ item.user }} &nbsp;&nbsp; 发布时间：{{ item.time }}</div>
                <span slot="reference">{{ item.title }}</span>
              </el-popover>
              </el-timeline-item>
            </template>
            <template v-else>
              <div style="color:#999; padding:12px">暂无公告</div>
            </template>
          </el-timeline>
        </div>
      </div>
    </div>

    <!-- 新增公告弹窗 -->
    <el-dialog title="新增公告" :visible.sync="showAddNotice" width="520px" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="addForm" :rules="addFormRules" ref="addFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="addForm.title" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" :rows="6" v-model="addForm.content" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddNotice = false">取消</el-button>
        <el-button type="primary" @click="submitAddNotice">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: [],
      showAddNotice: false,
      addForm: {
        title: '',
        content: ''
      },
      addFormRules:{
        title: [{required:true, message:'请输入标题', trigger:'blur'}],
        content: [{required:true, message:'请输入内容', trigger:'blur'}]
      }
    }
  },
  created() {
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || []
    })
  }
  ,
  methods: {
    openAddNotice(){
      this.addForm = {title:'', content:''}
      this.showAddNotice = true
    },
    submitAddNotice(){
      this.$refs.addFormRef.validate((valid)=>{
        if(!valid) return;
        this.$request.post('/notice/add', this.addForm).then(res=>{
            if(res.code === '200'){
            this.$message.success('公告新增成功')
            this.showAddNotice = false
            // 重新加载公告
            this.$request.get('/notice/selectAll').then(r=>{ 
              this.notices = r.data || [] 
              // 通知其它页面刷新（如前端首页）
              try{ localStorage.setItem('noticeUpdate', Date.now()) }catch(e){}
            })
          } else {
            this.$message.error(res.msg || '新增失败')
          }
        })
      })
    }
  }
}
</script>

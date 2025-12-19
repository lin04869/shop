<template>
  <div>
    <el-card style="width: 50%">
      <el-form :model="user" label-width="100px" style="padding-right: 50px">
        <div style="margin: 15px; text-align: center">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
          >
            <img v-if="user.avatar" :src="user.avatar" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </div>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="user.username" placeholder="用户名" disabled></el-input>
        </el-form-item>
        <el-form-item label="店铺名" prop="name">
          <el-input v-model="user.name" placeholder="店铺名"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="user.phone" placeholder="电话"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="user.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item label="介绍" prop="description">
          <el-input type="textarea" v-model="user.description" placeholder="店铺介绍"></el-input>
        </el-form-item>

        <div style="text-align: center; margin-bottom: 20px">
          <el-button type="primary" @click="update">保 存</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "AdminPerson",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}')
    }
  },
  created() {
    this.loadBusinessInfo()
  },
  methods: {
    loadBusinessInfo() {
      // 加载联想笔记本商家信息
      this.$request.get('/business/selectById/1').then(res => {
        if (res.code === '200') {
          // 获取当前登录用户的用户名
          let currentUser = JSON.parse(localStorage.getItem('xm-user') || '{}')
          this.user = res.data
          // 界面显示当前登录人的用户名，其他信息使用店铺信息
          if (currentUser.username) {
            this.user.username = currentUser.username
          }
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    update() {
      // 复制一份数据进行提交，避免修改界面显示的 username
      let data = JSON.parse(JSON.stringify(this.user))
      // 将 username 置空，防止更新时修改了 ID=1 账号的用户名
      data.username = null
      
      // 保存当前的用户信息到数据库
      this.$request.put('/business/update', data).then(res => {
        if (res.code === '200') {
          // 成功更新
          this.$message.success('保存成功')

          // 更新浏览器缓存里的用户信息（注意：这里只更新店铺相关信息，不要覆盖当前用户的 username）
          let currentUser = JSON.parse(localStorage.getItem('xm-user') || '{}')
          // 将更新后的店铺信息合并进去，但保留原 username
          let updatedUser = { ...this.user, username: currentUser.username }
          localStorage.setItem('xm-user', JSON.stringify(updatedUser))

          // 触发父级的数据更新
          this.$emit('update:user')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAvatarSuccess(response, file, fileList) {
      // 把user的头像属性换成上传的图片的链接
      this.$set(this.user, 'avatar', response.data)
    },
  }
}
</script>

<style scoped>
::v-deep.el-form-item__label {
  font-weight: bold;
}
::v-deep.el-upload {
  border-radius: 50%;
}
::v-deep.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border-radius: 50%;
}
::v-deep.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
  border-radius: 50%;
}
.avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 50%;
}
</style>
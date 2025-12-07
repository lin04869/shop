<template>
  <div class="main-content">
    <div style="width: 75%; margin: 30px auto; border-radius: 20px">
      <div style="height: 100px; padding: 0 10px; display: flex; align-items: center; border-radius: 25px; background-color: white;">
        <img :src="businessData.avatar" alt="" style="height: 60px; width: 60px; border-radius: 50%">
        <div style="width: 220px; margin: 0 30px 0 15px; font-size: 20px; font-weight: bold; display:flex; align-items:center">
          <div style="height: 30px; line-height: 30px">{{businessData.name}}</div>
          <div style="margin-left:10px">
            <el-button v-if="user && user.role === 'USER' && !isMember" type="primary" size="small" @click="joinMember">加入会员</el-button>
            <el-button v-else-if="user && user.role === 'USER' && isMember" type="text" size="small" disabled>已是会员</el-button>
          </div>
        </div>
        <div style="width: 150px; height: 100px; padding: 20px">
          <div style="font-size: 16px; height: 30px; line-height: 30px; color: #7F7F7FFF">店铺电话</div>
          <div style="font-size: 16px; height: 30px; line-height: 30px; ">{{businessData.phone}}</div>
        </div>
        <div style="width: 150px; height: 100px; padding: 20px">
          <div style="font-size: 16px; height: 30px; line-height: 30px; color: #7F7F7FFF">店铺邮箱</div>
          <div style="font-size: 16px; height: 30px; line-height: 30px; ">{{businessData.email}}</div>
        </div>
        <div style="width: 150px; height: 100px; padding: 20px">
          <div style="font-size: 16px; height: 30px; line-height: 30px; color: #7F7F7FFF">店铺介绍</div>
          <div style="font-size: 16px; height: 30px; line-height: 30px; ">{{businessData.description}}</div>
        </div>
      </div>
      <div style="border-radius: 20px; padding: 0 20px; background-color: white; margin-top: 20px">
        <div style="font-size: 18px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid">本店所有商品（{{goodsData.length}}件）</div>
        <div style="margin-top: 20px">
          <el-row>
            <el-col :span="5" style="margin-bottom: 20px" v-for="(item,index) in goodsData" :key="index">
              <img :src="item.img" alt="" style="width: 100%; height: 150px; border-radius: 10px; border: #cccccc 1px solid" @click="navTo('/front/detail?id=' + item.id)">
              <div style="margin-top: 10px; font-weight: 500; font-size: 16px; width: 160px; color: #000000FF; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">{{item.name}}</div>
              <div style="margin-top: 5px; font-size: 20px; color: #FF5000FF">￥ {{ item.price }}<span v-if="item.unit"> {{ item.unit }}</span></div>
            </el-col>
          </el-row>
        </div>
      </div>

    </div>
  </div>
</template>

<script>

export default {

  data() {
    let businessId = this.$route.query.id
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      businessId: businessId,
      goodsData: [],
      businessData: {},
      isMember: false
    }
  },
  mounted() {
    this.loadBusiness()
    this.loadGoods()
    this.loadUserStatus()
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadBusiness() {
      this.$request.get('/business/selectById/' + this.businessId).then(res => {
        if (res.code === '200') {
          this.businessData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadUserStatus() {
      let localUser = JSON.parse(localStorage.getItem('xm-user') || '{}')
      if (localUser && localUser.id) {
        this.$request.get('/user/selectById/' + localUser.id).then(res => {
          if (res.code === '200' && res.data) {
            this.isMember = (res.data.isMember === 1)
            // merge local token/role into fresh user info to avoid removing token
            let fresh = res.data
            fresh.token = fresh.token || localUser.token
            fresh.role = fresh.role || localUser.role
            const merged = Object.assign({}, localUser, fresh)
            this.user = merged
            // refresh localStorage user info (preserve token/role)
            localStorage.setItem('xm-user', JSON.stringify(merged))
          }
        })
      }
    },
    loadGoods() {
      this.$request.get('/goods/selectByBusinessId?id=' + this.businessId).then(res => {
        if (res.code === '200') {
          this.goodsData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    navTo(url) {
      location.href = url
    }
    ,
    joinMember() {
      this.$request.post('/business/member/join').then(res => {
        if (res.code === '200') {
          this.$message.success('加入会员成功！')
          // refresh status from server (and merge token)
          this.loadUserStatus()
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>

<style scoped>
.el-col-5{
  width: 20%;
  max-width: 20%;
  padding: 10px 10px;
}
</style>

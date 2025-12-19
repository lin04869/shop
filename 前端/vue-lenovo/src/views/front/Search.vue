<template>
  <div class="main-content">
    <div style="display: flex; width: 75%; background-color: white; margin: 30px auto; border-radius: 20px">
      <div style="flex: 1; padding: 0 20px">
        <div style="font-size: 18px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid">搜索"{{name}}"的结果</div>
        <div style="margin: 20px 0">
          <el-row :gutter="20">
            <el-col :span="6" style="margin-bottom: 20px" v-for="(item, index) in goodsData" :key="index">
              <img @click="navTo('/front/detail?id=' + item.id)" :src="item.img" alt="" style="width: 100%; height: 175px; border-radius: 10px; border: #cccccc 1px solid">
              <div style="margin-top: 10px; font-weight: 500; font-size: 16px; width: 180px; color: #000000FF; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">{{item.name}}</div>
              <div style="margin-top: 5px; font-size: 20px; color: #FF5000FF">
                <span v-if="item.originalPrice && item.originalPrice > item.price" style="text-decoration: line-through; color: #999; font-size: 14px; margin-right: 5px">￥{{ item.originalPrice }}</span>
                ￥ {{item.price}}
              </div>
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
    let name = this.$route.query.q
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      name: name,
      goodsData: [],
    }
  },
  mounted() {
    this.loadGoods()
  },
  watch: {
    '$route'(to) {
      this.name = to.query.q
      this.loadGoods()
    }
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadGoods() {
      this.$request.get('/goods/selectByName?name=' + this.name).then(res => {
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
  }
}
</script>

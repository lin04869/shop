<template>
  <div class="main-content">
    <div style="
        width: 100%;
        background-color: white;
        min-height: 1000px;

        border-radius: 20px;
      ">
      <div style="padding: 15px 20px; width: 75%; margin: 20px auto">
        <el-row :gutter="20">
          <el-col :span="12">
            <img :src="goodsData.img" alt="" style="width: 100%; height: 400px; border-radius: 0" />
          </el-col>
          <el-col :span="12">
            <div style="
                font-size: 20px;
                font-weight: 900;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
              ">
              {{ goodsData.name }}
            </div>

            <div style="margin-top: 20px"></div>  
            <div class="container">
              <div class="label">店铺：</div>
              <a :href="'/front/business?id=' + goodsData.businessId">{{goodsData.businessName}}</a>
            </div> 

            <div style="margin-top: 20px"></div>
            <div class="container">
              <div class="label">产品类别：</div>
              <div class="boxed-text">联想Lenovo笔记本电脑</div>
            </div> 

            <div style="margin-top: 20px"></div>
            <div class="container">
              <div class="label">操作系统：</div>
              <div class="boxed-text">Windows11家庭中文版</div>
            </div>

            <div style="margin-top: 20px"></div>
            <div class="container">
              <div class="label">配 置：</div>
              <div class="boxed-text">【十代i7/16GB/512G SSD/MX350独显】</div>
            </div>

            <div style="margin-top: 20px"></div>
            <div class="container">
              <div class="label">售后服务：</div>
              <div class="boxed-text">【7天试用/15天无理由退换货/赠运费险】</div>
            </div>

            <div style="color: red; margin-top: 15px">
              价格：<span style="font-size: 25px">{{ goodsData.price }} </span>
            </div>
            <!--
            <div style="color: #666666ff; margin-top: 20px">
              分类：<a
                href="#"
                @click="navTo('/front/type?id=' + goodsData.typeId)"
                >{{ goodsData.typeName }}</a
              >
            </div>
-->
            <div style="color: #666666ff; text-align: right; padding-right: 20px; margin-top: 15px">
              <el-button type="warning" @click="collect">收藏</el-button>
            </div>
            <div style="color: #666666ff; text-align: right; padding-right: 20px; margin-top: 15px">
              <el-button type="danger" @click="addCart">加入购物车</el-button>
              
            </div>
            

          </el-col>
        </el-row>
      </div>
      <div style="padding: 15px 20px">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="宝贝评价" name="second">
            <div style="margin-top: 10px">
              <div style="margin-top: 20px" v-for="(item, index) in commentData" :key="index">
                <div style="display: flex">
                  <div class="white-block"></div>
                  <div style="width: 40px">
                    <img :src="item.userAvatar" alt="" style="height: 40px; width: 40px; border-radius: 50%" />
                  </div>
                  <div style="width: 1500px; margin-left: 10px">
                    <div style="
                        font-weight: 700;
                        font-size: 17px;
                        color: #000000ff;
                      ">
                      {{ item.userName }}
                    </div>
                    <div style="color: #7a7a7aff">{{ item.time }}</div>
                  </div>
                </div>
                <div style="margin-top: 15px;margin-left: 300px;font-size: 16px">
                  {{ item.content }}
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    let goodsId = this.$route.query.id;
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      goodsId: goodsId,
      goodsData: {},
      activeName: 'first',
      commentData: [],
    };
  },
  mounted() {
    this.loadGoods();
    this.loadComments();
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadGoods() {
      this.$request.get('/goods/selectById?id=' + this.goodsId).then((res) => {
        if (res.code === '200') {
          this.goodsData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    handleClick(tab, event) {
      this.activeName = tab.name;
    },
    collect() {
      let data = {
        userId: this.user.id,
        businessId: this.goodsData.businessId,
        goodsId: this.goodsId,
      };
      this.$request.post('/collect/add', data).then((res) => {
        if (res.code === '200') {
          this.$message.success('收藏成功');
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    addCart() {
      let data = {
        num: 1,
        userId: this.user.id,
        goodsId: this.goodsId,
        businessId: this.goodsData.businessId,
      };
      this.$request.post('/cart/add', data).then((res) => {
        if (res.code === '200') {
          this.$message.success('操作成功');
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    loadComments() {
      this.$request
        .get('/comment/selectByGoodsId?id=' + this.goodsId)
        .then((res) => {
          if (res.code === '200') {
            this.commentData = res.data;
          } else {
            this.$message.error(res.msg);
          }
        });
    },
    navTo(url) {
      location.href = url;
    },
  },
};
</script>
<style>
.el-tabs__content img {
  width: 100%;
}

.el-tabs__header {
  width: 60% !important;
  margin: 0 auto;
}
</style>
<style>
.white-block {
  width: 400px;
  /* 设置宽度 */
  height: 40px;
  /* 设置高度 */
  background-color: white;
  /* 设置背景颜色为白色 */
}
</style>

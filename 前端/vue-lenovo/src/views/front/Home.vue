<template>
  <div class="main-content">
    <div style="display: flex">
      <!-- <div class="left"></div> -->
      <div style="width: 100%; background-color: white; margin-bottom: 50px">
        <div
          style="
            color: #fe0137ff;
            margin: 15px 0px 15px 18px;
            font-weight: bold;
            font-size: 16px;
          ">
        </div>
        <div style="display: flex; margin: 0 25px; height: 250px">
          <div style="padding-right: 0">
            <div
              style="display: flex; color: #666666ff; margin: 14px 0"
              v-for="(item, index) in typeData"
              :key="index"
              class="item-box"
            >
              <img :src="item.img" alt="" style="height: 30px; width: 30px" />
              <div style="margin-left: 10px; font-size: 20px">
                <a href="#" @click="navTo('/front/type?id=' + item.id)">{{
                  item.name
                }}</a>
              </div>
            </div>
          </div>
          <div style="flex: 5; margin-top: 0px">
            <div>
              <el-carousel height="600px" style="border-radius: 10px">
                <el-carousel-item v-for="(item, index) in carousel_top" :key="index">
                  <img
                    :src="item"
                    alt=""
                    style="width: 100%; height: 400px; border-radius: 10px"
                  />
                </el-carousel-item>
              </el-carousel>
            </div>
          </div>
        </div>
        <div class="white-block"></div>
        <div
          style="
            margin: 0 0 0 10px;
            height: 40px;
            font-size: 30px;
            color: black;
            width: 130px;
            font-weight: bold;
            line-height: 40px;
            text-align: center;
            border-radius: 20px;
          ">
        新品热销
        </div>
        <div style="margin: 10px 5px 0 5px">
          <el-row>
            <el-col :span="5" v-for="(item, index) in goodsData" :key="index">
              <img
                @click="navTo('/front/detail?id=' + item.id)"
                :src="item.img"
                alt=""
                style="
                  width: 100%;
                  height: 175px;
                  border-radius: 10px;
                  border: #cccccc 1px solid;
                "
              />
              <div
                style="
                  margin-top: 10px;
                  font-weight: 500;
                  font-size: 16px;
                  width: 180px;
                  color: #000000ff;
                  text-overflow: ellipsis;
                  overflow: hidden;
                  white-space: nowrap;
                "
              >
                {{ item.name }}
              </div>
              <div style="margin-top: 5px; font-size: 20px; color: #ff5000ff">
                ￥ {{ item.price }}
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <!-- <div class="right"></div> -->
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      typeData: [],
      top: null,
      notice: [],
      goodsData: [],
      recommendData: [],
      carousel_top: [
        require('@/assets/imgs/banner004.jpg'),
        require('@/assets/imgs/banner005.jpg'),
        require('@/assets/imgs/banner006.jpg'),
      ],
      carousel_left: [
        require('@/assets/imgs/banner004.jpg'),
        require('@/assets/imgs/banner005.jpg'),
        require('@/assets/imgs/banner006.jpg'),
      ],
      carousel_right: [
        require('@/assets/imgs/banner004.jpg'),
        require('@/assets/imgs/banner005.jpg'),
        require('@/assets/imgs/banner006.jpg'),
      ],
    };
  },
  mounted() {
    this.loadType();
    this.loadNotice();
    this.loadGoods();
    this.loadRecommend();
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadRecommend() {
      this.$request.get('/goods/recommend').then((res) => {
        if (res.code === '200') {
          this.recommendData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    loadType() {
      this.$request.get('/type/selectAll').then((res) => {
        if (res.code === '200') {
          this.typeData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    loadNotice() {
      this.$request.get('/notice/selectAll').then((res) => {
        this.notice = res.data;
        let i = 0;
        if (this.notice && this.notice.length) {
          this.top = this.notice[0].content;
          setInterval(() => {
            this.top = this.notice[i].content;
            i++;
            if (i === this.notice.length) {
              i = 0;
            }
          }, 2500);
        }
      });
    },
    loadGoods() {
      this.$request.get('/goods/selectTop15').then((res) => {
        if (res.code === '200') {
          this.goodsData = res.data;
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

<style scoped>
.item-box a:hover {
  border-bottom: 1px solid red;
  color: red;
}
img {
  display: block;
  object-fit: cover;
}
/*
.main-content {
  min-height: 100vh;
  background-size: 100%;
  background-image: url('@/assets/imgs/img.png');
}

.left {
  width: 17%;
  background-repeat: no-repeat;
  background-image: url('@/assets/imgs/left-img.png');
}
.right {
  width: 17%;
  background-repeat: no-repeat;
  background-image: url('@/assets/imgs/right-img.png');
}
*/
.el-col-5 {
  width: 20%;
  max-width: 20%;
  cursor: pointer;
  padding: 10px 10px;
}
</style>
<style>
.white-block {
  width: 400px; /* 设置宽度 */
  height: 200px; /* 设置高度 */
  background-color: white; /* 设置背景颜色为白色 */
}
</style>


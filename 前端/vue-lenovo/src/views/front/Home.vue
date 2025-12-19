<template>
  <div class="main-content">
    <div style="display: flex">
      <!-- <div class="left"></div> -->
      <div style="width: 100%; background-color: white; margin-bottom: 50px">
        <div style="margin:0; padding:0; height:0"></div>
        <div style="margin: 0 25px;">
          <div style="flex: 5; margin-top: 0px">
            <div>
              <el-carousel height="400px" style="border-radius: 10px">
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

          <!-- 公告列表：展示最近一条公告 -->
          <div style="margin-top: 0; padding-top: 0;">
            <div class="section-title" style="margin-bottom:6px; font-weight:600">公告</div>
            <div v-if="notice && notice.length">
              <el-card v-for="(n, idx) in notice.slice(0,1)" :key="n.id" shadow="never" style="margin-bottom:6px" :body-style="{ padding: '12px' }">
                <div style="display:flex; flex-direction:column">
                  <div style="font-weight:600; font-size:18px; margin-bottom: 5px">{{ n.title }}</div>
                  <div style="color: #666; font-size: 14px; margin-bottom: 5px">{{ n.content }}</div>
                  <div style="color:#888; font-size:12px; margin-top:4px">发布者：{{ n.user }} &nbsp;·&nbsp; {{ n.time }}</div>
                </div>
              </el-card>
            </div>
            <div v-else style="color:#999; font-size:12px">暂无公告</div>
          </div>

          <div style="margin-top:10px;">
            <div class="section-title" style="margin-top:0">商品分类</div>
          </div>

          <div style="display:flex; flex-wrap:wrap; justify-content:space-between; margin-top:16px; padding: 10px 6px;">
            <div v-for="(item, index) in orderedTypeData" :key="index" class="type-item" @click="navTo('/front/type?id=' + item.id)">
              <img :src="item.img" alt="" />
              <div>{{ item.name }}</div>
            </div>
          </div>

          <!-- 随机推荐：在分类下方显示 -->
          <div style="margin-top:20px">
            <div class="section-title" style="margin-top:0">为你推荐</div>
            <div style="margin: 10px 5px 40px 5px">
              <el-row>
                <el-col :span="5" v-for="(item, index) in shuffledGoods" :key="index">
                  <img
                    @click="navTo('/front/detail?id=' + item.id)"
                    :src="item.img"
                    alt=""
                    class="goods-img"
                  />
                  <div class="goods-name">{{ item.name }}</div>
                  <div class="goods-price">
                    <span v-if="item.originalPrice && item.originalPrice > item.price" style="text-decoration: line-through; color: #999; font-size: 12px; margin-right: 5px">￥{{ item.originalPrice }}</span>
                    <span>￥ {{ item.price }}</span>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
        <div class="white-block"></div>
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
      notice: [],
      goodsData: [],
      carousel_top: [
        require('@/assets/imgs/Carousel1.jpg'),
        require('@/assets/imgs/Carousel2.jpg'),
        require('@/assets/imgs/Carousel3.jpg'),
        require('@/assets/imgs/Carousel4.jpg'),
      ],
    };
  },
  mounted() {
    this.loadType();
    this.loadNotice();
    this.loadGoods();
    // 监听管理端新增公告的广播，自动刷新
    this.__noticeUpdateHandler = (e) => {
      if (e.key === 'noticeUpdate') {
        this.loadNotice();
      }
    }
    window.addEventListener('storage', this.__noticeUpdateHandler);
  },
  beforeDestroy() {
    if (this.__noticeUpdateHandler) {
      window.removeEventListener('storage', this.__noticeUpdateHandler);
    }
  },

  methods: {
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
      });
    },
    loadGoods() {
      this.$request.get('/goods/selectAll').then((res) => {
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

  computed: {
    shuffledGoods() {
      if (!this.goodsData || !this.goodsData.length) return [];
      // 筛选有折扣的商品
      const discounted = this.goodsData.filter(item => item.originalPrice && item.originalPrice > item.price);
      // 筛选没有折扣的商品
      const others = this.goodsData.filter(item => !item.originalPrice || item.originalPrice <= item.price);
      // 对没有折扣的商品进行随机打乱
      for (let i = others.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [others[i], others[j]] = [others[j], others[i]];
      }
      // 合并：优先展示折扣商品，剩下的用随机商品填充
      const result = [...discounted, ...others];
      // 取前15个
      return result.slice(0, 15);
    }
    ,
    orderedTypeData() {
      if (!this.typeData || !this.typeData.length) return [];
      const order = ['小新', '拯救者', 'think', 'yoga', '其他'];
      return (this.typeData || []).slice().sort((a, b) => {
        const nameA = (a.name || '').toLowerCase();
        const nameB = (b.name || '').toLowerCase();
        const idxA = order.findIndex(k => nameA.includes(k.toLowerCase()));
        const idxB = order.findIndex(k => nameB.includes(k.toLowerCase()));
        const posA = idxA === -1 ? order.length : idxA;
        const posB = idxB === -1 ? order.length : idxB;
        if (posA !== posB) return posA - posB;
        return nameA.localeCompare(nameB);
      });
    }
  }
}
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

.el-col-5 {
  width: 20%;
  max-width: 20%;
  cursor: pointer;
  padding: 10px 10px;
}
.type-item {
  flex: 1 0 20%;
  max-width: 20%;
  display:flex;
  flex-direction:column;
  align-items:center;
  justify-content:center;
  margin:6px 0;
  cursor:pointer;
  text-align:center;
}
.type-item img{ width:100%; height:175px; border-radius:8px; object-fit:cover }
.type-item{ max-width: 180px; }
.type-item div{ font-size: 16px; margin-top: 8px; }
.section-title{ margin: 0 0 0 10px; height:40px; font-size:24px; color:black; width:auto; font-weight:bold; line-height:40px; text-align:left; }
.goods-img{ width:100%; height:175px; border-radius:10px; border:1px solid #cccccc; }
.goods-name{ margin-top:10px; font-weight:500; font-size:16px; color:#000; text-overflow:ellipsis; overflow:hidden; white-space:nowrap; }
.goods-price{ margin-top:5px; font-size:20px; color:#ff5000 }
</style>
<style>
.white-block {
  width: 400px; /* 设置宽度 */
  height: 200px; /* 设置高度 */
  background-color: white; /* 设置背景颜色为白色 */
}
</style>
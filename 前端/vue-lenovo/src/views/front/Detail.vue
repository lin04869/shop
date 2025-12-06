<template>
  <div class="main-content">
    <div class="detail-wrapper">
      <div class="detail-inner">
        <!-- 商品图片 + 基础信息行 -->
        <el-row class="detail-top-row" :gutter="20">
          <el-col :span="12">
            <div class="goods-img-wrap">
              <img :src="goodsData.img || '/favicon.ico'" alt="商品图" class="goods-main-img" />
            </div>
          </el-col>
          <el-col :span="12" class="goods-info-col">
            <div class="goods-title">{{ goodsData.name }}</div>

            <div class="detail-meta">
              <div class="meta-row">
                <span class="label">店铺：</span>
                <a :href="'/front/business?id=' + goodsData.businessId">{{ goodsData.businessName }}</a>
              </div>
              <div class="meta-row">
                <span class="label">产品类别：</span>
                <span class="boxed-text">{{ goodsData.category || '联想Lenovo笔记本电脑' }}</span>
              </div>
              <div class="meta-row">
                <span class="label">操作系统：</span>
                <span class="boxed-text">{{ goodsOs || '—' }}</span>
              </div>
              <div class="meta-row">
                <span class="label">配 置：</span>
                <span class="boxed-text">{{ goodsConfig || '—' }}</span>
              </div>
              <div class="meta-row">
                <span class="label">颜 色：</span>
                <span class="boxed-text">{{ goodsColor || '—' }}</span>
              </div>
            </div>

            <div class="price">价格：<span class="price-value">{{ goodsData.price }}</span></div>

            <div class="actions">
              <el-button class="btn-fav" type="warning" @click="collect">收藏</el-button>
              <el-button class="btn-cart" type="danger" @click="addCart">加入购物车</el-button>
            </div>
          </el-col>
        </el-row>

        <!-- 评论行（占满整行，删除空列） -->
        <el-row class="comment-row" :gutter="20">
          <el-col :span="24">
            <el-tabs v-model="activeName" @tab-click="handleClick">
              <el-tab-pane label="商品评价" name="comments">
                <div class="comment-list">
                  <div v-if="!commentData || commentData.length === 0" class="no-comment">暂无评论</div>
                  <div class="comment-item" v-for="(item, index) in commentData" :key="item.id || index">
                    <div class="comment-head">
                      <img :src="item.userAvatar || '/favicon.ico'" class="comment-avatar" />
                      <div class="comment-meta">
                        <div class="comment-username">{{ item.userName }}</div>
                        <div class="comment-time">{{ item.time }}</div>
                      </div>
                    </div>
                    <div class="comment-content">{{ item.content }}</div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Detail",
  data() {
    return {
      goodsData: {},        // 商品基础数据
      goodsId: '',          // 商品ID
      goodsOs: '',          // 操作系统
      goodsConfig: '',      // 配置
      goodsColor: '',       // 颜色
      commentData: [],      // 评论数据
      activeName: 'comments', // 标签页默认选中
      user: JSON.parse(localStorage.getItem('xm-user') || '{}') // 当前登录用户
    };
  },
  created() {
    // 从路由参数获取商品ID
    this.goodsId = this.$route.query.id || '';
    if (this.goodsId) {
      this.loadGoodsData();
      this.loadComments();
      // 监听localStorage更新评论
      window.addEventListener('storage', this.onStorageEvent);
    }
  },
  beforeDestroy() {
    // 移除监听，避免内存泄漏
    window.removeEventListener('storage', this.onStorageEvent);
  },
  methods: {
    // 解析商品名称提取OS/配置/颜色

  parseGoodsInfo(name) {
    if (!name) return { os: '', configuration: '', color: '' };
    
    // 统一用斜杠分隔并清洗数据
    const normalized = name
      .replace(/[、，,；;|]/g, '/')  // 统一分隔符为斜杠
      .replace(/\s*\/\s*/g, '/')    // 清除斜杠前后空格
      .trim();
    const segments = normalized.split('/').map(s => s.trim()).filter(Boolean);  // 按斜杠分割成段
    
    // 颜色相关关键字集合
    const colorKeywords = ['黑', '白', '灰', '蓝', '红', '银', '金', '绿', '粉', '深', '浅', '炫彩', '渐变'];
    // 操作系统正则
    const osRegex = /(windows\s*11|win11|windows\s*10|win10|linux|macos|android|ios|鸿蒙)/i;
    
    let os = '', configuration = '', color = '';
    
    // 遍历所有斜杠分隔的段落
    segments.forEach(segment => {
      // 1. 优先匹配颜色段（包含任何颜色关键字的整段都作为颜色）
      if (!color && colorKeywords.some(keyword => segment.includes(keyword))) {
        color = segment;
      }
      // 2. 匹配操作系统段
      else if (!os && osRegex.test(segment)) {
        os = segment
          .replace(/^\s*win\s*/i, 'Windows ')
          .replace(/win11/i, 'Windows 11')
          .replace(/win10/i, 'Windows 10')
          .trim();
      }
      // 3. 剩余段落作为配置信息
      else {
        configuration = configuration ? `${configuration} / ${segment}` : segment;
      }
    });
    
    return { os, configuration, color };
    },
    // 加载商品基础数据
    loadGoodsData() {
      this.$request.get('/goods/selectById?id=' + this.goodsId)
        .then(res => {
          if (res.code === '200') {
            this.goodsData = res.data || {};
            // 解析商品名称提取信息
            // Prefer description over name when available
            const sourceText = (this.goodsData.description || this.goodsData.name || '').toString();
            const { os, configuration, color } = this.parseGoodsInfo(sourceText);
            this.goodsOs = os;
            this.goodsConfig = configuration;
            this.goodsColor = color;
          } else {
            this.$message.error(res.msg || '加载商品失败');
          }
        })
        .catch(err => {
          this.$message.error('网络错误，无法加载商品');
          console.error('加载商品失败：', err); // 便于调试
        });
    },
    // 加载评论数据
    loadComments() {
      if (!this.goodsId) return;
      this.$request.get('/comment/selectByGoodsId?id=' + this.goodsId)
        .then(res => {
          if (res.code === '200') {
            try {
              const list = (res.data || []).slice();
              // 按时间倒序排序
              list.sort((a, b) => (new Date(b.time).getTime() || 0) - (new Date(a.time).getTime() || 0));
              this.commentData = list;
            } catch (e) {
              this.commentData = res.data || [];
            }
          } else {
            this.$message.error(res.msg);
          }
        })
        .catch(err => {
          this.$message.error('加载评论失败，请重试');
          console.error('加载评论失败：', err);
        });
    },
    // 监听localStorage更新评论
    onStorageEvent(e) {
      if (e.key === 'commentUpdate') {
        this.loadComments();
      }
    },
    // 标签页切换
    handleClick(tab) {
      this.activeName = tab.name;
    },
    // 收藏商品
    collect() {
      if (!this.user || !this.user.id) {
        this.$message.warning('请先登录');
        this.$router.push('/login');
        return;
      }
      this.$request.post('/collect/add', { 
        userId: this.user.id, 
        businessId: this.goodsData.businessId, 
        goodsId: this.goodsId 
      }).then(res => {
        if (res.code === '200') {
          this.$message.success('收藏成功');
        } else {
          this.$message.error(res.msg);
        }
      }).catch(() => {
        this.$message.error('收藏失败，请重试');
      });
    },
    // 加入购物车
    addCart() {
      if (!this.user || !this.user.id) {
        this.$message.warning('请先登录');
        this.$router.push('/login');
        return;
      }
      this.$request.post('/cart/add', { 
        num: 1, 
        userId: this.user.id, 
        goodsId: this.goodsId, 
        businessId: this.goodsData.businessId 
      }).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功');
          // 触发localStorage更新，同步购物车
          localStorage.setItem('commentUpdate', Date.now());
        } else {
          this.$message.error(res.msg);
        }
      }).catch(() => {
        this.$message.error('加入购物车失败，请重试');
      });
    }
  }
};
</script>

<style scoped>
/* 基础布局样式 */
.detail-wrapper { 
  width: 100%; 
  background-color: #fff; 
  min-height: 640px; 
  border-radius: 12px; 
}
.detail-inner { 
  padding: 12px 12px; 
  width: 82%; 
  margin: 12px auto; 
}
.goods-img-wrap { 
  display:flex; 
  align-items:center; 
  justify-content:center; 
  width:100%; 
}
.goods-main-img { 
  width: auto; 
  max-width: 460px; 
  height: auto; 
  object-fit: contain; 
  border-radius: 8px;
  max-height: 520px; 
}
.goods-info-col { padding-left: 18px; }

/* 商品标题 */
.goods-title { 
  font-size: 18px; 
  font-weight: 700; 
  line-height: 1.3; 
  overflow: hidden; 
  text-overflow: ellipsis; 
  display: -webkit-box; 
  -webkit-line-clamp:2; 
  -webkit-box-orient: vertical; 
}

/* 商品元信息 */
.detail-meta { margin-top: 6px; }
.meta-row { 
  margin-top:4px; 
  display:flex; 
  align-items:center; 
}
.label { 
  width:80px; 
  color:#666; 
  font-size:14px; 
}
.boxed-text { 
  background:#f7f7f7; 
  padding: 6px 10px; 
  border-radius: 6px; 
  font-size:14px; 
}

/* 价格样式 */
.price { 
  color:red; 
  margin-top: 8px; 
  font-size:16px; 
}
.price-value { 
  font-size: 20px; 
  font-weight:700; 
}

/* 操作按钮 */
.actions { 
  margin-top: 6px; 
  display:flex; 
  gap:10px; 
  justify-content:flex-end; 
}

/* 评论区样式 */
.comment-row { margin-top: 6px; }
.comment-list { padding-top:6px; }
.no-comment { 
  color:#999; 
  text-align:left; 
  padding:12px 0; 
}
.comment-item { margin-bottom:10px; }
.comment-head { 
  display:flex; 
  align-items:center; 
}
.comment-avatar { 
  height:36px; 
  width:36px; 
  border-radius:50%; 
  margin-right:10px; 
}
.comment-meta { margin-left: 6px; }
.comment-username { 
  font-weight:700; 
  font-size:15px; 
}
.comment-time { 
  color:#888; 
  font-size:12px; 
}
.comment-content { 
  margin-top:6px; 
  padding-left:46px; 
  font-size:15px; 
  color:#333; 
}

/* 垂直居中 */
.detail-top-row { 
  display:flex; 
  align-items:center; 
}

/* 响应式适配 */
@media (max-width: 768px) {
  .detail-inner { width: 95%; }
  .goods-img-wrap { justify-content: flex-start; }
  .goods-main-img { 
    max-width: 100%; 
    width: 100%; 
    height: auto; 
  }
  .comment-avatar { 
    height: 28px; 
    width: 28px; 
  }
  .comment-content { padding-left:36px; }
  .goods-title { font-size: 16px; }
}
</style>
<template>
  <div class="front-root">
    <header class="site-header">
      <div class="header-left">
        <div class="brand" @click="$router.push('/front/home')">
          <div class="brand-title">联想</div>
          <div class="brand-sub">Lenovo Shop</div>
        </div>
      </div>

      <nav class="header-center">
        <router-link to="/front/home" class="nav-link" exact>首页</router-link>
        <router-link to="/front/orders" class="nav-link">订单</router-link>
        <router-link to="/front/cart" class="nav-link">购物车</router-link>
      </nav>

      <div class="header-right">
        <template v-if="user">
          <el-dropdown trigger="click" @command="onAuthCommand">
            <span class="username">{{ user.username }} <i class="el-icon-arrow-down el-icon--right"></i></span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="person">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="auth-link">登录</router-link>
          <router-link to="/register" class="auth-link">注册</router-link>
        </template>
      </div>
    </header>

    <!-- 搜索行：位于 header 下面 -->
    <div class="search-row">
      <el-input
        v-model="searchQuery"
        placeholder="搜索商品或商家..."
        class="search-input"
        clearable
        prefix-icon="el-icon-search"
        @keyup.enter.native="onSearch"
      />
      <el-button type="primary" @click="onSearch" class="search-btn">搜索</el-button>
    </div>

    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'Front',
  data() {
    return {
      user: null,
      searchQuery: ''
    }
  },
  created() {
    this.loadUser()
    this.$watch('$route', () => { this.loadUser() })
  },
  methods: {
    loadUser() {
      try {
        const raw = localStorage.getItem('xm-user')
        this.user = raw ? JSON.parse(raw) : null
      } catch (e) {
        this.user = null
      }
    },
    onAuthCommand(cmd) {
      if (cmd === 'logout') {
        localStorage.removeItem('xm-user')
        this.user = null
        this.$message.success('已退出')
        this.$router.push('/login')
      } else if (cmd === 'person') {
        this.$router.push('/front/person')
      }
    },
    onSearch() {
      const q = (this.searchQuery || '').trim()
      if (!q) {
        this.$message.warning('请输入搜索关键词')
        return
      }
      this.$router.push({ path: '/front/search', query: { q } })
    }
  }
}
</script>

<style scoped>
.site-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 7.5%;
  background: rgba(255,255,255,0.96);
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  position: sticky;
  top: 0;
  z-index: 50;
  gap: 20px;
}

/* 左侧品牌区：两行大 logo */
.header-left { display: flex; align-items: center; min-width: 220px; }
.brand { display:flex; flex-direction: column; align-items:flex-start; cursor:pointer; line-height:1; }
.brand-title {
  font-weight: 800;
  color: #d40000;
  font-size: 28px;
  letter-spacing: 1px;
}
.brand-sub {
  font-size: 13px;
  color: #666;
  margin-top: 2px;
}

/* 中间导航 */
.header-center {
  display: flex;
  gap: 20px;
  align-items: center;
  flex: 1;
  justify-content: center;
}
.nav-link {
  color: #333;
  text-decoration: none;
  padding: 8px 12px;
  border-radius: 4px;
}
.nav-link.router-link-exact-active,
.nav-link.router-link-active {
  color: #2d4a6f;
  font-weight: 600;
  background: rgba(45,74,111,0.06);
}

/* 右侧认证区 */
.header-right { display:flex; align-items:center; gap:12px; min-width:160px; justify-content:flex-end; }
.auth-link { color: #2d4a6f; text-decoration: none; padding: 6px 8px; }
.username { color: #2d4a6f; cursor: pointer; }

/* 搜索行样式 */
.search-row {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 7.5%;
  background: transparent;
  gap: 10px;
}
.search-input {
  width: 640px;
  max-width: 60%;
  min-width: 240px;
}
.search-btn {
  padding: 10px 18px;
}

.front-root { min-height: 100vh; background: transparent; }

@media (max-width: 900px) {
  .header-center { display: none; }
  .brand-sub { display: none; }
  .search-input { width: 60%; max-width: 320px; }
}
@media (max-width: 480px) {
  .search-row { padding: 8px 12px; }
  .search-input { width: 100%; max-width: none; }
  .site-header { padding: 8px 4%; }
}
</style>
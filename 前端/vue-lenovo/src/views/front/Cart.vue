<template>
  <div class="main-content">
    <div style="width: 70%; background-color: white; margin: 30px auto; border-radius: 20px">
      <div style="padding-bottom: 10px">
        <div style="display: flex; font-size: 18px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid;">
          <div style="margin-left: 20px; flex: 1">全部商品（{{ goodsData.length }}件）</div>
          <div style="flex: 2; text-align: right; display:flex; align-items:center; gap:8px; justify-content:flex-end">
            <el-select v-model="addressId" placeholder="请选择收货地址" style="width: 60%">
              <el-option v-for="(item, index) in addressData" :label="item.username + ' - ' + item.useraddress + ' - ' + item.phone" :value="item.id" :key="index"></el-option>
            </el-select>
            <el-button type="primary" size="mini" @click="openQuickAdd">快速新增</el-button>
            <el-button type="warning" size="mini" @click="openAddress">管理地址</el-button>
          </div>
          <div style="flex: 1; font-size: 16px; text-align: right; padding-right: 20px">
            已选商品 ￥ {{totalPrice}} <el-button type="danger" round @click="pay">下单</el-button>
          </div>
        </div>
        <div style="margin: 20px 0; padding: 0 50px">
          <div class="table">
            <el-table :data="goodsData" strip @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="55" align="center"></el-table-column>
              <el-table-column label="商品图片" width="120px">
                <template v-slot="scope">
                  <el-image style="width: 80px; height: 60px; border-radius: 3px" v-if="scope.row.goodsImg"
                            :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>
                </template>
              </el-table-column>
              <el-table-column prop="goodsName" label="商品名称" width="240px">
                <template v-slot="scope">
                  <a :href="'/front/detail?id=' + scope.row.goodsId">{{scope.row.goodsName}}</a>
                </template>
              </el-table-column>
              <el-table-column prop="businessName" label="店铺名称">
                <template v-slot="scope">
                  <a :href="'/front/business?id=' + scope.row.businessId">{{scope.row.businessName}}</a>
                </template>
              </el-table-column>
              <el-table-column prop="goodsPrice" label="商品价格"></el-table-column>
              <el-table-column prop="num" label="选择数量">
                <template v-slot="scope">
                  <el-input-number v-model="scope.row.num" style="width: 100px" @change="handleChange(scope.row)" :min="1"></el-input-number>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="180">
                <template v-slot="scope">
                  <el-button size="mini" type="danger" plain @click="del(scope.row.id)">移除购物车</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination" style="margin-top: 20px">
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
            </div>
            </div>
          <!-- 快速新增地址弹窗 -->
  <el-dialog title="新增地址" :visible.sync="addressDialogVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
    <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="100px" style="padding-right: 50px">
      <el-form-item prop="username" label="收货人">
        <el-input v-model="addressForm.username" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item prop="useraddress" label="收货地址">
        <el-input v-model="addressForm.useraddress" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item prop="phone" label="联系电话">
        <el-input v-model="addressForm.phone" autocomplete="off"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="addressDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="saveAddress">确 定</el-button>
    </div>
  </el-dialog>
  </div>
</template>

<script>

export default {

  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      goodsData: [],
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      totalPrice: 0,
      total: 0,
      addressId: null,
      addressData: [],
      selectedData: [],
      // 快速新增地址弹窗相关
      addressDialogVisible: false,
      addressForm: {
        id: null,
        userId: null,
        username: '',
        useraddress: '',
        phone: ''
      },
      addressRules: {
        username: [ { required: true, message: '请输入收货人', trigger: 'blur' } ],
        useraddress: [ { required: true, message: '请输入收货地址', trigger: 'blur' } ],
        phone: [ { required: true, message: '请输入联系电话', trigger: 'blur' } ]
      },
    }
  },
  mounted() {
    this.loadGoods(1)
    this.loadAddress()
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadAddress() {
      // 返回 Promise 以便调用方能链式处理（例如保存后刷新并选择新地址）
      return this.$request.get('/address/selectAll', { params: { userId: this.user?.id } }).then(res => {
        if (res.code === '200') {
          this.addressData = res.data || []
        } else {
          this.$message.error(res.msg)
        }
        return res
      })
    },
    openAddress() {
      this.$router.push('/front/address')
    },
    openQuickAdd() {
      this.addressForm = {
        id: null,
        userId: this.user?.id || null,
        username: '',
        useraddress: '',
        phone: ''
      }
      this.addressDialogVisible = true
    },
    saveAddress() {
      this.$refs.addressFormRef.validate(valid => {
        if (!valid) return
        if (!this.user || !this.user.id) {
          this.$message.error('请先登录');
          this.$router.push('/login');
          return;
        }
        // 提交新增地址
        const payload = Object.assign({}, this.addressForm)
        // 确保有 userId
        payload.userId = this.user?.id
        this.$request.post('/address/add', payload).then(res => {
          if (res.code === '200') {
            this.$message({ message: '保存成功', type: 'success', duration: 1200 })
            // 如果后端返回了插入的 address（含 id），直接选中并关闭对话框
            if (res.data && res.data.id) {
              // 先刷新列表，再设置选中的 addressId
              this.loadAddress().then(() => {
                this.addressId = res.data.id
                this.addressDialogVisible = false
              })
            } else {
              // 回退策略：通过字段匹配找到新增地址
              this.loadAddress().then(() => {
                const found = this.addressData.find(a => a.useraddress === payload.useraddress && a.phone === payload.phone)
                if (found) this.addressId = found.id
                this.addressDialogVisible = false
              })
            }
          } else {
            this.$message.error(res.msg)
          }
        }).catch(err => {
          console.error('saveAddress error:', err)
          this.$message.error('保存失败，请检查网络或稍后重试')
        })
      })
    },
    loadGoods(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/cart/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        }
      }).then(res => {
        if (res.code === '200') {
          this.goodsData = res.data?.list
          this.total = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    navTo(url) {
      location.href = url
    },
    del(id) {
      this.$request.delete('/cart/delete/' + id).then(res => {
        if (res.code === '200') {
          this.$message({ message: '移除成功', type: 'success', duration: 1000 })
          this.loadGoods(1)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleCurrentChange(pageNum) {
      this.loadGoods(pageNum)
    },
    handleSelectionChange(rows) {
      this.totalPrice = 0
      this.selectedData = rows
      this.selectedData.forEach(item => {
        this.totalPrice += (item.goodsPrice * item.num)
      })
    },
    handleChange(row) {
      this.totalPrice = 0
      this.selectedData.forEach(item => {
        this.totalPrice += item.goodsPrice * item.num
      })
    },
    pay() {
      if (!this.addressId) {
        this.$message.warning('请选择收货地址')
        return
      }
      if (!this.selectedData || this.selectedData.length === 0) {
        this.$message.warning('请选择商品')
        return
      }
      let data = {
        userId: this.user.id,
        addressId: this.addressId,
        status: '待发货',
        cartData: this.selectedData
      }
      this.$request.post('/orders/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功')
          this.loadGoods(1)
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>

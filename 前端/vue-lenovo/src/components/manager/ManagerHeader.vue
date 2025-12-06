<template>
  <div class="manager-header-component">
    <div class="left">
      <slot name="search">
        <!-- 默认搜索插槽：如需自定义请在父组件传入 -->
      </slot>
    </div>
    <div class="right">
      <slot name="operation">
        <!-- 默认操作插槽 -->
      </slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ManagerHeader'
}
</script>

<style scoped>
.manager-header-component{
  display:flex;
  align-items:center;
  height:50px;
  padding:0 10px;
  border-bottom:1px solid #eee;
  justify-content: space-between;
}
.manager-header-component .left,
.manager-header-component .right{
  display:flex;
  align-items:center;
}
</style>

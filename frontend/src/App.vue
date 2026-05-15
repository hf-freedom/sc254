<template>
  <div id="app">
    <el-container>
      <el-header>
        <h1>积分等级权益系统</h1>
        <el-menu :default-active="activeMenu" mode="horizontal" router>
          <el-menu-item index="/">用户列表</el-menu-item>
          <el-menu-item index="/exchange">积分兑换</el-menu-item>
          <el-menu-item index="/risk-control">风控管理</el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
  min-height: 100vh;
  background: #f5f7fa;
}

.el-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.el-header h1 {
  font-size: 24px;
  font-weight: 600;
}

.el-main {
  padding: 20px;
}
</style>

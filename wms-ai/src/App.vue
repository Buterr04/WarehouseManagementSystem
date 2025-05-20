<template>
  <el-container class="layout-container">
    <!-- 左侧菜单栏 -->
    <el-aside v-if="!isLoginPage" :width="isCollapse ? '60px' : '200px'">
      <div class="logo" v-if="!isCollapse">物流信息系统</div>
      <el-menu
          router
          :collapse="isCollapse"
          default-active="product-management"
          class="el-menu-vertical-demo"
      >
        <!-- 商品管理 -->
        <el-menu-item index="/product-management">
          <el-icon><i class="el-icon-box" /></el-icon>
          <span>商品管理</span>
        </el-menu-item>

        <!-- 采购管理 -->
        <el-sub-menu index="purchase-management">
          <template #title>
            <el-icon><i class="el-icon-shopping-cart-2" /></el-icon>
            <span>采购管理</span>
          </template>
          <el-menu-item index="/purchase-management/purchase-plan" :disabled="isMenuDisabled(3)">采购计划</el-menu-item>
          <el-menu-item index="/purchase-management/purchase-order" :disabled="isMenuDisabled(3)">采购订单</el-menu-item>
          <el-menu-item index="/purchase-management/supplier-management" :disabled="isMenuDisabled(3)">供应商管理</el-menu-item>
        </el-sub-menu>

        <!-- 库存管理 -->
        <el-sub-menu index="inventory-management">
          <template #title>
            <el-icon><i class="el-icon-warehouse" /></el-icon>
            <span>库存管理</span>
          </template>
          <el-menu-item index="/inventory-management/inventory-in" :disabled="isMenuDisabled(2)">入库管理</el-menu-item>
          <el-menu-item index="/inventory-management/return-management" :disabled="isMenuDisabled(2)">退货管理</el-menu-item>
          <el-menu-item index="/inventory-management/inventory-out" :disabled="isMenuDisabled(2)">出库管理</el-menu-item>
          <el-menu-item index="/inventory-management/inventory-view" :disabled="isMenuDisabled(2)">库存查询</el-menu-item>
        </el-sub-menu>

        <!-- 销售管理 -->
        <el-sub-menu index="sales-management">
          <template #title>
            <el-icon><i class="el-icon-s-ticket" /></el-icon>
            <span>销售管理</span>
          </template>
          <el-menu-item index="/sales-management/sales-order" :disabled="isMenuDisabled(1)">销售订单</el-menu-item>
          <el-menu-item index="/sales-management/customer-management" :disabled="isMenuDisabled(1)">客户管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 主区域 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header v-if="!isLoginPage" style="display: flex; justify-content: space-between; align-items: center;">
        <el-icon @click="toggleCollapse">
          <i :class="isCollapse ? 'el-icon-expand' : 'el-icon-fold'" />
        </el-icon>
        <el-dropdown @command="handleDropdown">
          <span class="el-dropdown-link">
            {{ userName }}
            <el-icon class="el-icon--right"><i class="el-icon-arrow-down" /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-item command="info">个人信息</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </template>
        </el-dropdown>
      </el-header>

      <!-- 主体页面 -->
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { user, setUser } from '@/stores/userStore'; // 关键：响应式 user 状态

const router = useRouter();
const route = useRoute();

const isCollapse = ref(false);
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};

// 判断当前是否在登录页
const isLoginPage = computed(() => route.path === '/login');

// 响应式用户名显示
const userName = computed(() => user.value?.name || '用户');
const roleId = computed(() => user.value?.roleId ?? -1);

// 根据角色禁用菜单项（roleId = 0 的超级管理员拥有所有权限）
const isMenuDisabled = (requiredRole) => {
  return roleId.value !== 0 && roleId.value !== requiredRole;
};

// 退出登录处理
const handleDropdown = (command) => {
  if (command === 'logout') {
    setUser(null); // 清空用户信息
    router.push('/login');
  }
};
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  background-color: #545c64;
}
.el-aside {
  background-color: #545c64;
  color: #fff;
  transition: width 0.3s ease;
}
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
}
.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.el-main {
  padding: 20px;
}
</style>
<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '60px' : '200px'">
      <div class="logo" v-if="!isCollapse">物流信息系统</div>
      <el-menu
          router
          :collapse="isCollapse"
          default-active="product-management"
          class="el-menu-vertical-demo"
      >
        <el-menu-item index="/product-management">
          <el-icon><i class="el-icon-box" /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-sub-menu index="purchase-management">
          <template #title>
            <el-icon><i class="el-icon-shopping-cart-2" /></el-icon>
            <span>采购管理</span>
          </template>
          <el-menu-item index="/purchase-management/purchase-plan">采购计划</el-menu-item>
          <el-menu-item index="/purchase-management/purchase-order">采购订单</el-menu-item>
          <el-menu-item index="/purchase-management/supplier-management">供应商管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="inventory-management">
          <template #title>
            <el-icon><i class="el-icon-warehouse" /></el-icon>
            <span>库存管理</span>
          </template>
          <el-menu-item index="/inventory-management/inventory-in">入库管理</el-menu-item>
          <el-menu-item index="/inventory-management/return-management">退货管理</el-menu-item>
          <el-menu-item index="/inventory-management/inventory-out">出库管理</el-menu-item>
          <el-menu-item index="/inventory-management/inventory-view">库存查询</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="sales-management">
          <template #title>
            <el-icon><i class="el-icon-s-ticket" /></el-icon>
            <span>销售管理</span>
          </template>
          <el-menu-item index="/sales-management/sales-order">销售订单</el-menu-item>
          <el-menu-item index="/sales-management/customer-management">客户管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="display: flex; justify-content: space-between; align-items: center;">
        <el-icon @click="toggleCollapse"><i :class="isCollapse ? 'el-icon-expand' : 'el-icon-fold'" /></el-icon>
        <el-dropdown>
          <span class="el-dropdown-link">
            用户 <el-icon class="el-icon--right"><i class="el-icon-arrow-down" /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-item>个人信息</el-dropdown-item>
            <el-dropdown-item divided>退出</el-dropdown-item>
          </template>
        </el-dropdown>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue';
const isCollapse = ref(false);

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
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
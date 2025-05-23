<template>
  <!-- ...existing code... -->
  <el-table :data="pagedStockOutList">
    <!-- ...existing code... -->
    <el-table-column label="操作">
      <template #default="scope">
        <!-- ...existing code... -->
        <el-button
          size="mini"
          type="primary"
          @click="handleCreateDelivery(scope.row)"
        >生成发货单</el-button>
        <!-- ...existing code... -->
      </template>
    </el-table-column>
    <!-- ...existing code... -->
  </el-table>
  <!-- ...existing code... -->
</template>

<script setup>
// ...existing code...
// 移除与销售单相关的变量、方法
// ...existing code...

// 生成发货单
const handleCreateDelivery = async (stockOutRow) => {
  try {
    // 直接用出库单信息生成发货单
    const deliveryData = {
      stockOutId: stockOutRow.stockOutId,
      customerName: stockOutRow.customerName,
      deliveryDate: new Date(),
      items: stockOutRow.items, // 假设出库单有items字段
      // 其他需要的字段
    };
    await api.createDelivery(deliveryData);
    ElMessage.success('发货单生成成功');
    // 可选：刷新发货单列表或出库单状态
  } catch (e) {
    ElMessage.error('发货单生成失败');
  }
};

// ...existing code...
</script>

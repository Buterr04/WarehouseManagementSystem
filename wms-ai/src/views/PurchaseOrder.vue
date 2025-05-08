<template>
  <div>
    <h1>采购管理</h1>
    <el-button type="primary" @click="goToCreatePurchaseOrder">创建采购单</el-button>

    <el-table :data="purchaseOrderList" style="width: 100%">
      <el-table-column prop="purchaseOrderId" label="采购单 ID" width="120"></el-table-column>
      <el-table-column prop="supplierName" label="供应商名称"></el-table-column>
      <el-table-column prop="purchaseDate" label="采购日期"></el-table-column>
      <el-table-column prop="totalAmount" label="总金额"></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" @click="goToEditPurchaseOrder(scope.row.purchaseOrderId)">编辑</el-button>
          <el-button size="small" type="danger" @click="deletePurchaseOrder(scope.row.purchaseOrderId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const purchaseOrderList = ref([]);

const fetchPurchaseOrderList = async () => {
  try {
    const response = await fetch('http://localhost:8090/purchase-order/list'); // 替换为你的实际 API 端点
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取采购单列表失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const data = await response.json();
    purchaseOrderList.value = data;
  } catch (error) {
    console.error('获取采购单列表失败:', error);
    ElMessage.error('获取采购单列表失败');
  }
};

onMounted(() => {
  fetchPurchaseOrderList();
});

const goToCreatePurchaseOrder = () => {
  router.push('/purchase-order/create'); // 假设你的创建采购单路由是 /purchase-order/create
};

const goToEditPurchaseOrder = (id) => {
  router.push(`/purchase-order/edit/${id}`); // 假设你的编辑采购单路由是 /purchase-order/edit/:id
};

const deletePurchaseOrder = (id) => {
  ElMessageBox.confirm(
      '确定要删除此采购单吗?',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          const response = await fetch(`http://localhost:8090/purchase-order/delete?id=${id}`, { // 替换为你的实际 API 端点
            method: 'GET', // 或者 DELETE，根据你的后端 API 定义
          });
          if (!response.ok) {
            const errorData = await response.json();
            throw new Error(`删除采购单失败: ${response.status} - ${errorData?.message || '未知错误'}`);
          }
          ElMessage.success('删除成功');
          await fetchPurchaseOrderList(); // 刷新列表
        } catch (error) {
          console.error('删除采购单失败:', error);
          ElMessage.error('删除采购单失败');
        }
      })
      .catch(() => {
        ElMessage.info('已取消删除');
      });
};
</script>

<style scoped>
/* 可以添加一些样式 */
</style>
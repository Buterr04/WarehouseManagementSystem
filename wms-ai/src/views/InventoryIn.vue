<template>
  <div>
    <h1>入库管理</h1>
    <el-button type="primary" @click="goToCreateInventoryIn">创建入库单</el-button>

    <el-table :data="inventoryInList" style="width: 100%">
      <el-table-column prop="inventoryInId" label="入库单 ID" width="120"></el-table-column>
      <el-table-column prop="inboundDate" label="入库日期"></el-table-column>
      <el-table-column prop="warehouseName" label="入库仓库"></el-table-column>
      <el-table-column prop="totalQuantity" label="总数量"></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" @click="goToEditInventoryIn(scope.row.inventoryInId)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteInventoryIn(scope.row.inventoryInId)">删除</el-button>
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
const inventoryInList = ref([]);

const fetchInventoryInList = async () => {
  try {
    const response = await fetch('http://localhost:8090/inventory-in/list'); // 替换为你的实际 API 端点
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取入库单列表失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const data = await response.json();
    inventoryInList.value = data;
  } catch (error) {
    console.error('获取入库单列表失败:', error);
    ElMessage.error('获取入库单列表失败');
  }
};

onMounted(() => {
  fetchInventoryInList();
});

const goToCreateInventoryIn = () => {
  router.push('/inventory-in/create'); // 假设你的创建入库单路由是 /inventory-in/create
};

const goToEditInventoryIn = (id) => {
  router.push(`/inventory-in/edit/${id}`); // 假设你的编辑入库单路由是 /inventory-in/edit/:id
};

const deleteInventoryIn = (id) => {
  ElMessageBox.confirm(
      '确定要删除此入库单吗?',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          const response = await fetch(`http://localhost:8090/inventory-in/delete?id=${id}`, { // 替换为你的实际 API 端点
            method: 'GET', // 或者 DELETE，根据你的后端 API 定义
          });
          if (!response.ok) {
            const errorData = await response.json();
            throw new Error(`删除入库单失败: ${response.status} - ${errorData?.message || '未知错误'}`);
          }
          ElMessage.success('删除成功');
          await fetchInventoryInList(); // 刷新列表
        } catch (error) {
          console.error('删除入库单失败:', error);
          ElMessage.error('删除入库单失败');
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
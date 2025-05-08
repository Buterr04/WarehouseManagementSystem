<template>
  <div>
    <h1>采购计划管理</h1>
    <el-button type="primary" @click="goToCreatePurchasePlan">创建采购计划</el-button>

    <el-table :data="purchasePlanList" style="width: 100%">
      <el-table-column prop="purchasePlanId" label="采购计划 ID" width="120"></el-table-column>
      <el-table-column prop="planName" label="计划名称"></el-table-column>
      <el-table-column prop="planDate" label="计划日期"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" @click="goToEditPurchasePlan(scope.row.purchasePlanId)">编辑</el-button>
          <el-button size="small" type="danger" @click="deletePurchasePlan(scope.row.purchasePlanId)">删除</el-button>
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
const purchasePlanList = ref([]);

const fetchPurchasePlanList = async () => {
  try {
    const response = await fetch('http://localhost:8090/purchase-plan/list'); // 替换为你的实际 API 端点
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取采购计划列表失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const data = await response.json();
    purchasePlanList.value = data;
  } catch (error) {
    console.error('获取采购计划列表失败:', error);
    ElMessage.error('获取采购计划列表失败');
  }
};

onMounted(() => {
  fetchPurchasePlanList();
});

const goToCreatePurchasePlan = () => {
  router.push('/purchase-plan/create'); // 假设你的创建采购计划路由是 /purchase-plan/create
};

const goToEditPurchasePlan = (id) => {
  router.push(`/purchase-plan/edit/${id}`); // 假设你的编辑采购计划路由是 /purchase-plan/edit/:id
};

const deletePurchasePlan = (id) => {
  ElMessageBox.confirm(
      '确定要删除此采购计划吗?',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          const response = await fetch(`http://localhost:8090/purchase-plan/delete?id=${id}`, { // 替换为你的实际 API 端点
            method: 'GET', // 或者 DELETE，根据你的后端 API 定义
          });
          if (!response.ok) {
            const errorData = await response.json();
            throw new Error(`删除采购计划失败: ${response.status} - ${errorData?.message || '未知错误'}`);
          }
          ElMessage.success('删除成功');
          await fetchPurchasePlanList(); // 刷新列表
        } catch (error) {
          console.error('删除采购计划失败:', error);
          ElMessage.error('删除采购计划失败');
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
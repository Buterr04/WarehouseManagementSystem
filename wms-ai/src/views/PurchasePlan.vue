<template>
  <div>
    <h1>采购计划管理</h1>
    <el-button type="primary" @click="openCreateDialog">创建采购计划</el-button>

    <el-table :data="purchasePlanList" style="width: 100%">
      <el-table-column prop="planId" label="计划 ID" width="100"></el-table-column>
      <el-table-column prop="purchaseDate" label="采购日期" width="150"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="openEditDialog(scope.row.planId)">编辑</el-button>
          <el-button size="small" type="danger" @click="deletePurchasePlan(scope.row.planId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="editDialogVisible" title="编辑采购计划" width="70%" @close="closeEditDialog">
      <purchase-plan-form
          v-if="editDialogVisible"
          :purchase-plan-id="editingPurchasePlanId"
          @success="fetchPurchasePlanList"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeEditDialog">取消</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
//import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
//import PurchasePlanForm from './PurchasePlanForm.vue'; // 确保路径正确

//const router = useRouter();
const purchasePlanList = ref([]);
const editDialogVisible = ref(false);
const editingPurchasePlanId = ref(null);

const fetchPurchasePlanList = async () => {
  try {
    const response = await fetch('http://localhost:8090/purchase-plan/list'); // 替换为你的实际 API endpoint
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取采购计划列表失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const responseData = await response.json();
    if (responseData.code === 200) {
      purchasePlanList.value = responseData.data;
    } else {
      ElMessage.error(`获取采购计划列表失败: ${responseData.msg}`);
      console.error('获取采购计划列表失败:', responseData);
    }
  } catch (error) {
    console.error('获取采购计划列表失败:', error);
    ElMessage.error('获取采购计划列表失败');
  }
};

onMounted(() => {
  fetchPurchasePlanList();
});

const openCreateDialog = () => {
  editingPurchasePlanId.value = null;
  editDialogVisible.value = true;
};

const openEditDialog = (id) => {
  editingPurchasePlanId.value = id;
  editDialogVisible.value = true;
};

const closeEditDialog = () => {
  editDialogVisible.value = false;
  editingPurchasePlanId.value = null;
};

const deletePurchasePlan = (id) => {
  ElMessageBox.confirm(
      '确定要删除此采购计划?',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          const response = await fetch(`http://localhost:8090/purchase-plan/delete?purchasePlanId=${id}`, { // 替换为你的实际 API endpoint
            method: 'GET', // 或 DELETE，根据你的后端 API 定义
          });
          if (!response.ok) {
            const errorData = await response.json();
            throw new Error(`删除采购计划失败: ${response.status} - ${errorData?.message || '未知错误'}`);
          }
          const responseData = await response.json();
          if (responseData.code === 200) {
            ElMessage.success('采购计划删除成功');
            await fetchPurchasePlanList(); // 刷新列表
          } else {
            ElMessage.error(`删除采购计划失败: ${responseData.msg}`);
            console.error('删除采购计划失败:', responseData);
          }
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
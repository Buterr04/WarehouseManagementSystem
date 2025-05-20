<template>
  <div>
    <h1>退货单管理</h1>
    <el-button type="primary" @click="openCreateDialog">创建退货单</el-button>

    <el-table :data="returnOrderList" style="width: 100%">
      <el-table-column prop="returnId" label="退货单号" width="120"></el-table-column>
      <el-table-column prop="returnDate" label="退货日期" width="150"></el-table-column>
      <el-table-column prop="supplierName" label="供应商" width="150"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="openEditDialog(scope.row.returnId)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteReturnOrder(scope.row.returnId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="editDialogVisible" title="编辑退货单" width="70%" @close="closeEditDialog">
      <return-order-form
          v-if="editDialogVisible"
          :return-order-id="editingReturnOrderId"
          @success="fetchReturnOrderList"
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
import ReturnOrderForm from './ReturnOrderForm.vue'; // 确保路径正确

//const router = useRouter();
const returnOrderList = ref([]);
const editDialogVisible = ref(false);
const editingReturnOrderId = ref(null);

const fetchReturnOrderList = async () => {
  try {
    const response = await fetch('http://localhost:8090/return-order/list'); // 替换为你的实际 API endpoint
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取退货单列表失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const responseData = await response.json();
    if (responseData.code === 200) {
      returnOrderList.value = responseData.data;
    } else {
      ElMessage.error(`获取退货单列表失败: ${responseData.msg}`);
      console.error('获取退货单列表失败:', responseData);
    }
  } catch (error) {
    console.error('获取退货单列表失败:', error);
    ElMessage.error('获取退货单列表失败');
  }
};

onMounted(() => {
  fetchReturnOrderList();
});

const openCreateDialog = () => {
  editingReturnOrderId.value = null;
  editDialogVisible.value = true;
};

const openEditDialog = (id) => {
  editingReturnOrderId.value = id;
  editDialogVisible.value = true;
};

const closeEditDialog = () => {
  editDialogVisible.value = false;
  editingReturnOrderId.value = null;
};

const deleteReturnOrder = (id) => {
  ElMessageBox.confirm(
      '确定要删除此退货单?',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          const response = await fetch(`http://localhost:8090/return-order/delete?id=${id}`, { // 替换为你的实际 API endpoint
            method: 'GET', // 或 DELETE，根据你的后端 API 定义
          });
          if (!response.ok) {
            const errorData = await response.json();
            throw new Error(`删除退货单失败: ${response.status} - ${errorData?.message || '未知错误'}`);
          }
          const responseData = await response.json();
          if (responseData.code === 200) {
            ElMessage.success('退货单删除成功');
            await fetchReturnOrderList(); // 刷新列表
          } else {
            ElMessage.error(`删除退货单失败: ${responseData.msg}`);
            console.error('删除退货单失败:', responseData);
          }
        } catch (error) {
          console.error('删除退货单失败:', error);
          ElMessage.error('删除退货单失败');
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
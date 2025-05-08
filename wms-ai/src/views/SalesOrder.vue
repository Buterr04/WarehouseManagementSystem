<template>
  <div>
    <h1>销售单管理</h1>
    <el-button type="primary" @click="goToCreateSalesOrder">创建销售单</el-button>

    <el-table :data="salesOrderList" style="width: 100%">
      <el-table-column prop="salesOrderId" label="销售单 ID" width="120"></el-table-column>
      <el-table-column prop="customerName" label="客户姓名"></el-table-column>
      <el-table-column prop="saleDate" label="销售日期"></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" @click="goToEditSalesOrder(scope.row.salesOrderId)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteSalesOrder(scope.row.salesOrderId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus'; // 引入 ElMessageBox

const router = useRouter();
const salesOrderList = ref([]);

const fetchSalesOrderList = async () => {
  try {
    const response = await fetch('http://localhost:8090/sales-order/list'); // 替换为你的实际 API 端点
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取销售单列表失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const responseData = await response.json();
    if (responseData.code === 200) {
      salesOrderList.value = responseData.data; // 从 data 字段中获取列表数据
    } else {
      ElMessage.error(`获取销售单列表失败: ${responseData.msg}`);
      console.error('获取销售单列表失败:', responseData);
    }
  } catch (error) {
    console.error('获取销售单列表失败:', error);
    ElMessage.error('获取销售单列表失败');
  }
};

onMounted(() => {
  fetchSalesOrderList();
});

const goToCreateSalesOrder = () => {
  router.push('/sales-order/create'); // 假设你的创建销售单路由是 /sales-order/create
};

const goToEditSalesOrder = (id) => {
  router.push(`/sales-order/edit/${id}`); // 假设你的编辑销售单路由是 /sales-order/edit/:id
};

const deleteSalesOrder = (id) => {
  ElMessageBox.confirm(
      '确定要删除此销售单吗?',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          const response = await fetch(`http://localhost:8090/sales-order/delete?id=${id}`, { // 替换为你的实际 API 端点
            method: 'GET', // 或者 DELETE，根据你的后端 API 定义
          });
          if (!response.ok) {
            const errorData = await response.json();
            throw new Error(`删除销售单失败: ${response.status} - ${errorData?.message || '未知错误'}`);
          }
          const responseData = await response.json();
          if (responseData.code === 200) {
            ElMessage.success('删除成功');
            await fetchSalesOrderList(); // 刷新列表
          } else {
            ElMessage.error(`删除销售单失败: ${responseData.msg}`);
            console.error('删除销售单失败:', responseData);
          }
        } catch (error) {
          console.error('删除销售单失败:', error);
          ElMessage.error('删除销售单失败');
        }
      })
      .catch(() => {
        ElMessage.info('已取消删除');
      });
};
</script>

<style scoped>
/* 可以添加一些样式 */
</style>>

<style scoped>
/* 可以添加一些样式 */
</style>
<template>
  <div>
    <h1>销售单管理</h1>
    <el-button type="primary" @click="goToCreateSalesOrder">创建销售单</el-button>

    <el-table :data="salesOrderList" style="width: 100%">
      <el-table-column prop="salesOrderId" label="销售单 ID" width="120"></el-table-column>
      <el-table-column prop="customerName" label="客户姓名"></el-table-column>
      <el-table-column prop="saleDate" label="销售日期"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="viewSalesOrderDetails(scope.row.salesOrderId)">查看</el-button>
          <el-button size="small" @click="goToEditSalesOrder(scope.row.salesOrderId)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteSalesOrder(scope.row.salesOrderId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="销售单明细" width="60%">
      <el-table :data="salesOrderDetails" style="width: 100%">
        <el-table-column prop="productName" label="商品名称(规格)">
          <template #default="scope">
            {{ scope.row.productName }} {{ scope.row.specifications ? `(${scope.row.specifications})` : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量"></el-table-column>
        <el-table-column prop="salePrice" label="销售单价">
          <template #default="scope">
            {{ formatCurrency(scope.row.salePrice) }}
          </template>
        </el-table-column>
        <el-table-column label="小计">
          <template #default="scope">
            {{ formatCurrency(scope.row.quantity * scope.row.salePrice) }}
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align: right; margin-top: 10px;">
        总价: <strong style="font-size: 1.2em;">{{ formatCurrency(totalPrice) }}</strong>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, ElDialog } from 'element-plus'; // 确保引入 ElDialog

const router = useRouter();
const salesOrderList = ref([]);
const dialogVisible = ref(false); // 控制弹窗显示隐藏的变量
const salesOrderDetails = ref([]); // 存储销售单明细数据的变量
const currentSalesOrderId = ref(null); // 存储当前查看的销售单ID

const totalPrice = computed(() => {
  return salesOrderDetails.value.reduce((sum, item) => sum + item.quantity * item.salePrice, 0);
});

const formatCurrency = (price) => {
  return price ? `¥ ${price.toFixed(2)}` : '¥ 0.00';
};

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

const fetchSalesOrderDetailsForView = async (id) => {
  if (id) {
    try {
      const response = await fetch(`http://localhost:8090/sales-order/items/${id}`); // 直接访问获取明细条目的 API
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(`获取销售单明细失败: ${response.status} - ${errorData?.message || '未知错误'}`);
      }
      const responseData = await response.json();
      if (responseData.code === 200 && responseData.data) {
        salesOrderDetails.value = responseData.data;
      } else {
        ElMessage.error(`获取销售单明细失败: ${responseData.msg}`);
        salesOrderDetails.value = []; // 清空明细数据
      }
    } catch (error) {
      console.error('获取销售单明细失败:', error);
      ElMessage.error('获取销售单明细失败');
      salesOrderDetails.value = []; // 清空明细数据
    }
  } else {
    salesOrderDetails.value = []; // 如果没有 ID，清空明细数据
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

const viewSalesOrderDetails = (id) => {
  currentSalesOrderId.value = id;
  fetchSalesOrderDetailsForView(id); // 调用获取明细方法
  dialogVisible.value = true;
};
</script>

<style scoped>

</style>
<template>
  <div>
    <h1>出库管理</h1>
    <el-button type="primary" @click="createOutboundOrder">创建出库单</el-button>

    <el-table :data="outboundOrderList" style="width: 100%">
      <el-table-column prop="stockOutId" label="出库单号" width="120"></el-table-column>
      <el-table-column prop="salesOrderId" label="销售单号" width="120"></el-table-column>
      <el-table-column prop="stockOutDate" label="出库日期" width="150"></el-table-column>
      <el-table-column prop="employeeId" label="操作员 ID" width="120"></el-table-column>
    </el-table>

    <el-dialog v-model="createOutboundDialogVisible" title="创建出库单" width="80%">
      <el-table :data="salesOrderList" style="width: 100%">
        <el-table-column prop="salesOrderId" label="销售单号" width="120"></el-table-column>
        <el-table-column prop="saleDate" label="销售日期" width="150"></el-table-column>
        <el-table-column prop="customerName" label="客户姓名"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="showOutboundForm(scope.row)">生成出库单</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createOutboundDialogVisible = false">取消</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="outboundFormVisible" title="出库处理" width="60%">
      <el-form :model="outboundForm" label-width="120px">
        <el-form-item label="销售单号">
          <el-input v-model="outboundForm.salesOrderId" :disabled="true" />
        </el-form-item>
        <el-table :data="outboundForm.items" style="width: 100%">
          <el-table-column prop="productId" label="商品 ID"></el-table-column>
          <el-table-column prop="productName" label="商品名称"></el-table-column>
          <el-table-column prop="quantity" label="销售数量"></el-table-column>
          <el-table-column prop="stockQuantity" label="库存数量"></el-table-column>
          <el-table-column prop="outboundQuantity" label="出库数量">
            <template #default="scope">
              <el-input-number v-model="scope.row.outboundQuantity" :min="0" :max="scope.row.quantity" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="outboundFormVisible = false">取消</el-button>
          <el-button type="primary" @click="processOutbound">确认出库</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();
const outboundOrderList = ref([]);
const salesOrderList = ref([]);
const createOutboundDialogVisible = ref(false);
const outboundFormVisible = ref(false);
const outboundForm = ref({
  salesOrderId: null,
  items: [],
});

const fetchOutboundOrderList = async () => {
  try {
    const response = await fetch('http://localhost:8090/stock-out/list'); // 替换为你的实际 API 端点
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取出库单列表失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const data = await response.json().then(res => res.data);
    outboundOrderList.value = data;
  } catch (error) {
    console.error('获取出库单列表失败:', error);
    ElMessage.error('获取出库单列表失败');
  }
};

const fetchSalesOrderList = async () => {
  try {
    const response = await fetch('http://localhost:8090/sales-order/list-with-customer'); // 替换为你的实际 API 端点 (需要包含客户姓名)
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取销售单列表失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const data = await response.json().then(res => res.data);
    salesOrderList.value = data;
  } catch (error) {
    console.error('获取销售单列表失败:', error);
    ElMessage.error('获取销售单列表失败');
  }
};

const fetchSalesOrderItems = async (salesOrderId) => {
  try {
    const response = await fetch(`http://localhost:8090/sales-order/items/${salesOrderId}`); // 替换为你的实际 API 端点
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取销售单明细失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const data = await response.json().then(res => res.data);
    // TODO: 需要从后端获取每个商品的库存数量
    outboundForm.value.items = data.map(item => ({ ...item, outboundQuantity: item.quantity, stockQuantity: Math.floor(Math.random() * 20) })); // 临时库存数据
  } catch (error) {
    console.error('获取销售单明细失败:', error);
    ElMessage.error('获取销售单明细失败');
  }
};

onMounted(() => {
  fetchOutboundOrderList();
  fetchSalesOrderList();
});

const createOutboundOrder = () => {
  createOutboundDialogVisible.value = true;
};

const showOutboundForm = (row) => {
  outboundForm.value.salesOrderId = row.salesOrderId;
  fetchSalesOrderItems(row.salesOrderId);
  outboundFormVisible.value = true;
};

const processOutbound = async () => {
  const hasStockShortage = outboundForm.value.items.some(item => item.outboundQuantity > item.stockQuantity);

  if (hasStockShortage) {
    ElMessage.warning('检测到缺货，请先处理采购');
    router.push('/purchase/manage'); // 引导跳转到采购页面 (你需要定义采购管理页面的路由)
    outboundFormVisible.value = false;
    return;
  }

  try {
    const response = await fetch('http://localhost:8090/stock-out/save', { // 替换为你的实际 API 端点
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(outboundForm.value),
    });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`出库处理失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    ElMessage.success('出库处理成功');
    outboundFormVisible.value = false;
    await fetchOutboundOrderList(); // 刷新出库单列表
  } catch (error) {
    console.error('出库处理失败:',error);
    ElMessage.error('出库处理失败');
  }
};
</script>

<style scoped>
/* 可以添加一些样式 */
</style>
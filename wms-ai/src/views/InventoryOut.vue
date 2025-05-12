<template>
  <div>
    <h1>出库管理</h1>
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchForm.stockOutId" placeholder="按出库单号搜索" clearable style="width: 180px; margin-right: 10px;"></el-input>
      <el-date-picker v-model="searchForm.stockOutDate" type="date" placeholder="按出库日期搜索" clearable style="width: 180px; margin-right: 10px;"></el-date-picker>
      <el-select v-model="searchForm.status" placeholder="按处理状态搜索" clearable style="width: 180px; margin-right: 10px;">
        <el-option label="待处理" :value="0"></el-option>
        <el-option label="已出库" :value="1"></el-option>
        <el-option label="缺货" :value="2"></el-option>
      </el-select>
      <el-button @click="fetchOutboundOrderList">查询</el-button>
      <el-button type="primary" @click="createOutboundOrder">创建出库单</el-button>
    </div>

    <el-table :data="outboundOrderList" style="width: 100%">
      <el-table-column prop="stockOutId" label="出库单号" width="120"></el-table-column>
      <el-table-column prop="salesOrderId" label="关联销售单号" width="120"></el-table-column>
      <el-table-column prop="saleDate" label="销售日期" width="150"></el-table-column>
      <el-table-column prop="stockOutDate" label="出库日期"></el-table-column>
      <el-table-column prop="statusText" label="出库状态"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="viewOutboundDetails(scope.row.stockOutId)">查看</el-button>
          <el-button size="small" @click="handleOutbound(scope.row)">处理</el-button>
          <el-button size="small" type="danger" @click="deleteOutbound(scope.row.StockOutId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="createOutboundDialogVisible" title="关联销售单创建出库单" width="80%">
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
const outboundDetails = ref([]);
const salesOrderList = ref([]);
const createOutboundDialogVisible = ref(false);
const outboundFormVisible = ref(false);
const outboundForm = ref({
  salesOrderId: null,
  items: [],
});
const searchForm = ref({
  stockOutId: '',
  stockOutDate: null,
  status: null,
});

const fetchOutboundOrderList = async () => {
  try {
    const response = await fetch('http://localhost:8090/stock-out/list'); // 替换为你的实际 API 端点
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取出库单列表失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const responseData = await response.json().then(res => res.data);
    outboundOrderList.value = responseData.map(order => ({ ...order, statusText: getStatusText(order.status) }));
  } catch (error) {
    console.error('获取出库单列表失败:', error);
    ElMessage.error('获取出库单列表失败');
  }
};

const fetchOutboundDetails = async (stockOutId) => {
  try {
    const response = await fetch(`http://localhost:8090/stock-out/details/${stockOutId}`);
    const responseData = await response.json();
    outboundDetails.value = responseData.data || [];
  } catch (error) {
    ElMessage.error('获取出库单明细失败');
  }
};

const viewOutboundDetails = (stockOutId) => {
  fetchOutboundDetails(stockOutId);
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
    const payload = {
      salesOrderId: outboundForm.value.salesOrderId,
      employeeId: 3, // TODO: 获取当前登录员工的 ID
      stockOutItems: outboundForm.value.items.map(item => ({
        productId: item.productId,
        quantity: item.outboundQuantity,
      })),
    };
    const response = await fetch('http://localhost:8090/stock-out/save', { // 替换为你的实际 API 端点
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`出库处理失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    ElMessage.success('出库处理成功');
    outboundFormVisible.value = false;
    await fetchOutboundOrderList(); // 刷新出库单列表
  } catch (error) {
    console.error('出库处理失败:', error);
    ElMessage.error('出库处理失败');
  }
};

const getStatusText = (status) => {
  // TODO: 根据实际状态值返回对应的文本
  return status === 0 ? '待处理' : status === 1 ? '已出库' : '缺货';
};

const handleOutbound = (row) => {
  outboundForm.value.salesOrderId = row.salesOrderId;
  fetchSalesOrderItems(row.salesOrderId);
  outboundFormVisible.value = true;
};

</script>

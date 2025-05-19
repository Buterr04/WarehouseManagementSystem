<template>
  <div>
    <h1>采购订单管理</h1>
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchForm.orderId" placeholder="按订单号搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-date-picker v-model="searchForm.orderDate" type="date" placeholder="按下单日期搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-select v-model="searchForm.status" placeholder="按订单状态搜索" clearable style="width: 180px; margin-right: 10px;">
        <el-option label="待收货" :value="0" />
        <el-option label="已完成" :value="1" />
        <el-option label="部分缺货" :value="2" />
      </el-select>
      <el-button @click="fetchOrderList">查询</el-button>
      <el-button type="primary" @click="navigateToPurchaseForm">新建采购订单</el-button>
    </div>

    <el-table :data="pagedOrderList" style="width: 100%">
      <el-table-column prop="orderId" label="订单号" width="120" />
      <el-table-column prop="supplierName" label="供应商" width="180" />
      <el-table-column prop="orderDate" label="下单日期" width="150" />
      <el-table-column prop="statusText" label="订单状态" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewOrderDetails(scope.row.orderId)">查看</el-button>
          <el-button size="small" :disabled="scope.row.status === 1" @click="handleOrder(scope.row)">处理</el-button>
          <el-button size="small" type="danger" @click="deleteOrder(scope.row.orderId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      layout="prev, pager, next"
      :total="filteredOrderList.length"
      :page-size="orderPageSize"
      :current-page="orderCurrentPage"
      @current-change="handleOrderPageChange"
      style="margin: 16px 0; text-align: right;"
    />

    <el-dialog v-model="dialogVisible" title="采购订单明细" width="60%">
      <el-table :data="orderDetails" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="采购数量" />
        <el-table-column prop="receivedQuantity" label="已到货数量" />
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="handleDialogVisible" title="确认收货" width="80%">
      <el-table :data="orderForm.items" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="采购数量" />
        <el-table-column label="实际到货数量">
          <template #default="scope">
            <el-input-number v-model="scope.row.receivedQuantity" :min="0" :max="scope.row.quantity" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOrderHandling">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router'; // 引入 useRouter
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter(); // 获取 router 实例

const orderList = ref([]);
const orderDetails = ref([]);
const searchForm = ref({ orderId: '', orderDate: null, status: null });
const orderForm = ref({ supplierId: null, items: [] });

const dialogVisible = ref(false);
const handleDialogVisible = ref(false);

// 公共函数
const extractArray = (data) => Array.isArray(data?.data) ? data.data : [];

const getStatusText = (status) => {
  return status === 0 ? '待收货' : status === 1 ? '已完成' : '部分缺货';
};

// 分页相关
const orderCurrentPage = ref(1);
const orderPageSize = ref(10);

// 过滤（如需后续扩展搜索条件）
const filteredOrderList = computed(() => {
  // 格式化为 yyyy-MM-dd
  const formatDate = (d) => {
    if (!d) return '';
    if (typeof d === 'string') {
      return d.slice(0, 10);
    }
    if (d instanceof Date) {
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
    return '';
  };
  return orderList.value.filter(order => {
    if (searchForm.value.orderId && !String(order.orderId).includes(searchForm.value.orderId)) {
      return false;
    }
    if (searchForm.value.orderDate) {
      const searchDateStr = formatDate(searchForm.value.orderDate);
      const orderDateStr = formatDate(order.orderDate);
      if (orderDateStr !== searchDateStr) return false;
    }
    if (searchForm.value.status !== null && searchForm.value.status !== '' && order.status !== searchForm.value.status) {
      return false;
    }
    return true;
  });
});

const pagedOrderList = computed(() => {
  const start = (orderCurrentPage.value - 1) * orderPageSize.value;
  const end = start + orderPageSize.value;
  return filteredOrderList.value.slice(start, end);
});

const handleOrderPageChange = (page) => {
  orderCurrentPage.value = page;
};

// 查询采购订单列表
const fetchOrderList = async () => {
  try {
    const res = await fetch('http://localhost:8090/purchase-order/list');
    const data = await res.json();
    orderList.value = extractArray(data).map(o => ({
      ...o,
      statusText: getStatusText(o.status),
    }));
    orderCurrentPage.value = 1; // 查询后重置页码
  } catch (err) {
    ElMessage.error('获取订单失败: ' + err.message);
  }
};

// 导航到 PurchaseForm 页面
const navigateToPurchaseForm = () => {
  router.push({ name: 'create-purchase-order' }); // 假设你的 PurchaseForm 路由 name 是 'PurchaseForm'
};

// 查看订单明细
const viewOrderDetails = async (orderId) => {
  try {
    const res = await fetch(`http://localhost:8090/purchase-order/items/${orderId}`);
    const data = await res.json();
    orderDetails.value = extractArray(data);
    dialogVisible.value = true;
  } catch (err) {
    ElMessage.error('加载订单明细失败: ' + err.message);
  }
};

// 处理收货
const handleOrder = async (row) => {
  try {
    const res = await fetch(`http://localhost:8090/purchase-order/items/${row.orderId}`);
    const data = await res.json();
    orderForm.value = {
      orderId: row.orderId,
      items: extractArray(data).map(i => ({
        ...i,
        receivedQuantity: i.quantity,
      })),
    };
    handleDialogVisible.value = true;
  } catch (err) {
    ElMessage.error('加载订单数据失败: ' + err.message);
  }
};

const submitOrderHandling = async () => {
  const hasShortage = orderForm.value.items.some(i => i.receivedQuantity < i.quantity);
  const payload = {
    orderId: orderForm.value.orderId,
    status: hasShortage ? 2 : 1,
    items: orderForm.value.items.map(i => ({
      productId: i.productId,
      quantity: i.receivedQuantity,
    })),
  };
  try {
    const res = await fetch('http://localhost:8090/purchase-order/modify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    const data = await res.json();
    if (data.code === 200) {
      ElMessage.success('处理完成');
      handleDialogVisible.value = false;
      fetchOrderList();
    } else {
      throw new Error(data.msg || '处理失败');
    }
  } catch (err) {
    ElMessage.error('处理失败: ' + err.message);
  }
};

// 删除订单
const deleteOrder = (orderId) => {
  ElMessageBox.confirm('确定删除该订单？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const res = await fetch(`http://localhost:8090/purchase-order/delete?purchaseOrderId=${orderId}`);
      const data = await res.json();
      if (data.code === 200) {
        ElMessage.success('删除成功');
        fetchOrderList();
      } else {
        throw new Error(data.msg || '删除失败');
      }
    } catch (err) {
      ElMessage.error('删除失败: ' + err.message);
    }
  });
};

onMounted(fetchOrderList);
</script>

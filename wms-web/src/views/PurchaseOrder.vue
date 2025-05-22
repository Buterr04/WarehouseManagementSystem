<template>
  <div>
    <h1>采购订单管理</h1>
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchForm.orderId" placeholder="按订单号搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-date-picker v-model="searchForm.orderDate" type="date" placeholder="按下单日期搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-select v-model="searchForm.status" placeholder="按订单状态搜索" clearable style="width: 180px; margin-right: 10px;">
        <el-option label="待收货" :value="0" />
        <el-option label="已到货" :value="1" />
        <el-option label="部分缺货" :value="2" />
      </el-select>
      <el-input v-model="searchForm.productName" placeholder="按商品名称搜索" clearable style="width: 200px; margin-right: 10px;" />
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

    <!-- 查看订单弹窗 -->
    <el-dialog v-model="dialogVisible" title="采购订单明细" width="60%">
      <div style="margin-bottom: 15px;">
        <el-row :gutter="20">
          <el-col :span="8"><strong>采购订单号：</strong>{{ orderInfo.orderId }}</el-col>
          <el-col :span="8"><strong>供应商：</strong>{{ orderInfo.supplierName }}</el-col>
          <el-col :span="8"><strong>下单日期：</strong>{{ orderInfo.orderDate }}</el-col>
        </el-row>
      </div>

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

    <!-- 处理订单弹窗 -->
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
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const orderList = ref([]);
const allOrderItems = ref([]);
const orderIdToProductNames = ref(new Map());
const orderDetails = ref([]);
const dialogVisible = ref(false);
const handleDialogVisible = ref(false);
const searchForm = ref({
  orderId: '',
  orderDate: null,
  status: null,
  productName: ''
});
const orderForm = ref({ supplierId: null, items: [] });

const orderInfo = ref({
  orderId: '',
  supplierName: '',
  orderDate: ''
});

const orderCurrentPage = ref(1);
const orderPageSize = ref(10);

// 获取订单状态文字
const getStatusText = (status) => {
  return status === 0 ? '待收货' : status === 1 ? '已到货' : '部分缺货';
};

// 处理分页
const handleOrderPageChange = (page) => {
  orderCurrentPage.value = page;
};

// 筛选订单列表
const filteredOrderList = computed(() => {
  const formatDate = (d) => {
    if (!d) return '';
    if (typeof d === 'string') return d.slice(0, 10);
    if (d instanceof Date) {
      const y = d.getFullYear(), m = String(d.getMonth() + 1).padStart(2, '0'), da = String(d.getDate()).padStart(2, '0');
      return `${y}-${m}-${da}`;
    }
    return '';
  };

  return orderList.value.filter(order => {
    if (searchForm.value.orderId && !String(order.orderId).includes(searchForm.value.orderId)) return false;
    if (searchForm.value.orderDate) {
      const orderDateStr = formatDate(order.orderDate);
      const searchDateStr = formatDate(searchForm.value.orderDate);
      if (orderDateStr !== searchDateStr) return false;
    }
    if (searchForm.value.status !== null && searchForm.value.status !== '' && order.status !== searchForm.value.status) {
      return false;
    }
    if (searchForm.value.productName) {
      const products = orderIdToProductNames.value.get(order.orderId);
      if (!products) return false;
      const match = [...products].some(p => p.includes(searchForm.value.productName));
      if (!match) return false;
    }
    return true;
  });
});

const pagedOrderList = computed(() => {
  const start = (orderCurrentPage.value - 1) * orderPageSize.value;
  return filteredOrderList.value.slice(start, start + orderPageSize.value);
});

// 拉取采购订单
const fetchOrderList = async () => {
  try {
    const res = await fetch('http://localhost:8090/purchase-order/list');
    const data = await res.json();
    orderList.value = Array.isArray(data.data) ? data.data.map(o => ({
      ...o,
      statusText: getStatusText(o.status),
    })) : [];
    orderCurrentPage.value = 1;
  } catch (err) {
    ElMessage.error('获取订单失败: ' + err.message);
  }
};

// 拉取订单明细项并构建映射
const fetchAllOrderItems = async () => {
  try {
    const res = await fetch('http://localhost:8090/purchase-order-item/list');
    const data = await res.json();
    allOrderItems.value = Array.isArray(data.data) ? data.data : [];

    const map = new Map();
    allOrderItems.value.forEach(item => {
      if (!item.productName) return;
      if (!map.has(item.orderId)) map.set(item.orderId, new Set());
      map.get(item.orderId).add(item.productName);
    });
    orderIdToProductNames.value = map;
  } catch (err) {
    ElMessage.error('加载订单明细失败: ' + err.message);
  }
};

// 查看订单详情
const viewOrderDetails = async (orderId) => {
  const order = orderList.value.find(o => o.orderId === orderId);
  if (order) {
    orderInfo.value = {
      orderId: order.orderId,
      supplierName: order.supplierName || '未知',
      orderDate: order.orderDate?.slice(0, 10) || ''
    };
  }

  try {
    const res = await fetch(`http://localhost:8090/purchase-order/items/${orderId}`);
    const data = await res.json();
    orderDetails.value = Array.isArray(data.data) ? data.data : [];
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
      items: Array.isArray(data.data) ? data.data.map(i => ({
        ...i,
        receivedQuantity: i.quantity
      })) : []
    };
    handleDialogVisible.value = true;
  } catch (err) {
    ElMessage.error('加载订单数据失败: ' + err.message);
  }
};

// 提交订单处理结果
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
      body: JSON.stringify(payload)
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

// 跳转到新建采购单
const navigateToPurchaseForm = () => {
  router.push({ name: 'create-purchase-order' });
};

onMounted(() => {
  fetchOrderList();
  fetchAllOrderItems();
});
</script>

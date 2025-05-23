<template>
  <div>
    <h1>发货单管理</h1>

    <div style="margin-bottom: 10px;">
      <el-input v-model="searchForm.deliveryId" placeholder="按发货单号搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-date-picker v-model="searchForm.deliveryDate" type="date" placeholder="按发货日期搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-select v-model="searchForm.status" placeholder="按状态搜索" clearable style="width: 180px; margin-right: 10px;">
        <el-option label="待发货" :value="0" />
        <el-option label="已完成" :value="1" />
      </el-select>
      <el-button @click="fetchList">查询</el-button>
      <el-button type="primary" @click="openCreateDialog">新建发货单</el-button>
    </div>

    <el-table :data="deliveryList" style="width: 100%">
      <el-table-column prop="deliveryId" label="发货单号" width="120" />
      <el-table-column prop="orderId" label="关联销售订单" />
      <el-table-column prop="deliveryDate" label="发货时间" />
      <el-table-column prop="statusText" label="状态" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewDetails(scope.row.deliveryId)">查看</el-button>
          <el-button size="small" type="danger" :disabled="scope.row.status === 1" @click="deleteOrder(scope.row.deliveryId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 发货明细查看 -->
    <el-dialog v-model="dialogVisible" title="发货明细" width="60%">
      <el-table :data="deliveryDetails" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="订单数量" />
        <el-table-column prop="shippedQuantity" label="发货数量" />
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 创建发货单 -->
    <el-dialog v-model="createDialogVisible" title="新建发货单" width="80%">
      <el-form :model="form" label-width="100px">
        <el-form-item label="选择出库单">
          <el-select v-model="form.stockOutId" placeholder="选择出库单" @change="loadStockOutItems">
            <el-option v-for="stockOut in stockOutOrders" :key="stockOut.stockOutId" :label="stockOut.stockOutId" :value="stockOut.stockOutId" />
          </el-select>
        </el-form-item>
      </el-form>
      <el-table :data="form.items" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="出库数量" />
        <el-table-column label="发货数量">
          <template #default="scope">
            <el-input-number v-model="scope.row.shippedQuantity" :min="0" :max="scope.row.quantity" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitDelivery">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const searchForm = ref({ deliveryId: '', deliveryDate: null, status: null });
const deliveryList = ref([]);
const deliveryDetails = ref([]);
// const salesOrders = ref([]); // 删除原销售订单引用
const stockOutOrders = ref([]); // 新增出库单列表
const form = ref({ stockOutId: null, items: [] }); // 修改表单结构
const dialogVisible = ref(false);
const createDialogVisible = ref(false);

const extractArray = (data) => Array.isArray(data?.data) ? data.data : [];

const fetchList = async () => {
  try {
    const res = await fetch('http://localhost:8090/delivery-order/list');
    const data = await res.json();
    deliveryList.value = extractArray(data).map(i => ({
      ...i,
      statusText: i.status === 1 ? '已完成' : '待发货',
    }));
  } catch {
    ElMessage.error('获取发货数据失败');
  }
};

const viewDetails = async (deliveryId) => {
  try {
    const res = await fetch(`http://localhost:8090/delivery-order/items/${deliveryId}`);
    deliveryDetails.value = extractArray(await res.json());
    dialogVisible.value = true;
  } catch {
    ElMessage.error('加载发货明细失败');
  }
};

const openCreateDialog = async () => {
  try {
    const res = await fetch('http://localhost:8090/stock-out/list');
    stockOutOrders.value = extractArray(await res.json());
    createDialogVisible.value = true;
  } catch {
    ElMessage.error('加载出库单失败');
  }
};

const loadStockOutItems = async (stockOutId) => {
  try {
    const res = await fetch(`http://localhost:8090/stock-out/items/${stockOutId}`);
    form.value.items = extractArray(await res.json()).map(i => ({
      ...i,
      shippedQuantity: i.quantity, // 默认全部发货
    }));
  } catch {
    ElMessage.error('加载出库明细失败');
  }
};

const submitDelivery = async () => {
  const payload = {
    deliveryOrder: {
      stockOutId: form.value.stockOutId,
      deliveryDate: new Date().toISOString().slice(0, 10),
      employeeId: 3,
      status: 0,
    },
    deliveryItems: form.value.items.map(i => ({
      productId: i.productId,
      quantity: i.quantity,
      shippedQuantity: i.shippedQuantity,
    })),
  };

  try {
    const res = await fetch('http://localhost:8090/delivery-order/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    const data = await res.json();
    if (data.code === 200) {
      ElMessage.success('发货成功');
      createDialogVisible.value = false;
      fetchList();
    } else {
      throw new Error(data.msg || '提交失败');
    }
  } catch (e) {
    ElMessage.error('提交失败: ' + e.message);
  }
};

const deleteOrder = (deliveryId) => {
  ElMessageBox.confirm('确定删除该发货单？', '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const res = await fetch(`http://localhost:8090/delivery-order/delete?deliveryId=${deliveryId}`);
      const data = await res.json();
      if (data.code === 200) {
        ElMessage.success('删除成功');
        fetchList();
      } else {
        ElMessage.error(data.msg || '删除失败');
      }
    } catch {
      ElMessage.error('请求失败');
    }
  });
};

onMounted(fetchList);
</script>

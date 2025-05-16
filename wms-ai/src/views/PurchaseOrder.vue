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
      <el-button type="primary" @click="openSelectPlanDialog">新建采购订单</el-button>
    </div>

    <el-table :data="orderList" style="width: 100%">
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

    <!-- 选择采购计划弹窗 -->
    <el-dialog v-model="selectPlanDialogVisible" title="选择采购计划" width="80%">
      <el-table :data="planList" style="width: 100%">
        <el-table-column prop="planId" label="计划单号" width="120" />
        <el-table-column prop="purchaseDate" label="计划日期" width="150" />
        <el-table-column prop="statusText" label="计划状态" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="loadPlanItems(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="selectPlanDialogVisible = false">取消</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 填写采购订单 -->
    <el-dialog v-model="createDialogVisible" title="新建采购订单" width="80%">
      <el-form :model="orderForm" label-width="100px">
        <el-form-item label="供应商">
          <el-select v-model="orderForm.supplierId" placeholder="选择供应商">
            <el-option v-for="s in supplierList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <el-table :data="orderForm.items" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="planQuantity" label="计划数量" />
        <el-table-column label="采购数量">
          <template #default="scope">
            <el-input-number v-model="scope.row.orderQuantity" :min="0" :max="scope.row.planQuantity" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveOrder">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 订单明细 -->
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

    <!-- 处理订单 -->
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
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const orderList = ref([]);
const orderDetails = ref([]);
const planList = ref([]);
const supplierList = ref([]);
const searchForm = ref({ orderId: '', orderDate: null, status: null });
const orderForm = ref({ supplierId: null, items: [] });

const dialogVisible = ref(false);
const createDialogVisible = ref(false);
const handleDialogVisible = ref(false);
const selectPlanDialogVisible = ref(false);

// 公共函数
const extractArray = (data) => Array.isArray(data?.data) ? data.data : [];

const getStatusText = (status) => {
  return status === 0 ? '待收货' : status === 1 ? '已完成' : '部分缺货';
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
  } catch (err) {
    ElMessage.error('获取订单失败: ' + err.message);
  }
};

// 查询采购计划列表（待处理）
const openSelectPlanDialog = async () => {
  try {
    const res = await fetch('http://localhost:8090/purchase-plan/list');
    const data = await res.json();
    planList.value = extractArray(data).filter(p => p.status === 0).map(p => ({
      ...p,
      statusText: '待处理',
    }));
    selectPlanDialogVisible.value = true;
  } catch (err) {
    ElMessage.error('加载采购计划失败: ' + err.message);
  }
};

// 加载选中的采购计划明细
const loadPlanItems = async (planRow) => {
  try {
    const itemRes = await fetch(`http://localhost:8090/purchase-plan/items/${planRow.planId}`);
    const itemData = await itemRes.json();
    const items = extractArray(itemData).map(i => ({
      ...i,
      orderQuantity: i.planQuantity,
    }));

    const supplierRes = await fetch('http://localhost:8090/supplier/list');
    supplierList.value = extractArray(await supplierRes.json());

    orderForm.value = {
      supplierId: null,
      items,
    };

    selectPlanDialogVisible.value = false;
    createDialogVisible.value = true;
  } catch (err) {
    ElMessage.error('加载采购计划明细失败: ' + err.message);
  }
};

// 保存采购订单
const saveOrder = async () => {
  const payload = {
    supplierId: orderForm.value.supplierId,
    orderDate: new Date().toISOString().slice(0, 10),
    status: 0,
    items: orderForm.value.items
        .filter(i => i.orderQuantity > 0)
        .map(i => ({
          productId: i.productId,
          quantity: i.orderQuantity,
        })),
  };
  try {
    const res = await fetch('http://localhost:8090/purchase-order/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    const data = await res.json();
    if (data.code === 200) {
      ElMessage.success('订单创建成功');
      createDialogVisible.value = false;
      fetchOrderList();
    } else {
      throw new Error(data.msg || '未知错误');
    }
  } catch (err) {
    ElMessage.error('保存失败: ' + err.message);
  }
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
      const res = await fetch(`http://localhost:8090/purchase-order/delete?id=${orderId}`);
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

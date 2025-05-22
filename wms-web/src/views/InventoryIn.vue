<template>
  <div>
    <h1>库存入库管理</h1>

    <div style="margin-bottom: 10px;">
      <el-input v-model="searchForm.stockInId" placeholder="按入库单号搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-date-picker v-model="searchForm.stockInDate" type="date" placeholder="按入库日期搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-select v-model="searchForm.status" placeholder="按状态搜索" clearable style="width: 180px; margin-right: 10px;">
        <el-option label="待入库" :value="0" />
        <el-option label="已完成" :value="1" />
      </el-select>
      <el-button @click="fetchInList">查询</el-button>
      <el-button type="primary" @click="openCreateDialog">新建入库单</el-button>
    </div>

    <el-table :data="pagedInList" style="width: 100%">
      <el-table-column prop="stockInId" label="入库单号" width="120" />
      <el-table-column prop="orderId" label="关联采购订单" width="150" />
      <el-table-column prop="stockInDate" label="入库时间" width="150" />
      <el-table-column prop="statusText" label="状态" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewDetails(scope.row.stockInId)">查看</el-button>
          <el-button size="small" type="primary" :disabled="scope.row.status === 1" @click="handleProcess(scope.row)">处理</el-button>
          <el-button size="small" type="danger" :disabled="scope.row.status === 1" @click="deleteIn(scope.row.stockInId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 入库明细查看 -->
    <el-dialog v-model="dialogVisible" title="入库明细" width="60%" @open="adjustDialogSize">
      <!-- 信息栏：入库单号、采购订单号、入库时间 -->
      <div style="margin-bottom: 15px;">
        <el-row :gutter="20">
          <el-col :span="8"><strong>入库单号：</strong>{{ stockInInfo.stockInId }}</el-col>
          <el-col :span="8"><strong>采购订单号：</strong>{{ stockInInfo.orderId }}</el-col>
          <el-col :span="8"><strong>入库时间：</strong>{{ stockInInfo.stockInDate }}</el-col>
        </el-row>
      </div>

      <!-- 表格 -->
      <el-table :data="inDetails" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="specifications" label="规格" />
        <el-table-column prop="quantity" label="到货数量" />
        <el-table-column prop="acceptedQuantity" label="已入库数量" />
      </el-table>

      <template #footer>
    <span class="dialog-footer">
      <el-button @click="dialogVisible = false">关闭</el-button>
    </span>
      </template>
    </el-dialog>


    <!-- 新建入库单弹窗 -->
    <el-dialog v-model="createDialogVisible" title="新建入库单" width="80%">
      <el-form :model="inForm" label-width="100px">
        <el-form-item label="选择采购订单">
          <el-input
            v-model="selectedOrderDisplay"
            placeholder="请选择采购订单"
            readonly
            style="width: 300px; margin-right: 10px;"
            @click="openOrderSelectDialog"
          />
          <el-button @click="openOrderSelectDialog">选择</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="inForm.items" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="订单数量" />
        <el-table-column label="入库数量">
          <template #default="scope">
            <el-input-number v-model="scope.row.inQuantity" :min="0" :max="scope.row.quantity" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitIn">提交</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 入库操作弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑入库单" width="80%">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="关联采购订单">
          <el-input v-model="editForm.orderId" readonly style="width: 300px;" />
        </el-form-item>
        <el-table :data="editForm.items" style="width: 100%">
          <el-table-column prop="productId" label="商品ID" />
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="specifications" label="规格" />
          <el-table-column prop="quantity" label="订单数量" />
          <el-table-column label="入库数量">
            <template #default="scope">
              <el-input-number v-model="scope.row.inQuantity" :min="0" :max="scope.row.quantity" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
    <span class="dialog-footer">
      <el-button @click="editDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitEdit">提交</el-button>
    </span>
      </template>
    </el-dialog>

    <!-- 采购订单选择弹窗 -->
    <el-dialog v-model="orderSelectDialogVisible" title="选择采购订单" width="60%">
      <!-- 新增：采购订单过滤条件 -->
      <div style="margin-bottom: 10px;">
        <el-input v-model="orderSearchForm.orderId" placeholder="按订单号搜索" clearable style="width: 180px; margin-right: 10px;" />
        <el-date-picker v-model="orderSearchForm.orderDate" type="date" placeholder="按下单日期搜索" clearable style="width: 180px; margin-right: 10px;" />
        <el-button @click="handleOrderSearch">查询</el-button>
      </div>
      <el-table :data="pagedFilteredPurchaseOrders" @row-dblclick="handleOrderSelect" highlight-current-row style="width: 100%">
        <el-table-column prop="orderId" label="订单号" />
        <el-table-column prop="orderDate" label="下单日期" />
        <el-table-column prop="statusText" label="状态" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" @click="handleOrderSelect(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        background
        layout="prev, pager, next"
        :total="filteredPurchaseOrders.length"
        :page-size="orderPageSize"
        :current-page="orderCurrentPage"
        @current-change="handleOrderPageChange"
        style="margin: 16px 0; text-align: right;"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="orderSelectDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const searchForm = ref({ stockInId: '', stockInDate: null, status: null });
const inList = ref([]);
const inDetails = ref([]);
const purchaseOrders = ref([]);
const inForm = ref({ orderId: null, items: [] });
const dialogVisible = ref(false);
const createDialogVisible = ref(false);
const orderSelectDialogVisible = ref(false);
const selectedOrderDisplay = ref('');
const editDialogVisible = ref(false);
const editForm = ref({ orderId: null, items: [] });

const stockInInfo = ref({
  stockInId: '',
  orderId: '',
  stockInDate: ''
});


// 安全提取
const extractArray = (data) => Array.isArray(data?.data) ? data.data : [];

// 分页相关
const inCurrentPage = ref(1);
const inPageSize = ref(10);
const pagedInList = computed(() => {
  const start = (inCurrentPage.value - 1) * inPageSize.value;
  const end = start + inPageSize.value;
  return filteredInList.value.slice(start, end);
});

// 过滤和分页
const filteredInList = computed(() => {
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
  return inList.value.filter(item => {
    if (searchForm.value.stockInId && !String(item.stockInId).includes(searchForm.value.stockInId)) {
      return false;
    }
    if (searchForm.value.stockInDate) {
      const searchDateStr = formatDate(searchForm.value.stockInDate);
      const itemDateStr = formatDate(item.stockInDate);
      if (itemDateStr !== searchDateStr) return false;
    }
    if (searchForm.value.status !== null && searchForm.value.status !== '' && item.status !== searchForm.value.status) {
      return false;
    }
    return true;
  });
});


// 获取入库列表
const fetchInList = async () => {
  try {
    const res = await fetch('http://localhost:8090/stock-in/list');
    const data = await res.json();
    inList.value = extractArray(data).map(i => ({
      ...i,
      statusText: i.status === 1 ? '已完成' : '待入库',
    }));
    inCurrentPage.value = 1;
  } catch (e) {
    ElMessage.error('获取入库数据失败');
  }
};

// 查看入库明细
const viewDetails = async (stockInId) => {
  try {
    // 设置表头信息
    const record = inList.value.find(item => item.stockInId === stockInId);
    if (record) {
      stockInInfo.value = {
        stockInId: record.stockInId,
        orderId: record.orderId,
        stockInDate: record.stockInDate?.slice(0, 10) || ''
      };
    }

    // 加载明细表格
    const res = await fetch(`http://localhost:8090/stock-in/items/${stockInId}`);
    inDetails.value = extractArray(await res.json());

    dialogVisible.value = true;
  } catch (e) {
    ElMessage.error('加载入库明细失败');
  }
};


// 打开新建入库单弹窗时，预加载采购订单数据
const openCreateDialog = async () => {
  try {
    const res = await fetch('http://localhost:8090/purchase-order/list');
    purchaseOrders.value = extractArray(await res.json()).map(i => ({
      ...i,
      statusText: i.status === 1 ? '已完成' : '待入库',
    }));
    inForm.value.orderId = null;
    inForm.value.items = [];
    selectedOrderDisplay.value = '';
    createDialogVisible.value = true;
  } catch {
    ElMessage.error('加载采购订单失败');
  }
};

// 打开采购订单选择弹窗
const openOrderSelectDialog = () => {
  orderSelectDialogVisible.value = true;
};

// 选择采购订单
const handleOrderSelect = async (order) => {
  inForm.value.orderId = order.orderId;
  selectedOrderDisplay.value = order.orderId;
  orderSelectDialogVisible.value = false;
  await loadOrderItems(order.orderId);
};

// 加载选中采购订单的商品明细
const loadOrderItems = async (orderId) => {
  if (!orderId) return;
  try {
    const res = await fetch(`http://localhost:8090/purchase-order/items/${orderId}`);
    inForm.value.items = extractArray(await res.json()).map(i => ({
      ...i,
      inQuantity: i.quantity, // 默认全部入库
      acceptedQuantity: 0     // 新增：新建时默认acceptedQuantity为0
    }));
  } catch {
    ElMessage.error('加载订单商品失败');
  }
};

// 提交入库单
const submitIn = async () => {
  const payload = {
    orderId: inForm.value.orderId,
    employeeId: 3,
    stockInDate: new Date().toISOString().slice(0, 10),
    status: 0,
    stockInItems: inForm.value.items.map(i => ({
      productId: i.productId,
      quantity: i.inQuantity,
      acceptedQuantity: 0 // 新建时acceptedQuantity为0
    })),
  };
  try {
    const res = await fetch('http://localhost:8090/stock-in/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    const data = await res.json();
    if (data.code === 200) {
      // 新增：入库成功后，更新采购订单状态为1
      try {
        await fetch('http://localhost:8090/purchase-order/updateStatus', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            orderId: inForm.value.orderId,
            status: '1'
          }),
        });
      } catch (e) {
        ElMessage.warning('采购订单状态更新失败');
      }
      ElMessage.success('入库成功');
      createDialogVisible.value = false;
      fetchInList();
    } else {
      throw new Error(data.msg || '保存失败');
    }
  } catch (e) {
    ElMessage.error('提交失败: ' + e.message);
  }
};

// 删除入库单
const deleteIn = (stockInId) => {
  ElMessageBox.confirm('确定删除该入库单？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const res = await fetch(`http://localhost:8090/stock-in/delete?stockInId=${stockInId}`);
      const data = await res.json();
      if (data.code === 200) {
        ElMessage.success('删除成功');
        fetchInList();
      } else {
        throw new Error(data.msg || '删除失败');
      }
    } catch (e) {
      ElMessage.error('删除失败: ' + e.message);
    }
  });
};

// 处理 ResizeObserver 警告
const adjustDialogSize = () => {
  requestAnimationFrame(() => {
    const dialog = document.querySelector('.el-dialog');
    if (dialog) dialog.style.maxWidth = '80%';
  });
};

// 采购订单选择弹窗的过滤表单
const orderSearchForm = ref({ orderId: '', orderDate: null });

// 采购订单分页
const orderCurrentPage = ref(1);
const orderPageSize = ref(10);
const handleOrderPageChange = (page) => {
  orderCurrentPage.value = page;
};

// 采购订单过滤（只显示状态为0的订单，并可按订单号和下单日期过滤）
const filteredPurchaseOrders = computed(() => {
  const formatDate = (d) => {
    if (!d) return '';
    if (typeof d === 'string') return d.slice(0, 10);
    if (d instanceof Date) {
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
    return '';
  };
  return purchaseOrders.value.filter(order => {
    if (order.status !== 0) return false;
    if (orderSearchForm.value.orderId && !String(order.orderId).includes(orderSearchForm.value.orderId)) {
      return false;
    }
    if (orderSearchForm.value.orderDate) {
      const searchDateStr = formatDate(orderSearchForm.value.orderDate);
      const orderDateStr = formatDate(order.orderDate);
      if (orderDateStr !== searchDateStr) return false;
    }
    return true;
  });
});
const pagedFilteredPurchaseOrders = computed(() => {
  const start = (orderCurrentPage.value - 1) * orderPageSize.value;
  const end = start + orderPageSize.value;
  return filteredPurchaseOrders.value.slice(start, end);
});
const handleOrderSearch = () => {
  orderCurrentPage.value = 1;
};


// 提交编辑后的数据
const submitEdit = async () => {
  const payload = {
    stockInId: editForm.value.stockInId,
    orderId: editForm.value.orderId,
    employeeId: 3,
    stockInDate: new Date().toISOString().slice(0, 10),
    status: '1',
    stockInItems: editForm.value.items.map(i => ({
      productId: i.productId,
      quantity: i.quantity, // 保持原始quantity不变
      acceptedQuantity: i.inQuantity // 只用inQuantity作为本次实际入库数量
    })),
  };
  try {
    const res = await fetch('http://localhost:8090/stock-in/modify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    const data = await res.json();
    if (data.code === 200) {
      for (const item of editForm.value.items) {
        const stockPayload = { productId: item.productId, stockQuantity: item.stockQuantity + item.inQuantity };
        await fetch('http://localhost:8090/product/modify', {
          method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(stockPayload)
        });
      }
      ElMessage.success('入库处理成功，库存已更新');
      editDialogVisible.value = false;
      fetchInList(); // 刷新列表
    } else {
      throw new Error(data.msg || '编辑失败');
    }
  } catch (e) {
    ElMessage.error('提交失败: ' + e.message);
  }
};

// 处理按钮逻辑：弹出明细并允许编辑入库数量
const handleProcess = async (row) => {
  try {
    const res = await fetch(`http://localhost:8090/stock-in/items/${row.stockInId}`);
    const items = extractArray(await res.json());
    editForm.value = {
      stockInId: row.stockInId,
      orderId: row.orderId,
      items: items.map(i => ({
        ...i,
        inQuantity: i.acceptedQuantity ?? 0 // 默认显示上次已入库数量，若无则为0
      })),
    };
    editDialogVisible.value = true;
  } catch (e) {
    ElMessage.error('加载入库明细失败');
  }
};

onMounted(() => {
  fetchInList();
});
</script>

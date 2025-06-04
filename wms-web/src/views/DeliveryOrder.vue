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

    <el-table :data="pagedDeliveryList" style="width: 100%">
      <el-table-column prop="deliveryId" label="发货单号" width="120" />
      <el-table-column prop="stockOutId" label="关联销售订单" />
      <el-table-column prop="deliveryDate" label="发货时间" />
      <el-table-column prop="statusText" label="状态" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewDetails(scope.row.deliveryId)">查看</el-button>
          <el-button size="small" type="danger" @click="deleteOrder(scope.row.deliveryId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      background
      layout="prev, pager, next"
      :total="filteredDeliveryList.length"
      :page-size="pageSize"
      :current-page="currentPage"
      @current-change="handlePageChange"
      style="margin: 16px 0; text-align: right;"
    />

    <!-- 发货明细查看 -->
    <el-dialog v-model="dialogVisible" title="发货明细" width="60%">
      <el-table :data="deliveryDetails" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="数量" />
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 出库单选择对话框 -->
    <el-dialog v-model="createDialogVisible" title="选择出库单创建发货单" width="80%">
      <!-- 出库单过滤条件 -->
      <div style="margin-bottom: 10px;">
        <el-input v-model="stockOutSearchForm.stockOutId" placeholder="按出库单号搜索" clearable style="width: 180px; margin-right: 10px;"></el-input>
        <el-date-picker v-model="stockOutSearchForm.stockOutDate" type="date" placeholder="按出库日期搜索" clearable style="width: 180px; margin-right: 10px;"></el-date-picker>
        <el-select v-model="stockOutSearchForm.status" placeholder="按状态搜索" clearable style="width: 180px; margin-right: 10px;">
          <el-option label="已出库" :value="1" />
        </el-select>
        <el-button @click="handleStockOutSearch">查询</el-button>
      </div>
      
      <!-- 出库单列表 -->
      <el-table :data="pagedStockOutList" style="width: 100%">
        <el-table-column prop="stockOutId" label="出库单号" width="120"></el-table-column>
        <el-table-column prop="salesOrderId" label="关联销售单号" width="120"></el-table-column>
        <el-table-column prop="stockOutDate" label="出库日期"></el-table-column>
        <el-table-column label="状态">
          <template #default="scope">
            <span>{{ scope.row.status === 1 ? '已出库' : '待处理' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="showDeliveryForm(scope.row)">生成发货单</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 出库单分页 -->
      <el-pagination
          background
          layout="prev, pager, next"
          :total="filteredStockOutList.length"
          :page-size="stockOutPageSize"
          :current-page="stockOutCurrentPage"
          @current-change="handleStockOutPageChange"
          style="margin-top: 20px; text-align: right;"
      />
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 创建发货单表单 -->
    <el-dialog v-model="deliveryFormVisible" title="新建发货单" width="80%">
      <el-form :model="form" label-width="100px">
        <el-form-item label="出库单号">
          <el-input v-model="form.stockOutId" :disabled="true" />
        </el-form-item>
      </el-form>
      <el-table :data="form.items" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="出库数量" />
        <el-table-column label="发货数量">
          <template #default="scope">
            <el-input-number v-model="scope.row.quantity" :min="0" :max="scope.row.quantity" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deliveryFormVisible = false">取消</el-button>
          <el-button type="primary" @click="submitDelivery">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const searchForm = ref({ deliveryId: '', deliveryDate: null, status: null });
const deliveryList = ref([]);
const deliveryDetails = ref([]);
const stockOutOrders = ref([]);
const form = ref({ stockOutId: null, items: [] });
const dialogVisible = ref(false);
const createDialogVisible = ref(false);
const deliveryFormVisible = ref(false);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const stockOutCurrentPage = ref(1);
const stockOutPageSize = ref(8);

// 出库单搜索表单
const stockOutSearchForm = ref({
  stockOutId: '',
  stockOutDate: null,
  status: 1 // 默认只显示已出库的
});

const extractArray = (data) => Array.isArray(data?.data) ? data.data : [];

// 添加过滤逻辑
const filteredDeliveryList = computed(() => {
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
  
  return deliveryList.value.filter(item => {
    if (searchForm.value.deliveryId && !String(item.deliveryId).includes(searchForm.value.deliveryId)) {
      return false;
    }
    if (searchForm.value.deliveryDate) {
      const searchDateStr = formatDate(searchForm.value.deliveryDate);
      const itemDateStr = formatDate(item.deliveryDate);
      if (itemDateStr !== searchDateStr) return false;
    }
    if (searchForm.value.status !== null && searchForm.value.status !== '' && item.status !== searchForm.value.status) {
      return false;
    }
    return true;
  });
});

// 分页后的数据
const pagedDeliveryList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredDeliveryList.value.slice(start, end);
});

// 过滤后的出库单列表
const filteredStockOutList = computed(() => {
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
  
  return stockOutOrders.value.filter(item => {
    // 只显示已出库的单据
    if (stockOutSearchForm.value.status !== null && item.status !== stockOutSearchForm.value.status) {
      return false;
    }
    
    // 出库单号过滤
    if (stockOutSearchForm.value.stockOutId && !String(item.stockOutId).includes(stockOutSearchForm.value.stockOutId)) {
      return false;
    }
    
    // 出库日期过滤
    if (stockOutSearchForm.value.stockOutDate) {
      const searchDateStr = formatDate(stockOutSearchForm.value.stockOutDate);
      const itemDateStr = formatDate(item.stockOutDate);
      if (itemDateStr !== searchDateStr) return false;
    }
    
    return true;
  });
});

// 分页后的出库单数据
const pagedStockOutList = computed(() => {
  const start = (stockOutCurrentPage.value - 1) * stockOutPageSize.value;
  const end = start + stockOutPageSize.value;
  return filteredStockOutList.value.slice(start, end);
});

// 页码变更处理
const handlePageChange = (page) => {
  currentPage.value = page;
};

// 出库单页码变更处理
const handleStockOutPageChange = (page) => {
  stockOutCurrentPage.value = page;
};

// 出库单查询按钮处理
const handleStockOutSearch = () => {
  stockOutCurrentPage.value = 1; // 重置为第一页
};

const fetchList = async () => {
  try {
    const res = await fetch('http://localhost:8090/delivery-order/list');
    const data = await res.json();
    deliveryList.value = extractArray(data).map(i => ({
      ...i,
      statusText: i.status === 1 ? '已完成' : '待发货',
    }));
    // 重置为第一页
    currentPage.value = 1;
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
  // 重置表单
  form.value = { stockOutId: null, items: [] };
  stockOutSearchForm.value = { stockOutId: '', stockOutDate: null, status: 1 }; // 重置搜索表单
  stockOutCurrentPage.value = 1; // 重置页码
  
  try {
    const res = await fetch('http://localhost:8090/stock-out/list');
    stockOutOrders.value = extractArray(await res.json());
    createDialogVisible.value = true;
  } catch {
    ElMessage.error('加载出库单失败');
  }
};

const showDeliveryForm = async (stockOutOrder) => {
  form.value = { stockOutId: stockOutOrder.stockOutId, items: [] };
  deliveryFormVisible.value = true;
  await loadStockOutItems(stockOutOrder.stockOutId);
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
    employeeId: 3,
    stockOutId: form.value.stockOutId,
    deliveryDate: new Date().toISOString().slice(0, 10),
    status: "1",
    deliveryOrderItems: form.value.items.map(i => ({
      productId: i.productId,
      quantity: i.quantity,
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
      // 更新出库单状态为已发货
      await updateStockOutStatus(form.value.stockOutId);
      ElMessage.success('发货成功');
      deliveryFormVisible.value = false;
      createDialogVisible.value = false;
      fetchList();
    } else {
      throw new Error(data.msg || '提交失败');
    }
  } catch (e) {
    ElMessage.error('提交失败: ' + e.message);
  }
};

// 新增函数：更新出库单状态为已发货
const updateStockOutStatus = async (stockOutId) => {
  try {
    const res = await fetch('http://localhost:8090/stock-out/updateStatus', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ stockOutId: stockOutId, status: "6" })
    });
    const data = await res.json();
    if (data.code !== 200) {
      console.warn('更新出库单状态失败:', data.msg);
      ElMessage.warning('出库单状态更新失败，请手动检查');
    }
  } catch (error) {
    console.error('更新出库单状态出错:', error);
    ElMessage.warning('出库单状态更新失败，请手动检查');
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

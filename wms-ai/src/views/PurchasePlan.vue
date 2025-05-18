<template>
  <div>
    <h1>采购计划管理</h1>

    <!-- 搜索栏 -->
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchForm.planId" placeholder="按计划单号搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-date-picker v-model="searchForm.planDate" type="date" placeholder="按计划日期搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-select v-model="searchForm.status" placeholder="按状态搜索" clearable style="width: 180px; margin-right: 10px;">
        <el-option label="待处理" :value="0" />
        <el-option label="已完成" :value="1" />
        <el-option label="缺货" :value="2" />
      </el-select>
      <el-button @click="fetchPlanList">查询</el-button>
      <el-button type="primary" @click="openCreatePlanDialog">新建采购计划</el-button>
    </div>

    <!-- 采购计划列表 -->
    <el-table :data="pagedPlanList" style="width: 100%">
      <el-table-column prop="planId" label="计划单号" width="120" />
      <el-table-column prop="stockOutId" label="关联出库单号" width="150" />
      <el-table-column prop="purchaseDate" label="计划日期" width="150" />
      <el-table-column prop="statusText" label="状态" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewPlanDetails(scope.row.planId)">查看</el-button>
          <el-button size="small" @click="editPlan(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deletePlan(scope.row.planId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      layout="prev, pager, next"
      :total="filteredPlanList.length"
      :page-size="planPageSize"
      :current-page="planCurrentPage"
      @current-change="handlePlanPageChange"
      style="margin: 16px 0; text-align: right;"
    />

    <!-- 查看计划明细 -->
    <el-dialog v-model="dialogVisible" title="采购计划明细" width="60%">
      <el-table :data="planDetails" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="specifications" label="规格" />
        <el-table-column prop="planQuantity" label="计划数量" />
        <el-table-column prop="stockQuantity" label="当前库存" />
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新建采购计划：选择缺货出库单 -->
    <el-dialog v-model="createPlanDialogVisible" title="关联出库单创建采购计划" width="80%">
      <!-- 新增：出库日期过滤 -->
      <div style="margin-bottom: 10px;">
        <el-date-picker v-model="stockOutSearchForm.stockOutDate" type="date" placeholder="按出库日期搜索" clearable style="width: 180px; margin-right: 10px;" />
      </div>
      <el-table :data="pagedStockOutShortageList" style="width: 100%">
        <el-table-column prop="stockOutId" label="出库单号" width="120" />
        <el-table-column prop="stockOutDate" label="出库日期" width="150" />
        <el-table-column prop="salesOrderId" label="销售单号"  />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <span>{{ scope.row.status === 0 ? '待处理' : scope.row.status === 1 ? '已出库' : scope.row.status === 2 ? '缺货' : "已请求采购" }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="selectOutstockForPlan(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        background
        layout="prev, pager, next"
        :total="filteredStockOutShortageList.length"
        :page-size="stockOutPageSize"
        :current-page="stockOutCurrentPage"
        @current-change="handleStockOutPageChange"
        style="margin: 16px 0; text-align: right;"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createPlanDialogVisible = false">取消</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 填写采购计划明细 -->
    <el-dialog v-model="planFormDialogVisible" title="填写采购计划明细" width="80%">
      <el-table :data="planForm.items" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="stockQuantity" label="当前库存" />
        <el-table-column prop="shortageQuantity" label="缺货数量" />
        <el-table-column label="计划采购数量">
          <template #default="scope">
            <el-input-number v-model="scope.row.planQuantity" :min="0" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="planFormDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePlan">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

// 状态变量
const planList = ref([]);
const planDetails = ref([]);
const planForm = ref({ stockOutId: null, items: [] });
const currentPlanId = ref(null);

// 搜索
const searchForm = ref({ planId: '', planDate: null, status: null });

// 弹窗控制
const dialogVisible = ref(false);
const createPlanDialogVisible = ref(false);
const planFormDialogVisible = ref(false);

// 缺货出库单列表
const stockOutShortageList = ref([]);

// 新增：出库单过滤表单
const stockOutSearchForm = ref({ stockOutDate: null });

// 新增：过滤后的出库单列表
const filteredStockOutShortageList = computed(() => {
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
  return stockOutShortageList.value.filter(item => {
    if (stockOutSearchForm.value.stockOutDate) {
      const searchDateStr = formatDate(stockOutSearchForm.value.stockOutDate);
      const itemDateStr = formatDate(item.stockOutDate);
      if (itemDateStr !== searchDateStr) return false;
    }
    return true;
  });
});

// 新增：采购计划过滤
const filteredPlanList = computed(() => {
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
  return planList.value.filter(plan => {
    // 计划单号过滤
    if (searchForm.value.planId && !String(plan.planId).includes(searchForm.value.planId)) {
      return false;
    }
    // 计划日期过滤
    if (searchForm.value.planDate) {
      const searchDateStr = formatDate(searchForm.value.planDate);
      const planDateStr = formatDate(plan.purchaseDate);
      if (planDateStr !== searchDateStr) return false;
    }
    // 状态过滤
    if (searchForm.value.status !== null && searchForm.value.status !== '' && plan.status !== searchForm.value.status) {
      return false;
    }
    return true;
  });
});

// 采购计划分页
const planCurrentPage = ref(1);
const planPageSize = ref(10);
const pagedPlanList = computed(() => {
  const start = (planCurrentPage.value - 1) * planPageSize.value;
  const end = start + planPageSize.value;
  return filteredPlanList.value.slice(start, end);
});
const handlePlanPageChange = (page) => {
  planCurrentPage.value = page;
};

// 缺货出库单分页
const stockOutCurrentPage = ref(1);
const stockOutPageSize = ref(10);
const pagedStockOutShortageList = computed(() => {
  const start = (stockOutCurrentPage.value - 1) * stockOutPageSize.value;
  const end = start + stockOutPageSize.value;
  return filteredStockOutShortageList.value.slice(start, end);
});
const handleStockOutPageChange = (page) => {
  stockOutCurrentPage.value = page;
};

// 采购计划状态映射
const getStatusText = (status) => {
  return status === 0 ? '待处理' : status === 1 ? '已完成' : '缺货';
};

// 获取采购计划列表
const fetchPlanList = async () => {
  try {
    const res = await fetch('http://localhost:8090/purchase-plan/list');
    const data = await res.json();
    planList.value = data.data.map(p => ({
      ...p,
      statusText: getStatusText(p.status),
    }));
    planCurrentPage.value = 1; // 查询后重置页码
  } catch {
    ElMessage.error('获取采购计划失败');
  }
};

// 查看明细
const viewPlanDetails = async (planId) => {
  currentPlanId.value = planId;
  const res = await fetch(`http://localhost:8090/purchase-plan/items/${planId}`);
  const data = await res.json();
  planDetails.value = data.data;
  dialogVisible.value = true;
};

// 打开“选择缺货出库单”
const openCreatePlanDialog = async () => {
  try {
    const res = await fetch('http://localhost:8090/stock-out/list');
    const data = await res.json();
    stockOutShortageList.value = data.data.filter(item => item.status === 2);
    stockOutCurrentPage.value = 1; // 打开弹窗时重置页码
    createPlanDialogVisible.value = true;
  } catch {
    ElMessage.error('获取出库单失败');
  }
};

// 选择某出库单 → 加载缺货商品
const selectOutstockForPlan = async (row) => {
  try {
    const res = await fetch(`http://localhost:8090/stock-out/items/${row.stockOutId}`);
    const data = await res.json();
    planForm.value.stockOutId = row.stockOutId;
    planForm.value.items = data.data.map(item => {
      const shortage = item.quantity - item.stockQuantity;
      return {
        ...item,
        shortageQuantity: shortage > 0 ? shortage : 0,
        planQuantity: shortage > 0 ? shortage : 0,
      };
    });
    createPlanDialogVisible.value = false;
    planFormDialogVisible.value = true;
    currentPlanId.value = null; // 新建时 planId 为 null
  } catch {
    ElMessage.error('加载出库单明细失败');
  }
};

// 保存采购计划
const savePlan = async () => {
  const payload = {
    planId: planForm.value.planId || null,
    stockOutId: planForm.value.stockOutId,
    purchaseDate: new Date().toISOString().slice(0, 10),
    status: 0,
    purchasePlanItems: planForm.value.items
      .filter(i => i.planQuantity > 0)
      .map(i => ({
        productId: i.productId,
        planQuantity: i.planQuantity,
      })),
  };

  // 判断是新建还是编辑，编辑时调用 modify 接口
  const isEdit = !!planForm.value.planId;
  const url = isEdit
    ? 'http://localhost:8090/purchase-plan/modify'
    : 'http://localhost:8090/purchase-plan/save';

  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });

  const data = await res.json();
  if (data.code === 200) {
    // 新增：同步更新出库单状态为3
    try {
      await fetch('http://localhost:8090/stock-out/updateStatus', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          stockOutId: planForm.value.stockOutId,
          status: "3"
        }),
      });
    } catch (e) {
      ElMessage.warning('出库单状态同步失败');
    }

    ElMessage.success('保存成功');
    planFormDialogVisible.value = false;
    fetchPlanList();
  } else {
    ElMessage.error('保存失败');
  }
};

// 编辑采购计划（替换原处理功能）
const editPlan = async (row) => {
  currentPlanId.value = row.planId;
  // 先获取采购计划明细
  const res = await fetch(`http://localhost:8090/purchase-plan/items/${row.planId}`);
  const data = await res.json();
  // 再获取出库单明细（用于获取最新库存和出库数量）
  const outRes = await fetch(`http://localhost:8090/stock-out/items/${row.stockOutId}`);
  const outData = await outRes.json();
  // 构建商品ID到出库单明细的映射
  const outMap = {};
  outData.data.forEach(item => {
    outMap[item.productId] = item;
  });
  // 用出库单明细的库存和出库数量来计算缺货数量，和新建逻辑一致
  planForm.value = {
    planId: row.planId,
    stockOutId: row.stockOutId,
    items: data.data.map(p => {
      const outItem = outMap[p.productId] || {};
      const stockQuantity = outItem.stockQuantity !== undefined ? outItem.stockQuantity : (p.stockQuantity !== undefined ? p.stockQuantity : 0);
      const quantity = outItem.quantity !== undefined ? outItem.quantity : (p.quantity !== undefined ? p.quantity : 0);
      const shortage = quantity - stockQuantity;
      return {
        ...p,
        stockQuantity,
        shortageQuantity: shortage > 0 ? shortage : 0,
        planQuantity: p.planQuantity
      };
    }),
  };
  planFormDialogVisible.value = true;
};

// 删除采购计划
const deletePlan = (planId) => {
  ElMessageBox.confirm('确认删除此采购计划？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const res = await fetch(`http://localhost:8090/purchase-plan/delete?purchasePlanId=${planId}`, {
      method: 'GET',
    });
    const data = await res.json();
    if (data.code === 200) {
      ElMessage.success('删除成功');
      fetchPlanList();
    } else {
      ElMessage.error('删除失败');
    }
  });
};

onMounted(fetchPlanList);
</script>

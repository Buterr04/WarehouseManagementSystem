<template>
  <div>
    <h1>采购计划管理</h1>
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchForm.planId" placeholder="按计划单号搜索" clearable style="width: 180px; margin-right: 10px;"></el-input>
      <el-date-picker v-model="searchForm.planDate" type="date" placeholder="按计划日期搜索" clearable style="width: 180px; margin-right: 10px;"></el-date-picker>
      <el-select v-model="searchForm.status" placeholder="按状态搜索" clearable style="width: 180px; margin-right: 10px;">
        <el-option label="待处理" :value="0" />
        <el-option label="已完成" :value="1" />
        <el-option label="缺货" :value="2" />
      </el-select>
      <el-button @click="fetchPlanList">查询</el-button>
      <el-button type="primary" @click="openCreatePlanDialog">新建采购计划</el-button>
    </div>

    <el-table :data="planList" style="width: 100%">
      <el-table-column prop="planId" label="计划单号" width="120" />
      <el-table-column prop="planDate" label="计划日期" width="150" />
      <el-table-column prop="statusText" label="状态" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewPlanDetails(scope.row.planId)">查看</el-button>
          <el-button size="small" :disabled="scope.row.status === 1" @click="handlePlan(scope.row)">处理</el-button>
          <el-button size="small" type="danger" @click="deletePlan(scope.row.planId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 查看计划明细 -->
    <el-dialog v-model="dialogVisible" title="采购计划明细" width="60%" @open="adjustDialogSize">
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

    <!-- 新建采购计划 -->
    <el-dialog v-model="createDialogVisible" title="新建采购计划" width="80%">
      <el-table :data="productList" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="stockQuantity" label="库存数量" />
        <el-table-column label="计划数量">
          <template #default="scope">
            <el-input-number v-model="scope.row.planQuantity" :min="0" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePlan">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 处理采购计划 -->
    <el-dialog v-model="handleDialogVisible" title="处理采购计划" width="80%">
      <el-table :data="planForm.items" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="planQuantity" label="计划数量" />
        <el-table-column prop="stockQuantity" label="库存数量" />
        <el-table-column label="实际采购数量">
          <template #default="scope">
            <el-input-number v-model="scope.row.realQuantity" :min="0" :max="scope.row.planQuantity" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPlanProcessing">确认处理</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

// 状态与数据
const planList = ref([]);
const productList = ref([]);
const planDetails = ref([]);
const searchForm = ref({ planId: '', planDate: null, status: null });
const planForm = ref({ planId: null, items: [] });
const currentPlanId = ref(null);

// 弹窗控制
const dialogVisible = ref(false);
const createDialogVisible = ref(false);
const handleDialogVisible = ref(false);

// 显示计划列表
const fetchPlanList = async () => {
  try {
    const res = await fetch('http://localhost:8090/purchase-plan/list');
    const data = await res.json();
    planList.value = data.data.map(p => ({
      ...p,
      statusText: getStatusText(p.status),
    }));
  } catch {
    ElMessage.error('获取采购计划失败');
  }
};

// 获取计划明细
const viewPlanDetails = async (planId) => {
  currentPlanId.value = planId;
  const res = await fetch(`http://localhost:8090/purchase-plan/items/${planId}`);
  const data = await res.json();
  planDetails.value = data.data;
  dialogVisible.value = true;
};

// 新建采购计划
const openCreatePlanDialog = async () => {
  const res = await fetch('http://localhost:8090/product/list');
  const data = await res.json();
  productList.value = data.data.map(p => ({ ...p, planQuantity: 0 }));
  createDialogVisible.value = true;
};

// 保存采购计划
const savePlan = async () => {
  const payload = {
    planDate: new Date().toISOString().slice(0, 10),
    status: 0,
    items: productList.value
        .filter(p => p.planQuantity > 0)
        .map(p => ({ productId: p.productId, quantity: p.planQuantity })),
  };
  const res = await fetch('http://localhost:8090/purchase-plan/save', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  const data = await res.json();
  if (data.code === 200) {
    ElMessage.success('保存成功');
    createDialogVisible.value = false;
    fetchPlanList();
  } else {
    ElMessage.error('保存失败');
  }
};

// 处理采购计划
const handlePlan = async (row) => {
  currentPlanId.value = row.planId;
  const res = await fetch(`http://localhost:8090/purchase-plan/items/${row.planId}`);
  const data = await res.json();
  planForm.value = {
    planId: row.planId,
    items: data.data.map(p => ({
      ...p,
      realQuantity: p.planQuantity,
    })),
  };
  handleDialogVisible.value = true;
};

// 提交采购处理
const submitPlanProcessing = async () => {
  const hasShortage = planForm.value.items.some(i => i.realQuantity < i.planQuantity);
  const payload = {
    planId: planForm.value.planId,
    status: hasShortage ? 2 : 1,
    items: planForm.value.items.map(i => ({
      productId: i.productId,
      quantity: i.realQuantity,
    })),
  };
  const res = await fetch('http://localhost:8090/purchase-plan/modify', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  const data = await res.json();
  if (data.code === 200) {
    ElMessage.success('处理成功');
    handleDialogVisible.value = false;
    fetchPlanList();
  } else {
    ElMessage.error('处理失败');
  }
};

// 删除采购计划
const deletePlan = (planId) => {
  ElMessageBox.confirm('确认删除此采购计划？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const res = await fetch(`http://localhost:8090/purchase-plan/delete?id=${planId}`, {
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

// 状态转换
const getStatusText = (status) => {
  return status === 0 ? '待处理' : status === 1 ? '已完成' : '缺货';
};

// 解决 ResizeObserver 报错
const adjustDialogSize = () => {
  requestAnimationFrame(() => {
    const dialog = document.querySelector('.el-dialog');
    if (dialog) dialog.style.maxWidth = '80%';
  });
};

// 初始化加载
onMounted(() => {
  fetchPlanList();
});
</script>

<template>
  <div>
    <h1>库存入库管理</h1>

    <div style="margin-bottom: 10px;">
      <el-input v-model="searchForm.inId" placeholder="按入库单号搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-date-picker v-model="searchForm.inDate" type="date" placeholder="按入库日期搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-select v-model="searchForm.status" placeholder="按状态搜索" clearable style="width: 180px; margin-right: 10px;">
        <el-option label="待入库" :value="0" />
        <el-option label="已完成" :value="1" />
      </el-select>
      <el-button @click="fetchInList">查询</el-button>
      <el-button type="primary" @click="openCreateDialog">新建入库单</el-button>
    </div>

    <el-table :data="inList" style="width: 100%">
      <el-table-column prop="inId" label="入库单号" width="120" />
      <el-table-column prop="orderId" label="关联采购订单" width="150" />
      <el-table-column prop="inDate" label="入库时间" width="150" />
      <el-table-column prop="statusText" label="状态" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewDetails(scope.row.inId)">查看</el-button>
          <el-button size="small" type="danger" :disabled="scope.row.status === 1" @click="deleteIn(scope.row.inId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 入库明细查看 -->
    <el-dialog v-model="dialogVisible" title="入库明细" width="60%" @open="adjustDialogSize">
      <el-table :data="inDetails" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="入库数量" />
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 创建入库单 -->
    <el-dialog v-model="createDialogVisible" title="新建入库单" width="80%">
      <el-form :model="inForm" label-width="100px">
        <el-form-item label="选择采购订单">
          <el-select v-model="inForm.orderId" placeholder="选择订单" @change="loadOrderItems">
            <el-option v-for="order in purchaseOrders" :key="order.orderId" :label="order.orderId" :value="order.orderId" />
          </el-select>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const searchForm = ref({ inId: '', inDate: null, status: null });
const inList = ref([]);
const inDetails = ref([]);
const purchaseOrders = ref([]);
const inForm = ref({ orderId: null, items: [] });
const dialogVisible = ref(false);
const createDialogVisible = ref(false);

// 安全提取
const extractArray = (data) => Array.isArray(data?.data) ? data.data : [];

// 获取入库列表
const fetchInList = async () => {
  try {
    const res = await fetch('http://localhost:8090/inventory-in/list');
    const data = await res.json();
    inList.value = extractArray(data).map(i => ({
      ...i,
      statusText: i.status === 1 ? '已完成' : '待入库',
    }));
  } catch (e) {
    ElMessage.error('获取入库数据失败');
  }
};

// 查看入库明细
const viewDetails = async (inId) => {
  try {
    const res = await fetch(`http://localhost:8090/inventory-in/items/${inId}`);
    inDetails.value = extractArray(await res.json());
    dialogVisible.value = true;
  } catch (e) {
    ElMessage.error('加载入库明细失败');
  }
};

// 打开创建入库单对话框
const openCreateDialog = async () => {
  try {
    const res = await fetch('http://localhost:8090/purchase-order/list');
    purchaseOrders.value = extractArray(await res.json());
    createDialogVisible.value = true;
  } catch {
    ElMessage.error('加载采购订单失败');
  }
};

// 加载选中采购订单的商品明细
const loadOrderItems = async (orderId) => {
  try {
    const res = await fetch(`http://localhost:8090/purchase-order/items/${orderId}`);
    inForm.value.items = extractArray(await res.json()).map(i => ({
      ...i,
      inQuantity: i.quantity, // 默认全部入库
    }));
  } catch {
    ElMessage.error('加载订单商品失败');
  }
};

// 提交入库单
const submitIn = async () => {
  const payload = {
    orderId: inForm.value.orderId,
    inDate: new Date().toISOString().slice(0, 10),
    status: 1,
    items: inForm.value.items.map(i => ({
      productId: i.productId,
      quantity: i.inQuantity,
    })),
  };
  try {
    const res = await fetch('http://localhost:8090/inventory-in/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    const data = await res.json();
    if (data.code === 200) {
      ElMessage.success('入库成功');
      createDialogVisible.value = false;
      fetchInList();
    } else {
      throw new Error(data.msg || '保存失败');
    }
  } catch (e) {
    ElMessage.error('提交失败：' + e.message);
  }
};

// 删除入库单
const deleteIn = (inId) => {
  ElMessageBox.confirm('确定删除该入库单？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const res = await fetch(`http://localhost:8090/inventory-in/delete?id=${inId}`);
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

onMounted(() => {
  fetchInList();
});
</script>

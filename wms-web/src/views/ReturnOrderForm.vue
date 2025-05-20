<template>
  <div>
    <h1>{{ returnOrderId ? '编辑退货单' : '创建退货单' }}</h1>
    <p v-if="returnOrderId">退货单号: {{ returnOrderId }}</p>
    <el-form :model="returnOrderForm" label-width="120px">
      <el-form-item label="退货日期">
        <el-date-picker v-model="returnOrderForm.returnDate" type="date" placeholder="选择日期" style="width: 100%;"></el-date-picker>
      </el-form-item>
      <el-form-item label="供应商">
        <el-select
            v-model="returnOrderForm.supplierId"
            placeholder="选择供应商"
            style="width: 300px;"
        >
          <el-option
              v-for="supplier in supplierList"
              :key="supplier.supplierId"
              :value="supplier.supplierId"
              :label="supplier.supplierName"
          />
        </el-select>
        <el-button @click="openSupplierDialog">新增供应商</el-button>
      </el-form-item>

      <h3>退货明细</h3>
      <el-table :data="returnOrderForm.items" style="width: 100%">
        <el-table-column prop="productName" label="商品名称"></el-table-column>
        <el-table-column prop="specifications" label="规格"></el-table-column>
        <el-table-column prop="quantity" label="退货数量"></el-table-column>
        <el-table-column prop="returnPrice" label="退货单价"></el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" type="danger" @click="removeReturnItem(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button @click="addReturnItem">添加明细</el-button>
    </el-form>
    <el-button type="primary" @click="saveReturnOrder">保存</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRoute } from 'vue-router';

const route = useRoute();
const returnOrderId = ref(route.params.id);
// eslint-disable-next-line no-undef
const emit = defineEmits(['success']);

const returnOrderForm = ref({
  returnId: null,
  returnDate: null,
  supplierId: null,
  items: [],
});
const supplierList = ref([]);ref([]);
// 你可能需要在选择商品时使用商品信息

const fetchReturnOrderDetails = async (id) => {
  if (id) {
    try {
      const response = await fetch(`http://localhost:8090/return-order/${id}`); // 获取主信息
      const responseItems = await fetch(`http://localhost:8090/return-order/items/${id}`); // 获取明细

      if (!response.ok || !responseItems.ok) {
        throw new Error('获取退货单详情失败');
      }

      const mainData = await response.json().then(res => res.data);
      const itemsData = await responseItems.json().then(res => res.data);

      returnOrderForm.value.returnId = mainData.returnId;
      returnOrderForm.value.returnDate = mainData.returnDate ? new Date(mainData.returnDate) : null;
      returnOrderForm.value.supplierId = mainData.supplierId;
      returnOrderForm.value.items = itemsData || [];

    } catch (error) {
      console.error('获取退货单详情失败:', error);
      ElMessage.error('获取退货单详情失败');
    }
  } else {
    addReturnItem();
  }
};

const fetchSupplierList = async () => {
  try {
    const response = await fetch('http://localhost:8090/supplier/list'); // 替换为你的实际 API endpoint
    if (!response.ok) {
      await response.json();
      throw new Error('获取供应商列表失败');
    }
    const data = await response.json().then(res => res.data);
    supplierList.value = data;
  } catch (error) {
    console.error('获取供应商列表失败:', error);
    ElMessage.error('获取供应商列表失败');
  }
};

onMounted(() => {
  fetchSupplierList();
  if (returnOrderId.value) {
    fetchReturnOrderDetails(returnOrderId.value);
  } else {
    addReturnItem();
  }
});

const addReturnItem = () => {
  returnOrderForm.value.items.push({ productId: null, productName: '', specifications: '', quantity: 1, returnPrice: 0 });
};

const removeReturnItem = (index) => {
  returnOrderForm.value.items.splice(index, 1);
};

const saveReturnOrder = async () => {
  try {
    const url = returnOrderId.value
        ? 'http://localhost:8090/return-order/modify'
        : 'http://localhost:8090/return-order/save';
    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(returnOrderForm.value),
    });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`保存退货单失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    ElMessage.success('退货单保存成功');
    emit('success');
  } catch (error) {
    console.error('保存退货单失败:', error);
    ElMessage.error('保存退货单失败');
  }
};

const openSupplierDialog = () => {
  // TODO: 实现新增供应商的对话框逻辑
  console.log('打开新增供应商对话框');
};
</script>

<style scoped>
/* 可以添加一些样式 */
</style>
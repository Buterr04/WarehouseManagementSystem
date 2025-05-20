<template>
  <div>
    <h1>填写采购订单</h1>

    <el-skeleton v-if="isLoading" rows="6" animated />

    <div v-else>
      <el-form label-width="120px">
        <!-- 供应商选择 -->
        <el-form-item label="供应商">
          <el-input
              v-model="selectedSupplier.supplierName"
              placeholder="点击选择供应商"
              readonly
              style="width: 300px;"
              @click="supplierDialogVisible = true"
          />
          <el-button @click="supplierDialogVisible = true" style="margin-left: 10px;">选择供应商</el-button>
        </el-form-item>

        <!-- 交货日期 -->
        <el-form-item label="预计交货日期">
          <el-date-picker
              v-model="purchaseOrderForm.deliveryDate"
              type="date"
              placeholder="选择交货日期"
              style="width: 300px;"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <!-- 明细表格 -->
        <el-form-item label="采购明细">
          <el-table :data="paginatedPlanItems" style="width: 100%">
            <el-table-column prop="productName" label="商品名称" />
            <el-table-column prop="specifications" label="规格" />
            <el-table-column prop="planQuantity" label="计划数量" />
            <el-table-column label="采购数量" width="180">
              <template #default="scope">
                <el-input-number
                    v-model="scope.row.purchaseQuantity"
                    :min="0"
                    :max="scope.row.planQuantity"
                />
              </template>
            </el-table-column>
            <el-table-column prop="unitPrice" label="单价" />
            <el-table-column label="小计">
              <template #default="scope">
                ￥{{ (scope.row.purchaseQuantity || 0) * (scope.row.unitPrice || 0) }}
              </template>
            </el-table-column>
          </el-table>

          <div style="text-align: right; margin-top: 10px;">
            <el-pagination
                background
                layout="prev, pager, next"
                :total="purchaseOrderForm.items.length"
                :page-size="itemsPageSize"
                :current-page="currentPageItems"
                @current-change="handleItemPageChange"
            />
          </div>
        </el-form-item>


        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitPurchaseOrder">生成采购订单</el-button>
        </el-form-item>

        <!-- 总价 -->
        <div style="text-align: right; font-size: 1.2em;">
          总价：￥{{ totalPrice }}
        </div>
      </el-form>
    </div>

    <!-- 供应商弹窗 -->
    <el-dialog v-model="supplierDialogVisible" title="选择供应商" width="60%" :destroy-on-close="true">
      <div style="display: flex; flex-direction: column; height: 400px;">
        <div style="margin-bottom: 10px;">
          <el-input v-model="supplierSearch" placeholder="搜索供应商" clearable style="width: 200px;" />
        </div>
        <div style="flex: 1; overflow-y: auto; margin-bottom: 10px;">
          <el-table :data="paginatedSupplierList" style="width: 100%;">
            <el-table-column prop="supplierName" label="供应商名称" />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" @click="selectSupplier(scope.row)">选择</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div style="text-align: right;">
          <el-pagination
              background
              layout="prev, pager, next"
              :total="filteredSupplierList.length"
              :page-size="supplierPageSize"
              :current-page="currentPageSupplier"
              @current-change="page => currentPageSupplier.value = page"
          />
        </div>
      </div>
    </el-dialog>
  </div>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import router from '@/router';

const isLoading = ref(true);
const selectedSupplier = ref({ supplierId: null, supplierName: '' });
const supplierDialogVisible = ref(false);
const supplierSearch = ref('');
const currentPageSupplier = ref(1);
const supplierPageSize = 5;

const currentPageItems = ref(1);
const itemsPageSize = 5;


const purchaseOrderForm = ref({
  supplierId: null,
  orderDate: new Date().toISOString().slice(0, 10),
  deliveryDate: '',
  items: []
});

// 1. 只读取 status 为 0 的计划项，并按 supplierId 分类
const supplierList = ref([]);
const supplierPlanMap = ref({}); // supplierId => [计划明细项]
const planDetailMap = ref({});   // purchasePlanId => 完整计划信息

onMounted(async () => {
  try {
    const [supplierRes, planRes, itemRes] = await Promise.all([
      fetch('http://localhost:8090/supplier/list'),
      fetch('http://localhost:8090/purchase-plan/list'),
      fetch('http://localhost:8090/purchase-plan-item/list'),
    ]);

    const supplierJson = await supplierRes.json();
    const planJson = await planRes.json();
    const itemJson = await itemRes.json();

    // 1. 供应商列表
    supplierList.value = Array.isArray(supplierJson)
        ? supplierJson
        : supplierJson.data || [];

    // 2. 所有计划单
    const planList = Array.isArray(planJson)
        ? planJson
        : planJson.data || [];

    // 构建：计划详情映射 + 未完成计划ID列表
    const activePlanIds = new Set();
    planDetailMap.value = {};

    for (const plan of planList) {
      planDetailMap.value[plan.purchasePlanId] = plan;
      if (plan.status === 0) {
        activePlanIds.add(plan.purchasePlanId);
      }
    }

    // 3. 所有计划明细
    const itemList = Array.isArray(itemJson)
        ? itemJson
        : itemJson.data || [];

    // 只保留属于未完成计划的明细项
    const unprocessedItems = itemList.filter(item =>
        activePlanIds.has(item.purchasePlanId)
    );

    // 4. 按 supplierId 分类明细项
    supplierPlanMap.value = {};
    for (const item of unprocessedItems) {
      if (!supplierPlanMap.value[item.supplierId]) {
        supplierPlanMap.value[item.supplierId] = [];
      }
      supplierPlanMap.value[item.supplierId].push(item);
    }
  } catch (e) {
    console.error('数据加载失败:', e);
    ElMessage.error('数据加载失败');
  } finally {
    isLoading.value = false;
  }
});



// 2. 选择供应商并合并相同商品
const selectSupplier = (supplier) => {
  selectedSupplier.value = supplier;
  purchaseOrderForm.value.supplierId = supplier.supplierId;
  supplierDialogVisible.value = false;
  currentPageItems.value = 1;

  const rawItems = supplierPlanMap.value[supplier.supplierId] || [];

  const mergedMap = new Map();
  for (const item of rawItems) {
    if (!mergedMap.has(item.productId)) {
      mergedMap.set(item.productId, {
        productId: item.productId,
        productName: item.productName,
        specifications: item.specifications,
        unitPrice: item.unitPrice,
        planQuantity: item.planQuantity,
        purchaseQuantity: item.planQuantity
      });
    } else {
      const existing = mergedMap.get(item.productId);
      existing.planQuantity += item.planQuantity;
      existing.purchaseQuantity += item.planQuantity;
    }
  }

  purchaseOrderForm.value.items = Array.from(mergedMap.values());
};

// 分页
const paginatedPlanItems = computed(() => {
  const start = (currentPageItems.value - 1) * itemsPageSize;
  return purchaseOrderForm.value.items.slice(start, start + itemsPageSize);
});
const handleItemPageChange = (page) => {
  currentPageItems.value = page;
};

// 总价
const totalPrice = computed(() =>
    purchaseOrderForm.value.items.reduce((sum, item) =>
        sum + (item.purchaseQuantity || 0) * (item.unitPrice || 0), 0)
);

// 过滤供应商
const filteredSupplierList = computed(() =>
    supplierList.value.filter(s =>
        s.supplierName?.toLowerCase().includes(supplierSearch.value.toLowerCase())
    )
);
const paginatedSupplierList = computed(() => {
  const start = (currentPageSupplier.value - 1) * supplierPageSize;
  return filteredSupplierList.value.slice(start, start + supplierPageSize);
});

// 提交采购订单
const submitPurchaseOrder = async () => {
  try {
    const usedItems = purchaseOrderForm.value.items.filter(i => i.purchaseQuantity > 0);

    const payload = {
      supplierId: selectedSupplier.value.supplierId,
      orderDate: purchaseOrderForm.value.orderDate,
      deliveryDate: purchaseOrderForm.value.deliveryDate,
      status: 0, // 默认为待收货状态
      purchaseOrderItems: usedItems.map(i => ({
        productId: i.productId,
        quantity: i.purchaseQuantity,
        purchasePrice: i.unitPrice || 0,
      })),
    };

    const res = await fetch('http://localhost:8090/purchase-order/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (!res.ok) throw new Error('保存采购订单失败');
    ElMessage.success('采购订单生成成功');

    // 🔍【附加逻辑】构造计划单 => status = 1 + items
    const rawItems = supplierPlanMap.value[selectedSupplier.value.supplierId] || [];

    // 提取所有参与此次采购的原始计划条目（根据 productId 匹配）
    const usedProductIds = new Set(usedItems.map(i => i.productId));
    const usedPlanItems = rawItems.filter(i => usedProductIds.has(i.productId));

    // 按 purchasePlanId 分组
    const planMap = new Map();
    for (const item of usedPlanItems) {
      if (!planMap.has(item.purchasePlanId)) {
        planMap.set(item.purchasePlanId, []);
      }
      planMap.get(item.purchasePlanId).push({
        productId: item.productId,
        planQuantity: item.planQuantity,
        unitPrice: item.unitPrice,
        supplierId: item.supplierId
      });
    }

    // 发请求标记所有计划单为完成（status = 1）
    for (const [planId] of planMap.entries()) {
      const originPlan = planDetailMap.value[planId];
      if (!originPlan) continue;

      const fullPayload = {
        planId: originPlan.planId,
        status: '1',
      };

      await fetch('http://localhost:8090/purchase-plan/updateStatus', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(fullPayload),
      });
    }

    // 跳转
    router.push('/purchase-management/purchase-order');
  } catch (err) {
    console.error(err);
    ElMessage.error('生成采购订单失败');
  }
};

</script>

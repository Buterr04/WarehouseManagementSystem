<template>
  <div>
    <h1>填写销售单</h1>
    <el-form :model="salesOrderForm" label-width="120px">
      <!-- 客户选择 -->
      <el-form-item label="客户">
        <el-input
            v-model="selectedCustomer.customerName"
            placeholder="点击选择客户"
            readonly
            style="width: 300px;"
            @click="customerDialogVisible = true"
        />
        <el-button @click="customerDialogVisible = true" style="margin-left: 10px;">选择客户</el-button>
      </el-form-item>

      <!-- 销售商品选择 -->
      <el-form-item label="销售商品">
        <el-table :data="salesOrderForm.items" style="width: 100%">
          <el-table-column label="商品">
            <template #default="scope">
              <el-input
                  v-model="scope.row._productDisplay"
                  readonly
                  placeholder="点击选择商品"
                  style="width: 200px;"
                  @click="openProductDialog(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="数量" width="120">
            <template #default="scope">
              <el-input-number v-model="scope.row.quantity" :min="1" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="100">
            <template #default="scope">
              {{ scope.row.unitPrice }}
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="scope">
              ￥{{ scope.row.quantity * scope.row.unitPrice }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="danger" size="small" @click="removeSalesItem(scope.$index)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button @click="addSalesItem">添加商品</el-button>
      </el-form-item>

      <!-- 总价和提交 -->
      <el-form-item>
        <el-button type="primary" @click="generateSalesOrder">生成销售单</el-button>
      </el-form-item>

      <div style="text-align: right; font-size: 1.2em;">
        总价：￥{{ totalPrice }}
      </div>
    </el-form>

    <!-- 客户选择弹窗 -->
    <el-dialog
        v-model="customerDialogVisible"
        title="选择客户"
        width="60%"
        :destroy-on-close="true"
    >
      <!-- 外层容器：纵向布局，高度撑满 -->
      <div style="display: flex; flex-direction: column; height: 400px;">
        <!-- 顶部搜索 -->
        <div style="margin-bottom: 10px;">
          <el-input
              v-model="customerSearch"
              placeholder="搜索客户"
              clearable
              style="width: 200px;"
          />
        </div>

        <!-- 中部表格区域：可滚动 -->
        <div style="flex: 1; overflow-y: auto; margin-bottom: 10px;">
          <el-table :data="paginatedCustomerList" style="width: 100%;">
            <el-table-column prop="customerName" label="客户名称" />
            <el-table-column prop="contactPhone" label="联系方式" />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" @click="selectCustomer(scope.row)">选择</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 底部分页：始终贴底 -->
        <div style="text-align: right;">
          <el-pagination
              background
              layout="prev, pager, next"
              :total="filteredCustomerList.length"
              :page-size="pageSize"
              :current-page="currentPageCustomer"
              @current-change="page => currentPageCustomer = page"
          />
        </div>
      </div>
    </el-dialog>


    <!-- 商品选择弹窗 -->
    <el-dialog
        v-model="productDialogVisible"
        title="选择商品"
        width="60%"
        :destroy-on-close="true"
    >
      <!-- 外层纵向布局，固定高度 -->
      <div style="display: flex; flex-direction: column; height: 400px;">
        <!-- 顶部搜索区域 -->
        <div style="margin-bottom: 10px;">
          <el-input
              v-model="productSearch"
              placeholder="搜索商品名称"
              clearable
              style="width: 250px;"
          />
        </div>

        <!-- 表格区域：可滚动 -->
        <div style="flex: 1; overflow-y: auto; margin-bottom: 10px;">
          <el-table :data="paginatedProductList" style="width: 100%">
            <el-table-column label="商品信息">
              <template #default="scope">
                {{ scope.row.productName }}（{{ scope.row.specifications }}） - ￥{{ scope.row.unitPrice }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" @click="selectProduct(scope.row)">选择</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页区域：固定底部 -->
        <div style="text-align: right;">
          <el-pagination
              background
              layout="prev, pager, next"
              :total="filteredProductList.length"
              :page-size="pageSize"
              :current-page="currentPageProduct"
              @current-change="page => currentPageProduct = page"
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

// 表单数据
const salesOrderForm = ref({
  customerId: null,
  items: [{ productId: null, quantity: 1, unitPrice: 0, _productDisplay: '' }],
});

// 客户选择相关
const customerList = ref([]);
const customerDialogVisible = ref(false);
const selectedCustomer = ref({ customerId: null, customerName: '' });
const customerSearch = ref('');
const currentPageCustomer = ref(1);
const pageSize = 5;

const filteredCustomerList = computed(() =>
    customerList.value.filter(c =>
        c.customerName.toLowerCase().includes(customerSearch.value.toLowerCase())
    )
);
const paginatedCustomerList = computed(() => {
  const start = (currentPageCustomer.value - 1) * pageSize;
  return filteredCustomerList.value.slice(start, start + pageSize);
});
const selectCustomer = (customer) => {
  selectedCustomer.value = customer;
  salesOrderForm.value.customerId = customer.customerId;
  customerDialogVisible.value = false;
};

// 商品选择相关
const productList = ref([]);
const productDialogVisible = ref(false);
const productSearch = ref('');
const currentPageProduct = ref(1);
const currentEditingRow = ref(null);

const filteredProductList = computed(() =>
    productList.value.filter(p =>
        p.productName.toLowerCase().includes(productSearch.value.toLowerCase())
    )
);
const paginatedProductList = computed(() => {
  const start = (currentPageProduct.value - 1) * pageSize;
  return filteredProductList.value.slice(start, start + pageSize);
});
const openProductDialog = (row) => {
  currentEditingRow.value = row;
  productDialogVisible.value = true;
};
const selectProduct = (product) => {
  const row = currentEditingRow.value;
  row.productId = product.productId;
  row.unitPrice = product.unitPrice;
  row._productDisplay = `${product.productName}（${product.specifications}）`;
  productDialogVisible.value = false;
};

const addSalesItem = () => {
  salesOrderForm.value.items.push({ productId: null, quantity: 1, unitPrice: 0, _productDisplay: '' });
};
const removeSalesItem = (index) => {
  salesOrderForm.value.items.splice(index, 1);
};

const totalPrice = computed(() =>
    salesOrderForm.value.items.reduce((sum, item) => sum + item.quantity * item.unitPrice, 0)
);

// 初始化
onMounted(async () => {
  try {
    const customerRes = await fetch('http://localhost:8090/customer/list');
    customerList.value = await customerRes.json();
    const productRes = await fetch('http://localhost:8090/product/list');
    productList.value = await productRes.json();
  } catch (e) {
    ElMessage.error('数据加载失败');
  }
});

const generateSalesOrder = async () => {
  try {
    const payload = {
      salesOrderId: '1',
      customerId: salesOrderForm.value.customerId,
      saleDate: new Date().toISOString().slice(0, 10),
      salesOrderItems: salesOrderForm.value.items.map(item => ({
        salesItemId: '1',
        productId: item.productId,
        quantity: item.quantity,
        salePrice: item.unitPrice,
      })),
    };
    const res = await fetch('http://localhost:8090/sales-order/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    if (!res.ok) throw new Error('生成失败');
    ElMessage.success('销售单生成成功');
    router.push('/sales-management/sales-order');
  } catch (err) {
    ElMessage.error('生成销售单失败');
  }
};
</script>

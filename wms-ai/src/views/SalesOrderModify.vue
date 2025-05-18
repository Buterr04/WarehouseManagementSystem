<template>
  <div>
    <h1>编辑销售单</h1>
    <p v-if="salesOrderId">销售单 ID: {{ salesOrderId }} 销售日期: {{ salesOrderDate }}</p>
    <el-form :model="salesOrderForm" label-width="120px">
      <el-form-item label="客户姓名">
        <el-select v-model="salesOrderForm.customerId" placeholder="选择客户" style="width: 300px;">
          <el-option v-for="customer in customerList" :key="customer.customerId" :value="customer.customerId" :label="customer.customerName" />
        </el-select>
        <el-button @click="openCustomerDialog">新增客户</el-button>
      </el-form-item>

      <el-form-item label="销售商品">
        <el-table :data="salesOrderForm.items" style="width: 100%">
          <el-table-column label="商品">
            <template #default="scope">
              <el-button @click="openProductDialog(scope.$index)">
                {{ scope.row.productName ? `${scope.row.productName} (${scope.row.specifications})` : '选择商品' }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="200">
            <template #default="scope">
              <el-input-number v-model="scope.row.quantity" :min="1" @change="calculateSubtotal(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column prop="unitPrice" label="单价" width="120">
            <template #default="scope">{{ scope.row.unitPrice }}</template>
          </el-table-column>
          <el-table-column label="小计" width="150">
            <template #default="scope">{{ scope.row.quantity * scope.row.unitPrice }}</template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button size="small" type="danger" @click="removeSalesItem(scope.$index)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button @click="addSalesItem">添加商品</el-button>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="updateSalesOrder">保存修改</el-button>
      </el-form-item>

      <div style="text-align: right; margin-top: 20px; font-size: 1.2em;">
        总价：￥{{ totalPrice }}
      </div>
    </el-form>

    <el-dialog v-model="customerDialogVisible" title="新增客户" width="30%">
      <el-form :model="newCustomerForm" label-width="80px">
        <el-form-item label="客户名称">
          <el-input v-model="newCustomerForm.name" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="newCustomerForm.phone" />
        </el-form-item>
        <el-form-item label="联系地址">
          <el-input v-model="newCustomerForm.contactAddress" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="customerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveNewCustomer">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="productDialogVisible" title="选择商品" width="60%">
      <el-table :data="productList" style="width: 100%">
        <el-table-column prop="productName" label="商品名称"></el-table-column>
        <el-table-column prop="specifications" label="型号"></el-table-column>
        <el-table-column prop="unitPrice" label="单价"></el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" type="primary" @click="selectProduct(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="productDialogVisible = false">取消</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import router from '@/router';
import { useRoute } from 'vue-router';

const route = useRoute();
const salesOrderId = ref(route.params.id);
const salesOrderForm = ref({ customerId: null, items: [], saleDate: null, status: null });
const customerList = ref([]);
const productList = ref([]);
const customerDialogVisible = ref(false);
const newCustomerForm = ref({ name: '', phone: '', contactAddress: '' });
const productDialogVisible = ref(false);
const currentItemIndex = ref(null);

const salesOrderDate = computed(() => salesOrderForm.value.saleDate || new Date().toISOString().split('T')[0]);

const totalPrice = computed(() => {
  return salesOrderForm.value.items.reduce((sum, item) => sum + (item.quantity * item.unitPrice), 0);
});

const calculateSubtotal = () => {
  // 可扩展逻辑
};

const fetchCustomerList = async () => {
  try {
    const res = await fetch('http://localhost:8090/customer/list');
    customerList.value = await res.json();
  } catch (err) {
    ElMessage.error('获取客户列表失败');
  }
};

const fetchProductList = async () => {
  try {
    const res = await fetch('http://localhost:8090/product/list');
    productList.value = await res.json();
  } catch (err) {
    ElMessage.error('获取商品列表失败');
  }
};

const fetchSalesOrderDetails = async () => {
  if (!salesOrderId.value) return;
  try {
    const detailRes = await fetch(`http://localhost:8090/sales-order/items/${salesOrderId.value}`);
    const detailData = await detailRes.json();
    if (detailData.code === 200) {
      salesOrderForm.value.items = detailData.data.map(item => {
        const matchedProduct = productList.value.find(p => Number(p.productId) === Number(item.productId));
        return {
          salesItemId: item.salesItemId,
          productId: item.productId,
          quantity: item.quantity,
          unitPrice: item.unitPrice || item.salePrice || 0,
          productName: matchedProduct?.productName || '未知商品',
          specifications: matchedProduct?.specifications || ''
        };
      });


      const mainRes = await fetch(`http://localhost:8090/sales-order/${salesOrderId.value}`);
      const mainData = await mainRes.json();
      salesOrderForm.value.customerId = mainData.data.customerId;
      salesOrderForm.value.saleDate = mainData.data.saleDate?.split('T')[0];
    } else {
      ElMessage.error(detailData.msg);
    }
  } catch (err) {
    ElMessage.error('获取销售单失败');
  }
};

onMounted(async () => {
  await fetchCustomerList();
  await fetchProductList();
  await fetchSalesOrderDetails();
});

const openCustomerDialog = () => {
  customerDialogVisible.value = true;
  newCustomerForm.value = { name: '', phone: '', contactAddress: '' };
};

const saveNewCustomer = async () => {
  try {
    const payload = { customerName: newCustomerForm.value.name, contactPhone: newCustomerForm.value.phone, contactAddress: newCustomerForm.value.contactAddress };
    await fetch('http://localhost:8090/customer/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    await fetchCustomerList();
    const added = customerList.value.find(c => c.customerName === payload.customerName);
    if (added) salesOrderForm.value.customerId = added.customerId;
    customerDialogVisible.value = false;
    ElMessage.success('客户添加成功');
  } catch (err) {
    ElMessage.error('保存客户失败');
  }
};

const addSalesItem = () => {
  salesOrderForm.value.items.push({ productId: null, quantity: 1, unitPrice: 0, productName: '', specifications: '' });
};

const removeSalesItem = (index) => {
  salesOrderForm.value.items.splice(index, 1);
};

const openProductDialog = (index) => {
  currentItemIndex.value = index;
  productDialogVisible.value = true;
};

const selectProduct = (product) => {
  if (currentItemIndex.value !== null) {
    const item = salesOrderForm.value.items[currentItemIndex.value];
    item.productId = product.productId;
    item.unitPrice = product.unitPrice;
    item.productName = product.productName;
    item.specifications = product.specifications;
    calculateSubtotal(item);
  }
  productDialogVisible.value = false;
  currentItemIndex.value = null;
};

const updateSalesOrder = async () => {
  try {
    const payload = {
      salesOrderId: salesOrderId.value,
      customerId: salesOrderForm.value.customerId,
      saleDate: salesOrderForm.value.saleDate,
      status: salesOrderForm.value.status,
      salesOrderItems: salesOrderForm.value.items.map((item, index) => ({
        salesItemId: item.salesItemId || index + 1,
        productId: item.productId,
        quantity: item.quantity,
        salePrice: item.unitPrice
      }))
    };
   await fetch('http://localhost:8090/sales-order/modify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    ElMessage.success('销售单更新成功');
    router.push('/sales-management/sales-order');
  } catch (err) {
    ElMessage.error('更新销售单失败');
  }
};
</script>

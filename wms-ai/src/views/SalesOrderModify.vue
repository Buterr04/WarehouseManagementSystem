<template>
  <div>
    <h1>编辑销售单</h1>
    <p v-if="salesOrderId">销售单 ID: {{ salesOrderId }}  销售日期: {{ salesOrderDate }}</p>
    <el-form :model="salesOrderForm" label-width="120px">
      <el-form-item label="客户姓名">
        <el-select
            v-model="salesOrderForm.customerId"
            placeholder="选择客户"
            style="width: 300px;"
        >
          <el-option
              v-for="customer in customerList"
              :key="customer.customerId"
              :value="customer.customerId"
              :label="customer.customerName"
          />
        </el-select>
        <el-button @click="openCustomerDialog">新增客户</el-button>
      </el-form-item>

      <el-form-item label="销售商品">
        <el-table :data="salesOrderForm.items" style="width: 100%">
          <el-table-column label="商品">
            <template #default="scope">
              <el-button @click="openProductDialog(scope.$index)">
                {{ getProductNameAndSpecifications(scope.row.productId) || '选择商品' }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="200">
            <template #default="scope">
              <el-input-number
                  v-model="scope.row.quantity"
                  :min="1"
                  @change="calculateSubtotal(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="unitPrice" label="单价" width="120">
            <template #default="scope">{{ getProductUnitPrice(scope.row.productId) }}</template>
          </el-table-column>
          <el-table-column label="小计" width="150">
            <template #default="scope">{{ scope.row.quantity * getProductUnitPrice(scope.row.productId) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button
                  size="small"
                  type="danger"
                  @click="removeSalesItem(scope.$index)"
              >
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button @click="addSalesItem">添加商品</el-button>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="updateSalesOrder">保存修改</el-button>
      </el-form-item>

      <div style="text-align: right; margin-top: 20px; font-size: 1.2em;">
        总价：￥{{ totalPrice.value }}
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

const salesOrderForm = ref({
  customerId: null,
  items: [],
  saleDate: null,
});
const customerList = ref([]);
const productList = ref([]);
const customerDialogVisible = ref(false);
const newCustomerForm = ref({
  name: '',
  phone: '',
  contactAddress: '',
});
const productDialogVisible = ref(false);
const currentItemIndex = ref(null);

const calculateSubtotal = (row) => {
  console.log('数量已改变:', row.quantity);
};

const totalPrice = computed(() => {
  return salesOrderForm.value.items.reduce((sum, item) => sum + (item.quantity * getProductUnitPrice(item.productId)), 0);
});

const getProductUnitPrice = (productId) => {
  const product = productList.value.find(p => p.productId === productId);
  return product ? product.unitPrice : 0;
};

const getProductNameAndSpecifications = (productId) => {
  const product = productList.value.find(p => p.productId === productId);
  return product ? `${product.productName} (${product.specifications})` : '';
};

/*
const updateUnitPrice = (item) => {
  const selectedProduct = productList.value.find(product => product.productId === item.productId);
  if (selectedProduct) {
    item.unitPrice = selectedProduct.unitPrice;
  } else {
    item.unitPrice = 0;
  }
};
 */

const fetchCustomerList = async () => {
  try {
    const response = await fetch('http://localhost:8090/customer/list');
    if (!response.ok) {
      throw new Error(`获取客户列表失败: ${response.status}`);
    }
    const data = await response.json();
    customerList.value = data;
  } catch (error) {
    console.error('获取客户列表失败:', error);
    ElMessage.error('获取客户列表失败');
  }
};

const fetchProductList = async () => {
  try {
    const response = await fetch('http://localhost:8090/product/list');
    if (!response.ok) {
      throw new Error(`获取商品列表失败: ${response.status}`);
    }
    const data = await response.json();
    productList.value = data;
  } catch (error) {
    console.error('获取商品列表失败:', error);
    ElMessage.error('获取商品列表失败');
  }
};

const fetchSalesOrderDetails = async () => {
  if (salesOrderId.value) {
    try {
      const response = await fetch(`http://localhost:8090/sales-order/items/${salesOrderId.value}`);
      if (!response.ok) {
        throw new Error(`获取销售单明细失败: ${response.status}`);
      }
      const responseData = await response.json();
      if (responseData.code === 200 && responseData.data) {
        salesOrderForm.value.items = responseData.data.map(item => ({
          salesItemId: item.salesItemId,
          productName: item.productName,
          productId: item.productId,
          quantity: item.quantity,
          unitPrice: item.unitPrice || item.salePrice || 0, // 优先使用 unitPrice，其次 salePrice，都没有则默认为 0
          salePrice: item.salePrice
        }));
        const mainResponse = await fetch(`http://localhost:8090/sales-order/${salesOrderId.value}`);
        if (mainResponse.ok) {
          const mainData = await mainResponse.json();
          salesOrderForm.value.customerId = mainData.data.customerId;
          salesOrderForm.value.saleDate = mainData.data.saleDate ? new Date(mainData.data.saleDate).toISOString().split('T')[0] : null;
        }
      } else {
        ElMessage.error(`获取销售单明细失败: ${responseData.msg}`);
      }
    } catch (error) {
      console.error('获取销售单明细失败:', error);
      ElMessage.error('获取销售单详情失败');
    }
  } else {
    salesOrderForm.value.saleDate = new Date().toISOString().split('T')[0];
  }
};

onMounted(() => {
  fetchCustomerList();
  fetchProductList();

  fetchSalesOrderDetails();
});

const openCustomerDialog = () => {
  customerDialogVisible.value = true;
  newCustomerForm.value = { name: '', phone: '', contactAddress: '' };
};

const saveNewCustomer = async () => {
  try {
    const payload = {
      customerName: newCustomerForm.value.name,
      contactPhone: newCustomerForm.value.phone,
      contactAddress: newCustomerForm.value.contactAddress,
    };
    const response = await fetch('http://localhost:8090/customer/save', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });
    if (!response.ok) {
      throw new Error(`保存客户失败: ${response.status}`);
    }
    const newCustomer = await response.json();
    customerList.value.push(newCustomer);
    customerDialogVisible.value = false;
    ElMessage.success('客户添加成功');
    await fetchCustomerList();
  } catch (error) {
    console.error('保存客户失败:', error);
    ElMessage.error('保存客户失败');
  }
};

const addSalesItem = () => {
  salesOrderForm.value.items.push({ productId: null, quantity: 1, unitPrice: 0 });
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
    salesOrderForm.value.items[currentItemIndex.value].productId = product.productId;
    salesOrderForm.value.items[currentItemIndex.value].unitPrice = product.unitPrice;
  }
  productDialogVisible.value = false;
  currentItemIndex.value = null;
};

const updateSalesOrder = async () => {
  try {
    const payload = {
      salesOrderId: salesOrderId.value || null, // 如果是新增则为 null
      customerId: salesOrderForm.value.customerId,
      saleDate: salesOrderForm.value.saleDate,
      salesOrderItems: salesOrderForm.value.items.map((item, index) => ({
        salesItemId: item.salesItemId || index + 1, // 如果是已存在的 item，则保留 ID，否则前端生成
        productId: item.productId,
        quantity: item.quantity,
        salePrice: item.unitPrice // 这里假设前端的 unitPrice 对应后端的 salePrice
      })),
    };
    const response = await fetch('http://localhost:8090/sales-order/modify', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });
    if (!response.ok) {
      throw new Error(`更新销售单失败: ${response.status}`);
    }
    ElMessage.success('销售单更新成功');
    router.push('/sales-management/sales-order');
  } catch (error) {
    console.error('更新销售单失败:', error);
    ElMessage.error('更新销售单失败');
  }
};
</script>
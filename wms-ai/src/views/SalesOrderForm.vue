<template>
  <div>
    <h1>填写销售单</h1>
    <p v-if="salesOrderId">销售单 ID: {{ salesOrderId }}</p>
    <el-form :model="salesOrderForm" label-width="120px">
      <el-form-item label="客户">
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
          <el-table-column prop="productId" label="商品">
            <template #default="scope">
              <el-select
                  v-model="scope.row.productId"
                  placeholder="选择商品"
                  style="width: 200px;"
                  @change="updateUnitPrice(scope.row)"
              >
                <el-option
                    v-for="product in productList"
                    :key="product.productId"
                    :value="product.productId"
                    :label="`${product.productName} (${product.specifications})`"
                >
                  <span>{{ product.productName }} ({{ product.specifications }})</span>
                  <span style="float: right; color: #8492a6; font-size: 12px;">
    ￥{{ product.unitPrice }}
  </span>
                </el-option>
              </el-select>
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
        <el-button type="primary" @click="generateSalesOrder">生成销售单</el-button>
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
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue';
import { ElMessage } from 'element-plus';
import router from "@/router";

const salesOrderForm = ref({
  customerId: null,
  items: [
    { productId: null, quantity: 1, unitPrice: 0 },
  ],
});
const customerList = ref([]);
const productList = ref([]);
const customerDialogVisible = ref(false);
const newCustomerForm = ref({
  name: '',
  phone: '',
  contactAddress: '',
});

const calculateSubtotal = (row) => {
  console.log('数量已改变:', row.quantity);
};

const totalPrice = computed(() => {
  return salesOrderForm.value.items.reduce((sum, item) => sum + (item.quantity * item.unitPrice), 0);
});
const updateUnitPrice = (item) => {
  const selectedProduct = productList.value.find(product => product.productId === item.productId);
  if (selectedProduct) {
    item.unitPrice = selectedProduct.unitPrice;
  } else {
    item.unitPrice = 0;
  }
};
const fetchCustomerList = async () => {
  try {
    const response = await fetch('http://localhost:8090/customer/list'); // 替换为你的实际 API 端点
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
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
    const response = await fetch('http://localhost:8090/product/list'); // 替换为你的实际 API 端点
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    productList.value = data;
  } catch (error) {
    console.error('获取商品列表失败:', error);
    ElMessage.error('获取商品列表失败');
  }
};

onMounted(() => {
  fetchCustomerList();
  fetchProductList();
});

const openCustomerDialog = () => {
  customerDialogVisible.value = true;
  newCustomerForm.value = { customerId:"", customerName:"", contactPhone:"", contactAddress:"" };
};

const saveNewCustomer = async () => {
  try {
    const payload = {
      customerId: "",
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
      const errorData = await response.json();
      throw new Error(`保存客户失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const newCustomer = await response.json();
    customerList.value.push(newCustomer);
    customerDialogVisible.value = false;
    ElMessage.success('客户添加成功');
    await fetchCustomerList(); // 重新获取客户列表
  } catch (error) {
    console.error('保存客户失败:', error);
    ElMessage.error('保存客户失败');
  }
};

const addSalesItem = () => {
  salesOrderForm.value.items.push({ productId: null, quantity: 1 });
};

const removeSalesItem = (index) => {
  salesOrderForm.value.items.splice(index, 1);
};

const generateSalesOrder = async () => {
  try {
    const payload = {
      salesOrderId: "1",
      customerId: salesOrderForm.value.customerId,
      saleDate: new Date().toISOString().slice(0, 10),
      salesOrderItems: salesOrderForm.value.items.map(item => ({
        salesItemId: "1",
        productId: item.productId,
        quantity: item.quantity,
        salePrice: item.unitPrice,
      })),
    };
    const response = await fetch('http://localhost:8090/sales-order/save', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`生成销售单失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    ElMessage.success('销售单生成成功');
    console.log('销售单数据:', await response.json());
    router.push('/sales-management/sales-order'); // 跳转到销售单管理页面
    salesOrderForm.value = { customerId: null, items: [{ productId: null, quantity: 1, unitPrice: 0 }] };
  } catch (error) {
    console.error('生成销售单失败:', error);
    ElMessage.error('生成销售单失败');
  }
};
</script>

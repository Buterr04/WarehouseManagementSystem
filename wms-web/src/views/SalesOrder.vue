<template>
  <div>
    <h1>销售单管理</h1>

    <div style="margin-bottom: 10px;">
      <el-input
          v-model="searchQuery.customerName"
          placeholder="按客户姓名搜索"
          clearable
          style="width: 200px; margin-right: 10px;"
      />
      <el-date-picker
          v-model="searchQuery.saleDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="margin-right: 10px;"
          value-format="YYYY-MM-DD"
      />
      <el-button @click="handleSearch">搜索</el-button>
      <el-button type="primary" @click="goToCreateSalesOrder">创建销售单</el-button>
    </div>

    <el-table :data="paginatedSalesOrderList" style="width: 100%">
      <el-table-column prop="salesOrderId" label="销售单 ID" width="120"></el-table-column>
      <el-table-column prop="customerName" label="客户姓名"></el-table-column>
      <el-table-column prop="saleDate" label="销售日期"></el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <span>{{ scope.row.status === 0 ? '待处理' : scope.row.status === 1 ? '已出库' : '缺货' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="viewSalesOrderDetails(scope.row.salesOrderId)">查看</el-button>
          <el-button size="small" @click="goToEditSalesOrder(scope.row.salesOrderId)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteSalesOrder(scope.row.salesOrderId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        background
        layout="prev, pager, next"
        :total="filteredSalesOrderList.length"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
        style="margin-top: 20px; text-align: right;"
    />

    <el-dialog v-model="dialogVisible" title="销售单明细" width="60%">
      <!-- 信息栏：销售单号、客户名称、销售日期 -->
      <div style="margin-bottom: 15px;">
        <el-row :gutter="20">
          <el-col :span="8"><strong>销售单号：</strong>{{ salesOrderInfo.salesOrderId }}</el-col>
          <el-col :span="8"><strong>客户名称：</strong>{{ salesOrderInfo.customerName }}</el-col>
          <el-col :span="8"><strong>销售日期：</strong>{{ salesOrderInfo.saleDate }}</el-col>
        </el-row>
      </div>

      <!-- 原表格不变 -->
      <el-table :data="salesOrderDetails" style="width: 100%">
        <el-table-column prop="productName" label="商品名称(规格)">
          <template #default="scope">
            {{ scope.row.productName }} {{ scope.row.specifications ? `(${scope.row.specifications})` : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量"></el-table-column>
        <el-table-column prop="salePrice" label="销售单价">
          <template #default="scope">
            {{ formatCurrency(scope.row.salePrice) }}
          </template>
        </el-table-column>
        <el-table-column label="小计">
          <template #default="scope">
            {{ formatCurrency(scope.row.quantity * scope.row.salePrice) }}
          </template>
        </el-table-column>
      </el-table>

      <div style="text-align: right; margin-top: 10px;">
        总价: <strong style="font-size: 1.2em;">{{ formatCurrency(totalPrice) }}</strong>
      </div>

      <template #footer>
    <span class="dialog-footer">
      <el-button @click="dialogVisible = false">关闭</el-button>
    </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const salesOrderList = ref([]);
const dialogVisible = ref(false);
const salesOrderDetails = ref([]);
const currentSalesOrderId = ref(null);

const salesOrderInfo = ref({
  salesOrderId: '',
  customerName: '',
  saleDate: '',
});


// 搜索表单
const searchQuery = ref({
  customerName: '',
  saleDateRange: [],
});

// 分页变量
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索过滤后的数据
const filteredSalesOrderList = computed(() => {
  return salesOrderList.value.filter(order => {
    const nameMatch = order.customerName.toLowerCase().includes(searchQuery.value.customerName.toLowerCase());
    const dateMatch = (() => {
      if (!searchQuery.value.saleDateRange || searchQuery.value.saleDateRange.length !== 2) return true;
      const [start, end] = searchQuery.value.saleDateRange;
      const orderDate = new Date(order.saleDate);
      return new Date(start) <= orderDate && orderDate <= new Date(end);
    })();
    return nameMatch && dateMatch;
  });
});

// 当前页展示的数据
const paginatedSalesOrderList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredSalesOrderList.value.slice(start, end);
});

const handlePageChange = (page) => {
  currentPage.value = page;
};

const handleSearch = () => {
  currentPage.value = 1; // 搜索后重置页码
};

const totalPrice = computed(() => {
  return salesOrderDetails.value.reduce((sum, item) => sum + item.quantity * item.salePrice, 0);
});

const formatCurrency = (price) => {
  return price ? `¥ ${price.toFixed(2)}` : '¥ 0.00';
};

const fetchSalesOrderList = async () => {
  try {
    const response = await fetch('http://localhost:8090/sales-order/list');
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const responseData = await response.json();
    if (responseData.code === 200) {
      salesOrderList.value = responseData.data;
    } else {
      ElMessage.error(`获取销售单列表失败: ${responseData.msg}`);
    }
  } catch (error) {
    console.error('获取销售单列表失败:', error);
    ElMessage.error('获取销售单列表失败');
  }
};

const fetchSalesOrderDetailsForView = async (id) => {
  if (!id) return salesOrderDetails.value = [];
  try {
    const response = await fetch(`http://localhost:8090/sales-order/items/${id}`);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const responseData = await response.json();
    if (responseData.code === 200) {
      salesOrderDetails.value = responseData.data;
    } else {
      ElMessage.error(`获取销售单明细失败: ${responseData.msg}`);
    }
  } catch (error) {
    console.error('获取销售单明细失败:', error);
    ElMessage.error('获取销售单明细失败');
  }
};

onMounted(() => {
  fetchSalesOrderList();
});

const goToCreateSalesOrder = () => {
  router.push('/sales-order/create');
};

const goToEditSalesOrder = (id) => {
  router.push(`/sales-order/edit/${id}`);
};

const deleteSalesOrder = (id) => {
  ElMessageBox.confirm('确定要删除此销售单吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const response = await fetch(`http://localhost:8090/sales-order/delete?id=${id}`, {
        method: 'GET',
      });
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const responseData = await response.json();
      if (responseData.code === 200) {
        ElMessage.success('删除成功');
        await fetchSalesOrderList();
      } else {
        ElMessage.error(`删除失败: ${responseData.msg}`);
      }
    } catch (error) {
      console.error('删除销售单失败:', error);
      ElMessage.error('删除销售单失败');
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

const viewSalesOrderDetails = (id) => {
  const order = salesOrderList.value.find(o => o.salesOrderId === id);
  if (order) {
    salesOrderInfo.value = {
      salesOrderId: order.salesOrderId,
      customerName: order.customerName,
      saleDate: order.saleDate,
    };
  }
  currentSalesOrderId.value = id;
  fetchSalesOrderDetailsForView(id);
  dialogVisible.value = true;
};

</script>

<style scoped>
</style>

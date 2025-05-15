<template>
  <div>
    <h1>出库管理</h1>
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchForm.stockOutId" placeholder="按出库单号搜索" clearable style="width: 180px; margin-right: 10px;"></el-input>
      <el-date-picker v-model="searchForm.stockOutDate" type="date" placeholder="按出库日期搜索" clearable style="width: 180px; margin-right: 10px;"></el-date-picker>
      <el-select v-model="searchForm.status" placeholder="按处理状态搜索" clearable style="width: 180px; margin-right: 10px;">
        <el-option label="待处理" :value="0"></el-option>
        <el-option label="已出库" :value="1"></el-option>
        <el-option label="缺货" :value="2"></el-option>
      </el-select>
      <el-button @click="fetchOutboundOrderList">查询</el-button>
      <el-button type="primary" @click="createOutboundOrder">生成出库单</el-button>
    </div>

    <el-table :data="outboundOrderList" style="width: 100%">
      <el-table-column prop="stockOutId" label="出库单号" width="120"></el-table-column>
      <el-table-column prop="salesOrderId" label="关联销售单号" width="120"></el-table-column>
      <el-table-column prop="saleDate" label="销售日期" width="150"></el-table-column>
      <el-table-column prop="stockOutDate" label="出库日期"></el-table-column>
      <el-table-column prop="statusText" label="出库状态"></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewOutboundDetails(scope.row.stockOutId)">查看</el-button>
          <el-button
              size="small"
              @click="handleOutbound(scope.row)"
              :disabled="scope.row.status === 1"
          >
            处理
          </el-button>
          <el-button size="small" type="danger" @click="deleteOutbound(scope.row.stockOutId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="出库单明细" width="60%">
      <el-table :data="outboundDetails" style="width: 100%">
        <el-table-column prop="productId" label="商品 ID"></el-table-column>
        <el-table-column prop="productName" label="商品名称"></el-table-column>
        <el-table-column prop="specifications" label="规格"></el-table-column>
        <el-table-column prop="quantity" label="出库数量"></el-table-column>
        <el-table-column prop="stockQuantity" label="销售数量"></el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="createOutboundDialogVisible" title="关联销售单创建出库单" width="80%">
      <el-table :data="salesOrderList" style="width: 100%">
        <el-table-column prop="salesOrderId" label="销售单号" width="120"></el-table-column>
        <el-table-column prop="saleDate" label="销售日期" width="150"></el-table-column>
        <el-table-column prop="customerName" label="客户姓名"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="showOutboundForm(scope.row)">生成出库单</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createOutboundDialogVisible = false">取消</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="saveOutboundFormVisible" title="生成出库单" width="80%">
      <el-form :model="outboundForm" label-width="120px">
        <el-form-item label="销售单号">
          <el-input v-model="outboundForm.salesOrderId" :disabled="true" />
        </el-form-item>
        <el-table :data="outboundForm.items" style="width: 100%">
          <el-table-column prop="productId" label="商品 ID"></el-table-column>
          <el-table-column prop="productName" label="商品名称"></el-table-column>
          <el-table-column prop="quantity" label="销售数量"></el-table-column>
          <el-table-column prop="stockQuantity" label="库存数量"></el-table-column>
          <el-table-column prop="outboundQuantity" label="出库数量">
            <template #default="scope">
              <el-input-number v-model="scope.row.outboundQuantity" :min="0" :max="scope.row.quantity" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="outboundFormVisible = false">取消</el-button>
          <el-button type="primary" @click="saveOutboundOrder">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="outboundFormVisible" title="出库处理" width="80%">
      <el-form :model="outboundForm" label-width="120px">
        <el-form-item label="销售单号">
          <el-input v-model="outboundForm.salesOrderId" :disabled="true" />
        </el-form-item>
        <el-table :data="outboundForm.items" style="width: 100%">
          <el-table-column prop="productId" label="商品 ID"></el-table-column>
          <el-table-column prop="productName" label="商品名称"></el-table-column>
          <el-table-column prop="quantity" label="销售数量"></el-table-column>
          <el-table-column prop="stockQuantity" label="库存数量"></el-table-column>
          <el-table-column prop="outboundQuantity" label="出库数量">
            <template #default="scope">
              <el-input-number v-model="scope.row.outboundQuantity" :min="0" :max="scope.row.quantity" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="outboundFormVisible = false">取消</el-button>
          <el-button type="primary" @click="processOutbound">确认出库</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
//import { useRouter } from 'vue-router';
import {ElMessage, ElMessageBox} from 'element-plus';

//const router = useRouter();
const outboundOrderList = ref([]);
const outboundDetails = ref([]);
const salesOrderList = ref([]);
const currentOutboundId = ref(null);
const createOutboundDialogVisible = ref(false);
const dialogVisible = ref(false);
const outboundFormVisible = ref(false);
const saveOutboundFormVisible = ref(false);
const outboundForm = ref({
  salesOrderId: null,
  items: [],
});
const searchForm = ref({
  stockOutId: '',
  stockOutDate: null,
  status: null,
});

const fetchOutboundOrderList = async () => {
  try {
    let apiUrl = 'http://localhost:8090/stock-out/list';
    const queryParams = [];

    if (searchForm.value.stockOutId) {
      queryParams.push(`stockOutId=${searchForm.value.stockOutId}`);
    }
    if (searchForm.value.stockOutDate) {
      // Format the date to yyyy-MM-dd for the API
      const formattedDate = new Date(searchForm.value.stockOutDate).toISOString().slice(0, 10);
      queryParams.push(`stockOutDate=${formattedDate}`);
    }
    if (searchForm.value.status !== null) {
      queryParams.push(`status=${searchForm.value.status}`);
    }

    if (queryParams.length > 0) {
      apiUrl += `?${queryParams.join('&')}`;
    }

    const response = await fetch(apiUrl);
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取出库单列表失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const responseData = await response.json().then(res => res.data);
    outboundOrderList.value = responseData.map(order => ({ ...order, statusText: getStatusText(order.status) }));
  } catch (error) {
    console.error('获取出库单列表失败:', error);
    ElMessage.error('获取出库单列表失败');
  }
};

const fetchOutboundDetailsForView = async (stockOutId) => {
  if (stockOutId) {
    try {
      const response = await fetch(`http://localhost:8090/stock-out/items/${stockOutId}`); // 直接访问获取明细条目的 API
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(`获取出库单明细失败: ${response.status} - ${errorData?.message || '未知错误'}`);
      }
      const responseData = await response.json();
      if (responseData.code === 200 && responseData.data) {
        outboundDetails.value = responseData.data;
      } else {
        ElMessage.error(`获取出库单明细失败: ${responseData.msg}`);
        outboundDetails.value = []; // 清空明细数据
      }
    } catch (error) {
      console.error('获取出库单明细失败:', error);
      ElMessage.error('获取出库单明细失败');
      outboundDetails.value = []; // 清空明细数据
    }
  } else {
    outboundDetails.value = []; // 如果没有 ID，清空明细数据
  }
};
const viewOutboundDetails = (stockOutId) => {
  currentOutboundId.value = stockOutId;
  fetchOutboundDetailsForView(stockOutId);
  dialogVisible.value = true;
};

const fetchSalesOrderList = async () => {
  try {
    const response = await fetch('http://localhost:8090/sales-order/list'); // 替换为你的实际 API 端点
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取销售单列表失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const responseData = await response.json();
    if (responseData.code === 200) {
      salesOrderList.value = responseData.data; // 从 data 字段中获取列表数据
    } else {
      ElMessage.error(`获取销售单列表失败: ${responseData.msg}`);
      console.error('获取销售单列表失败:', responseData);
    }
  } catch (error) {
    console.error('获取销售单列表失败:', error);
    ElMessage.error('获取销售单列表失败');
  }
};

const fetchSalesOrderItems = async (salesOrderId) => {
  try {
    const response = await fetch(`http://localhost:8090/sales-order/items/${salesOrderId}`); // 替换为你的实际 API 端点
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`获取销售单明细失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    const data = await response.json().then(res => res.data);
    // TODO: 需要从后端获取每个商品的库存数量
    outboundForm.value.items = data.map(item => ({ ...item, outboundQuantity: item.quantity, stockQuantity: item.stockQuantity })); // 临时库存数据
  } catch (error) {
    console.error('获取销售单明细失败:', error);
    ElMessage.error('获取销售单明细失败');
  }
};

onMounted(() => {
  fetchOutboundOrderList();
  fetchSalesOrderList();
});

const createOutboundOrder = () => {
  createOutboundDialogVisible.value = true;
};

const showOutboundForm = (row) => {
  outboundForm.value.salesOrderId = row.salesOrderId;
  fetchSalesOrderItems(row.salesOrderId);
  saveOutboundFormVisible.value = true;
};

const saveOutboundOrder = async () => {
  try {
    const payload = {
      salesOrderId: outboundForm.value.salesOrderId,
      employeeId: 3, // TODO: 获取当前登录员工的 ID
      stockOutDate: new Date().toISOString().slice(0, 10), // 设置出库日期
      status: 0, // 标记为待处理
      stockOutItems: outboundForm.value.items.map(item => ({
        productId: item.productId,
        quantity: item.outboundQuantity,
      })),
    };

    const response = await fetch('http://localhost:8090/stock-out/save', { // 替换为你的实际保存出库单的 API 端点
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`保存出库单失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }

    ElMessage.success('出库单创建成功');
    outboundFormVisible.value = false;
    createOutboundDialogVisible.value = false;
    await fetchOutboundOrderList(); // 刷新出库单列表

  } catch (error) {
    console.error('保存出库单失败:', error);
    ElMessage.error('保存出库单失败');
  }
};

const processOutbound = async () => {
  const hasStockShortage = outboundForm.value.items.some(item => item.outboundQuantity > item.stockQuantity);

  if (hasStockShortage) {
    ElMessageBox.confirm(
        '检测到部分商品缺货，是否将此出库单标记为缺货？',
        '缺货提示',
        {
          confirmButtonText: '标记缺货',
          cancelButtonText: '取消',
          type: 'warning',
        }
    ).then(async () => {
      try {
        const payload = {
          stockOutId: currentOutboundId.value,
          status: 2, // 设置状态为缺货
          stockOutItems: outboundForm.value.items.map(item => ({
            productId: item.productId,
            quantity: item.outboundQuantity,
          })),
        };
        const response = await fetch('http://localhost:8090/stock-out/modify', { // 替换为你的实际 API 端点
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(payload),
        });
        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(`标记缺货失败: ${response.status} - ${errorData?.message || '未知错误'}`);
        }
        ElMessage.warning('此出库单已标记为缺货');
        outboundFormVisible.value = false;
        await fetchOutboundOrderList(); // 刷新出库单列表
      } catch (error) {
        console.error('标记缺货失败:', error);
        ElMessage.error('标记缺货失败');
      }
    }).catch(() => {
      ElMessage.info('已取消操作');
    });
    return; // 停止当前出库处理流程
  }

  // 如果没有缺货，则执行正常的出库处理并更新库存
  try {
    const stockOutPayload = {
      stockOutId: currentOutboundId.value,
      salesOrderId: outboundForm.value.salesOrderId,
      employeeId: 3, // TODO: 获取当前登录员工的 ID
      stockOutDate: new Date().toISOString(),
      status: 1,
      stockOutItems: outboundForm.value.items.map(item => ({
        productId: item.productId,
        quantity: item.outboundQuantity,
      })),
    };
    const stockOutResponse = await fetch('http://localhost:8090/stock-out/modify', { // 替换为你的实际 API 端点
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(stockOutPayload),
    });
    if (!stockOutResponse.ok) {
      const errorData = await stockOutResponse.json();
      throw new Error(`出库处理失败: ${stockOutResponse.status} - ${errorData?.message || '未知错误'}`);
    }

    // 出库成功后，更新库存
    for (const item of outboundForm.value.items) {
      const updateStockPayload = {
        productId: item.productId,
        stockQuantity: item.stockQuantity-item.outboundQuantity, // 负数表示减少库存
      };
      const updateStockResponse = await fetch('http://localhost:8090/product/modify', { // 替换为你的实际更新库存 API 端点
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updateStockPayload),
      });
      if (!updateStockResponse.ok) {
        const errorData = await updateStockResponse.json();
        throw new Error(`更新商品 ${item.productName} 库存失败: ${updateStockResponse.status} - ${errorData?.message || '未知错误'}`);
      }
    }

    ElMessage.success('出库处理成功，库存已更新');
    outboundFormVisible.value = false;
    await fetchOutboundOrderList(); // 刷新出库单列表
  } catch (error) {
    console.error('出库处理失败:', error);
    ElMessage.error('出库处理失败');
  }
};

const getStatusText = (status) => {
  // TODO: 根据实际状态值返回对应的文本
  return status === 0 ? '待处理' : status === 1 ? '已出库' : '缺货';
};

const deleteOutbound = (stockOutId) => {
  ElMessageBox.confirm(
      '确定要删除此出库单吗?',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          const response = await fetch(`http://localhost:8090/stock-out/delete?id=${stockOutId}`, { // 替换为你的实际 API 端点
            method: 'GET', // 或者 DELETE，根据你的后端 API 定义
          });
          if (!response.ok) {
            const errorData = await response.json();
            throw new Error(`删除出库单失败: ${response.status} - ${errorData?.message || '未知错误'}`);
          }
          const responseData = await response.json();
          if (responseData.code === 200) {
            ElMessage.success('删除成功');
            await fetchOutboundOrderList(); // 刷新列表
          } else {
            ElMessage.error(`删除出库单失败: ${responseData.msg}`);
            console.error('删除出库单失败:', responseData);
          }
        } catch (error) {
          console.error('删除出库单失败:', error);
          ElMessage.error('删除出库单失败');
        }
      })
      .catch(() => {
        ElMessage.info('已取消删除');
      });
};

const handleOutbound = (row) => {
  currentOutboundId.value = row.stockOutId; // 设置当前出库单的 ID
  outboundForm.value.salesOrderId = row.salesOrderId;
  fetchSalesOrderItems(row.salesOrderId);
  outboundFormVisible.value = true;
};

</script>

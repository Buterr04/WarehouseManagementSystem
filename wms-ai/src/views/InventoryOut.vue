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
      <el-button @click="handleSearch">查询</el-button>
      <el-button type="primary" @click="createOutboundOrder">生成出库单</el-button>
    </div>

    <el-table :data="pagedOutboundOrderList" style="width: 100%">
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

    <el-pagination
        background
        layout="prev, pager, next"
        :total="filteredOutboundOrderList.length"
        :page-size="outboundPageSize"
        :current-page="outboundCurrentPage"
        @current-change="handleOutboundPageChange"
        style="margin-top: 20px; text-align: right;"
    />

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
      <!-- 新增：销售单过滤条件 -->
      <div style="margin-bottom: 10px;">
        <el-input v-model="salesOrderSearchForm.customerName" placeholder="按客户姓名搜索" clearable style="width: 180px; margin-right: 10px;"></el-input>
        <el-date-picker v-model="salesOrderSearchForm.saleDate" type="date" placeholder="按销售日期搜索" clearable style="width: 180px; margin-right: 10px;"></el-date-picker>
        <el-button @click="handleSalesOrderSearch">查询</el-button>
      </div>
      <el-table :data="pagedSalesOrderList" style="width: 100%">
        <el-table-column prop="salesOrderId" label="销售单号" width="120"></el-table-column>
        <el-table-column prop="saleDate" label="销售日期" width="150"></el-table-column>
        <el-table-column prop="customerName" label="客户姓名"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <span>{{ scope.row.status === 0 ? '待处理' : scope.row.status === 1 ? '已出库' : scope.row.status === 2 ? '缺货' : "已请求采购" }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="showOutboundForm(scope.row)">生成出库单</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          background
          layout="prev, pager, next"
          :total="filteredSalesOrderList.length"
          :page-size="salesPageSize"
          :current-page="salesCurrentPage"
          @current-change="handleSalesPageChange"
          style="margin-top: 20px; text-align: right;"
      />

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
          <el-button @click="saveOutboundFormVisible = false">取消</el-button>
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
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const outboundOrderList = ref([]);
const outboundDetails = ref([]);
const salesOrderList = ref([]);
const currentOutboundId = ref(null);
const createOutboundDialogVisible = ref(false);
const dialogVisible = ref(false);
const outboundFormVisible = ref(false);
const saveOutboundFormVisible = ref(false);
const outboundForm = ref({ salesOrderId: null, items: [] });
const searchForm = ref({ stockOutId: '', stockOutDate: null, status: null });

// 分页相关变量
const outboundCurrentPage = ref(1);
const outboundPageSize = ref(10);
const salesCurrentPage = ref(1);
const salesPageSize = ref(8);

// 搜索过滤后的出库单列表
const filteredOutboundOrderList = computed(() => {
  return outboundOrderList.value.filter(order => {
    // 出库单号
    if (searchForm.value.stockOutId && !String(order.stockOutId).includes(searchForm.value.stockOutId)) {
      return false;
    }
    // 出库日期（修正：两边都转为 yyyy-MM-dd 字符串再比较，兼容 Date 和字符串）
    if (searchForm.value.stockOutDate) {
      // 格式化为 yyyy-MM-dd
      const formatDate = (d) => {
        if (!d) return '';
        if (typeof d === 'string') {
          // 兼容字符串格式
          return d.slice(0, 10);
        }
        if (d instanceof Date) {
          // 防止时区问题，直接取本地年月日
          const year = d.getFullYear();
          const month = String(d.getMonth() + 1).padStart(2, '0');
          const day = String(d.getDate()).padStart(2, '0');
          return `${year}-${month}-${day}`;
        }
        return '';
      };
      const searchDateStr = formatDate(searchForm.value.stockOutDate);
      const orderDateStr = formatDate(order.stockOutDate);
      if (orderDateStr !== searchDateStr) return false;
    }
    // 状态
    if (searchForm.value.status !== null && searchForm.value.status !== '' && order.status !== searchForm.value.status) {
      return false;
    }
    return true;
  });
});

// 出库单分页（基于过滤后数据）
const pagedOutboundOrderList = computed(() => {
  const start = (outboundCurrentPage.value - 1) * outboundPageSize.value;
  const end = start + outboundPageSize.value;
  return filteredOutboundOrderList.value.slice(start, end);
});

// 新增：如需对销售单做过滤，可在这里加条件
const filteredSalesOrderList = computed(() => {
  return salesOrderList.value.filter(order => {
    // 只显示未处理的销售单
    if (order.status !== 0) {
      return false;
    }
    // 客户姓名过滤
    if (salesOrderSearchForm.value.customerName && !order.customerName?.includes(salesOrderSearchForm.value.customerName)) {
      return false;
    }
    // 销售日期过滤（修正：两边都转为 yyyy-MM-dd 字符串再比较，兼容 Date 和字符串）
    if (salesOrderSearchForm.value.saleDate) {
      const formatDate = (d) => {
        if (!d) return '';
        if (typeof d === 'string') {
          return d.slice(0, 10);
        }
        if (d instanceof Date) {
          const year = d.getFullYear();
          const month = String(d.getMonth() + 1).padStart(2, '0');
          const day = String(d.getDate()).padStart(2, '0');
          return `${year}-${month}-${day}`;
        }
        return '';
      };
      const searchDateStr = formatDate(salesOrderSearchForm.value.saleDate);
      const orderDateStr = formatDate(order.saleDate);
      if (orderDateStr !== searchDateStr) return false;
    }
    return true;
  });
});

// 新增：销售单分页
const pagedSalesOrderList = computed(() => {
  const start = (salesCurrentPage.value - 1) * salesPageSize.value;
  const end = start + salesPageSize.value;
  return filteredSalesOrderList.value.slice(start, end);
});

// 分页切换事件
const handleOutboundPageChange = (page) => {
  outboundCurrentPage.value = page;
};
const handleSalesPageChange = (page) => {
  salesCurrentPage.value = page;
};

const fetchOutboundOrderList = async () => {
  try {
    let apiUrl = 'http://localhost:8090/stock-out/list';
    const queryParams = [];
    if (searchForm.value.stockOutId) queryParams.push(`stockOutId=${searchForm.value.stockOutId}`);
    if (searchForm.value.stockOutDate) {
      const formattedDate = new Date(searchForm.value.stockOutDate).toISOString().slice(0, 10);
      queryParams.push(`stockOutDate=${formattedDate}`);
    }
    if (searchForm.value.status !== null) queryParams.push(`status=${searchForm.value.status}`);
    if (queryParams.length > 0) apiUrl += `?${queryParams.join('&')}`;

    const response = await fetch(apiUrl);
    const responseData = await response.json();
    outboundOrderList.value = responseData.data.map(order => ({ ...order, statusText: getStatusText(order.status) }));
  } catch (error) {
    console.error('获取出库单列表失败:', error);
    ElMessage.error('获取出库单列表失败');
  }
};

const fetchSalesOrderList = async () => {
  try {
    const response = await fetch('http://localhost:8090/sales-order/list');
    const responseData = await response.json();
    if (responseData.code === 200) {
      salesOrderList.value = responseData.data;
    } else {
      ElMessage.error(`获取销售单列表失败: ${responseData.msg}`);
    }
  } catch (error) {
    ElMessage.error('获取销售单列表失败');
  }
};

const fetchSalesOrderItems = async (salesOrderId) => {
  try {
    const response = await fetch(`http://localhost:8090/sales-order/items/${salesOrderId}`);
    const raw = await response.json();
    const data = raw.data || [];
    // ✅ 始终创建新数组，防止 Vue 引用复用引起渲染重复
    outboundForm.value.items = data.map(item => ({
      productId: item.productId,
      productName: item.productName,
      quantity: item.quantity,
      stockQuantity: item.stockQuantity,
      outboundQuantity: item.quantity
    }));
  } catch (error) {
    ElMessage.error('获取销售单明细失败');
    outboundForm.value.items = []; // 清空
  }
};

const showOutboundForm = async (row) => {
  saveOutboundFormVisible.value = false;
  outboundForm.value = { salesOrderId: row.salesOrderId, items: [] };
  await fetchSalesOrderItems(row.salesOrderId);
  // ✅ 打印调试一次，避免重复加载
  console.log('🧾 明细已加载：', outboundForm.value.items);
  saveOutboundFormVisible.value = true;
};

const saveOutboundOrder = async () => {
  try {
    // ✅ 确保无重复
    const uniqueItems = Array.from(new Map(outboundForm.value.items.map(item => [item.productId, item])).values());

    const payload = {
      salesOrderId: outboundForm.value.salesOrderId,
      employeeId: 3,
      stockOutDate: new Date().toISOString().slice(0, 10),
      status: 0,
      stockOutItems: uniqueItems.map(item => ({
        productId: item.productId,
        quantity: item.outboundQuantity
      }))
    };

    const response = await fetch('http://localhost:8090/stock-out/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (!response.ok) throw new Error('保存失败');

    ElMessage.success('出库单创建成功');
    saveOutboundFormVisible.value = false;
    createOutboundDialogVisible.value = false;
    outboundForm.value = { salesOrderId: null, items: [] };
    await fetchOutboundOrderList();
  } catch (error) {
    console.error('保存出库单失败:', error);
    ElMessage.error('保存出库单失败');
  }
};

const processOutbound = async () => {
  const hasStockShortage = outboundForm.value.items.some(item => item.outboundQuantity > item.stockQuantity);
  if (hasStockShortage) {
    ElMessageBox.confirm('检测到部分商品缺货，是否将此出库单标记为缺货？', '缺货提示', {
      confirmButtonText: '标记缺货', cancelButtonText: '取消', type: 'warning'
    }).then(async () => {
      await markOutOfStock();
    }).catch(() => {
      ElMessage.info('已取消操作');
    });
    return;
  }
  await confirmNormalOutbound();
};

const markOutOfStock = async () => {
  const payload = {
    stockOutId: currentOutboundId.value,
    status: "2",
  };
  await fetch('http://localhost:8090/stock-out/updateStatus', {
    method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload)
  });
  // 同步更新销售单状态为缺货（假设缺货状态为2，按实际后端定义调整）
  try {
    const outbound = outboundOrderList.value.find(o => o.stockOutId === currentOutboundId.value);
    if (outbound && outbound.salesOrderId) {
      await fetch('http://localhost:8090/sales-order/updateStatus', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ salesOrderId: outbound.salesOrderId, status: "2" })
      });
    }
  } catch (e) {
    // 可选：错误提示
    ElMessage.warning('销售单状态同步失败');
  }
  // 新增：自动计算缺货数量并调用 updateShortage 接口
  try {
    for (const item of outboundForm.value.items) {
      if (item.outboundQuantity > item.stockQuantity) {
        const shortQuantity = item.outboundQuantity - item.stockQuantity;
        await fetch('http://localhost:8090/product/updateShortage', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            productId: item.productId,
            shortQuantity: shortQuantity
          })
        });
      }
    }
  } catch (e) {
    ElMessage.warning('商品缺货数量更新失败');
  }
  ElMessage.warning('此出库单已标记为缺货');
  outboundFormVisible.value = false;
  await fetchOutboundOrderList();
};

const confirmNormalOutbound = async () => {
  const payload = {
    stockOutId: currentOutboundId.value,
    salesOrderId: outboundForm.value.salesOrderId,
    employeeId: 3,
    stockOutDate: new Date().toISOString(),
    status: 1,
    stockOutItems: outboundForm.value.items.map(item => ({ productId: item.productId, quantity: item.outboundQuantity }))
  };
  await fetch('http://localhost:8090/stock-out/modify', {
    method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload)
  });
  // 同步更新销售单状态
  try {
    const outbound = outboundOrderList.value.find(o => o.stockOutId === currentOutboundId.value);
    if (outbound && outbound.salesOrderId) {
      await fetch('http://localhost:8090/sales-order/updateStatus', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ salesOrderId: outbound.salesOrderId, status: "1" })
      });
    }
  } catch (e) {
    // 可选：错误提示
    ElMessage.warning('销售单状态同步失败');
  }
  for (const item of outboundForm.value.items) {
    const stockPayload = { productId: item.productId, stockQuantity: item.stockQuantity - item.outboundQuantity };
    await fetch('http://localhost:8090/product/modify', {
      method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(stockPayload)
    });
  }
  ElMessage.success('出库处理成功，库存已更新');
  outboundFormVisible.value = false;
  await fetchOutboundOrderList();
};

const getStatusText = (status) => status === 0 ? '待处理' : status === 1 ? '已出库' : status === 2 ? '缺货' : "已请求采购" ;

const deleteOutbound = (stockOutId) => {
  ElMessageBox.confirm('确定要删除此出库单吗?', '警告', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await fetch(`http://localhost:8090/stock-out/delete?id=${stockOutId}`, { method: 'GET' });
    ElMessage.success('删除成功');
    await fetchOutboundOrderList();
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

const viewOutboundDetails = (stockOutId) => {
  currentOutboundId.value = stockOutId;
  fetchOutboundDetailsForView(stockOutId);
  dialogVisible.value = true;
};

const fetchOutboundDetailsForView = async (stockOutId) => {
  try {
    const response = await fetch(`http://localhost:8090/stock-out/items/${stockOutId}`);
    const responseData = await response.json();
    outboundDetails.value = responseData.code === 200 ? responseData.data : [];
  } catch (error) {
    ElMessage.error('获取出库单明细失败');
    outboundDetails.value = [];
  }
};

const createOutboundOrder = () => {
  createOutboundDialogVisible.value = true;
};

const handleOutbound = (row) => {
  currentOutboundId.value = row.stockOutId;
  outboundForm.value = { salesOrderId: row.salesOrderId, items: [] };
  fetchSalesOrderItems(row.salesOrderId);
  outboundFormVisible.value = true;
};

// 查询按钮：重置页码即可
const handleSearch = () => {
  outboundCurrentPage.value = 1;
};

// 新增：销售单过滤表单
const salesOrderSearchForm = ref({
  customerName: '',
  saleDate: null
});

// 查询按钮：重置页码即可
const handleSalesOrderSearch = () => {
  salesCurrentPage.value = 1;
};

onMounted(() => {
  fetchOutboundOrderList();
  fetchSalesOrderList();
});
</script>

<template>
  <div>
    <h1>商品库存管理</h1>
    <div style="margin-bottom: 10px;">
      <el-input
          v-model="searchQuery.productName"
          placeholder="按商品名称搜索"
          clearable
          style="width: 200px; margin-right: 10px;"
      />
      <el-input
          v-model="searchQuery.specifications"
          placeholder="按商品规格搜索"
          clearable
          style="width: 200px; margin-right: 10px;"
      />
      <el-button @click="handleSearch">搜索</el-button>
      <el-button type="primary" @click="openDialog">添加商品</el-button>
    </div>

    <el-table :data="paginatedProductList" style="width: 100%">
      <el-table-column prop="productId" label="ID" width="80"></el-table-column>
      <el-table-column prop="productName" label="商品名称"></el-table-column>
      <el-table-column prop="specifications" label="商品规格"></el-table-column>
      <el-table-column prop="unitPrice" label="价格"></el-table-column>
      <el-table-column prop="stockQuantity" label="库存数量"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="editItem(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteItem(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- ✅ 分页控件自动贴底 -->
    <div style="text-align: right; margin-top: 20px; padding: 10px 20px;">
      <el-pagination
          background
          layout="prev, pager, next"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="filteredProductList.length"
          @current-change="handlePageChange"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="30%">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品名称">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="商品规格">
          <el-input v-model="form.category"></el-input>
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="form.unit"></el-input>
        </el-form-item>
        <el-form-item label="库存数量">
          <el-input-number
              v-model="form.stockQuantity"
              :min="0"
              :step="1"
              :controls="true"
              placeholder="请输入库存数量"
              style="width: 100%;"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveItem">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';

const productList = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('添加商品');
const form = ref({
  id: null,
  name: '',
  category: '',
  unit: '',
  stockQuantity: '',
});
const editingId = ref(null);
const searchQuery = ref({
  productName: '',
  specifications: '',
});

// 分页变量
const currentPage = ref(1);
const pageSize = ref(10);

// 获取商品列表
const fetchProductList = async () => {
  try {
    const response = await fetch('http://localhost:8090/product/list');
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const data = await response.json();
    productList.value = data;
  } catch (error) {
    console.error('获取商品列表失败:', error);
    ElMessage.error('获取商品列表失败');
  }
};

onMounted(() => {
  fetchProductList();
});

// 搜索过滤
const filteredProductList = computed(() => {
  return productList.value.filter(product => {
    if (product && product.productName && product.specifications) {
      const nameMatch = product.productName.toLowerCase().includes(searchQuery.value.productName.toLowerCase());
      const specMatch = product.specifications.toLowerCase().includes(searchQuery.value.specifications.toLowerCase());
      return nameMatch && specMatch;
    }
    return false;
  });
});

// 分页处理
const paginatedProductList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredProductList.value.slice(start, end);
});
const handlePageChange = (page) => {
  currentPage.value = page;
};

// 搜索按钮（此处只是前端过滤）
const handleSearch = () => {
  currentPage.value = 1; // 搜索后回到第一页
};

// 打开添加商品对话框
const openDialog = () => {
  dialogTitle.value = '添加商品';
  form.value = { id: null, name: '', category: '', unit: '', stockQuantity: '' };
  editingId.value = null;
  dialogVisible.value = true;
};

// 编辑商品
const editItem = (row) => {
  dialogTitle.value = '编辑商品';
  form.value = {
    id: row.productId,
    name: row.productName,
    category: row.specifications,
    unit: row.unitPrice,
    stockQuantity: row.stockQuantity,
  };
  editingId.value = row.productId;
  dialogVisible.value = true;
};

// 删除商品
const deleteItem = async (row) => {
  try {
    const response = await fetch(`http://localhost:8090/product/delete?id=${row.productId}`, {
      method: 'GET',
    });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`删除商品失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    ElMessage.success('删除成功');
    await fetchProductList();
  } catch (error) {
    console.error('删除商品失败:', error);
    ElMessage.error('删除商品失败');
  }
};

// 保存商品
const saveItem = async () => {
  try {
    const payload = {
      productId: editingId.value || "0",
      productName: form.value.name,
      specifications: form.value.category,
      unitPrice: form.value.unit,
      stockQuantity: form.value.stockQuantity,
    };
    const method = 'POST';
    const url = editingId.value
        ? 'http://localhost:8090/product/modify'
        : 'http://localhost:8090/product/save';

    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`保存商品失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }

    const newData = await response.json();
    if (editingId.value) {
      const index = productList.value.findIndex(item => item.productId === editingId.value);
      if (index !== -1) productList.value[index] = newData;
      ElMessage.success('更新成功');
    } else {
      productList.value = [...productList.value, newData];
      ElMessage.success('添加成功');
    }

    dialogVisible.value = false;
    form.value = { id: null, name: '', category: '', unit: '', stockQuantity: '' };
    editingId.value = null;
    await fetchProductList();
  } catch (error) {
    console.error('保存商品失败:', error);
    ElMessage.error('保存商品失败');
  }
};
</script>

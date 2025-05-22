<template>
  <div>
    <h1>商品管理</h1>

    <!-- 搜索栏 -->
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

    <!-- 商品表格 -->
    <el-table :data="paginatedProductList" style="width: 100%">
      <el-table-column prop="productId" label="ID" width="80"></el-table-column>
      <el-table-column prop="productName" label="商品名称"></el-table-column>
      <el-table-column prop="specifications" label="商品规格"></el-table-column>
      <el-table-column prop="unitPrice" label="价格"></el-table-column>
      <el-table-column prop="supplierName" label="供应商"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="editItem(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteItem(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        background
        layout="prev, pager, next"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="filteredProductList.length"
        @current-change="handlePageChange"
        style="margin-top: 20px; text-align: right;"
    />

    <!-- 商品弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="30%">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="商品规格">
          <el-input v-model="form.category" />
        </el-form-item>
        <el-form-item label="价格" prop="unit">
          <el-input-number v-model="form.unit" :min="0.01" placeholder="请输入价格" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="供应商">
          <el-select v-model="form.supplierId" placeholder="请选择供应商">
            <el-option
                v-for="supplier in supplierList"
                :key="supplier.supplierId"
                :label="supplier.supplierName"
                :value="supplier.supplierId"
            />
          </el-select>
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
const supplierList = ref([]);

const dialogVisible = ref(false);
const dialogTitle = ref('添加商品');
const form = ref({
  id: null,
  name: '',
  category: '',
  unit: '',
  supplierId: '',
});
const editingId = ref(null);

const searchQuery = ref({
  productName: '',
  specifications: '',
});

const currentPage = ref(1);
const pageSize = ref(10);

// 获取商品列表
const fetchProductList = async () => {
  try {
    const response = await fetch('http://localhost:8090/product/list');
    if (!response.ok) throw new Error('HTTP错误');
    const data = await response.json();
    productList.value = data;
  } catch (error) {
    console.error('获取商品失败:', error);
    ElMessage.error('获取商品列表失败');
  }
};

// 获取供应商列表
const fetchSupplierList = async () => {
  try {
    const response = await fetch('http://localhost:8090/supplier/list');
    if (!response.ok) throw new Error('HTTP错误');
    const data = await response.json();
    supplierList.value = data;
  } catch (error) {
    console.error('获取供应商失败:', error);
    ElMessage.error('获取供应商失败');
  }
};

onMounted(() => {
  fetchProductList();
  fetchSupplierList();
});

// 搜索过滤
const filteredProductList = computed(() => {
  return productList.value.filter(product => {
    const nameMatch = product.productName?.toLowerCase().includes(searchQuery.value.productName.toLowerCase()) ?? false;
    const specMatch = product.specifications?.toLowerCase().includes(searchQuery.value.specifications.toLowerCase()) ?? false;
    return nameMatch && specMatch;
  });
});

// 分页
const paginatedProductList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredProductList.value.slice(start, start + pageSize.value);
});

const handlePageChange = (page) => {
  currentPage.value = page;
};

const handleSearch = () => {
  currentPage.value = 1;
};

const openDialog = () => {
  dialogTitle.value = '添加商品';
  form.value = { id: null, name: '', category: '', unit: '', supplierId: '' };
  editingId.value = null;
  dialogVisible.value = true;
};

const editItem = (row) => {
  dialogTitle.value = '编辑商品';
  form.value = {
    id: row.productId,
    name: row.productName,
    category: row.specifications,
    unit: row.unitPrice,
    supplierId: row.supplierId,
  };
  editingId.value = row.productId;
  dialogVisible.value = true;
};

const deleteItem = async (row) => {
  try {
    const res = await fetch(`http://localhost:8090/product/delete?id=${row.productId}`, { method: 'GET' });
    if (!res.ok) throw new Error('删除失败');
    ElMessage.success('删除成功');
    await fetchProductList();
  } catch (err) {
    console.error(err);
    ElMessage.error('删除商品失败');
  }
};

const saveItem = async () => {
  try {
    const payload = {
      productId: editingId.value || '0',
      productName: form.value.name,
      specifications: form.value.category,
      unitPrice: form.value.unit,
      supplierId: form.value.supplierId,
    };

    const url = editingId.value
        ? 'http://localhost:8090/product/modify'
        : 'http://localhost:8090/product/save';

    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      const errorData = await res.json();
      throw new Error(errorData?.message || '保存失败');
    }

    ElMessage.success(editingId.value ? '更新成功' : '添加成功');
    dialogVisible.value = false;
    await fetchProductList();
  } catch (err) {
    console.error(err);
    ElMessage.error('保存商品失败');
  }
};
</script>

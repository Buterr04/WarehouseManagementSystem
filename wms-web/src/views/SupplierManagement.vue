<template>
  <div>
    <h1>供应商管理</h1>
    <div style="margin-bottom: 10px;">
      <el-input
          v-model="searchQuery.supplierName"
          placeholder="按供应商名称搜索"
          clearable
          style="width: 200px; margin-right: 10px;"
      />
      <el-input
          v-model="searchQuery.contactInfo"
          placeholder="按联系方式搜索"
          clearable
          style="width: 200px; margin-right: 10px;"
      />
      <el-button @click="handleSearch">搜索</el-button>
      <el-button type="primary" @click="openDialog">添加供应商</el-button>
    </div>

    <el-table :data="paginatedSupplierList" style="width: 100%">
      <el-table-column prop="supplierId" label="ID" width="80"></el-table-column>
      <el-table-column prop="supplierName" label="供应商名称"></el-table-column>
      <el-table-column prop="contactPerson" label="联系人"></el-table-column>
      <el-table-column prop="contactInfo" label="联系方式"></el-table-column>
      <el-table-column prop="address" label="地址"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="editItem(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteItem(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        background
        layout="prev, pager, next"
        :total="filteredSupplierList.length"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
        style="margin-top: 20px; text-align: right;"
    />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="30%">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="form.supplierName"></el-input>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="form.contactPerson"></el-input>
        </el-form-item>
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input v-model="form.contactInfo"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address"></el-input>
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

const supplierList = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('添加供应商');
const form = ref({ supplierId: null, supplierName: '', contactPerson: '', contactInfo: '', address: '' });
const formRules = ref({
  supplierName: [{ required: true, message: '供应商名称不能为空', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '联系人不能为空', trigger: 'blur' }],
  contactInfo: [{ required: true, message: '联系电话不能为空', trigger: 'blur' },
    {
      pattern: /^\d{3}-\d{3}-\d{4}$|^\d{11}$/,
      message: '请输入正确的电话号码格式 (例如: 123-456-7890 或 138xxxxxxxx)',
      trigger: 'blur',
    },],
  address: [{ required: true, message: '地址不能为空', trigger: 'blur' }],
});
const formRef = ref(null);
const editingId = ref(null);
const searchQuery = ref({ supplierName: '', contactInfo: '' });

const currentPage = ref(1);
const pageSize = ref(9);

const fetchSupplierList = async () => {
  try {
    const response = await fetch('http://localhost:8090/supplier/list');
    if (!response.ok) throw new Error(`HTTP 错误! 状态码: ${response.status}`);
    const data = await response.json();
    supplierList.value = data;
  } catch (error) {
    console.error('获取供应商列表失败:', error);
    ElMessage.error('获取供应商列表失败');
  }
};

onMounted(() => {
  fetchSupplierList();
});

const filteredSupplierList = computed(() => {
  return supplierList.value.filter(supplier => {
    if (supplier && supplier.supplierName && supplier.contactInfo) {
      const nameMatch = supplier.supplierName.toLowerCase().includes(searchQuery.value.supplierName.toLowerCase());
      const infoMatch = supplier.contactInfo.toLowerCase().includes(searchQuery.value.contactInfo.toLowerCase());
      return nameMatch && infoMatch;
    }
    return false;
  });
});

const paginatedSupplierList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredSupplierList.value.slice(start, end);
});

const handlePageChange = (page) => {
  currentPage.value = page;
};

const handleSearch = () => {
  currentPage.value = 1;
};

const openDialog = () => {
  dialogTitle.value = '添加供应商';
  form.value = { supplierId: null, supplierName: '', contactPerson: '', contactInfo: '', address: '' };
  editingId.value = null;
  formRef.value?.resetFields();
  dialogVisible.value = true;
};

const editItem = (row) => {
  dialogTitle.value = '编辑供应商';
  form.value = { supplierId: row.supplierId, supplierName: row.supplierName, contactPerson: row.contactPerson, contactInfo: row.contactInfo, address: row.address };
  editingId.value = row.supplierId;
  dialogVisible.value = true;
};

const deleteItem = async (row) => {
  try {
    const response = await fetch(`http://localhost:8090/supplier/delete?id=${row.supplierId}`, { method: 'GET' });
    if (!response.ok) throw new Error(`删除供应商失败: ${response.status}`);
    ElMessage.success('删除成功');
    await fetchSupplierList();
  } catch (error) {
    console.error('删除供应商失败:', error);
    ElMessage.error('删除供应商失败');
  }
};

const saveItem = async () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        const payload = {
          supplierId: editingId.value,
          supplierName: form.value.supplierName,
          contactPerson: form.value.contactPerson,
          contactInfo: form.value.contactInfo,
          address: form.value.address,
        };
        const url = editingId.value
            ? 'http://localhost:8090/supplier/modify'
            : 'http://localhost:8090/supplier/save';

        const response = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        });

        if (!response.ok) throw new Error(`保存供应商失败: ${response.status}`);

        const newData = await response.json();
        if (editingId.value) {
          const index = supplierList.value.findIndex(item => item.supplierId === editingId.value);
          if (index !== -1) supplierList.value[index] = newData;
          ElMessage.success('更新成功');
        } else {
          supplierList.value = [...supplierList.value, newData];
          ElMessage.success('添加成功');
        }

        dialogVisible.value = false;
        form.value = { supplierId: null, supplierName: '', contactPerson: '', contactInfo: '', address: '' };
        editingId.value = null;
        await fetchSupplierList();
      } catch (error) {
        console.error('保存供应商失败:', error);
        ElMessage.error('保存供应商失败');
      }
    } else {
      console.log('表单验证失败');
    }
  });
};
</script>
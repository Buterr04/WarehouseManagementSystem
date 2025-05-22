<template>
  <div>
    <h1>客户管理</h1>
    <div style="margin-bottom: 10px;">
      <el-input
          v-model="searchQuery.customerName"
          placeholder="按客户名称搜索"
          clearable
          style="width: 200px; margin-right: 10px;"
      />
      <el-input
          v-model="searchQuery.contactPhone"
          placeholder="按联系电话搜索"
          clearable
          style="width: 200px; margin-right: 10px;"
      />
      <el-button @click="handleSearch">搜索</el-button>
      <el-button type="primary" @click="openDialog">添加客户</el-button>
    </div>

    <el-table :data="paginatedCustomerList" style="width: 100%">
      <el-table-column prop="customerId" label="ID" width="80"></el-table-column>
      <el-table-column prop="customerName" label="客户名称"></el-table-column>
      <el-table-column prop="contactPhone" label="联系电话"></el-table-column>
      <el-table-column
          prop="contactAddress"
          label="联系地址"
          show-overflow-tooltip
      >
      </el-table-column>

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
        :total="filteredCustomerList.length"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
        style="margin-top: 20px; text-align: right;"
    />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="30%">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="form.customerName"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone"></el-input>
        </el-form-item>
        <el-form-item label="联系地址" prop="contactAddress">
          <el-input v-model="form.contactAddress"></el-input>
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

const customerList = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('添加客户');
const form = ref({ customerId: null, customerName: '', contactPhone: '', contactAddress: '' });
const formRules = ref({
  customerName: [{ required: true, message: '客户名称不能为空', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '联系电话不能为空', trigger: 'blur' },
    {
      pattern: /^\d{3}-\d{3}-\d{4}$|^\d{11}$/,
      message: '请输入正确的电话号码格式 (例如: 123-456-7890 或 138xxxxxxxx)',
      trigger: 'blur',
    },
  ],
  contactAddress: [{ required: true, message: '联系地址不能为空', trigger: 'blur' }],
});
const formRef = ref(null);
const editingId = ref(null);
const searchQuery = ref({ customerName: '', contactPhone: '' });

const currentPage = ref(1);
const pageSize = ref(8);

const fetchCustomerList = async () => {
  try {
    const response = await fetch('http://localhost:8090/customer/list');
    if (!response.ok) throw new Error(`HTTP 错误! 状态码: ${response.status}`);
    const data = await response.json();
    customerList.value = data;
  } catch (error) {
    console.error('获取客户列表失败:', error);
    ElMessage.error('获取客户列表失败');
  }
};

onMounted(() => {
  fetchCustomerList();
});

const filteredCustomerList = computed(() => {
  return customerList.value.filter(customer => {
    if (customer && customer.customerName && customer.contactPhone) {
      const nameMatch = customer.customerName.toLowerCase().includes(searchQuery.value.customerName.toLowerCase());
      const phoneMatch = customer.contactPhone.toLowerCase().includes(searchQuery.value.contactPhone.toLowerCase());
      return nameMatch && phoneMatch;
    }
    return false;
  });
});

const paginatedCustomerList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredCustomerList.value.slice(start, end);
});

const handlePageChange = (page) => {
  currentPage.value = page;
};

const handleSearch = () => {
  currentPage.value = 1;
};

const openDialog = () => {
  dialogTitle.value = '添加客户';
  form.value = { customerId: null, customerName: '', contactPhone: '', contactAddress: '' };
  editingId.value = null;
  formRef.value?.resetFields();
  dialogVisible.value = true;
};

const editItem = (row) => {
  dialogTitle.value = '编辑客户';
  form.value = { customerId: row.customerId, customerName: row.customerName, contactPhone: row.contactPhone, contactAddress: row.contactAddress };
  editingId.value = row.customerId;
  dialogVisible.value = true;
};

const deleteItem = async (row) => {
  try {
    const response = await fetch(`http://localhost:8090/customer/delete?id=${row.customerId}`, { method: 'GET' });
    if (!response.ok) throw new Error(`删除客户失败: ${response.status}`);
    ElMessage.success('删除成功');
    await fetchCustomerList();
  } catch (error) {
    console.error('删除客户失败:', error);
    ElMessage.error('删除客户失败');
  }
};

const saveItem = async () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        const payload = {
          customerId: editingId.value,
          customerName: form.value.customerName,
          contactPhone: form.value.contactPhone,
          contactAddress: form.value.contactAddress,
        };
        const url = editingId.value
            ? 'http://localhost:8090/customer/modify'
            : 'http://localhost:8090/customer/save';

        const response = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        });

        if (!response.ok) throw new Error(`保存客户失败: ${response.status}`);

        const newData = await response.json();
        if (editingId.value) {
          const index = customerList.value.findIndex(item => item.customerId === editingId.value);
          if (index !== -1) customerList.value[index] = newData;
          ElMessage.success('更新成功');
        } else {
          customerList.value = [...customerList.value, newData];
          ElMessage.success('添加成功');
        }

        dialogVisible.value = false;
        form.value = { customerId: null, customerName: '', contactPhone: '', contactAddress: '' };
        editingId.value = null;
        await fetchCustomerList();
      } catch (error) {
        console.error('保存客户失败:', error);
        ElMessage.error('保存客户失败');
      }
    } else {
      console.log('表单验证失败');
    }
  });
};
</script>

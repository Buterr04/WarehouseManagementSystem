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

    <el-table :data="filteredCustomerList" style="width: 100%">
      <el-table-column prop="customerId" label="ID" width="80"></el-table-column>
      <el-table-column prop="customerName" label="客户名称"></el-table-column>
      <el-table-column prop="contactPhone" label="联系电话"></el-table-column>
      <el-table-column prop="contactAddress" label="联系地址"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="editItem(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteItem(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

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

const customerList = ref([]); // 客户列表数据
const dialogVisible = ref(false); // 控制对话框显示/隐藏
const dialogTitle = ref('添加客户'); // 对话框标题
const form = ref({ // 表单数据对象
  customerId: null,
  customerName: '',
  contactPhone: '',
  contactAddress: '',
});
const formRules = ref({ // 表单验证规则
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
const formRef = ref(null); // 用于获取表单组件实例，方便进行验证和重置
const editingId = ref(null); // 正在编辑的客户ID
const searchQuery = ref({ // 搜索条件
  customerName: '',
  contactPhone: '',
});

// 获取客户列表数据
const fetchCustomerList = async () => {
  try {
    const response = await fetch('http://localhost:8090/customer/list');
    if (!response.ok) {
      throw new Error(`HTTP 错误! 状态码: ${response.status}`);
    }
    const data = await response.json();
    customerList.value = data;
  } catch (error) {
    console.error('获取客户列表失败:', error);
    ElMessage.error('获取客户列表失败');
  }
};

// 组件挂载后获取客户列表
onMounted(() => {
  fetchCustomerList();
});

// 计算属性：根据搜索条件过滤客户列表
const filteredCustomerList = computed(() => {
  return customerList.value.filter(customer => {
    if (customer && customer.customerName && customer.contactPhone) {
      const nameMatch = customer.customerName.toLowerCase().includes(searchQuery.value.customerName.toLowerCase());
      const phoneMatch = customer.contactPhone.toLowerCase().includes(searchQuery.value.contactPhone.toLowerCase());
      return nameMatch && phoneMatch;
    }
    return false; // 如果 customer 或其关键属性不存在，则不匹配
  });
});

// 处理搜索
const handleSearch = () => {
  // 这里我们直接使用前端过滤，你也可以选择在点击搜索按钮时调用后端模糊查询接口
  // 如果选择调用后端，你需要实现 performFuzzySearch 函数 (如之前的示例)
};

// 打开添加客户对话框
const openDialog = () => {
  dialogTitle.value = '添加客户';
  form.value = { customerId: null, customerName: '', contactPhone: '', contactAddress: '' };
  editingId.value = null;
  formRef.value?.resetFields(); // 重置表单验证状态
  dialogVisible.value = true;
};

// 编辑客户
const editItem = (row) => {
  dialogTitle.value = '编辑客户';
  form.value = { customerId: row.customerId, customerName: row.customerName, contactPhone: row.contactPhone, contactAddress: row.contactAddress };
  editingId.value = row.customerId;
  dialogVisible.value = true;
};

// 删除客户
const deleteItem = async (row) => {
  try {
    const response = await fetch(`http://localhost:8090/customer/delete?id=${row.customerId}`, {
      method: 'GET',
    });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(`删除客户失败: ${response.status} - ${errorData?.message || '未知错误'}`);
    }
    ElMessage.success('删除成功');
    await fetchCustomerList();
  } catch (error) {
    console.error('删除客户失败:', error);
    ElMessage.error('删除客户失败');
  }
};

// 保存客户信息（添加或编辑）
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
        const method = editingId.value ? 'POST' : 'POST';
        const url = editingId.value
            ? 'http://localhost:8090/customer/modify'
            : 'http://localhost:8090/customer/save';

        const response = await fetch(url, {
          method: method,
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(payload),
        });

        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(`保存客户失败: ${response.status} - ${errorData?.message || '未知错误'}`);
        }

        const newData = await response.json();
        if (editingId.value) {
          // 更新
          const index = customerList.value.findIndex(item => item.customerId === editingId.value);
          if (index !== -1) {
            customerList.value[index] = newData;
          }
          ElMessage.success('更新成功');
        } else {
          // 添加
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
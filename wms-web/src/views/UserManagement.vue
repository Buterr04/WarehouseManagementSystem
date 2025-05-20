<template>
  <div>
    <h1>用户管理</h1>

    <!-- 查询栏 -->
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchQuery.no" placeholder="按账号搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-input v-model="searchQuery.name" placeholder="按姓名搜索" clearable style="width: 180px; margin-right: 10px;" />
      <el-button type="primary" @click="fetchUsers">查询</el-button>
      <el-button @click="openUserDialog(null)">添加用户</el-button>
    </div>

    <!-- 用户表格 -->
    <el-table :data="filteredUsers" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="no" label="账号" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="roleId" label="角色">
        <template #default="scope">
          {{ roleMap[scope.row.roleId] || '未知' }}
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="age" label="年龄" />
      <el-table-column prop="sex" label="性别">
        <template #default="scope">
          {{ scope.row.sex === 0 ? '女' : '男' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="openUserDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑用户弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '添加用户'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="form.no" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleId" placeholder="选择角色">
            <el-option v-for="(label, key) in roleMap" :key="key" :label="label" :value="Number(key)" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="1" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUser">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const userList = ref([]);
const searchQuery = ref({ no: '', name: '' });
const dialogVisible = ref(false);
const form = ref({});

const roleMap = {
  0: '超级管理员',
  1: '销售员',
  2: '库存员',
  3: '采购员'
};

// 过滤搜索
const filteredUsers = computed(() => {
  return userList.value.filter(user => {
    return (!searchQuery.value.no || user.no.includes(searchQuery.value.no)) &&
        (!searchQuery.value.name || user.name.includes(searchQuery.value.name));
  });
});

// 查询用户
const fetchUsers = async () => {
  try {
    const res = await fetch('http://localhost:8090/user/list');
    const result = await res.json();
    if (result.code === 200) {
      userList.value = result.data || [];
    } else {
      ElMessage.error(result.msg || '获取用户失败');
    }
  } catch {
    ElMessage.error('请求失败');
  }
};

// 打开弹窗（新增或编辑）
const openUserDialog = (user) => {
  form.value = user ? { ...user } : { sex: 1, roleId: 1 };
  dialogVisible.value = true;
};

// 保存用户（新增或编辑）
// 保存用户（新增或编辑）
const submitUser = async () => {
  try {
    const url = form.value.id ? 'modify' : 'save';
    const res = await fetch(`http://localhost:8090/user/${url}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value)
    });
    const result = await res.json();
    if (result.code === 200) {
      ElMessage.success('保存成功');
      dialogVisible.value = false;
      fetchUsers();
    } else {
      ElMessage.error(result.msg || '保存失败');
    }
  } catch {
    ElMessage.error('请求失败');
  }
};

// 删除用户
const deleteUser = (id) => {
  ElMessageBox.confirm('确认删除该用户吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await fetch(`http://localhost:8090/user/delete?id=${id}`);
      const result = await res.json();
      if (result.code === 200) {
        ElMessage.success('删除成功');
        fetchUsers();
      } else {
        ElMessage.error(result.msg || '删除失败');
      }
    } catch {
      ElMessage.error('请求失败');
    }
  });
};

onMounted(fetchUsers);
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>

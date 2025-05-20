<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 style="text-align: center; margin-bottom: 20px;">用户登录</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="form.no" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item style="text-align: center;">
          <el-button type="primary" style="width: 100%;" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();
const form = ref({
  no: '',
  password: ''
});

const handleLogin = async () => {
  if (!form.value.no || !form.value.password) {
    return ElMessage.warning('请输入账号和密码');
  }

  try {
    const res = await fetch('http://localhost:8090/user/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value)
    });

    const data = await res.json();
    if (data.code === 200) {
      ElMessage.success('登录成功');
      localStorage.setItem('user', JSON.stringify(data.data));

      router.push('/').then(() => {
        location.reload(); //  强制刷新所有状态
      });
      return; //  登录成功后直接 return
    } else {
      ElMessage.error(data.msg || '登录失败');
    }
  } catch (e) {
    ElMessage.error('服务器请求失败');
  }
};

</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f5f5f5;
}

.login-card {
  width: 400px;
  padding: 20px;
}
</style>

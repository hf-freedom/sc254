<template>
  <div class="risk-control">
    <el-card shadow="hover">
      <template #header>风控名单</template>
      <el-empty v-if="riskUsers.length === 0" description="暂无风控用户" />
      <el-table v-else :data="riskUsers" border style="width: 100%">
        <el-table-column prop="userId" label="用户ID" width="300" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="removeFromRisk(row)">
              解除风控
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const riskUsers = ref([])

const loadRiskUsers = async () => {
  try {
    const res = await axios.get('/api/risk/list')
    if (res.data.success) {
      riskUsers.value = res.data.data.map(id => ({ userId: id }))
    }
  } catch (error) {
    ElMessage.error('加载失败')
  }
}

const removeFromRisk = async (user) => {
  try {
    await ElMessageBox.confirm(
      '确定要解除该用户的风控状态吗？',
      '确认解除',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    const res = await axios.post(`/api/risk/remove/${user.userId}`)
    if (res.data.success) {
      ElMessage.success('解除成功')
      loadRiskUsers()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadRiskUsers()
})
</script>

<style scoped>
.risk-control {
  max-width: 800px;
  margin: 0 auto;
}
</style>

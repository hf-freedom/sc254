<template>
  <div class="exchange">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>积分兑换商品</span>
          <div style="display: flex; align-items: center; gap: 10px">
            <el-alert
              v-if="selectedUser && selectedUser.owedPoints > 0"
              :title="'当前用户欠积分 ' + selectedUser.owedPoints + '，无法进行兑换'"
              type="warning"
              :closable="false"
              style="width: auto"
            />
            <el-select v-model="selectedUserId" placeholder="选择用户" style="width: 200px">
              <el-option
                v-for="user in users"
                :key="user.userId"
                :label="user.username + ' (可用:' + user.availablePoints + (user.owedPoints > 0 ? ', 欠:' + user.owedPoints : '') + ')'"
                :value="user.userId"
              />
            </el-select>
          </div>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col v-for="product in products" :key="product.productId" :span="8">
          <el-card shadow="hover" class="product-card">
            <div class="product-icon">
              <el-icon :size="50"><Goods /></el-icon>
            </div>
            <h3>{{ product.name }}</h3>
            <p class="points">{{ product.pointsRequired }} 积分</p>
            <p class="stock">库存: {{ product.stock }}</p>
            <el-button
              type="primary"
              :disabled="!selectedUserId || product.stock <= 0 || (selectedUser && selectedUser.owedPoints > 0)"
              @click="exchangeProduct(product)"
            >
              {{ selectedUser && selectedUser.owedPoints > 0 ? '欠积分无法兑换' : '立即兑换' }}
            </el-button>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Goods } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const products = ref([])
const users = ref([])
const selectedUserId = ref('')

const selectedUser = computed(() => {
  return users.value.find(u => u.userId === selectedUserId.value)
})

const loadProducts = async () => {
  try {
    const res = await axios.get('/api/exchange/products')
    if (res.data.success) {
      products.value = res.data.data
    }
  } catch (error) {
    ElMessage.error('加载商品失败')
  }
}

const loadUsers = async () => {
  try {
    const res = await axios.get('/api/users')
    if (res.data.success) {
      users.value = res.data.data
    }
  } catch (error) {}
}

const exchangeProduct = async (product) => {
  try {
    await ElMessageBox.confirm(
      `确定要花费 ${product.pointsRequired} 积分兑换 ${product.name} 吗？`,
      '确认兑换',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    const res = await axios.post('/api/exchange', {
      userId: selectedUserId.value,
      productId: product.productId
    })

    if (res.data.success) {
      ElMessage.success('兑换成功')
      loadProducts()
      loadUsers()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('兑换失败')
    }
  }
}

onMounted(() => {
  loadProducts()
  loadUsers()
})
</script>

<style scoped>
.exchange {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-card {
  text-align: center;
  margin-bottom: 20px;
}

.product-icon {
  color: #409eff;
  margin-bottom: 10px;
}

.product-card h3 {
  font-size: 18px;
  margin: 10px 0;
}

.points {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

.stock {
  color: #909399;
  margin: 10px 0;
}
</style>

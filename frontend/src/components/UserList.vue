<template>
  <div class="user-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <div class="header-buttons">
            <el-button type="danger" @click="goToRiskControl">
              <el-icon><Warning /></el-icon>
              风控名单
            </el-button>
            <el-button type="primary" @click="showCreateDialog">创建用户</el-button>
          </div>
        </div>
      </template>

      <el-table :data="users" border style="width: 100%">
        <el-table-column prop="username" label="用户名" width="180" />
        <el-table-column prop="availablePoints" label="可用积分" width="120" />
        <el-table-column prop="totalPoints" label="总积分" width="120" />
        <el-table-column label="会员等级" width="220">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 5px; flex-wrap: wrap">
              <el-tag :type="getLevelType(row.currentLevel.name)">
                {{ row.currentLevel.name }}
              </el-tag>
              <span v-if="row.levelExpireTime" class="expire-time">
                至 {{ formatDate(row.levelExpireTime) }}
              </span>
              <span v-else class="expire-time permanent">永久</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="等级状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="isLevelExpired(row)" type="danger" size="small">已过期</el-tag>
            <el-tag v-else-if="isLevelExpiringSoon(row)" type="warning" size="small">即将过期</el-tag>
            <el-tag v-else type="success" size="small">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="折扣" width="100">
          <template #default="{ row }">
            {{ (row.currentLevel.discount * 100).toFixed(0) }}折
          </template>
        </el-table-column>
        <el-table-column label="状态" width="280">
          <template #default="{ row }">
            <div style="display: flex; flex-wrap: wrap; gap: 5px">
              <el-tag v-if="row.inRiskControl" type="danger" size="small">
                <el-icon><Warning /></el-icon>
                风控中
              </el-tag>
              <el-tag v-if="row.owedPoints > 0" type="warning" size="small">
                欠积分{{ row.owedPoints }}
              </el-tag>
              <el-tag v-if="row.recentAbnormalPoints" type="danger" size="small" effect="plain">
                <el-icon><CircleClose /></el-icon>
                异常积分
              </el-tag>
              <el-tag v-if="!row.inRiskControl && row.owedPoints <= 0 && !row.recentAbnormalPoints" type="success" size="small">
                正常
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="350">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="doSignIn(row.userId)">
              签到
            </el-button>
            <el-button type="primary" size="small" @click="showOrderDialog(row.userId)">
              消费
            </el-button>
            <el-button type="warning" size="small" @click="showReviewDialog(row.userId)">
              评价
            </el-button>
            <el-button type="danger" size="small" @click="showRefundDialog(row.userId)">
              退单
            </el-button>
            <el-button type="info" size="small" @click="goToDetail(row.userId)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="createDialogVisible" title="创建用户" width="400">
      <el-form :model="newUser" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="newUser.username" placeholder="请输入用户名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createUser">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="orderDialogVisible" title="创建消费订单" width="400">
      <el-form :model="orderForm" label-width="80px">
        <el-form-item label="消费金额">
          <el-input-number v-model="orderForm.amount" :min="1" :max="10000" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="orderDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createOrder">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="reviewDialogVisible" title="评价订单" width="400">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="选择订单">
          <el-select v-model="reviewForm.orderId" placeholder="请选择订单" style="width: 100%">
            <el-option
              v-for="order in userOrders"
              :key="order.orderId"
              :label="'订单 ' + order.orderId.substring(0, 8) + ' - ' + order.amount + '元'"
              :value="order.orderId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" show-text />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="refundDialogVisible" title="退单" width="400">
      <el-form label-width="80px">
        <el-form-item label="选择订单">
          <el-select v-model="refundOrderId" placeholder="请选择订单" style="width: 100%">
            <el-option
              v-for="order in refundableOrders"
              :key="order.orderId"
              :label="'订单 ' + order.orderId.substring(0, 8) + ' - ' + order.amount + '元 (+' + order.pointsEarned + '积分)'"
              :value="order.orderId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitRefund">确定退单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Warning, CircleClose } from '@element-plus/icons-vue'

const router = useRouter()
const users = ref([])
const createDialogVisible = ref(false)
const newUser = ref({ username: '' })
const orderDialogVisible = ref(false)
const reviewDialogVisible = ref(false)
const refundDialogVisible = ref(false)
const orderForm = ref({ userId: '', amount: 100 })
const reviewForm = ref({ userId: '', orderId: '', rating: 5 })
const userOrders = ref([])
const refundableOrders = ref([])
const currentUserId = ref('')
const refundOrderId = ref('')

const loadUsers = async () => {
  try {
    const res = await axios.get('/api/users')
    if (res.data.success) {
      users.value = res.data.data
    }
  } catch (error) {
    ElMessage.error('加载用户失败')
  }
}

const showCreateDialog = () => {
  newUser.value.username = ''
  createDialogVisible.value = true
}

const createUser = async () => {
  if (!newUser.value.username) {
    ElMessage.warning('请输入用户名')
    return
  }
  try {
    const res = await axios.post('/api/users', newUser.value)
    if (res.data.success) {
      ElMessage.success('创建成功')
      createDialogVisible.value = false
      loadUsers()
    }
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const goToDetail = (userId) => {
  router.push(`/user/${userId}`)
}

const goToRiskControl = () => {
  router.push('/risk-control')
}

const getLevelType = (level) => {
  const types = {
    '青铜': 'info',
    '白银': 'warning',
    '黄金': 'success',
    '铂金': 'primary',
    '钻石': 'danger'
  }
  return types[level] || 'info'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const isLevelExpired = (user) => {
  if (!user.levelExpireTime) return false
  return new Date(user.levelExpireTime) < new Date()
}

const isLevelExpiringSoon = (user) => {
  if (!user.levelExpireTime) return false
  const expireDate = new Date(user.levelExpireTime)
  const now = new Date()
  const diffDays = Math.ceil((expireDate - now) / (1000 * 60 * 60 * 24))
  return diffDays > 0 && diffDays <= 7
}

const doSignIn = async (userId) => {
  currentUserId.value = userId
  try {
    const res = await axios.post('/api/points/signin', { userId })
    if (res.data.success) {
      ElMessage.success('签到成功，获得 ' + res.data.data.points + ' 积分')
      loadUsers()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('签到失败')
  }
}

const showOrderDialog = (userId) => {
  currentUserId.value = userId
  orderForm.value = { userId, amount: 100 }
  orderDialogVisible.value = true
}

const createOrder = async () => {
  try {
    const res = await axios.post('/api/orders', orderForm.value)
    if (res.data.success) {
      ElMessage.success('订单创建成功，获得 ' + res.data.data.pointsEarned + ' 积分')
      orderDialogVisible.value = false
      loadUsers()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('创建订单失败')
  }
}

const showReviewDialog = async (userId) => {
  currentUserId.value = userId
  reviewForm.value = { userId, orderId: '', rating: 5 }
  userOrders.value = []
  try {
    const res = await axios.get(`/api/orders/user/${userId}`)
    if (res.data.success) {
      userOrders.value = res.data.data.filter(o => !o.refunded)
      if (userOrders.value.length === 0) {
        ElMessage.warning('该用户暂无可评价订单')
        return
      }
      reviewDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载订单失败')
  }
}

const submitReview = async () => {
  if (!reviewForm.value.orderId) {
    ElMessage.warning('请选择订单')
    return
  }
  try {
    const res = await axios.post('/api/reviews', reviewForm.value)
    if (res.data.success) {
      ElMessage.success('评价成功，获得 ' + res.data.data.points + ' 积分')
      reviewDialogVisible.value = false
      loadUsers()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('评价失败')
  }
}

const showRefundDialog = async (userId) => {
  currentUserId.value = userId
  refundOrderId.value = ''
  refundableOrders.value = []
  try {
    const res = await axios.get(`/api/orders/user/${userId}`)
    if (res.data.success) {
      refundableOrders.value = res.data.data.filter(o => !o.refunded)
      if (refundableOrders.value.length === 0) {
        ElMessage.warning('该用户暂无可退订单')
        return
      }
      refundDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载订单失败')
  }
}

const submitRefund = async () => {
  if (!refundOrderId.value) {
    ElMessage.warning('请选择订单')
    return
  }
  try {
    const res = await axios.post(`/api/orders/refund/${refundOrderId.value}`)
    if (res.data.success) {
      ElMessage.success('退单成功，积分已扣回')
      refundDialogVisible.value = false
      loadUsers()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('退单失败')
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.user-list {
  max-width: 1400px;
  margin: 0 auto;
}

.expire-time {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.expire-time.permanent {
  color: #67c23a;
  font-weight: bold;
}
</style>

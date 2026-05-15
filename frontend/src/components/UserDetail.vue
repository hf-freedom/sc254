<template>
  <div class="user-detail">
    <el-button @click="$router.back()" style="margin-bottom: 20px">
      <el-icon><ArrowLeft /></el-icon> 返回
    </el-button>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>用户信息</template>
          <div v-if="user">
            <p><strong>用户名：</strong>{{ user.username }}</p>
            <p><strong>可用积分：</strong>{{ user.availablePoints }}</p>
            <p><strong>总积分：</strong>{{ user.totalPoints }}</p>
            <p><strong>已用积分：</strong>{{ user.usedPoints }}</p>
            <p><strong>欠积分：</strong>{{ user.owedPoints }}</p>
            <p><strong>会员等级：</strong>
              <el-tag :type="getLevelType(user.currentLevel.name)">
                {{ user.currentLevel.name }}
              </el-tag>
            </p>
            <p><strong>折扣：</strong>{{ (user.currentLevel.discount * 100).toFixed(0) }}折</p>
            <p v-if="user.levelExpireTime">
              <strong>等级到期：</strong>{{ user.levelExpireTime }}
            </p>
            <p><strong>状态：</strong>
              <el-tag v-if="user.inRiskControl" type="danger">风控中</el-tag>
              <el-tag v-else type="success">正常</el-tag>
            </p>
          </div>
        </el-card>

        <el-card shadow="hover" style="margin-top: 20px">
          <template #header>快捷操作</template>
          <el-button type="success" @click="showSignIn" :disabled="hasSignedIn">
            每日签到
          </el-button>
          <el-button type="primary" @click="showOrderDialog">消费订单</el-button>
          <el-button type="warning" @click="showReviewDialog">评价订单</el-button>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="积分记录" name="points">
            <el-table :data="pointsRecords" border style="width: 100%">
              <el-table-column prop="points" label="积分" width="100" />
              <el-table-column prop="source.description" label="来源" width="120" />
              <el-table-column prop="description" label="说明" />
              <el-table-column prop="createdAt" label="时间" width="180" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="订单记录" name="orders">
            <el-table :data="orders" border style="width: 100%">
              <el-table-column prop="amount" label="金额" width="120" />
              <el-table-column prop="pointsEarned" label="获得积分" width="120" />
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag v-if="row.refunded" type="danger">已退款</el-tag>
                  <el-tag v-else type="success">正常</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="时间" width="180" />
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button
                    v-if="!row.refunded"
                    type="danger"
                    size="small"
                    @click="refundOrder(row.orderId)"
                  >
                    退款
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="兑换记录" name="exchange">
            <el-table :data="exchangeRecords" border style="width: 100%">
              <el-table-column prop="productName" label="商品" width="200" />
              <el-table-column prop="pointsUsed" label="使用积分" width="120" />
              <el-table-column prop="status" label="状态" width="100" />
              <el-table-column prop="createdAt" label="时间" width="180" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>

    <el-dialog v-model="orderDialogVisible" title="创建订单" width="400">
      <el-form :model="orderForm" label-width="80px">
        <el-form-item label="金额">
          <el-input-number v-model="orderForm.amount" :min="1" :max="10000" />
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
          <el-select v-model="reviewForm.orderId" placeholder="请选择">
            <el-option
              v-for="order in reviewableOrders"
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userId = route.params.id

const user = ref(null)
const pointsRecords = ref([])
const orders = ref([])
const exchangeRecords = ref([])
const activeTab = ref('points')
const hasSignedIn = ref(false)
const orderDialogVisible = ref(false)
const reviewDialogVisible = ref(false)
const orderForm = ref({ amount: 100 })
const reviewForm = ref({ orderId: '', rating: 5 })

const reviewableOrders = computed(() => {
  return orders.value.filter(o => !o.refunded)
})

const loadUser = async () => {
  try {
    const res = await axios.get(`/api/users/${userId}`)
    if (res.data.success) {
      user.value = res.data.data
    }
  } catch (error) {
    ElMessage.error('加载用户失败')
  }
}

const checkSignIn = async () => {
  try {
    const res = await axios.get(`/api/points/signin/check/${userId}`)
    hasSignedIn.value = res.data.data
  } catch (error) {}
}

const loadPointsRecords = async () => {
  try {
    const res = await axios.get(`/api/points/records/${userId}`)
    if (res.data.success) {
      pointsRecords.value = res.data.data
    }
  } catch (error) {}
}

const loadOrders = async () => {
  try {
    const res = await axios.get(`/api/orders/user/${userId}`)
    if (res.data.success) {
      orders.value = res.data.data
    }
  } catch (error) {}
}

const loadExchangeRecords = async () => {
  try {
    const res = await axios.get(`/api/exchange/records/${userId}`)
    if (res.data.success) {
      exchangeRecords.value = res.data.data
    }
  } catch (error) {}
}

const showSignIn = async () => {
  try {
    const res = await axios.post('/api/points/signin', { userId })
    if (res.data.success) {
      ElMessage.success('签到成功，获得 ' + res.data.data.points + ' 积分')
      hasSignedIn.value = true
      loadUser()
      loadPointsRecords()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('签到失败')
  }
}

const showOrderDialog = () => {
  orderForm.value.amount = 100
  orderDialogVisible.value = true
}

const createOrder = async () => {
  try {
    const res = await axios.post('/api/orders', { userId, amount: orderForm.value.amount })
    if (res.data.success) {
      ElMessage.success('订单创建成功')
      orderDialogVisible.value = false
      loadUser()
      loadOrders()
      loadPointsRecords()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('创建订单失败')
  }
}

const refundOrder = async (orderId) => {
  try {
    const res = await axios.post(`/api/orders/refund/${orderId}`)
    if (res.data.success) {
      ElMessage.success('退款成功')
      loadUser()
      loadOrders()
      loadPointsRecords()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('退款失败')
  }
}

const showReviewDialog = () => {
  reviewForm.value = { orderId: '', rating: 5 }
  reviewDialogVisible.value = true
}

const submitReview = async () => {
  if (!reviewForm.value.orderId) {
    ElMessage.warning('请选择订单')
    return
  }
  try {
    const res = await axios.post('/api/reviews', {
      userId,
      orderId: reviewForm.value.orderId,
      rating: reviewForm.value.rating
    })
    if (res.data.success) {
      ElMessage.success('评价成功，获得 ' + res.data.data.points + ' 积分')
      reviewDialogVisible.value = false
      loadUser()
      loadPointsRecords()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('评价失败')
  }
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

onMounted(() => {
  loadUser()
  checkSignIn()
  loadPointsRecords()
  loadOrders()
  loadExchangeRecords()
})
</script>

<style scoped>
.user-detail {
  max-width: 1400px;
  margin: 0 auto;
}

.el-card p {
  margin: 10px 0;
  line-height: 1.8;
}
</style>

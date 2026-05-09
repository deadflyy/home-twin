<template>
  <view class="container">
    <view class="stats-header">
      <text class="header-title">📊 统计分析</text>
      <text class="header-subtitle">查看家中物品的详细统计</text>
    </view>

    <view class="summary-section">
      <view class="summary-card">
        <view class="summary-item">
          <text class="summary-icon">📦</text>
          <view class="summary-info">
            <text class="summary-value">{{ stats.totalItems }}</text>
            <text class="summary-label">总物品数</text>
          </view>
        </view>
        <view class="summary-item">
          <text class="summary-icon">✅</text>
          <view class="summary-info">
            <text class="summary-value active">{{ stats.activeItems }}</text>
            <text class="summary-label">使用中</text>
          </view>
        </view>
        <view class="summary-item">
          <text class="summary-icon">🗑️</text>
          <view class="summary-info">
            <text class="summary-value cleaned">{{ stats.cleanedItems }}</text>
            <text class="summary-label">已清理</text>
          </view>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">🏠 各房间物品数</text>
      </view>
      <view class="card">
        <view 
          v-for="room in roomStats" 
          :key="room.id" 
          class="stats-row"
          @click="goToRoom(room.id)"
        >
          <view class="stats-left">
            <text class="room-icon">{{ room.icon }}</text>
            <text class="room-name">{{ room.name }}</text>
          </view>
          <view class="stats-right">
            <view class="progress-bar">
              <view 
                class="progress-fill" 
                :style="{ width: getProgress(room.count) + '%', backgroundColor: room.color }"
              ></view>
            </view>
            <text class="stats-count">{{ room.count }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">👤 各人物物品数</text>
      </view>
      <view class="card">
        <view 
          v-for="person in personStats" 
          :key="person.id" 
          class="stats-row person-row"
        >
          <view class="stats-left">
            <text class="person-avatar">{{ person.avatar }}</text>
            <text class="person-name">{{ person.name }}</text>
          </view>
          <view class="stats-right">
            <view class="progress-bar">
              <view 
                class="progress-fill" 
                :style="{ width: getPersonProgress(person.count) + '%' }"
              ></view>
            </view>
            <text class="stats-count">{{ person.count }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">📈 使用状态占比</text>
      </view>
      <view class="card chart-card">
        <view class="pie-chart">
          <view class="pie-container">
            <view class="pie" :style="pieStyle"></view>
            <view class="pie-center">
              <text class="pie-value">{{ activePercent }}%</text>
              <text class="pie-label">使用中</text>
            </view>
          </view>
          <view class="pie-legend">
            <view class="legend-item">
              <view class="legend-dot active"></view>
              <text class="legend-text">使用中 ({{ stats.activeItems }})</text>
            </view>
            <view class="legend-item">
              <view class="legend-dot cleaned"></view>
              <text class="legend-text">已清理 ({{ stats.cleanedItems }})</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="action-section">
      <view class="action-btn" @click="goToAddItem">
        <text>➕ 添加新物品</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getStatistics, getRooms, getPersons, type Statistics, type Room, type Person } from '@/api'

const stats = ref<Statistics>({
  totalItems: 0,
  activeItems: 0,
  cleanedItems: 0,
  roomStats: {},
  personStats: {}
})

const rooms = ref<Room[]>([])
const persons = ref<Person[]>([])

const roomStats = computed(() => {
  return rooms.value.map(room => ({
    id: room.id,
    name: room.name,
    icon: room.icon,
    color: room.color,
    count: stats.value.roomStats[room.id] || 0
  })).sort((a, b) => b.count - a.count)
})

const personStats = computed(() => {
  return persons.value.map(person => ({
    id: person.id,
    name: person.name,
    avatar: person.avatar,
    count: stats.value.personStats[person.id] || 0
  })).sort((a, b) => b.count - a.count)
})

const maxRoomCount = computed(() => {
  const counts = roomStats.value.map(r => r.count)
  return counts.length > 0 ? Math.max(...counts) : 1
})

const maxPersonCount = computed(() => {
  const counts = personStats.value.map(p => p.count)
  return counts.length > 0 ? Math.max(...counts) : 1
})

const activePercent = computed(() => {
  if (stats.value.totalItems === 0) return 0
  return Math.round((stats.value.activeItems / stats.value.totalItems) * 100)
})

const pieStyle = computed(() => {
  const percent = activePercent.value
  return {
    background: `conic-gradient(#4A90D9 0deg ${percent * 3.6}deg, #67C23A ${percent * 3.6}deg 360deg)`
  }
})

onMounted(async () => {
  await loadData()
})

async function loadData() {
  try {
    stats.value = await getStatistics()
    rooms.value = await getRooms()
    persons.value = await getPersons()
  } catch (error) {
    console.error('加载统计数据失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

function getProgress(count: number): number {
  return (count / maxRoomCount.value) * 100
}

function getPersonProgress(count: number): number {
  return (count / maxPersonCount.value) * 100
}

function goToRoom(roomId: string) {
  uni.navigateTo({
    url: `/pages/room/room?id=${roomId}`
  })
}

function goToAddItem() {
  uni.navigateTo({
    url: '/pages/item/add'
  })
}
</script>

<style lang="scss" scoped>
.container {
  padding: 32rpx;
  padding-bottom: 180rpx;
  min-height: 100vh;
  background: linear-gradient(180deg, #4A90D9 0%, #6BA3E0 25%, #f5f5f5 25%);
}

.stats-header {
  padding: 100rpx 0 40rpx;
}

.header-title {
  font-size: 48rpx;
  font-weight: bold;
  color: #ffffff;
  display: block;
}

.header-subtitle {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 12rpx;
  display: block;
}

.summary-section {
  margin-bottom: 24rpx;
}

.summary-card {
  display: flex;
  background: #ffffff;
  border-radius: 20rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.summary-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.summary-icon {
  font-size: 48rpx;
  margin-bottom: 12rpx;
}

.summary-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.summary-value {
  font-size: 44rpx;
  font-weight: bold;
  color: #333333;

  &.active {
    color: #4A90D9;
  }

  &.cleaned {
    color: #67C23A;
  }
}

.summary-label {
  font-size: 24rpx;
  color: #999999;
  margin-top: 4rpx;
}

.section {
  margin-bottom: 24rpx;
}

.section-header {
  margin-bottom: 16rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.stats-row {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: #f8f9fa;
  }
}

.stats-left {
  flex: 1;
  display: flex;
  align-items: center;
}

.room-icon {
  font-size: 40rpx;
  margin-right: 16rpx;
}

.room-name {
  font-size: 30rpx;
  color: #333333;
}

.stats-right {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.progress-bar {
  width: 160rpx;
  height: 12rpx;
  background: #f0f0f0;
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 6rpx;
  transition: width 0.3s;
  
  &:not([style*='background-color']) {
    background: #4A90D9;
  }
}

.stats-count {
  font-size: 28rpx;
  color: #666666;
  font-weight: 500;
  min-width: 60rpx;
  text-align: right;
}

.person-avatar {
  font-size: 44rpx;
  margin-right: 16rpx;
}

.person-name {
  font-size: 30rpx;
  color: #333333;
}

.chart-card {
  padding: 32rpx;
}

.pie-chart {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.pie-container {
  position: relative;
  width: 240rpx;
  height: 240rpx;
  margin-bottom: 32rpx;
}

.pie {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.pie-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 160rpx;
  height: 160rpx;
  background: #ffffff;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.pie-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #4A90D9;
}

.pie-label {
  font-size: 22rpx;
  color: #999999;
}

.pie-legend {
  display: flex;
  gap: 32rpx;
}

.legend-item {
  display: flex;
  align-items: center;
}

.legend-dot {
  width: 20rpx;
  height: 20rpx;
  border-radius: 4rpx;
  margin-right: 12rpx;

  &.active {
    background: #4A90D9;
  }

  &.cleaned {
    background: #67C23A;
  }
}

.legend-text {
  font-size: 26rpx;
  color: #666666;
}

.action-section {
  margin-top: 24rpx;
}

.action-btn {
  background: linear-gradient(135deg, #4A90D9 0%, #6BA3E0 100%);
  color: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
  box-shadow: 0 4rpx 16rpx rgba(74, 144, 217, 0.3);
}
</style>

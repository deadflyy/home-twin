<template>
  <view class="container">
    <view class="header">
      <view class="header-title">
        <text class="title">🏠 家庭数字孪生</text>
        <text class="subtitle">管理家中的每一件物品</text>
      </view>
    </view>

    <view class="person-section card">
      <view class="section-header">
        <text class="section-title">人物分类</text>
      </view>
      <scroll-view scroll-x class="person-scroll">
        <view class="person-list">
          <view
            v-for="person in persons"
            :key="person.id"
            class="person-card"
            :class="{ active: selectedPerson === person.id }"
            @click="selectPerson(person.id)"
          >
            <text class="person-avatar">{{ person.avatar }}</text>
            <text class="person-name">{{ person.name }}</text>
            <text class="person-count">{{ person.itemCount }}件</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <view class="room-section">
      <view class="section-header">
        <text class="section-title">户型图</text>
        <text class="section-subtitle">点击光点进入房间</text>
      </view>

      <view class="fp-wrapper" :style="{ height: wrapperH + 'px' }">
        <view class="fp-scaler" :style="{ transform: 'scale(' + fpScale + ')' }">
          <view class="fp-container">
            <view class="fp-bg" />

            <view
              v-for="marker in markers"
              :key="marker.id"
              class="fp-marker"
              :class="{ 'is-active': activeMarker === marker.id }"
              :style="{ left: marker.cx + '%', top: marker.cy + '%', '--m-color': marker.color }"
              @click="goToRoom(marker.id)"
              @mouseenter="activeMarker = marker.id"
              @mouseleave="activeMarker = ''"
            >
              <view class="marker-pulse" />
              <view class="marker-dot" />
              <view class="marker-label">
                <text class="marker-icon">{{ marker.icon }}</text>
                <text class="marker-name">{{ marker.name }}</text>
                <text class="marker-count">{{ getRoomItemCount(marker.id) }}件</text>
              </view>

              <view v-if="activeMarker === marker.id" class="marker-tip">
                <text class="tip-name">{{ marker.name }}</text>
                <view class="tip-row">
                  <text class="tip-k">面积</text>
                  <text class="tip-v">{{ marker.area }}m²</text>
                </view>
                <view class="tip-row">
                  <text class="tip-k">物品</text>
                  <text class="tip-v">{{ getRoomItemCount(marker.id) }}件</text>
                </view>
                <view class="tip-row" v-if="marker.categories">
                  <text class="tip-k">分类</text>
                  <text class="tip-v">{{ marker.categories }}</text>
                </view>
                <text class="tip-go">点击查看 →</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="quick-actions card">
      <view class="section-header">
        <text class="section-title">快捷操作</text>
      </view>
      <view class="action-grid">
        <view class="action-item" @click="goToAddItem">
          <view class="action-icon">📷</view>
          <text class="action-text">拍照添加</text>
        </view>
        <view class="action-item" @click="goToAddItem">
          <view class="action-icon">✏️</view>
          <text class="action-text">手动添加</text>
        </view>
        <view class="action-item" @click="goToSearch">
          <view class="action-icon">🔍</view>
          <text class="action-text">查找物品</text>
        </view>
        <view class="action-item" @click="goToStatistics">
          <view class="action-icon">📊</view>
          <text class="action-text">统计分析</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getRooms, getPersons, type Room, type Person } from '@/api'

const PLAN_W = 1080
const PLAN_H = 829

interface Marker {
  id: string
  name: string
  icon: string
  area: number
  color: string
  categories: string
  cx: number
  cy: number
}

const markers: Marker[] = [
  {
    id: 'room-005', name: '', icon: '🍳', area: 8.4,
    color: '#E89A50', categories: '厨具、调料、餐具',
    cx: 26.8, cy: 19.8,
  },
  {
    id: 'room-006', name: '', icon: '🚿', area: 4.4,
    color: '#64A0DC', categories: '洗浴用品、清洁工具',
    cx: 33.2, cy: 33.5,
  },
  {
    id: 'room-001', name: '', icon: '🛏️', area: 13.8,
    color: '#A078C8', categories: '衣物、床品、收纳',
    cx: 33.2, cy: 57.5,
  },
  {
    id: 'room-004', name: '', icon: '🍽️', area: 9.2,
    color: '#D2AA5A', categories: '餐具、餐椅、装饰',
    cx: 55.8, cy: 33.5,
  },
  {
    id: 'room-003', name: '', icon: '🛋️', area: 16.6,
    color: '#50BEB4', categories: '沙发、电视、玩具',
    cx: 55.8, cy: 58.5,
  },
  {
    id: 'room-002', name: '', icon: '🌙', area: 10.4,
    color: '#DC8296', categories: '衣物、床品、书籍',
    cx: 74.2, cy: 33.5,
  },
  {
    id: 'room-007', name: '', icon: '🌿', area: 8.5,
    color: '#64BE82', categories: '绿植、晾衣架、储物',
    cx: 49.2, cy: 78.8,
  },
]

const rooms = ref<Room[]>([])
const persons = ref<Person[]>([])
const selectedPerson = ref<string>('')
const activeMarker = ref<string>('')
const screenWidth = ref(375)

const fpScale = computed(() => {
  const available = screenWidth.value - 48
  return Math.min(available / PLAN_W, 1)
})

const wrapperH = computed(() => {
  return PLAN_H * fpScale.value
})

const roomMap = computed(() => {
  const map: Record<string, Room> = {}
  rooms.value.forEach(room => {
    map[room.id] = room
  })
  return map
})

function getRoomItemCount(roomId: string): number {
  return roomMap.value[roomId]?.itemCount || 0
}

onMounted(async () => {
  const sysInfo = uni.getSystemInfoSync()
  screenWidth.value = sysInfo.windowWidth || 375
  await loadData()
})

async function loadData() {
  try {
    rooms.value = await getRooms()
    persons.value = await getPersons()
  } catch (error) {
    console.error('加载数据失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

function selectPerson(personId: string) {
  selectedPerson.value = selectedPerson.value === personId ? '' : personId
}

function goToRoom(roomId: string) {
  uni.navigateTo({
    url: `/pages/room/room?id=${roomId}`
  })
}

function goToAddItem() {
  uni.navigateTo({ url: '/pages/item/add' })
}

function goToSearch() {
  uni.switchTab({ url: '/pages/item/search' })
}

function goToStatistics() {
  uni.switchTab({ url: '/pages/statistics/statistics' })
}
</script>

<style lang="scss" scoped>
.container {
  padding: 0 32rpx;
  padding-bottom: 180rpx;
  background: linear-gradient(180deg, #4A90D9 0%, #6BA3E0 30%, #f5f5f5 30%);
  min-height: 100vh;
}

.header {
  padding: 80rpx 0 60rpx;
}

.header-title {
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 48rpx;
  font-weight: bold;
  color: #ffffff;
  text-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.1);
}

.subtitle {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 8rpx;
}

.card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
}

.person-section {
  margin-top: -30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
}

.section-subtitle {
  font-size: 22rpx;
  color: #999999;
}

.person-scroll {
  white-space: nowrap;
}

.person-list {
  display: inline-flex;
  gap: 16rpx;
}

.person-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 28rpx;
  background: #f8f9fa;
  border-radius: 14rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s;

  &.active {
    background: #e8f0fe;
    border-color: #4A90D9;
  }
}

.person-avatar {
  font-size: 48rpx;
  margin-bottom: 6rpx;
}

.person-name {
  font-size: 24rpx;
  color: #333333;
}

.person-count {
  font-size: 20rpx;
  color: #999999;
  margin-top: 4rpx;
}

.room-section {
  margin-top: 20rpx;
}

.fp-wrapper {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 16rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: center;
  overflow: hidden;
}

.fp-scaler {
  transform-origin: top center;
}

.fp-container {
  position: relative;
  width: 1080px;
  height: 829px;
  border-radius: 12px;
  overflow: hidden;
  background: #e8e8e8;
}

.fp-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url(/static/floor-plan.jpg) center / 100% 100% no-repeat;
}

.fp-marker {
  position: absolute;
  z-index: 5;
  cursor: pointer;
  transform: translate(-50%, -50%);

  .marker-pulse {
    position: absolute;
    width: 44px;
    height: 44px;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    border-radius: 50%;
    background: var(--m-color);
    opacity: 0.25;
    animation: pulse 2.5s ease-in-out infinite;
  }

  .marker-dot {
    position: relative;
    width: 18px;
    height: 18px;
    margin: 13px auto 0;
    border-radius: 50%;
    background: var(--m-color);
    box-shadow: 0 0 8px var(--m-color), 0 0 20px color-mix(in srgb, var(--m-color) 40%, transparent);
    transition: all 0.3s ease;
  }

  .marker-label {
    position: absolute;
    top: 52px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    white-space: nowrap;
    background: rgba(255, 255, 255, 0.92);
    backdrop-filter: blur(6px);
    padding: 4px 10px;
    border-radius: 10px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    pointer-events: none;
  }

  .marker-icon {
    font-size: 14px;
    line-height: 1;
  }

  .marker-name {
    font-size: 11px;
    font-weight: 700;
    color: #333;
    line-height: 1.3;
  }

  .marker-count {
    font-size: 9px;
    color: #999;
    line-height: 1.3;
  }

  .marker-tip {
    position: absolute;
    bottom: calc(100% + 6px);
    left: 50%;
    transform: translateX(-50%);
    background: rgba(255, 255, 255, 0.97);
    border-radius: 12px;
    padding: 12px 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
    z-index: 100;
    min-width: 160px;
    pointer-events: none;
    animation: tipIn 0.2s ease-out;

    &::after {
      content: '';
      position: absolute;
      top: 100%;
      left: 50%;
      transform: translateX(-50%);
      border: 6px solid transparent;
      border-top-color: rgba(255, 255, 255, 0.97);
    }
  }

  &:hover,
  &.is-active {
    z-index: 10;

    .marker-pulse {
      animation: pulse-active 1.2s ease-in-out infinite;
      opacity: 0.4;
    }

    .marker-dot {
      width: 22px;
      height: 22px;
      margin-top: 11px;
      box-shadow: 0 0 12px var(--m-color), 0 0 30px color-mix(in srgb, var(--m-color) 50%, transparent);
    }

    .marker-label {
      background: rgba(255, 255, 255, 0.97);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
    }
  }

  &:active {
    .marker-dot {
      transform: scale(0.85);
    }
  }
}

.tip-name {
  font-size: 14px;
  font-weight: 700;
  color: #333;
  margin-bottom: 6px;
}

.tip-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2px 0;
}

.tip-k {
  font-size: 11px;
  color: #999;
}

.tip-v {
  font-size: 11px;
  color: #333;
  font-weight: 500;
}

.tip-go {
  display: block;
  margin-top: 6px;
  font-size: 10px;
  color: #4A90D9;
  text-align: center;
}

@keyframes pulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.2;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.6);
    opacity: 0.08;
  }
}

@keyframes pulse-active {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.3;
  }
  50% {
    transform: translate(-50%, -50%) scale(2);
    opacity: 0.1;
  }
}

@keyframes tipIn {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(6px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

.quick-actions {
  margin-top: 20rpx;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 0;
  background: #f8f9fa;
  border-radius: 14rpx;

  &:active {
    background: #e8f0fe;
  }
}

.action-icon {
  font-size: 40rpx;
  margin-bottom: 8rpx;
}

.action-text {
  font-size: 22rpx;
  color: #666666;
}
</style>

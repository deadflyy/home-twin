<template>
  <view class="container">
    <view class="search-header">
      <view class="search-bar">
        <text class="search-icon">🔍</text>
        <input 
          v-model="keyword" 
          class="search-input" 
          placeholder="搜索物品名称、位置..."
          @confirm="handleSearch"
        />
        <text v-if="keyword" class="clear-icon" @click="clearSearch">✕</text>
      </view>
    </view>

    <view class="filter-section">
      <scroll-view scroll-x class="filter-scroll">
        <view class="filter-list">
          <view 
            class="filter-item"
            :class="{ active: !selectedRoom }"
            @click="selectRoom(null)"
          >
            <text>全部房间</text>
          </view>
          <view 
            v-for="room in rooms" 
            :key="room.id" 
            class="filter-item"
            :class="{ active: selectedRoom === room.id }"
            @click="selectRoom(room.id)"
          >
            <text>{{ room.icon }} {{ room.name }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <view v-if="isSearching" class="loading">
      <text>搜索中...</text>
    </view>

    <view v-else-if="items.length > 0" class="item-list">
      <view 
        v-for="item in items" 
        :key="item.id" 
        class="item-card"
        @click="goToDetail(item.id)"
      >
        <view class="item-photo-wrap">
          <image 
            v-if="item.photo" 
            :src="item.photo" 
            class="item-photo"
            mode="aspectFill"
          />
          <view v-else class="item-placeholder">
            <text class="placeholder-icon">📦</text>
          </view>
        </view>
        <view class="item-info">
          <text class="item-name">{{ item.name }}</text>
          <text class="item-room">{{ item.roomName }}</text>
          <text v-if="item.location" class="item-location">📍 {{ item.location }}</text>
        </view>
        <view class="item-status" :class="item.status">
          {{ item.status === 'active' ? '使用中' : '已清理' }}
        </view>
      </view>
    </view>

    <view v-else class="empty-state">
      <text class="empty-icon">🔍</text>
      <text class="empty-text">{{ keyword ? '未找到相关物品' : '输入关键词搜索物品' }}</text>
    </view>

    <view v-if="hasMore && items.length > 0" class="load-more">
      <text @click="loadMore">加载更多</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { searchItems, getRooms, type Room, type Item, type PageResponse } from '@/api'

const rooms = ref<Room[]>([])
const keyword = ref('')
const selectedRoom = ref('')
const items = ref<Item[]>([])
const page = ref(0)
const size = 10
const hasMore = ref(true)
const isSearching = ref(false)

onMounted(async () => {
  await loadRooms()
})

async function loadRooms() {
  try {
    rooms.value = await getRooms()
  } catch (error) {
    console.error('加载房间失败:', error)
  }
}

watch(keyword, () => {
  if (!keyword.value.trim()) {
    items.value = []
    page.value = 0
    hasMore.value = true
  }
})

async function handleSearch() {
  if (!keyword.value.trim()) return
  
  page.value = 0
  hasMore.value = true
  await doSearch()
}

async function doSearch(isLoadMore = false) {
  if (!keyword.value.trim()) return
  
  isSearching.value = true
  
  try {
    const currentPage = isLoadMore ? page.value + 1 : 0
    const res: PageResponse<Item> = await searchItems(keyword.value, selectedRoom.value || undefined, currentPage, size)
    
    if (isLoadMore) {
      items.value = [...items.value, ...res.content]
    } else {
      items.value = res.content
    }
    
    page.value = currentPage
    hasMore.value = currentPage < res.totalPages - 1
  } catch (error) {
    console.error('搜索失败:', error)
    uni.showToast({ title: '搜索失败', icon: 'none' })
  } finally {
    isSearching.value = false
  }
}

function selectRoom(roomId: string | null) {
  selectedRoom.value = roomId || ''
  page.value = 0
  hasMore.value = true
  if (keyword.value.trim()) {
    doSearch()
  }
}

function clearSearch() {
  keyword.value = ''
  items.value = []
  page.value = 0
  hasMore.value = true
}

function loadMore() {
  doSearch(true)
}

function goToDetail(itemId: string) {
  uni.navigateTo({
    url: `/pages/item/detail?id=${itemId}`
  })
}
</script>

<style lang="scss" scoped>
.container {
  padding: 0 32rpx;
  padding-bottom: 180rpx;
  min-height: 100vh;
  background: #f5f5f5;
}

.search-header {
  padding: 120rpx 0 24rpx;
}

.search-bar {
  display: flex;
  align-items: center;
  background: #ffffff;
  border-radius: 40rpx;
  padding: 0 28rpx;
  height: 88rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
}

.search-icon {
  font-size: 36rpx;
  margin-right: 16rpx;
}

.search-input {
  flex: 1;
  font-size: 30rpx;
}

.clear-icon {
  font-size: 32rpx;
  color: #999999;
  padding: 8rpx;
}

.filter-section {
  background: #ffffff;
  padding: 20rpx 0;
  margin: 0 -32rpx;
  padding-left: 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.filter-scroll {
  white-space: nowrap;
}

.filter-list {
  display: inline-flex;
  gap: 16rpx;
}

.filter-item {
  padding: 16rpx 28rpx;
  background: #f5f5f5;
  border-radius: 32rpx;
  font-size: 26rpx;
  color: #666666;
  border: 2rpx solid transparent;
  transition: all 0.3s;

  &.active {
    background: #e8f0fe;
    color: #4A90D9;
    border-color: #4A90D9;
  }
}

.loading {
  text-align: center;
  padding: 100rpx 0;
  font-size: 28rpx;
  color: #999999;
}

.item-list {
  padding: 20rpx 0;
}

.item-card {
  display: flex;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.item-photo-wrap {
  width: 140rpx;
  height: 140rpx;
  border-radius: 12rpx;
  overflow: hidden;
  flex-shrink: 0;
}

.item-photo {
  width: 100%;
  height: 100%;
}

.item-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 48rpx;
}

.item-info {
  flex: 1;
  padding: 0 24rpx;
  display: flex;
  flex-direction: column;
}

.item-name {
  font-size: 32rpx;
  font-weight: 500;
  color: #333333;
}

.item-room {
  font-size: 24rpx;
  color: #4A90D9;
  margin-top: 8rpx;
}

.item-location {
  font-size: 24rpx;
  color: #999999;
  margin-top: 4rpx;
}

.item-status {
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  align-self: flex-start;

  &.active {
    background: #e8f0fe;
    color: #4A90D9;
  }

  &.cleaned {
    background: #f0f9eb;
    color: #67C23A;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 100rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 32rpx;
  color: #999999;
}

.load-more {
  text-align: center;
  padding: 32rpx;
  font-size: 28rpx;
  color: #999999;
}
</style>

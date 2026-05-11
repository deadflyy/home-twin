<template>
  <view class="container">
    <view class="photo-section card">
      <view class="section-header">
        <text class="section-title">物品照片</text>
      </view>
      <view class="photo-upload" @click="chooseImage">
        <view v-if="!photo" class="upload-placeholder">
          <text class="upload-icon">📷</text>
          <text class="upload-text">点击拍照或上传照片</text>
        </view>
        <view v-else class="photo-wrapper">
          <image :src="photo" class="preview-photo" mode="aspectFill" />
          <view v-if="recognizing" class="recognizing-overlay">
            <view class="recognizing-spinner"></view>
            <text class="recognizing-text">AI 识别中...</text>
          </view>
        </view>
      </view>
      <view class="upload-tips">
        <text v-if="recognizing" class="tips-text tips-loading">🤖 AI 正在分析物品信息，请稍候...</text>
        <text v-else-if="recognized" class="tips-text tips-success">✅ AI 已识别完成，可手动修正以下信息</text>
        <text v-else class="tips-text">支持拍照或从相册选择，系统将自动识别物品</text>
      </view>
    </view>

    <view class="form-section card">
      <view class="form-item">
        <text class="form-label">物品名称 *</text>
        <input 
          v-model="form.name" 
          class="form-input" 
          placeholder="请输入物品名称"
          maxlength="100"
        />
      </view>

      <view class="form-item">
        <text class="form-label">物品类别</text>
        <input 
          v-model="form.category" 
          class="form-input" 
          placeholder="如：玩具、电子产品、衣物"
          maxlength="50"
        />
      </view>

      <view class="form-item">
        <text class="form-label">所属房间 *</text>
        <view class="form-picker" @click="showRoomPicker = true">
          <text class="picker-text">{{ selectedRoomName || '请选择房间' }}</text>
          <text class="picker-arrow">›</text>
        </view>
      </view>

      <view class="form-item">
        <text class="form-label">所属人物</text>
        <view class="form-picker" @click="showPersonPicker = true">
          <text class="picker-text">{{ selectedPersonName || '请选择（可选）' }}</text>
          <text class="picker-arrow">›</text>
        </view>
      </view>

      <view class="form-item">
        <text class="form-label">具体位置</text>
        <input 
          v-model="form.location" 
          class="form-input" 
          placeholder="如：书架第二层"
          maxlength="200"
        />
      </view>

      <view class="form-item">
        <text class="form-label">物品描述</text>
        <textarea 
          v-model="form.description" 
          class="form-textarea" 
          placeholder="请输入物品描述（可选）"
          maxlength="500"
        />
      </view>
    </view>

    <view class="submit-btn" @click="submitForm">
      <text>保存物品</text>
    </view>
  </view>

  <view v-if="showRoomPicker" class="picker-mask" @click="showRoomPicker = false">
    <view class="picker-content" @click.stop>
      <view class="picker-header">
        <text class="picker-title">选择房间</text>
        <text class="picker-close" @click="showRoomPicker = false">×</text>
      </view>
      <scroll-view scroll-y class="picker-list">
        <view 
          v-for="room in rooms" 
          :key="room.id" 
          class="picker-item"
          :class="{ active: form.roomId === room.id }"
          @click="selectRoom(room)"
        >
          <text class="room-icon">{{ room.icon }}</text>
          <text class="room-name">{{ room.name }}</text>
        </view>
      </scroll-view>
    </view>
  </view>

  <view v-if="showPersonPicker" class="picker-mask" @click="showPersonPicker = false">
    <view class="picker-content" @click.stop>
      <view class="picker-header">
        <text class="picker-title">选择人物</text>
        <text class="picker-close" @click="showPersonPicker = false">×</text>
      </view>
      <scroll-view scroll-y class="picker-list">
        <view 
          class="picker-item"
          :class="{ active: !form.personId }"
          @click="selectPerson(null)"
        >
          <text class="room-icon">✕</text>
          <text class="room-name">不选择</text>
        </view>
        <view 
          v-for="person in persons" 
          :key="person.id" 
          class="picker-item"
          :class="{ active: form.personId === person.id }"
          @click="selectPerson(person)"
        >
          <text class="room-icon">{{ person.avatar }}</text>
          <text class="room-name">{{ person.name }}</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getRooms, getPersons, createItem, type Room, type Person } from '@/api'
import { recognizeItem, imageFilePathToBase64, type RecognizeResult } from '@/utils/mimo'

const rooms = ref<Room[]>([])
const persons = ref<Person[]>([])
const photo = ref('')
const recognizing = ref(false)
const recognized = ref(false)
const showRoomPicker = ref(false)
const showPersonPicker = ref(false)

const form = ref({
  name: '',
  roomId: '',
  personId: '',
  location: '',
  description: '',
  category: ''
})

const selectedRoomName = computed(() => {
  if (!form.value.roomId) return ''
  return rooms.value.find(r => r.id === form.value.roomId)?.name || ''
})

const selectedPersonName = computed(() => {
  if (!form.value.personId) return ''
  return persons.value.find(p => p.id === form.value.personId)?.name || ''
})

const ROOM_KEYWORDS: Record<string, string[]> = {
  '主卧': ['主卧', '卧室B', '父母'],
  '次卧': ['次卧', '卧室A', '小孩', '儿童'],
  '客厅': ['客厅'],
  '餐厅': ['餐厅'],
  '厨房': ['厨房'],
  '卫生间': ['卫生间', '浴室', '洗手间'],
  '阳台': ['阳台']
}

const PERSON_KEYWORDS: Record<string, string[]> = {
  '小女孩': ['小孩', '儿童', '女孩', '小朋友', '女儿', '小孩'],
  '父亲': ['父亲', '爸爸', '男人', '男性'],
  '母亲': ['母亲', '妈妈', '女人', '女性'],
  '外婆': ['外婆', '姥姥', '老人', '老年']
}

function matchRoomId(hint: string): string | undefined {
  if (!hint) return undefined
  for (const [roomName, keywords] of Object.entries(ROOM_KEYWORDS)) {
    if (hint.includes(roomName) || keywords.some(k => hint.includes(k))) {
      const room = rooms.value.find(r => r.name.includes(roomName))
      if (room) return room.id
    }
  }
  const matched = rooms.value.find(r => hint.includes(r.name))
  return matched?.id
}

function matchPersonId(hint: string): string | undefined {
  if (!hint) return undefined
  if (hint.includes('共用') || hint.includes('通用')) return undefined
  for (const [personName, keywords] of Object.entries(PERSON_KEYWORDS)) {
    if (hint.includes(personName) || keywords.some(k => hint.includes(k))) {
      const person = persons.value.find(p => p.name.includes(personName))
      if (person) return person.id
    }
  }
  const matched = persons.value.find(p => hint.includes(p.name))
  return matched?.id
}

onLoad((options) => {
  if (options?.roomId) {
    form.value.roomId = options.roomId as string
  }
})

onMounted(async () => {
  await loadRooms()
  await loadPersons()
})

async function loadRooms() {
  try {
    rooms.value = await getRooms()
  } catch (error) {
    console.error('加载房间失败:', error)
  }
}

async function loadPersons() {
  try {
    persons.value = await getPersons()
  } catch (error) {
    console.error('加载人物失败:', error)
  }
}

function chooseImage() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const filePath = res.tempFilePaths[0]
      photo.value = filePath
      recognized.value = false
      await startRecognize(filePath)
    }
  })
}

async function startRecognize(filePath: string) {
  recognizing.value = true
  try {
    const base64 = await imageFilePathToBase64(filePath)
    const result = await recognizeItem(base64)
    fillFormFromResult(result)
    recognized.value = true
    uni.showToast({ title: '识别完成', icon: 'success' })
  } catch (error) {
    console.error('AI 识别失败:', error)
    uni.showToast({ title: '识别失败，请手动填写', icon: 'none' })
  } finally {
    recognizing.value = false
  }
}

function fillFormFromResult(result: RecognizeResult) {
  if (result.name) form.value.name = result.name
  if (result.description) form.value.description = result.description
  if (result.category) form.value.category = result.category
  if (result.locationHint) form.value.location = result.locationHint

  if (!form.value.roomId && result.locationHint) {
    const matchedRoomId = matchRoomId(result.locationHint)
    if (matchedRoomId) form.value.roomId = matchedRoomId
  }

  if (!form.value.personId && result.personHint) {
    const matchedPersonId = matchPersonId(result.personHint)
    if (matchedPersonId) form.value.personId = matchedPersonId
  }
}

function selectRoom(room: Room) {
  form.value.roomId = room.id
  showRoomPicker.value = false
}

function selectPerson(person: Person | null) {
  form.value.personId = person?.id || ''
  showPersonPicker.value = false
}

async function submitForm() {
  if (!form.value.name.trim()) {
    uni.showToast({ title: '请输入物品名称', icon: 'none' })
    return
  }
  if (!form.value.roomId) {
    uni.showToast({ title: '请选择所属房间', icon: 'none' })
    return
  }

  try {
    uni.showLoading({ title: '保存中...' })
    
    await createItem({
      name: form.value.name.trim(),
      description: form.value.description.trim() || undefined,
      roomId: form.value.roomId,
      personId: form.value.personId || undefined,
      location: form.value.location.trim() || undefined,
      photo: photo.value || undefined
    })

    uni.hideLoading()
    uni.showToast({ title: '保存成功', icon: 'success' })
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    uni.hideLoading()
    console.error('保存失败:', error)
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}
</script>

<style lang="scss" scoped>
.container {
  padding: 20rpx 32rpx;
  padding-bottom: 160rpx;
  min-height: 100vh;
  background: #f5f5f5;
}

.card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.section-header {
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.photo-section {
  margin-top: 20rpx;
}

.photo-upload {
  width: 100%;
  height: 320rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background: #f8f9fa;
}

.photo-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-icon {
  font-size: 80rpx;
  margin-bottom: 16rpx;
}

.upload-text {
  font-size: 28rpx;
  color: #999999;
}

.preview-photo {
  width: 100%;
  height: 100%;
}

.recognizing-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.recognizing-spinner {
  width: 64rpx;
  height: 64rpx;
  border: 6rpx solid rgba(255, 255, 255, 0.3);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16rpx;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.recognizing-text {
  font-size: 26rpx;
  color: #ffffff;
}

.upload-tips {
  margin-top: 16rpx;
}

.tips-text {
  font-size: 24rpx;
  color: #999999;
}

.tips-loading {
  color: #4A90D9;
}

.tips-success {
  color: #67C23A;
}

.form-section {
  margin-top: 20rpx;
}

.form-item {
  margin-bottom: 28rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.form-label {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 12rpx;
  display: block;
}

.form-input {
  width: 100%;
  height: 88rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
  box-sizing: border-box;
}

.form-textarea {
  width: 100%;
  height: 200rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 24rpx;
  font-size: 30rpx;
  box-sizing: border-box;
}

.form-picker {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 0 24rpx;
}

.picker-text {
  font-size: 30rpx;
  color: #333333;
}

.picker-arrow {
  font-size: 36rpx;
  color: #cccccc;
}

.submit-btn {
  position: fixed;
  bottom: 32rpx;
  left: 32rpx;
  right: 32rpx;
  background: linear-gradient(135deg, #4A90D9 0%, #6BA3E0 100%);
  color: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
  box-shadow: 0 4rpx 16rpx rgba(74, 144, 217, 0.3);
}

.picker-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 100;
}

.picker-content {
  width: 100%;
  background: #ffffff;
  border-radius: 24rpx 24rpx 0 0;
  max-height: 70vh;
}

.picker-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.picker-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #333333;
}

.picker-close {
  font-size: 48rpx;
  color: #999999;
  line-height: 1;
}

.picker-list {
  max-height: 60vh;
}

.picker-item {
  display: flex;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #f5f5f5;

  &.active {
    background: #e8f0fe;
  }
}

.room-icon {
  font-size: 44rpx;
  margin-right: 20rpx;
}

.room-name {
  font-size: 32rpx;
  color: #333333;
}
</style>

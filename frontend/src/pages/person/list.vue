<template>
  <view class="container">
    <view class="header">
      <view class="header-title">
        <text class="title">👥 人物管理</text>
        <text class="subtitle">管理家庭成员头像</text>
      </view>
      <view class="add-btn" @click="showAddModal">
        <text class="add-icon">+</text>
      </view>
    </view>

    <view class="person-list">
      <view
        v-for="person in persons"
        :key="person.id"
        class="person-card"
      >
        <view class="avatar-wrapper" @click="editAvatar(person)">
          <image
            v-if="person.avatar && person.avatar.startsWith('http')"
            :src="person.avatar"
            class="avatar-image"
            mode="aspectFill"
          />
          <view v-else class="avatar-emoji">{{ person.avatar || '👤' }}</view>
          <view class="avatar-edit">
            <text class="edit-icon">✏️</text>
          </view>
        </view>
        <view class="person-info">
          <text class="person-name">{{ person.name }}</text>
          <text class="person-relation">{{ person.relation || '家庭成员' }}</text>
        </view>
        <view class="person-stats">
          <text class="item-count">{{ person.itemCount }}</text>
          <text class="item-label">件物品</text>
        </view>
        <view class="person-actions">
          <view class="action-btn" @click="editPerson(person)">
            <text>编辑</text>
          </view>
          <view class="action-btn delete" @click="confirmDelete(person)">
            <text>删除</text>
          </view>
        </view>
      </view>

      <view v-if="persons.length === 0" class="empty-state">
        <text class="empty-icon">👥</text>
        <text class="empty-text">暂无人物，点击上方 + 添加</text>
      </view>
    </view>

    <view v-if="showModal" class="modal-mask" @click="closeModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ editingPerson ? '编辑人物' : '添加人物' }}</text>
          <text class="modal-close" @click="closeModal">×</text>
        </view>

        <view class="avatar-upload-section">
          <view class="avatar-upload" @click="chooseAvatar">
            <image
              v-if="form.avatar && form.avatar.startsWith('http')"
              :src="form.avatar"
              class="preview-avatar"
              mode="aspectFill"
            />
            <view v-else class="avatar-placeholder">
              <text class="placeholder-icon">{{ form.avatar || '👤' }}</text>
            </view>
            <view class="upload-overlay">
              <text class="upload-text">点击更换</text>
            </view>
          </view>
          <view class="emoji-hint">
            <text>或输入表情符号</text>
          </view>
        </view>

        <view class="form-group">
          <text class="form-label">姓名 *</text>
          <input
            v-model="form.name"
            class="form-input"
            placeholder="请输入姓名"
            maxlength="20"
          />
        </view>

        <view class="form-group">
          <text class="form-label">关系</text>
          <input
            v-model="form.relation"
            class="form-input"
            placeholder="如：父亲、母亲、小女孩"
            maxlength="20"
          />
        </view>

        <view class="emoji-selector">
          <text class="emoji-label">快捷表情</text>
          <view class="emoji-grid">
            <text
              v-for="emoji in emojiOptions"
              :key="emoji"
              class="emoji-option"
              :class="{ active: form.avatar === emoji }"
              @click="form.avatar = emoji"
            >{{ emoji }}</text>
          </view>
        </view>

        <view class="modal-footer">
          <view class="cancel-btn" @click="closeModal">
            <text>取消</text>
          </view>
          <view class="save-btn" @click="savePerson">
            <text>{{ editingPerson ? '保存' : '添加' }}</text>
          </view>
        </view>
      </view>
    </view>

    <view v-if="showAvatarPicker" class="modal-mask" @click="showAvatarPicker = false">
      <view class="modal-content avatar-picker-modal" @click.stop>
        <view class="modal-header">
          <text class="modal-title">设置头像</text>
          <text class="modal-close" @click="showAvatarPicker = false">×</text>
        </view>

        <view class="avatar-type-tabs">
          <view
            class="tab-btn"
            :class="{ active: avatarTab === 'emoji' }"
            @click="avatarTab = 'emoji'"
          >
            <text>表情</text>
          </view>
          <view
            class="tab-btn"
            :class="{ active: avatarTab === 'upload' }"
            @click="avatarTab = 'upload'"
          >
            <text>上传</text>
          </view>
        </view>

        <view v-if="avatarTab === 'emoji'" class="emoji-list">
          <text
            v-for="emoji in emojiOptions"
            :key="emoji"
            class="emoji-item"
            :class="{ active: tempAvatar === emoji }"
            @click="tempAvatar = emoji"
          >{{ emoji }}</text>
        </view>

        <view v-if="avatarTab === 'upload'" class="upload-section">
          <view class="upload-preview" @click="chooseImage">
            <image
              v-if="tempAvatar && tempAvatar.startsWith('http')"
              :src="tempAvatar"
              class="preview-image"
              mode="aspectFill"
            />
            <view v-else class="upload-placeholder">
              <text class="upload-icon">📷</text>
              <text class="upload-text">点击上传图片</text>
            </view>
          </view>
        </view>

        <view class="modal-footer">
          <view class="cancel-btn" @click="showAvatarPicker = false">
            <text>取消</text>
          </view>
          <view class="save-btn" @click="confirmAvatar">
            <text>确认</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPersons, createPerson, updatePerson, deletePerson, type Person } from '@/api'
import { uploadAvatar as uploadAvatarApi } from '@/utils/upload'

const persons = ref<Person[]>([])
const showModal = ref(false)
const showAvatarPicker = ref(false)
const editingPerson = ref<Person | null>(null)
const avatarTab = ref<'emoji' | 'upload'>('emoji')
const tempAvatar = ref('')
const currentEditPerson = ref<Person | null>(null)

const emojiOptions = [
  '👤', '👨', '👩', '👴', '👵',
  '👦', '👧', '👶', '🧒',
  '🦸', '🦹', '🧙', '🧚',
  '🐱', '🐶', '🐰', '🦊',
  '🐼', '🐨', '🐯', '🦁',
  '😀', '😎', '🥰', '🤩',
  '🌟', '💫', '✨', '🔥',
  '🎮', '🎨', '📚', '🎵'
]

const form = ref({
  name: '',
  relation: '',
  avatar: ''
})

onMounted(async () => {
  await loadPersons()
})

async function loadPersons() {
  try {
    persons.value = await getPersons()
  } catch (error) {
    console.error('加载人物失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

function showAddModal() {
  editingPerson.value = null
  form.value = { name: '', relation: '', avatar: '' }
  showModal.value = true
}

function editPerson(person: Person) {
  editingPerson.value = person
  form.value = {
    name: person.name,
    relation: person.relation || '',
    avatar: person.avatar || ''
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingPerson.value = null
}

async function savePerson() {
  if (!form.value.name.trim()) {
    uni.showToast({ title: '请输入姓名', icon: 'none' })
    return
  }

  try {
    uni.showLoading({ title: '保存中...' })

    if (editingPerson.value) {
      await updatePerson(editingPerson.value.id, {
        name: form.value.name.trim(),
        relation: form.value.relation.trim() || undefined,
        avatar: form.value.avatar || undefined
      })
      uni.showToast({ title: '更新成功', icon: 'success' })
    } else {
      await createPerson({
        name: form.value.name.trim(),
        relation: form.value.relation.trim() || undefined,
        avatar: form.value.avatar || undefined
      })
      uni.showToast({ title: '添加成功', icon: 'success' })
    }

    uni.hideLoading()
    closeModal()
    await loadPersons()
  } catch (error) {
    uni.hideLoading()
    console.error('保存失败:', error)
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}

function editAvatar(person: Person) {
  currentEditPerson.value = person
  tempAvatar.value = person.avatar || ''
  avatarTab.value = 'emoji'
  showAvatarPicker.value = true
}

function chooseAvatar() {
  showAvatarPicker.value = true
  currentEditPerson.value = null
  tempAvatar.value = form.value.avatar || ''
  avatarTab.value = 'emoji'
}

async function chooseImage() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const filePath = res.tempFilePaths[0]
      try {
        uni.showLoading({ title: '上传中...' })
        const url = await uploadAvatarApi(filePath)
        tempAvatar.value = url
        uni.hideLoading()
      } catch (error) {
        uni.hideLoading()
        console.error('上传失败:', error)
        uni.showToast({ title: '上传失败', icon: 'none' })
      }
    }
  })
}

async function confirmAvatar() {
  if (currentEditPerson.value) {
    try {
      await updatePerson(currentEditPerson.value.id, { avatar: tempAvatar.value })
      await loadPersons()
    } catch (error) {
      console.error('更新头像失败:', error)
    }
  } else {
    form.value.avatar = tempAvatar.value
  }
  showAvatarPicker.value = false
}

function confirmDelete(person: Person) {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除 "${person.name}" 吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await deletePerson(person.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          await loadPersons()
        } catch (error) {
          console.error('删除失败:', error)
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 50%, #f5f5f5 50%);
  padding: 0 32rpx;
  padding-bottom: 120rpx;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 80rpx 0 40rpx;
}

.header-title {
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 48rpx;
  font-weight: bold;
  color: #ffffff;
  text-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
}

.subtitle {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 8rpx;
}

.add-btn {
  width: 88rpx;
  height: 88rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.4);
}

.add-icon {
  font-size: 48rpx;
  color: #667eea;
  line-height: 1;
}

.person-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.person-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.avatar-wrapper {
  position: relative;
  width: 120rpx;
  height: 120rpx;
  flex-shrink: 0;
}

.avatar-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 24rpx;
  object-fit: cover;
}

.avatar-emoji {
  width: 120rpx;
  height: 120rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60rpx;
}

.avatar-edit {
  position: absolute;
  bottom: -8rpx;
  right: -8rpx;
  width: 48rpx;
  height: 48rpx;
  background: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
}

.edit-icon {
  font-size: 24rpx;
}

.person-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.person-name {
  font-size: 34rpx;
  font-weight: 600;
  color: #333333;
}

.person-relation {
  font-size: 24rpx;
  color: #999999;
  margin-top: 4rpx;
}

.person-stats {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 20rpx;
}

.item-count {
  font-size: 40rpx;
  font-weight: 700;
  color: #667eea;
}

.item-label {
  font-size: 20rpx;
  color: #999999;
}

.person-actions {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.action-btn {
  padding: 10rpx 20rpx;
  background: #f0f2f5;
  border-radius: 12rpx;
  font-size: 24rpx;
  color: #666666;

  &.delete {
    color: #ff4d4f;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  opacity: 0.5;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
  margin-top: 24rpx;
}

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 32rpx;
}

.modal-content {
  width: 100%;
  max-width: 600rpx;
  background: #ffffff;
  border-radius: 32rpx;
  padding: 40rpx 32rpx;
}

.avatar-picker-modal {
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;
}

.modal-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
}

.modal-close {
  font-size: 56rpx;
  color: #cccccc;
  line-height: 1;
}

.avatar-upload-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32rpx;
}

.avatar-upload {
  width: 180rpx;
  height: 180rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 32rpx;
  overflow: hidden;
  position: relative;
}

.preview-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 80rpx;
}

.upload-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  padding: 12rpx;
  text-align: center;
}

.upload-text {
  font-size: 22rpx;
  color: #ffffff;
}

.emoji-hint {
  margin-top: 12rpx;
  font-size: 22rpx;
  color: #999999;
}

.form-group {
  margin-bottom: 28rpx;
}

.form-label {
  font-size: 26rpx;
  color: #666666;
  margin-bottom: 12rpx;
  display: block;
}

.form-input {
  width: 100%;
  height: 88rpx;
  background: #f5f6f7;
  border-radius: 16rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
  box-sizing: border-box;
}

.emoji-selector {
  margin-bottom: 32rpx;
}

.emoji-label {
  font-size: 26rpx;
  color: #666666;
  margin-bottom: 16rpx;
  display: block;
}

.emoji-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.emoji-option {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f6f7;
  border-radius: 16rpx;
  font-size: 36rpx;
  transition: all 0.2s;

  &.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    transform: scale(1.1);
  }
}

.avatar-type-tabs {
  display: flex;
  background: #f5f6f7;
  border-radius: 16rpx;
  padding: 6rpx;
  margin-bottom: 24rpx;
}

.tab-btn {
  flex: 1;
  padding: 16rpx;
  text-align: center;
  font-size: 28rpx;
  color: #666666;
  border-radius: 12rpx;
  transition: all 0.2s;

  &.active {
    background: #ffffff;
    color: #667eea;
    font-weight: 600;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
  }
}

.emoji-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  padding: 16rpx 0;
  max-height: 400rpx;
  overflow-y: auto;
}

.emoji-item {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f6f7;
  border-radius: 20rpx;
  font-size: 44rpx;
  transition: all 0.2s;

  &.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    transform: scale(1.1);
  }
}

.upload-section {
  padding: 16rpx 0;
}

.upload-preview {
  width: 240rpx;
  height: 240rpx;
  background: #f5f6f7;
  border-radius: 24rpx;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.upload-icon {
  font-size: 64rpx;
  margin-bottom: 12rpx;
}

.upload-text {
  font-size: 24rpx;
  color: #999999;
}

.modal-footer {
  display: flex;
  gap: 24rpx;
  margin-top: 32rpx;
}

.cancel-btn {
  flex: 1;
  height: 96rpx;
  background: #f5f6f7;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #666666;
}

.save-btn {
  flex: 1;
  height: 96rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 500;
}
</style>
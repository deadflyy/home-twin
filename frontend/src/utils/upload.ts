const BASE_URL = 'http://localhost:8080/api'

export async function uploadAvatar(filePath: string): Promise<string> {
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: `${BASE_URL}/upload/avatar`,
      filePath,
      name: 'file',
      success: (res) => {
        if (res.statusCode === 200) {
          try {
            const data = JSON.parse(res.data)
            if (data.code === 200 && data.data.url) {
              resolve(data.data.url)
            } else {
              reject(new Error(data.message || '上传失败'))
            }
          } catch (e) {
            reject(new Error('解析响应失败'))
          }
        } else {
          reject(new Error(`上传失败 (${res.statusCode})`))
        }
      },
      fail: (err) => {
        reject(new Error(err.errMsg || '上传失败'))
      }
    })
  })
}

export async function uploadAvatarBase64(base64: string): Promise<string> {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${BASE_URL}/upload/avatar/base64`,
      method: 'POST',
      header: {
        'Content-Type': 'application/json',
      },
      data: JSON.stringify({ image: base64 }),
      success: (res) => {
        if (res.statusCode === 200) {
          const data = res.data as { code: number; data: { url: string }; message?: string }
          if (data.code === 200 && data.data.url) {
            resolve(data.data.url)
          } else {
            reject(new Error(data.message || '上传失败'))
          }
        } else {
          reject(new Error(`上传失败 (${res.statusCode})`))
        }
      },
      fail: (err) => {
        reject(new Error(err.errMsg || '上传失败'))
      }
    })
  })
}

export function imageFilePathToBase64(filePath: string): Promise<string> {
  return new Promise((resolve, reject) => {
    plus.io.convertLocalFileSystemURL(filePath)
    uni.getFileInfo({
      filePath,
      success: (info) => {
        const fs = plus.io.convertLocalFileSystemURL(filePath)
        if (!fs) {
          resolve('')
          return
        }
        const reader = new plus.io.FileReader()
        reader.onloadend = (e) => {
          const result = e.target?.result as string
          if (result) {
            const base64 = result.split(',')[1]
            resolve(base64 || '')
          } else {
            resolve('')
          }
        }
        reader.onerror = () => reject(new Error('读取文件失败'))
        reader.readAsDataURL(fs)
      },
      fail: () => resolve('')
    })
  })
}

export function getImageInfo(filePath: string): Promise<{ width: number; height: number }> {
  return new Promise((resolve, reject) => {
    uni.getImageInfo({
      src: filePath,
      success: (info) => {
        resolve({ width: info.width, height: info.height })
      },
      fail: () => reject(new Error('获取图片信息失败'))
    })
  })
}

export function compressImage(
  filePath: string,
  maxWidth = 800,
  maxHeight = 800
): Promise<string> {
  return new Promise((resolve, reject) => {
    uni.compressImage({
      src: filePath,
      quality: 80,
      success: (res) => {
        resolve(res.tempFilePath)
      },
      fail: () => {
        resolve(filePath)
      }
    })
  })
}
const BASE_URL = 'http://localhost:8080/api'

interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export async function request<T>(
  url: string,
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' = 'GET',
  data?: Record<string, unknown>
): Promise<ApiResponse<T>> {
  const options: UniApp.RequestOptions = {
    url: `${BASE_URL}${url}`,
    method,
    header: {
      'Content-Type': 'application/json',
    },
    data: data ? JSON.stringify(data) : undefined,
  }

  return new Promise((resolve, reject) => {
    uni.request({
      ...options,
      success: (res) => {
        if (res.statusCode !== 200) {
          let msg = `请求失败 (${res.statusCode})`
          try {
            const body = res.data as ApiResponse<T>
            if (body && body.message) msg = body.message
          } catch {}
          reject(new Error(msg))
          return
        }
        const response = res.data as ApiResponse<T>
        if (response.code === 200) {
          resolve(response)
        } else {
          reject(new Error(response.message || '请求失败'))
        }
      },
      fail: (err) => {
        reject(new Error(err.errMsg || '网络错误'))
      },
    })
  })
}

export async function get<T>(url: string, params?: Record<string, unknown>): Promise<ApiResponse<T>> {
  if (params) {
    const queryString = new URLSearchParams(params as Record<string, string>).toString()
    url += `?${queryString}`
  }
  return request<T>(url, 'GET')
}

export async function post<T>(url: string, data?: Record<string, unknown>): Promise<ApiResponse<T>> {
  return request<T>(url, 'POST', data)
}

export async function put<T>(url: string, data?: Record<string, unknown>): Promise<ApiResponse<T>> {
  return request<T>(url, 'PUT', data)
}

export async function del<T>(url: string): Promise<ApiResponse<T>> {
  return request<T>(url, 'DELETE')
}

import OpenAI from 'openai'

const MIMO_API_KEY = 'sk-c9f073ubqnq2dnnj7v4botd9z761dwe9rlqpi9uxpinnvhv0'

const client = new OpenAI({
  apiKey: MIMO_API_KEY,
  baseURL: 'https://api.xiaomimimo.com/v1',
  dangerouslyAllowBrowser: true,
})

export interface RecognizeResult {
  name: string
  category: string
  description: string
  personHint: string
  locationHint: string
}

const SYSTEM_PROMPT = `你是一个家庭物品识别助手。用户会给你一张家庭物品的照片，你需要识别并返回以下信息：
1. name: 物品名称（简短，如"乐高积木"、"遥控器"）
2. category: 物品类别（如"玩具"、"电子产品"、"衣物"、"书籍"、"食品"、"日用品"、"家具"、"其他"）
3. description: 物品简要描述（一句话）
4. personHint: 推断该物品最可能属于谁（如"小女孩"、"父亲"、"母亲"、"外婆"、"家庭共用"）
5. locationHint: 推断该物品最可能放在哪个房间（如"主卧"、"次卧"、"客厅"、"餐厅"、"厨房"、"卫生间"、"阳台"）

家庭成员信息：小女孩（4岁）、父亲、母亲、外婆。

请以JSON格式返回，不要包含markdown代码块标记。示例：
{"name":"乐高积木","category":"玩具","description":"一套彩色塑料拼装积木","personHint":"小女孩","locationHint":"次卧"}`

export async function recognizeItem(imageBase64: string): Promise<RecognizeResult> {
  const response = await client.chat.completions.create({
    model: 'mimo-v2.5',
    messages: [
      {
        role: 'system',
        content: SYSTEM_PROMPT,
      },
      {
        role: 'user',
        content: [
          {
            type: 'image_url',
            image_url: {
              url: imageBase64.startsWith('data:')
                ? imageBase64
                : `data:image/jpeg;base64,${imageBase64}`,
            },
          },
          {
            type: 'text',
            text: '请识别这张图片中的物品，返回JSON格式结果。',
          },
        ],
      },
    ],
    max_completion_tokens: 1024,
  })

  const content = response.choices[0]?.message?.content || ''

  const jsonMatch = content.match(/\{[\s\S]*\}/)
  if (!jsonMatch) {
    throw new Error('AI 返回格式异常')
  }

  return JSON.parse(jsonMatch[0]) as RecognizeResult
}

export function imageFilePathToBase64(filePath: string): Promise<string> {
  return new Promise((resolve, reject) => {
    // #ifdef H5
    fetch(filePath)
      .then((res) => res.blob())
      .then((blob) => {
        const reader = new FileReader()
        reader.onloadend = () => {
          const result = reader.result as string
          const base64 = result.split(',')[1] || result
          resolve(base64)
        }
        reader.onerror = reject
        reader.readAsDataURL(blob)
      })
      .catch(reject)
    // #endif

    // #ifdef MP-WEIXIN
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const fs = uni.getFileSystemManager() as any
    fs.readFile({
      filePath,
      encoding: 'base64',
      success: (res: any) => resolve(String(res.data)),
      fail: (err: any) => reject(err),
    })
    // #endif
  })
}

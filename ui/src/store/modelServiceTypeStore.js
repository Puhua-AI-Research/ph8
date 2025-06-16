import { create } from 'zustand'

const servieTypeMap = {
  textConv: 'llm',
  imageGenerate: 'text2img',
  videoGenerate: 'text2video',
  imageUnderstand: 'vllm',
}

const servieTypeDisplayMap = {
  textConv: '文本对话',
  imageGenerate: '文本生图',
  videoGenerate: '视频生成',
  imageUnderstand: '图像理解',
}

export const getModelCategory = (serviceType) => (servieTypeMap[serviceType])

export const getModelCategoryDisplay = (serviceType) => (servieTypeDisplayMap[serviceType])

export const getServiceTypeFromModelCategory = (modelCategory) => {
  for (const [key, value] of Object.entries(servieTypeMap)) {
    if (value === modelCategory) return key
  }
}


export const useModelServiceTypeStore = create(
  (set, get) => ({
    serviceType: 'textConv',
    setServiceType: (serviceType) => set({ serviceType }),
  })
)
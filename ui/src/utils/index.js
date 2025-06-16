import dayjs from 'dayjs';

export function formatTime(ts) {
  if (!ts) {
    return ''
  }

  return dayjs(ts).format('YYYY-MM-DD HH:mm:ss');
}

export const defaultAllModelCategory = [
  'all', 'text2img', 'llm', 'text2video', 'vllm'
]

export const modelCategoryMap = {
  'all': '全部',
  'llm': '大语言模型',
  'text2img': '生图模型',
  'text2video': '视频模型',
  'vllm': '图像理解'
}

export const chargeModeMap = {
  'call_times': '按次数计费',
  'fee': '付费',
  'free': '免费',
  'token_fee': '每千token价格 * 数量',
  'image_fee': '每张图价格 * 张数',
}

export const changeTypeMap = {
  'token_cost': '消费',
  'recharge': '充值',
}

export const businessModeMap = {
  'time_based': '计时消耗',
  'recharge': '充值',
}

export function formatJson(text) {
  try {
    return JSON.stringify(JSON.parse(text), null, 2);
  } catch (e) { }
  return text
}

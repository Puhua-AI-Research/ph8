import request from './index'

const modelListUrl = '/app-api/ai/modelRepository/list'
const textConvUrl = '/app-api/ai/chat/completion'
const vllmConvUrl = '/app-api/ai/vllm/completion'
const uploadFilePreSignUrl = '/app-api/infra/file/presigned-url'
const generateImageUrl = '/app-api/ai/image/generate'
const generateVideoUrl = '/app-api/ai/video/generate'
const generateVideoV2Url = '/app-api/ai/video/generations'
const queryVideoUrl = '/app-api/ai/video/get'

export function getModelListApi({ type }) {
  return request.post(modelListUrl, { type })
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function textConvApi(params) {
  return request.post(textConvUrl, params)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function uploadFilePreSignApi({ filename, scene }) {
  return request.post(uploadFilePreSignUrl, {filename, scene})
  .then(res => res.data)
  .catch(err => console.error(err))
}

export function vllmConvApi(params) {
  return request.post(vllmConvUrl, params)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function generateImageApi(params) {
  return request.post(generateImageUrl, params)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function generateVideoApi(params) {
  return request.post(generateVideoUrl, params)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function generateVideoApiV2(params) {
  return request.post(generateVideoV2Url, params)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function queryVideoApi({ id }) {
  return request.get(queryVideoUrl + `?id=${id}`)
    .then(res => res.data)
    .catch(err => console.error(err))
}

import request from './index'

const modelGalleryListUrl = '/app-api/ai/modelRepository/square'
const modelTagListUrl = '/app-api/ai/api-tags/list'
const modelTagListV2Url = '/app-api/ai/api-tags/newList'

export function getModelGalleryListApi({ type, tags }) {
  return request.post(modelGalleryListUrl, { type, tags })
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function getModelTagListApi() {
  return request.get(modelTagListUrl)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function getModelTagListV2Api() {
  return request.get(modelTagListV2Url)
    .then(res => res.data)
    .catch(err => console.error(err))
}

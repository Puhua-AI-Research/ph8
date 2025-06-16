import request from './index'

const apiKeyListUrl = '/app-api/ai/apiKey/list'
const createApiKeyUrl = '/app-api/ai/apiKey/create'
const updateApiKeyUrl = '/app-api/ai/apiKey/update'
const deleteApiKeyUrl = '/app-api/ai/apiKey/delete'

export function apiKeyListApi() {
  return request.get(apiKeyListUrl)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function createApiKeyApi({ name }) {
  return request.post(createApiKeyUrl, { name })
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function updateApiKeyApi({ id, name, status }) {
  return request.put(updateApiKeyUrl, { id, name, status })
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function deleteApiKeyApi({ id }) {
  return request.delete(deleteApiKeyUrl, {
    params: { id }
  })
    .then(res => res.data)
    .catch(err => console.error(err))
}



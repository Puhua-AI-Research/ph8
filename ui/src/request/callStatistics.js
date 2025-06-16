import request from './index'
import qs from 'qs';

const taskStatisticsUrl = '/app-api/ai/task/statistics'
const taskListUrl = '/app-api/ai/task/list'

export function taskStatisticsApi() {
  return request.get(taskStatisticsUrl)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function taskListApi({ pageNo, pageSize, ...params } = {}) {
  const queryString = qs.stringify({ pageNo, pageSize, ...params });
  return request.get(`${taskListUrl}?${queryString}`)
    .then(res => res.data)
    .catch(err => console.error(err))
}

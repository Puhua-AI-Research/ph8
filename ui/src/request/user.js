import request from './index'
import qs from 'qs';

const getUserInfoUrl = '/app-api/member/user/userInfo'
const editUserInfoUrl = '/app-api/member/user/update'
const getUserBalanceLogUrl = '/app-api/member/balance-log/page'

export function getUserInfoApi() {
  return request.get(getUserInfoUrl, {
    timeout: 1000 * 10,
  })
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function editUserInfoApi(params) {
  return request.put(editUserInfoUrl, params)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function getUserBalanceLogApi({ pageNo, pageSize, ...params } = {}) {
  const queryString = qs.stringify({ pageNo, pageSize, ...params });
  return request.get(`${getUserBalanceLogUrl}?${queryString}`)
    .then(res => res.data)
    .catch(err => console.error(err))
}

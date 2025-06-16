import request from './index'

const getProductListUrl = '/app-api/member/quota-product/list'
const createOrderUrl = '/app-api/member/order/create'
const checkOrderUrl = '/app-api/member/order/check'

export function getProductListApi() {
  return request.get(getProductListUrl)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function createOrderApi({ spuId, payChannelCode }) {
  return request.post(createOrderUrl, { spuId, payChannelCode })
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function checkOrderApi(orderId) {
  return request.get(checkOrderUrl + `?id=${orderId}`)
    .then(res => res.data)
    .catch(err => console.error(err))
}

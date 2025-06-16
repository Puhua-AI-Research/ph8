import request from './index'

const passwordLoginUrl = '/app-api/member/auth/login'
const mobileCodeLoginUrl = '/app-api/member/auth/captchaLogin'
const getMobileCodeUrl = '/app-api/member/user/sendMobileSms'
const mobileRegisterUrl = '/app-api/member/user/smsRegister'

const getGzhQrCodeUrl = '/app-api/member/auth/getGzhQrCode'
const checkGzhScanResultUrl = '/app-api/member/auth/checkGzhLogin/'
const gzhBindMobileLoginUrl = '/app-api/member/auth/gzhBindMobileLogin'
const logoutUrl = '/app-api/member/auth/logout'
const removeUserUrl = '/app-api/member/user/cancel'

export function logoutApi() {
  return request.post(logoutUrl)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function passwordLoginApi({ mobile, password }) {
  return request.post(passwordLoginUrl, { mobile, password })
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function mobileCodeLoginApi({ mobile, code }) {
  return request.post(mobileCodeLoginUrl, { mobile, code })
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function getMobileCodeApi({ mobile, scene = 1 }) {
  return request.post(getMobileCodeUrl, { mobile, scene })
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function mobileRegisterApi({ mobile, code, password }) {
  return request.post(mobileRegisterUrl, { mobile, code, password })
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function getGzhQrCodeApi() {
  return request.get(getGzhQrCodeUrl)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function checkGzhScanResultApi(ticket) {
  return request.get(checkGzhScanResultUrl + ticket)
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function gzhBindMobileLoginApi({ mobile, code, ticket }) {
  return request.post(gzhBindMobileLoginUrl, { mobile, code, ticket })
    .then(res => res.data)
    .catch(err => console.error(err))
}

export function removeUserApi({ code }) {
  return request.post(removeUserUrl, { code })
    .then(res => res.data)
    .catch(err => console.error(err))
}
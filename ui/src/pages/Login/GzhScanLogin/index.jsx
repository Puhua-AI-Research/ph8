import { useGlobalStore } from '@/store/useGlobalStore'
import { App, Form, message } from 'antd';
import styles from './index.less'
import { useRequest } from 'ahooks';
import { checkGzhScanResultApi, getGzhQrCodeApi } from '@/request/login'
import MobileLogin from '../MobileLogin'
import { useState } from 'react';
import { QRCodeSVG } from 'qrcode.react';
import { ACCESS_TOKEN } from "@/constant";

export default () => {
  const { loginType, setLoginType, ticket, setTicket } = useGlobalStore()
  const [qrcodeUrl, setQrcodeUrl] = useState(null)
  const [qrcodeInValid, setQrcodeInValid] = useState(false)

  const { run: getGzhQrCode } = useRequest(getGzhQrCodeApi, {
    onBefore: () => {
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      setQrcodeUrl(data.qrcodeUrl)
      setTicket(data.ticket)
      setQrcodeInValid(false)
    },
  });

  useRequest(() => checkGzhScanResultApi(ticket), {
    ready: !!ticket && !qrcodeInValid,
    pollingInterval: 2500,
    onBefore: () => {
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0 && code !== 1002000005) {
        // 状态码异常，不再轮询
        setQrcodeInValid(true)
        message.warning(msg)
        return
      }
      if (code === 1002000005) {
        message.warning(msg)
        // 扫码成功，跳转到绑定手机号页面
        setLoginType('gzhBindMobile')
        return
      }
      // 登录的判断放前面，expired会被同时设置
      if (code === 0 && data?.loginOk === true) {
        message.success('登录成功')
        setLoginType('')
        localStorage.setItem(ACCESS_TOKEN, data.accessToken)
        setTimeout(() => {
          window.location.reload()
        }, 500);
      }
      if (code === 0 && data?.expired === true) {
        // 结果返回过期，不再轮询
        setQrcodeInValid(true)
        return
      }
    },
  });

  return (
    <>
      <div className={styles.container}>
        <div className={styles.mobileLoginContainer}>
          <MobileLogin />
        </div>

        <div className={styles.scanContainer}>
          <div className={styles.title}>
            微信扫码登录
          </div>

          <div className={styles.notes}>
            未注册的微信号将自动创建账号
          </div>

          <div className={styles.qrcodeContainer}>
            <div className={styles.qrcode}>
              <QRCodeSVG
                value={qrcodeUrl}
                size={200}
                marginSize={2}
              />
            </div>

            {
              qrcodeInValid && <div className={styles.qrcodeMask} onClick={() => getGzhQrCode()}>二维码失效<br />点击重新生成</div>
            }
          </div>

          <div className={styles.agree}>
            【扫码登陆】代表同意<a href='https://m1r239or5aw.feishu.cn/wiki/GEaqw6R71i9IVMkpsmLcJdY0nKd?from=from_copylink' target='__blank'>《用户协议》</a> <a href='https://m1r239or5aw.feishu.cn/wiki/Hk5qwG8Aki65xQkhsnmca8W3nKg?from=from_copylink' target='__blank'>《隐私协议》</a>
          </div>
        </div>
      </div>
    </>
  );
}

import { useGlobalStore } from '@/store/useGlobalStore'
import { useMemo } from 'react';
import { App, Button, Checkbox, Form, Input, message } from 'antd';
import styles from './index.less'
import { mobileRegex, passwordRegex } from '@/constant'
import { useRequest } from 'ahooks';
import { passwordLoginApi } from '@/request/login'
import WechatSvg from '@/assets/webchat.svg';
import { ACCESS_TOKEN } from "@/constant";

export default () => {
  const { loginType, setLoginType } = useGlobalStore()

  const onFinish = (values) => {
    if (!values.agree) return message.error('请先阅读并同意《用户协议》和《隐私协议》')
    if (!values.mobile) return message.error('请输入手机号')
    if (!values.password) return message.error('请输入密码')

    if (!mobileRegex.test(values.mobile)) return message.error('请输入正确的手机号');
    // if (!passwordRegex.test(values.password)) return message.error('密码格式不正确，密码需不少于8位，至少包含字母和数字');

    requestLogin(values)
  };

  const { loading: loging, run: requestLogin } = useRequest(passwordLoginApi, {
    manual: true,
    onBefore: () => {
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      message.success('登录成功')
      setLoginType('')
      localStorage.setItem(ACCESS_TOKEN, data.accessToken)
      setTimeout(() => {
        window.location.reload()
      }, 500);
    },
  });

  return (
    <>
      <Form
        name="passwordLoginForm"
        className={styles.loginForm}
        layout='vertical'
        initialValues={{
          agree: false,
        }}
        onFinish={onFinish}
        autoComplete="off"
      >
        <Form.Item
          label="手机号"
          name="mobile"
        >
          <Input placeholder='请输入手机号' />
        </Form.Item>

        <Form.Item
          label="密码"
          name="password"
        >
          <Input.Password placeholder='请输入密码' />
        </Form.Item>

        <Form.Item name="agree" valuePropName="checked" label={null}>
          <Checkbox>
            <div className={styles.agree}>
              我已阅读并同意<a href='https://m1r239or5aw.feishu.cn/wiki/GEaqw6R71i9IVMkpsmLcJdY0nKd?from=from_copylink' target='__blank'>《用户协议》</a>和<a href='https://m1r239or5aw.feishu.cn/wiki/Hk5qwG8Aki65xQkhsnmca8W3nKg?from=from_copylink' target='__blank'>《隐私协议》</a>
            </div>
          </Checkbox>
        </Form.Item>

        <Form.Item label={null}>
          <Button type="primary" htmlType="submit" style={{ width: '100%' }} loading={loging}>
            登录
          </Button>
        </Form.Item>
      </Form>

      <div className={styles.footer}>
        <div className={styles.gzhScan} onClick={() => setLoginType('gzhScanLogin')}>
          <img src={WechatSvg} alt="" />
          <span>微信扫码</span>
        </div>

        <div className={styles.regWrapper}>
          <div className={styles.reg} onClick={() => setLoginType('register')}>
            注册账号
          </div>
          <div className={styles.forgetPassword}>
            忘记密码?
          </div>
        </div>
      </div>
    </>
  );
}

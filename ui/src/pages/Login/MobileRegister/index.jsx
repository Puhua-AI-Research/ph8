import { useGlobalStore } from '@/store/useGlobalStore'
import { useMemo, useEffect, useState } from 'react';
import { App, Button, Checkbox, Form, Input, message } from 'antd';
import styles from './index.less'
import { mobileRegex, passwordRegex } from '@/constant'
import { useRequest } from 'ahooks';
import { mobileRegisterApi, getMobileCodeApi } from '@/request/login'
import WechatSvg from '@/assets/webchat.svg';
import { ACCESS_TOKEN } from "@/constant";

export default () => {
  const { loginType, setLoginType } = useGlobalStore()
  const [hasMobile, setHasMobile] = useState(false)
  const [form] = Form.useForm();

  const [countdown, setCountdown] = useState(0);
  const [isCounting, setIsCounting] = useState(false);

  useEffect(() => {
    let timer;

    if (isCounting && countdown > 0) {
      timer = setTimeout(() => {
        setCountdown(countdown - 1);
      }, 1000);
    } else if (countdown === 0) {
      setIsCounting(false);
    }

    return () => clearTimeout(timer);
  }, [countdown, isCounting]);

  const startCountdown = () => {
    setIsCounting(true);
    setCountdown(60);
  }

  const onFinish = (values) => {
    if (!values.agree) return message.error('请先阅读并同意《用户协议》和《隐私协议》')
    if (!values.mobile) return message.error('请输入手机号')
    if (!values.password) return message.error('请输入密码')
    if (!values.code) return message.error('请输入验证码')

    if (!mobileRegex.test(values.mobile)) return message.error('请输入正确的手机号');
    if (!passwordRegex.test(values.password)) return message.error('密码格式不正确，密码需不少于8位，至少包含字母和数字');

    mobileRegister(values)
  };

  const { loading: getMobileCodeLoading, run: getMobileCode } = useRequest(getMobileCodeApi, {
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
      message.info('验证码已发送，请注意查收')
    },
  });

  const { loading: registering, run: mobileRegister } = useRequest(mobileRegisterApi, {
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
      message.success('注册成功')
      setLoginType('login')
    },
  });

  const onValuesChange = (changedValues) => {
    if ('mobile' in changedValues) {
      if (mobileRegex.test(changedValues.mobile)) {
        setHasMobile(true)
      } else {
        setHasMobile(false)
      }
    }
  };

  const handleSendCode = () => {
    if (!hasMobile || isCounting) return
    startCountdown()
    getMobileCode({ mobile: form.getFieldValue('mobile') })
  }

  return (
    <>
      <Form
        name="basic"
        form={form}
        className={styles.loginForm}
        onValuesChange={onValuesChange}
        layout='vertical'
        initialValues={{
          agree: false,
        }}
        onFinish={onFinish}
        autoComplete="off"
      >
        <div className={styles.title}>
          欢迎注册PH08
        </div>
        <Form.Item
          label="手机号"
          name="mobile"
        >
          <Input placeholder='请输入手机号' />
        </Form.Item>

        <Form.Item
          label="输入密码"
          name="password"
        >
          <Input.Password placeholder='请输入密码' />
        </Form.Item>

        <Form.Item
          label="确认密码"
          name="confirmPassword"
          dependencies={['password']}
          rules={[
            {
              required: true,
              message: '请再次输入密码'
            },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('password') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(new Error('两次输入密码不一致!'));
              },
            }),
          ]}
        >
          <Input.Password placeholder='请输入密码' />
        </Form.Item>

        <Form.Item
          label="验证码"
          name="code"
        >
          <Input
            placeholder="请输入验证码"
            suffix={
              <Button color="cyan" variant="link" onClick={handleSendCode} disabled={!hasMobile || isCounting} loading={getMobileCodeLoading} style={{ height: '20px' }}>
                {isCounting ? `${countdown}秒后重新发送` : '发送验证码'}
              </Button>
            }
          />
        </Form.Item>

        <Form.Item name="agree" valuePropName="checked" label={null}>
          <Checkbox>
            <div className={styles.agree}>
              我已阅读并同意<a href='https://m1r239or5aw.feishu.cn/wiki/GEaqw6R71i9IVMkpsmLcJdY0nKd?from=from_copylink' target='__blank'>《用户协议》</a>和<a href='https://m1r239or5aw.feishu.cn/wiki/Hk5qwG8Aki65xQkhsnmca8W3nKg?from=from_copylink' target='__blank'>《隐私协议》</a>
            </div>
          </Checkbox>
        </Form.Item>

        <Form.Item label={null}>
          <Button type="primary" htmlType="submit" style={{ width: '100%' }} loading={registering}>
            注册
          </Button>
        </Form.Item>
      </Form>

      <div className={styles.footer}>
        <div className={styles.gzhScan} onClick={() => setLoginType('gzhScanLogin')}>
          <img src={WechatSvg} alt="" />
          <span>微信扫码</span>
        </div>

        <div className={styles.loginWrapper}>
          <div className={styles.login} onClick={() => setLoginType('login')}>
            登录账号
          </div>
        </div>
      </div>
    </>
  );
}

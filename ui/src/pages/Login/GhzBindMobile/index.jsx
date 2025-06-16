import { useGlobalStore } from '@/store/useGlobalStore'
import { useMemo, useEffect, useState } from 'react';
import { App, Button, Checkbox, Form, Input, message } from 'antd';
import styles from './index.less'
import { mobileRegex, passwordRegex } from '@/constant'
import { useRequest } from 'ahooks';
import { gzhBindMobileLoginApi, getMobileCodeApi } from '@/request/login'
import WechatSvg from '@/assets/webchat.svg';
import { ACCESS_TOKEN } from "@/constant";

export default () => {
  const { loginType, setLoginType, ticket } = useGlobalStore()
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
    if (!values.mobile) return message.error('请输入手机号')
    if (!values.code) return message.error('请输入验证码')

    if (!mobileRegex.test(values.mobile)) return message.error('请输入正确的手机号');

    gzhBindMobileLogin({ ...values, ticket })
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

  const { loading: binding, run: gzhBindMobileLogin } = useRequest(gzhBindMobileLoginApi, {
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
        }}
        onFinish={onFinish}
        autoComplete="off"
      >
        <div className={styles.title}>
          请填写微信账户相关的手机号
        </div>
        <Form.Item
          label="手机号"
          name="mobile"
        >
          <Input placeholder='请输入手机号' />
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

        <div className={styles.notes}>
          说明：若新用户讲自动创建账户，您稍后可前往个人中心编辑相关信息
        </div>

        <Form.Item label={null}>
          <Button type="primary" htmlType="submit" style={{ width: '100%' }} loading={binding}>
            确定
          </Button>
        </Form.Item>
      </Form>
    </>
  );
}

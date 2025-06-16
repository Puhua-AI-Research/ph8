import { App, Tabs } from 'antd';
import { useGlobalStore } from '@/store/useGlobalStore'
import { useMemo } from 'react';
import CodeLogin from './CodeLogin'
import PasswordLogin from './PasswordLogin'

export default () => {
  const items = [
    {
      key: 'code',
      label: '验证码登录',
      children: <CodeLogin />,
    },
    {
      key: 'password',
      label: '密码登录',
      children: <PasswordLogin />,
    },
  ];

  return (
    <>
      <Tabs
        centered
        defaultActiveKey='code'
        items={items}
      />
    </>
  );
}

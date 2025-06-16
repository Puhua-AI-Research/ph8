import React, { useEffect } from 'react';
import styles from './index.less'
import { Button } from 'antd';
import { useGlobalStore } from '@/store/useGlobalStore'

export default () => {
  const { profile, setLoginType } = useGlobalStore()

  const showLogin = () => {
    setLoginType('login')
  }

  useEffect(() => {
    showLogin()
  }, [])

  return (
    <div className={styles.container}>
      <span>
        请先登录再进行操作!
      </span>
      <Button onClick={showLogin} type='primary'>登录</Button>
    </div>
  )
}





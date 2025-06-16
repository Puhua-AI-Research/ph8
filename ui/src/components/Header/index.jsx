import React, { useState } from 'react';
import { UserOutlined, CaretDownOutlined, LeftCircleOutlined, RightCircleOutlined, AlignLeftOutlined, AlignRightOutlined } from '@ant-design/icons';
import { Avatar, Button, Dropdown, App, message, Spin } from 'antd';
import { useGlobalStore } from '@/store/useGlobalStore'
import { StarIcon } from '@/utils/icons'
import LogoSvg from '@/assets/logo.svg';
import styles from './index.less';
import { logoutApi } from '@/request/login'
import { useRequest } from 'ahooks';
import { ACCESS_TOKEN } from "@/constant";
import { useOrderStore } from '@/store/useOrderStore'
import { useNavigate } from 'umi';

export default function Header() {
  const { collapseSidebar, setCollapseSidebar, profile, setLoginType, setShowDeleteUserModal } = useGlobalStore()
  const { setShowOrderDialog } = useOrderStore()
  const navigate = useNavigate();

  const { run: logout } = useRequest(logoutApi, {
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
      message.success('退出成功')
      localStorage.removeItem(ACCESS_TOKEN)
      setTimeout(() => {
        window.location.reload()
      }, 500);
    },
  });

  const items = [
    {
      key: 'logout',
      label: <div>退出登录</div>,
      onClick: () => logout()
    },
    {
      type: 'divider',
    },
    {
      key: 'deleteUser',
      label: <div>注销账户</div>,
      onClick: () => setShowDeleteUserModal(true)
    },
  ]

  const handleClickRecharge = () => {
    setShowOrderDialog(true)
  }

  const handleClickAvatar = () => {
    navigate('/userCenter')
  }

  return (
    <div className={styles.headerWrapper}>
      <div className={styles.logoWrapper}>
        <span className={styles.menuCollapse} onClick={() => setCollapseSidebar(!collapseSidebar)}>
          {collapseSidebar ? (
            <AlignLeftOutlined
              style={{ fontSize: '24px' }}
            />
          ) : (
            <AlignRightOutlined
              style={{ fontSize: '24px' }}
            />
          )}
        </span>

        {/* <span className={styles.showMenuBtn} onClick={() => setShowSidebarDrawer(true)}>
          <AlignLeftOutlined
            style={{ fontSize: '24px' }}
          />
        </span> */}

        <img src={LogoSvg} className={styles.logoImg} />

        <div className={styles.title}>
          PH8大模型开放平台
        </div>
      </div>

      <div className={styles.userInfo}>
        <div className={styles.points}>
          <StarIcon
            style={{
              fontSize: '16px',
              color: '#00F0FF',
            }} />

          {
            profile && (
              <span>
                {profile?.balance || 0}积分
              </span>
            )
          }

          <Button type="primary" onClick={handleClickRecharge}>充值</Button>
        </div>

        <a href='https://m1r239or5aw.feishu.cn/wiki/SegzwS4x1i2P4OksFY2cMvujn9f?from=from_copylink' target='__blank'>使用指南</a>

        {
          profile && (
            <div className={styles.userNameWrapper}>
              <div
                className={styles.userAvatar}
                onClick={handleClickAvatar}
              >
                <Avatar
                  size={36}
                  icon={<UserOutlined />}
                  style={{
                    backgroundColor: '#FFFFFF1A',
                  }}
                />
              </div>
              <Dropdown
                menu={{ items: items }}
                trigger={['click']}
              >
                <div className={styles.userName}>
                  <span>
                    {profile.nickname}
                  </span>
                  <CaretDownOutlined style={{ marginLeft: '8px' }} />
                </div>
              </Dropdown>
            </div>
          )
        }

        {
          !profile && (
            <Button color="cyan" variant="link" onClick={() => setLoginType('login')}>
              登录
            </Button>
          )
        }
      </div>
    </div>
  );
}

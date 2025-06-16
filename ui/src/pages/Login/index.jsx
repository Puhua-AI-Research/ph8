import { App, Modal } from 'antd';
import { useGlobalStore } from '@/store/useGlobalStore'
import { useState } from 'react';
import { CloseCircleOutlined } from '@ant-design/icons';
import styles from './index.less';
import MobileLogin from './MobileLogin'
import MobileRegister from './MobileRegister'
import GzhScanLogin from './GzhScanLogin'
import GhzBindMobile from './GhzBindMobile'

export default () => {
  const { loginType, setLoginType } = useGlobalStore()

  const handleCancel = () => {
    setLoginType('')
  };

  const getModalWidth = (loginType) => {
    switch (loginType) {
      case 'gzhScanLogin':
        return 930;
      default:
        return 540;
    }
  }

  return (
    <>
      <Modal
        open={loginType !== ''}
        className={styles.modalContainer}
        // style={{ top: 200}}
        width={getModalWidth(loginType)}
        onCancel={handleCancel}
        footer={null}
        maskClosable={false}
        closable={{ closeIcon: <CloseCircleOutlined style={{ fontSize: '18px' }} /> }}
      >
        {
          loginType === 'login' && <MobileLogin />
        }
        {
          loginType === 'register' && <MobileRegister />
        }
        {
          loginType === 'gzhScanLogin' && <GzhScanLogin />
        }
        {
          loginType === 'gzhBindMobile' && <GhzBindMobile />
        }
      </Modal>
    </>
  );
}

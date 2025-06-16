import { Drawer } from 'antd';
import { useGlobalStore } from '@/store/useGlobalStore'
import Header from '@/components/Header';
import SideBar from '@/components/SideBar';
import Content from '@/components/Content';
import PageFooter from '@/components/PageFooter';
import styles from './index.less';
import LoginModal from '@/pages/Login'
import OrderDialog from '@/pages/OrderDialog'
import DeleteUserDialog from '@/pages/DeleteUser'
import { useEffect } from 'react';
import classNames from 'classnames';

export default function Layout() {
  const { collapseSidebar, showSidebarDrawer, setShowSidebarDrawer, fetchProfile, showCopyRight } = useGlobalStore()

  const onClose = () => {
    setShowSidebarDrawer(false);
  };

  useEffect(() => {
    fetchProfile()
  }, [])

  return (
    <div className={styles.layout}>
      <div className={styles.header} >
        <Header />
      </div>

      <div className={classNames(styles.main, { [styles.collapsed]: collapseSidebar })}>
        <div className={classNames(styles.sidebar, { [styles.collapsed]: collapseSidebar })}>
          <SideBar />
        </div>

        <Drawer
          placement='left'
          closable={false}
          onClose={onClose}
          style={{
            maxWidth: '250px',
          }}
          styles={{
            body: {
              padding: 0,
              background: '#06080C',
              borderRight: '1px solid #303030',
            }
          }}
          open={showSidebarDrawer}
        >
          <SideBar />
        </Drawer>

        <div className={styles.content}>
          <div className={styles.contentMain}>
            <Content />
          </div>
          {
            showCopyRight && (
              <div className={styles.contentFooter}>
                <PageFooter />
              </div>
            )
          }
        </div>
      </div>

      <LoginModal />
      <OrderDialog />
      <DeleteUserDialog />
    </div>
  );
}

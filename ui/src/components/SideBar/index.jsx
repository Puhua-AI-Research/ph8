import { useEffect, useState } from 'react';
import { useNavigate, useRouteProps  } from 'umi';
import { AppstoreOutlined, KeyOutlined, PieChartOutlined, DatabaseOutlined, UnorderedListOutlined, UserOutlined, FileTextOutlined } from '@ant-design/icons';
import MenuItem from '../MenuItem';
import { useGlobalStore } from '@/store/useGlobalStore'
import styles from './index.less';
import classNames from 'classnames';

const items = [
  {
    label: '模型广场',
    key: 'model',
    path: '/modelGallary',
    icon: <DatabaseOutlined />,
  },
  {
    label: '体验中心',
    key: 'experience',
    icon: <AppstoreOutlined />,
    path: '/experience'
  },

  {
    type: 'divider',
  },
  {
    key: 'control',
    label: '控制台',
    type: 'group',
  },
  {
    label: '调用统计',
    key: 'statistics',
    path: '/statistics',
    icon: <PieChartOutlined />,
  },
  {
    label: '密钥管理',
    key: 'apiKey',
    path: '/apiKey',
    icon: <KeyOutlined />,
  },

  {
    type: 'divider',
  },
  {
    key: 'personal',
    label: '个人空间',
    type: 'group',
  },
  {
    label: '个人中心',
    key: 'userCenter',
    path: '/userCenter',
    icon: <UserOutlined />,
  },
];

export default function SideBar() {
  const [selectMenuKey, setSelectMenuKey] = useState('experience')
  const { collapseSidebar } = useGlobalStore()
  const navigate = useNavigate();
  const routeProps = useRouteProps()

  const onClick = ({name, path}) => {
    setSelectMenuKey(name);
    if (path) navigate(path);
  };

  useEffect(() => {
    if (routeProps.key) {
      const {key} = routeProps;
      setSelectMenuKey(key)
    }
  }, [routeProps])

  return (
    <div className={classNames(styles.sideWrapper, { [styles.collapsed]: collapseSidebar })}>
      {
        items.map((item, idx) => {
          return (
            <MenuItem
              key={idx}
              name={item.key}
              path={item.path}
              type={item.type}
              onClick={onClick}
              icon={item.icon}
              label={item.label}
              collapsed={collapseSidebar}
              selected={selectMenuKey === item.key}
            />
          )
        })
      }
    </div>
  );
}
import { App, ConfigProvider, theme } from 'antd';
import { matchRoutes } from 'umi';
import 'normalize.css';
// import '@/css/reset.css';
import '@/css/global.css';
import { useGlobalStore } from "@/store/useGlobalStore";

export function rootContainer(container) {
  return (
    <ConfigProvider
      theme={{
        algorithm: theme.darkAlgorithm,
        token: {
          colorPrimary: '#00F0FF',
        },
      }}
    >
      <App>{container}</App>
    </ConfigProvider>
  )
}

export function onRouteChange({ clientRoutes, location }) {
  if (location.pathname === '/modelGallary') {
    useGlobalStore.getState().setShowCopyRight(true)
  } else {
    useGlobalStore.getState().setShowCopyRight(false)
  }
  const route = matchRoutes(clientRoutes, location.pathname)?.pop()?.route;
  if (route) {
    document.title = route.title || '';
  }
}

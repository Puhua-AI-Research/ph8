import { defineConfig } from "umi";

export default defineConfig({
  hash: true,
  metas: [
    { 'http-equiv': 'Cache-Control', content: 'no-cache, no-store, must-revalidate' },
    { 'http-equiv': 'Pragma', content: 'no-cache' },
    { 'http-equiv': 'Expires', content: '0' },
  ],
  routes: [
    {
      path: '/',
      component: '@/layouts/MenuLayout',
      routes: [
        { path: '/', redirect: '/experience' },
        { path: "/experience", component: "@/pages/Experience", title: "体验中心", key: 'experience', },
        { path: "/modelGallary", component: "@/pages/ModelGallery/ModelListV2", title: "模型广场", key: 'model' },
        { path: "/modelDetail/:id", component: "@/pages/ModelGallery/ModelDetail", title: "模型详情", key: 'model' },
        { path: "/statistics", component: "@/pages/CallStatistics", title: "调用统计", key: 'statistics', },
        { path: "/apiKey", component: "@/pages/ApiKey", title: "密钥管理", key: 'apiKey', },
        { path: "/userCenter", component: "@/pages/UserCenter", title: "个人中心", key: 'userCenter' },
      ],
    },
    { path: '/*', component: '@/pages/NotFound' },
  ],
  npmClient: 'pnpm',
  proxy: {
    '/app-api': {
      'target': 'http://127.0.0.1:10300',
      'changeOrigin': true,
    },
  },
});

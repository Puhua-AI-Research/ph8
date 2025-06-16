import { message } from 'antd';
import axios from "axios";
import { throttle } from 'lodash';
import { unstable_batchedUpdates } from 'react-dom';
import { ACCESS_TOKEN } from "@/constant";
import { useGlobalStore } from "@/store/useGlobalStore";

const notify = throttle((msg) => {
  message.warning(`${msg}`)
}, 3000, {'leading': true, 'trailing': false})

function initRequest() {
  const instance = axios.create({
    // 跨域
    // baseURL: '/api',
    headers: {
      'Content-Type': 'application/json',
    }
  })

  // 添加请求拦截器
  instance.interceptors.request.use(
    function (config) {
      // 可以在这里添加token
      const token = localStorage.getItem(ACCESS_TOKEN)
      if (token && !config.headers['Authorization']) {
        config.headers['Authorization'] = token
      }
      return config;
    },
    function (error) {
      // 对请求错误做些什么
      return Promise.reject(error);
    }
  );

  // 添加响应拦截器
  instance.interceptors.response.use(
    function (response) {
      const { data } = response
      // 后端返回token过期，清理本地的用户缓存信息
      if (data && (data.code === 10021 || data.code === 401)) {
        localStorage.removeItem(ACCESS_TOKEN)
        // 组件外更新state
        unstable_batchedUpdates(() => {
          useGlobalStore.getState().setProfile(null)
        })
        // notify(data.msg)
      }
      return response;
    },
    function (error) {
      // 对响应错误做点什么
      return Promise.reject(error);
    }
  );

  return instance
}

const requestInstance = initRequest()

export default requestInstance

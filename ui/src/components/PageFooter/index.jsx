import styles from './index.less'
import wxAssitant from '@/assets/wxassitant.jpg'
import wxGzh from '@/assets/wxgzh.jpg'

const links = [
  {
    'name': 'CodeFlux',
    'url': 'https://marketplace.visualstudio.com/items?itemName=Puhua.CodeFlux&ssr=false#review-details'
  },
  {
    'name': '普华云',
    'url': 'https://puhuacloud.com/'
  },
]

export default () => {
  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <div className={styles.contact}>
          <div className={styles.title}>
            联系我们
          </div>
          <div>
            业务资询：info@puhuacloud.com
          </div>
          <div>
            公司地址：山东省青岛市高新区汇智桥路151号中科研发城3号楼901-50室
          </div>
        </div>

        <div className={styles.product}>
          <div className={styles.title}>
            产品链接
          </div>
          <div className={styles.linkList}>
            {
              links.map((item, idx) => <a href={item.url} target='__blank' key={idx}>{item.name}</a>)
            }
          </div>
        </div>

        <div className={styles.privacy}>
          <div className={styles.title}>
            相关条款
          </div>
          <div>
            <a href='https://m1r239or5aw.feishu.cn/wiki/GEaqw6R71i9IVMkpsmLcJdY0nKd?from=from_copylink' target='__blank'>用户协议</a>
          </div>
          <div>
            <a href='https://m1r239or5aw.feishu.cn/wiki/Hk5qwG8Aki65xQkhsnmca8W3nKg?from=from_copylink' target='__blank'>隐私协议</a>
          </div>
        </div>

        <div className={styles.wxgzh}>
          <div className={styles.barcode}>
            <img src={wxGzh}></img>
          </div>
          <div>
            PH8开放平台
          </div>
          <div>
            公众号
          </div>
        </div>

        <div className={styles.wxdeveloper}>
          <div className={styles.barcode}>
            <img src={wxAssitant}></img>
          </div>
          <div>
            PH8开放平台
          </div>
          <div>
            小助手微信
          </div>
        </div>
      </div>

      <div className={styles.copyright}>
        <div>
          @中航普华（山东）信息科技有限公司 2025 版权所有
        </div>
        <div>
          ｜
        </div>
        <div>
          Copyright © 鲁ICP备2022032597号
        </div>
      </div>
    </div>
  )
}

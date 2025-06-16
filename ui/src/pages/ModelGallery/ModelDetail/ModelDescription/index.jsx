import { App } from 'antd';
import styles from './index.less';
import { isEmpty } from 'lodash'
import PrettyMarkdown from '@/components/PrettyMarkdown';

export default ({ modelData }) => {
  const {
    imageList,
    inferencePrice,
    chargeMode,
    settlementInterval,
    introductionContent,
    introduction
  } = modelData || {}

  return (
    <div className={styles.container}>
      <div className={styles.banner}>
        <div className={styles.imageList}>
          {/* {
            imageList?.map((item, index) => {
              return (
                <div className={styles.imgItem} key={index}>
                  <img src={item} />
                </div>
              )
            })
          } */}
          {
            imageList?.length > 0 && (
              <div className={styles.imgItem}>
                <img src={imageList[0]} />
              </div>
            )
          }
        </div>
      </div>

      {
        !isEmpty(introductionContent) && (
          <div className={styles.content}>
            {
              introductionContent.map((item, index) => {
                return (
                  <div className={styles.displayItem} key={index}>
                    <div className={styles.title}>{item?.title}</div>
                    <div className={styles.displayContent}>
                      {item?.content}
                    </div>
                  </div>)
              })
            }
          </div>
        )
      }

      {
        isEmpty(introductionContent) && !isEmpty(introduction) && (
          <div className={styles.modelDescription}>
            <PrettyMarkdown content={introduction} withGithubStyle={true}/>
          </div>
        )
      }
    </div>
  )
}

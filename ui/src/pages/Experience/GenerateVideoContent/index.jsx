import { useState, useEffect, useMemo } from 'react';
import { App, Image, Empty, Spin, Space, Breadcrumb } from 'antd';
import { useGenerateVideoStore } from '@/store/generateVideoStore'
import { useModelServiceTypeStore, getModelCategoryDisplay } from '@/store/modelServiceTypeStore'
import { isEmpty } from 'lodash'
import styles from './index.less'
import vedioSvg from '@/assets/vedio.svg';
import eventBus from '@/utils/eventBus';

export default () => {
  const { modelParams, updateParams, respVideos, setRespVideos, processing } = useGenerateVideoStore()
  const { serviceType } = useModelServiceTypeStore()
  const modelCategoryDisplay = useMemo(() => getModelCategoryDisplay(serviceType), [serviceType])

  useEffect(() => {
    eventBus.on('text2video:modelChange', () => setRespVideos([]));
    return () => {
      eventBus.off('text2video:modelChange');
    };
  }, [])

  const GenerateResult = ({ processing, videoList }) => {
    if (processing || !isEmpty(videoList)) {
      return (
        <div className={styles.generateResults}>
          <div className={styles.title}>
            生成结果
          </div>
          <div className={styles.subTitle}>
            结果预览
          </div>
          <div className={styles.videoWrapper}>
            {
              processing && (
                <div className={styles.loading}>
                  <Space>
                    <span>生成中...</span><Spin></Spin>
                  </Space>
                </div>
              )
            }
            {
              !processing && videoList.map((item, index) => {
                return (<div className={styles.videoItem} key={index}>
                  <video controls>
                    <source src={item.url} type="video/mp4"></source>
                    <source src={item.url} type="video/webm"></source>
                    <source src={item.url} type="video/ogg"></source>
                    <p>抱歉，你的浏览器不支持HTML5视频播放。</p>
                  </video>
                </div>)
              })
            }
          </div>
        </div>
      )
    }

    return (
      <div className={styles.intro}>
        <div className={styles.titleWrapper}>
          <span className={styles.title}>
            欢迎体验 <span className={styles.modelName}>{modelParams.model}</span> 模型
          </span>
        </div>
        <p>暂无内容，开始你的创意吧～</p>
        <div>
          <img src={vedioSvg}></img>
        </div>
      </div>
    )
  }

  return (
    <>
      {
        (processing || !isEmpty(respVideos)) && (
          <Breadcrumb
            className={styles.breadcrumb}
            separator=">"
            items={[
              {
                title: modelCategoryDisplay,
              },
              {
                title: modelParams.model,
              }
            ]}
          />
        )
      }

      <div className={styles.content}>
        <GenerateResult processing={processing} videoList={respVideos} />
      </div>
    </>
  );
};

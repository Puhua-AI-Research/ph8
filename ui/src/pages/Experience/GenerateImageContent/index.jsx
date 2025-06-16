import { useState, useEffect, useMemo } from 'react';
import { App, Image, Empty, Spin, Space, Breadcrumb } from 'antd';
import { useSD15GenImageStore } from '@/store/generateImageStore'
import { useModelServiceTypeStore, getModelCategoryDisplay } from '@/store/modelServiceTypeStore'
import { isEmpty } from 'lodash'
import styles from './index.less'
import pictureSvg from '@/assets/picture.svg';
import eventBus from '@/utils/eventBus';

export default () => {
  const { modelParams, updateParams, respImages, setRespImages, processing } = useSD15GenImageStore()
  const { serviceType } = useModelServiceTypeStore()
  const modelCategoryDisplay = useMemo(() => getModelCategoryDisplay(serviceType), [serviceType])

  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState('');

  useEffect(() => {
    eventBus.on('text2img:modelChange', () => setRespImages([]));
    return () => {
      eventBus.off('text2img:modelChange');
    };
  }, [])

  const getBase64 = (file) =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = (error) => reject(error);
    });

  const handlePreview = async (fileUrl) => {
    setPreviewImage(fileUrl);
    setPreviewOpen(true);
  };

  const GenerateResult = ({ processing, imgList }) => {
    if (processing || !isEmpty(imgList)) {
      return (
        <div className={styles.generateResults}>
          <div className={styles.title}>
            生成结果
          </div>
          <div className={styles.subTitle}>
            结果预览
          </div>
          <div className={styles.imgGallery}>
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
              !processing && imgList.map((item, index) => {
                return (<div className={styles.imgItem} key={index}>
                  <img src={item.url} onClick={() => handlePreview(item.url)}></img>
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
          <img src={pictureSvg}></img>
        </div>
      </div>
    )
  }

  return (
    <>
      {
        (processing || !isEmpty(respImages)) && (
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
        <GenerateResult processing={processing} imgList={respImages}></GenerateResult>

        {previewImage && (
          <Image
            wrapperStyle={{
              display: 'none',
            }}
            preview={{
              visible: previewOpen,
              onVisibleChange: (visible) => setPreviewOpen(visible),
              afterOpenChange: (visible) => !visible && setPreviewImage(''),
            }}
            src={previewImage}
          />
        )}
      </div>
    </>
  );
};

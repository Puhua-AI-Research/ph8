import React, { useEffect, useMemo, useState } from 'react';
import { App, message, Tabs, Button } from 'antd';
import { useModelServiceTypeStore, getModelCategory, getServiceTypeFromModelCategory } from '@/store/modelServiceTypeStore'
import { useModelListStore } from '@/store/modelListStore'
import TextConvParams from './TextConvParams';
import TextConvContent from './TextConvContent';
import VllmConvParams from './VllmConvParams';
import VllmConvContent from './VllmConvContent';
import GenerateImageParams from './GenerateImageParams';
import GenerateImageContent from './GenerateImageContent';
import GenerateVideoParams from './GenerateVideoParams';
import GenerateVideoContent from './GenerateVideoContent';
import { useRequest } from 'ahooks';
import { getModelListApi } from '@/request/experience';
import EmptyPage from './../EmptyPage';
import styles from './index.less';
import { useSearchParams } from 'umi';
import eventBus from '@/utils/eventBus';
import { useSD15GenImageStore } from '@/store/generateImageStore'
import { useGenerateVideoStore } from '@/store/generateVideoStore'
import classNames from 'classnames';
import { DoubleLeftOutlined, DoubleRightOutlined, AlignLeftOutlined, AlignRightOutlined } from '@ant-design/icons';

export default () => {
  const { serviceType, setServiceType } = useModelServiceTypeStore()
  const modelCategory = useMemo(() => getModelCategory(serviceType), [serviceType])
  const { updateModelList, updateModelDetailId } = useModelListStore()
  const [searchParams, setSearchParams] = useSearchParams();
  const [paramBarVisible, setParamBarVisible] = useState(true)
  const { processing: imgProcessing } = useSD15GenImageStore()
  const { processing: vedioProcessing } = useGenerateVideoStore()

  useEffect(() => {
    const category = searchParams.get('modelCategory')
    if (category) {
      const serviceType = getServiceTypeFromModelCategory(category)
      if (serviceType) {
        setServiceType(serviceType)
      }
    }
  }, [searchParams])

  useEffect(() => {
    document.body.classList.add('bodyNoScroll');

    return () => {
      document.body.classList.remove('bodyNoScroll');
    };
  }, []);

  useRequest(() => getModelListApi({ type: modelCategory }), {
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      const res = (data || []).map((item) => {
        return item.name
      })
      const ids = (data || []).map((item) => {
        return item.id
      })
      updateModelList({ [modelCategory]: res })
      updateModelDetailId({ [modelCategory]: ids })
    },
    refreshDeps: [modelCategory],
  })

  const tabItems = [
    {
      label: <span style={{ fontSize: '16px' }}>文本对话</span>,
      key: 'textConv',
      children: <TextConvParams type='textConv' />,
    },
    {
      label: <span style={{ fontSize: '16px' }}>文本生图</span>,
      key: 'imageGenerate',
      children: <GenerateImageParams type='imageGenerate' />,
    },
    {
      label: <span style={{ fontSize: '16px' }}>视频生成</span>,
      key: 'videoGenerate',
      children: <GenerateVideoParams type='videoGenerate' />,
    },
    {
      label: <span style={{ fontSize: '16px' }}>图像理解</span>,
      key: 'imageUnderstand',
      children: <VllmConvParams type='imageUnderstand' />,
    },
  ]

  const onTabChange = (key) => {
    setSearchParams({})
    setServiceType(key)
  }

  const handleGenerateImg = () => {
    eventBus.emit('text2img:generate')
  }

  const handleGenerateVideo = () => {
    eventBus.emit('text2video:generate')
  }

  return (
    <div className={styles.container}>
      <div className={classNames(styles.leftSide, { [styles.hideParam]: !paramBarVisible })}>
        <div className={styles.leftSideHeader}>
          <AlignRightOutlined style={{ fontSize: 18 }} onClick={() => setParamBarVisible(false)} />
        </div>

        <Tabs
          activeKey={serviceType}
          onChange={onTabChange}
          style={{ height: 0, flex: 1 }}
          tabBarStyle={{
            marginBottom: 0,
            fontWeight: 'bold'
          }}
          centered
          items={tabItems}
        />
        {
          (serviceType === 'imageGenerate' || serviceType === 'videoGenerate') && (
            <div className={styles.footer}>
              {
                serviceType === 'imageGenerate' && <Button type="primary" style={{ width: '100%' }} loading={imgProcessing} onClick={handleGenerateImg}>生成图片</Button>
              }
              {
                serviceType === 'videoGenerate' && <Button type="primary" style={{ width: '100%' }} loading={vedioProcessing} onClick={handleGenerateVideo}>生成视频</Button>
              }
            </div>
          )
        }
      </div>

      <div className={styles.rightContent}>
        {
          !paramBarVisible && (
            <div className={styles.showLeftSideBar}>
              <AlignLeftOutlined style={{ fontSize: 18 }} onClick={() => setParamBarVisible(true)} />
            </div>
          )
        }
        {serviceType === 'textConv' && <TextConvContent />}
        {serviceType === 'imageUnderstand' && <VllmConvContent />}
        {serviceType === 'imageGenerate' && <GenerateImageContent />}
        {serviceType === 'videoGenerate' && <GenerateVideoContent />}
      </div>
    </div>
  )
}

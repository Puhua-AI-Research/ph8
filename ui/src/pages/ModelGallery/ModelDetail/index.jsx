import { App, Button, message, Tabs } from 'antd';
import { useModelGalleryStore } from '@/store/useModelGalleryStore'
import { useRequest } from 'ahooks';
import { getModelGalleryListApi } from '@/request/modelGallery';
import styles from './index.less';
import classNames from 'classnames';
import { useMemo, useState } from 'react';
import { useNavigate, useParams } from 'umi';
import { formatTime } from '@/utils'
import ModelDescription from './ModelDescription'
import ModelExample from './ModelExample'
import eventBus from '@/utils/eventBus';

export default () => {
  const { modelList, setModelList } = useModelGalleryStore()
  const params = useParams();
  const [activeKey, setActiveKey] = useState('description')
  const navigate = useNavigate();

  useRequest(() => getModelGalleryListApi({ type: '' }), {
    manual: false,
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      setModelList(data || [])
    },
  })

  const currentModel = useMemo(() => {
    return modelList.find(item => item.id + '' === params.id + '')
  }, [modelList, params])

  const items = [
    {
      key: 'description',
      label: currentModel?.modelName,
      children: <ModelDescription modelData={currentModel} />,
    },
    {
      key: 'example',
      label: 'API调用',
      children: <ModelExample modelData={currentModel} />,
    },
  ];

  const gotoModelExperience = (modelName, modelType) => {
    if (!modelName || !modelType) return
    navigate(`/experience?modelName=${encodeURIComponent(modelName)}&modelCategory=${modelType}`)
  }

  const ModelExtraContent = ({ model }) => {
    return (<div className={styles.tabBarExtraContent}>
      <div>
        ID：{model?.modelId}
      </div>

      <div>
        支持芯片类型：{model?.supportChipList?.join(', ') || ''}
      </div>

      <div>
        上线时间：{formatTime(model?.pubTime)}
      </div>
    </div>)
  };

  const handleClickExample = () => {
    setActiveKey('example')
    setTimeout(() => {
      eventBus.emit('scrollToCallExample');
    }, 0);
  }

  return (
    <div className={styles.container}>
      {/* <ModelExtraContent model={currentModel} /> */}

      <div className={styles.pageWrapper}>
        <Tabs activeKey={activeKey} items={items} onChange={key => setActiveKey(key)} className={styles.main} />

        <div className={styles.rightSide}>
          <div className={styles.header}>
            <div className={styles.title}>{currentModel?.modelName}</div>
            <div className={classNames(styles.description, styles.multiLineEllipsis)}>{currentModel?.briefIntroduction}</div>
          </div>

          <div className={styles.operation}>
            <div>
              <Button type='primary' onClick={() => gotoModelExperience(currentModel?.name, currentModel?.type)}>立即体验</Button>
            </div>
            <div>
              <Button onClick={handleClickExample}>API调用</Button>
            </div>
          </div>

          <div className={styles.footer}>
            <div className={styles.item}>
              {`版本号： ${currentModel?.version || ''}`}
            </div>
            <div className={styles.item}>
              {`更新时间： ${formatTime(currentModel?.updateDate)}`}
            </div>
            <div className={styles.item}>
              {`更新内容： ${currentModel?.updateContent || ''}`}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

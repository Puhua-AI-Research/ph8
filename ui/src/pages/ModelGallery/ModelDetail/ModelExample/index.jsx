import { App, message, Select } from 'antd';
import styles from './index.less';
import { useMemo, useRef, useEffect, useCallback, useState } from 'react';
import { CopyOutlined } from '@ant-design/icons';
import copy from 'clipboard-copy';
import { useNavigate } from 'umi';
import SyntaxHighlighter from 'react-syntax-highlighter';
import { a11yDark } from 'react-syntax-highlighter/dist/esm/styles/hljs'
import { chargeModeMap } from '@/utils';
import eventBus from '@/utils/eventBus';

export default ({ modelData }) => {
  const navigate = useNavigate();
  const callExampleRef = useRef(null);
  const [exampleType, setExampleType] = useState('python')

  const {
    imageList,
    inferencePrice,
    chargeMode,
    settlementInterval,
    example: pyRawExample,
    curlExample: curlRawExample,
  } = modelData || {}

  // 替换开头和结尾的特殊html标签
  let pyExample = pyRawExample?.replace(/^\s*<\s*pre\s*><\s*code\s*>\s*/, '');
  pyExample = pyExample?.replace(/\s*<\s*\/code\s*><\s*\/pre\s*><\s*p\s*><\s*br\s*><\s*\/p\s*>\s*$/, '');

  let curlExample = curlRawExample?.replace(/^\s*<\s*pre\s*><\s*code\s*>\s*/, '');
  curlExample = curlExample?.replace(/\s*<\s*\/code\s*><\s*\/pre\s*><\s*p\s*><\s*br\s*><\s*\/p\s*>\s*$/, '');

  const selExample = useMemo(() => {
    switch (exampleType) {
      case 'python':
        return pyExample;
      case 'curl':
        return curlExample;
      default:
        return pyExample;
    }
  }, [pyExample, curlExample, exampleType])

  const handleScrollToCallExample = useCallback(() => {
    if (callExampleRef.current) {
      window.scrollTo({
        top: callExampleRef.current.offsetTop + 100,
        behavior: 'smooth',
      });
    }
  }, [])

  useEffect(() => {
    eventBus.on('scrollToCallExample', handleScrollToCallExample);
    return () => {
      eventBus.off('scrollToCallExample');
    };
  }, [])

  const settlementIntervalDisplay = useMemo(() => {
    if (settlementInterval?.toLowerCase() === 'hour') {
      return '按小时结算'
    } else if (settlementInterval?.toLowerCase() === 'day') {
      return '按天结算'
    }
  }, [settlementInterval])

  const handleCopy = (text) => {
    copy(text);
    message.success('复制成功');
  };

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

      <div className={styles.content}>
        <div className={styles.displayItem}>
          <div className={styles.title}>调用信息</div>

          <div className={styles.callInfo}>
            <div className={styles.item}>
              推理费用：<span>{`${inferencePrice}`}</span>
            </div>

            <div className={styles.item}>
              计费方式：<span>{chargeModeMap[chargeMode] || chargeMode}</span>
            </div>

            <div className={styles.item}>
              用量统计：<span><a onClick={() => navigate('/userCenter')}>点击查看</a></span>
            </div>
          </div>
        </div>

        <div className={styles.displayItem}>
          <div className={styles.title}>调用准备</div>

          <div className={styles.callPrepare}>
            <p>
              在调用该模型的服务接口时,您需要获取您的APIKey用于API请求认证和鉴权 <span><a onClick={() => navigate('/apiKey')}>点击获取</a></span>
            </p>
            <p>
              若该模型支持多种芯片，您需要在调用地址里模型名称（modelid）后的字段配置相应的芯片类型（chiptype）信息，详细说明可参考 <span><a href="https://m1r239or5aw.feishu.cn/wiki/SegzwS4x1i2P4OksFY2cMvujn9f?from=from_copylink" target="_blank">帮助文档</a></span>
            </p>
            <p>
              基于API的调用方式可参考以下调用示例，详细请求、返回说明可参考 <span><a href="https://m1r239or5aw.feishu.cn/wiki/SegzwS4x1i2P4OksFY2cMvujn9f?from=from_copylink" target="_blank">帮助文档</a></span>
            </p>
          </div>
        </div>

        <div className={styles.displayItem} ref={callExampleRef}>
          <div className={styles.title}>调用示例</div>

          <div className={styles.selExample}>
            <Select
              value={exampleType}
              style={{
                width: 100,
              }}
              onChange={val => setExampleType(val)}
              options={[
                {
                  value: 'python',
                  label: 'python',
                },
                {
                  value: 'curl',
                  label: 'curl',
                },
              ]}
            />
          </div>

          <div className={styles.callExample}>
            <div className={styles.code} >
              <SyntaxHighlighter language={exampleType} style={a11yDark} showLineNumbers>
                {selExample}
              </SyntaxHighlighter>
            </div>
            <CopyOutlined className={styles.copyIcon} onClick={() => handleCopy(selExample)} />
          </div>
        </div>
      </div>
    </div>
  )
}

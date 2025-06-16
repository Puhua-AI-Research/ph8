import { useRef, useState, useEffect, useMemo } from 'react';
import { Button, Form, Select, Input, App, Avatar, Spin, message, Breadcrumb } from 'antd';
import { UserOutlined, CopyOutlined, RedoOutlined } from '@ant-design/icons';
import { useTextConvStore } from '@/store/textConvStore'
import { useModelServiceTypeStore, getModelCategoryDisplay } from '@/store/modelServiceTypeStore'
import { createEventSource } from '@/utils/eventSource'
import { SendIcon, StopIcon, AiIcon } from '@/utils/icons';
import { SEND_LLM_QUESTION_URL } from '@/constant'
import { isEmpty } from 'lodash'
import classNames from 'classnames';
import styles from './index.less'
import { useGlobalStore } from '@/store/useGlobalStore'
import eventBus from '@/utils/eventBus';
import PrettyMarkdown from '@/components/PrettyMarkdown'
import { UpOutlined, DownOutlined } from '@ant-design/icons';
import copy from 'clipboard-copy';

const { Option } = Select;
const { TextArea } = Input;
export default () => {
  const [form] = Form.useForm();
  const { modelParams, updateParams, historyConv, addHistoryConv, popHistoryConv, updateAnswer, updateThinkContent, finishAnswer, resetConv, processing, setProcessing } = useTextConvStore()
  const abortControllerRef = useRef(null)
  const { profile, setLoginType } = useGlobalStore()
  const [showThinkContent, setShowThinkContent] = useState(true)
  const { serviceType } = useModelServiceTypeStore()
  const modelCategoryDisplay = useMemo(() => getModelCategoryDisplay(serviceType), [serviceType])

  useEffect(() => {
    eventBus.on('llm:modelChange', () => resetConv());
    return () => {
      eventBus.off('llm:modelChange');
    };
  }, [])

  const handleSendQuestion = (questionParam) => {
    // 登录拦截
    if (!profile) {
      message.warning('请先登录')
      setLoginType('login')
      return
    }

    const { question: questionInState, systemPrompt, model } = modelParams;
    let question = questionParam || questionInState;
    if (processing) {
      message.warning('正在处理中，请稍后')
      return
    }
    if (!question) {
      message.warning('请输入问题')
      return
    }
    if (!model) {
      message.warning('请选择模型')
      return
    }

    const sendParams = {
      ...modelParams,
      messages: [],
    }
    delete sendParams.systemPrompt;
    delete sendParams.question;

    if (systemPrompt) {
      sendParams.messages.unshift({ role: 'system', content: systemPrompt })
    }
    sendParams.messages = [
      ...sendParams.messages,
      ...historyConv.filter(item => !item.isLoading).map(item => ({ role: item.role, content: item.content })),
      { role: 'user', content: question }
    ]

    doProcessTextConv(sendParams, question)
  }

  const handleSendQuestionFinished = (answer, thinkContent) => {
    abortControllerRef.current = null
    setProcessing(false)
    finishAnswer(answer)
    updateThinkContent(thinkContent)
  }

  const doProcessTextConv = async (params, question) => {
    addHistoryConv(question)
    updateParams({ question: '' })
    setProcessing(true)

    let respAnswer = ''
    let thinkContent = ''
    const abortController = new AbortController();
    abortControllerRef.current = abortController;
    await createEventSource(
      {
        route: SEND_LLM_QUESTION_URL,
        body: params,
        method: 'POST',
        abortController,
      },
      {
        onStart: () => {
        },
        onMessage: ({ data }) => {
          if (data.type === 'text') {
            respAnswer += data.content
            updateAnswer(respAnswer)
          } else if (data.type === 'thinking') {
            thinkContent += data.content
            updateThinkContent(thinkContent)
          }
        },
        onSuccess: () => {
          handleSendQuestionFinished(respAnswer, thinkContent)
        },
        onError: (error) => {
          handleSendQuestionFinished(respAnswer, thinkContent)
        },
        onAbort: () => {
          handleSendQuestionFinished(respAnswer, thinkContent)
        }
      }
    )
  }

  const handleKeyDown = (event) => {
    // 检测 Alt + Enter
    if (event.key === 'Enter' && event.altKey) {
      event.preventDefault();
      if (modelParams.question) {
        handleSendQuestion()
      }
    }
  };

  const handleClickDemoQuestion = (question) => {
    if (!processing) {
      handleSendQuestion(question)
    }
  }

  const handleCopy = (text) => {
    copy(text);
    message.success('复制成功');
  };

  const handleReSendQuestion = () => {
    if (historyConv.length < 2) {
      return
    }
    const lastQuestionSend = historyConv[historyConv.length - 2].content
    popHistoryConv()
    handleSendQuestion(lastQuestionSend)
  };

  const handleStopConv = () => {
    if (abortControllerRef.current) {
      abortControllerRef.current?.abort()
    }
  }

  const demoQuestion = [
    '用小红书体写一个攻略。',
    '仿照鲁迅的风格写一封情书。',
    '介绍一下transformer架构。',
  ]

  return (
    <>
      {
        !isEmpty(historyConv) && (
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

      <div className={styles.convContent}>
        {
          isEmpty(historyConv) && (
            <div className={styles.intro}>
              <div className={styles.titleWrapper}>
                <span className={styles.title}>
                  欢迎体验 <span className={styles.modelName}>{modelParams.model}</span> 模型
                </span>
              </div>
              <p>您可在下方输入问题或点击推荐示例快速体验模型能力</p>
              <p>请注意，模型可能无法回答不合适的问题</p>
            </div>
          )
        }

        {
          !isEmpty(historyConv) && (
            <div className={styles.main}>
              {
                historyConv.map((item, index) => {
                  return (
                    <div key={index} className={styles.convItem}>
                      <div className={styles.convRole}>
                        {
                          item.role === 'user' ? <Avatar
                            size={30}
                            icon={<UserOutlined />}
                            style={{
                              backgroundColor: '#FFFFFF1A',
                            }}
                          /> : <AiIcon
                            style={{
                              fontSize: '30px',
                              color: '#00F0FF',
                            }}
                          />
                        }
                      </div>
                      <div className={classNames(styles.convText, { [styles.assistant]: item.role === 'assistant' })}>
                        {
                          !isEmpty(item?.thinkContent) && (
                            <>
                              {
                                showThinkContent ?
                                  <a onClick={() => setShowThinkContent(!showThinkContent)} style={{ display: 'inline-block', marginBottom: '10px' }}>
                                    思考过程 &nbsp;
                                    <UpOutlined />
                                  </a>
                                  :
                                  <a onClick={() => setShowThinkContent(!showThinkContent)} style={{ display: 'inline-block', marginBottom: '10px' }}>
                                    思考过程 &nbsp;
                                    <DownOutlined />
                                  </a>
                              }
                              {
                                showThinkContent && (
                                  <div className={styles.thinkContent}>
                                    <PrettyMarkdown content={item.thinkContent} />
                                  </div>
                                )
                              }
                            </>
                          )
                        }
                        {
                          item.isLoading && isEmpty(item.content) ? <Spin /> : (
                            <PrettyMarkdown content={item.content} loading={item.isLoading} />
                          )
                        }
                        {
                          !item.isLoading && (
                            <div className={styles.convAction}>
                              <CopyOutlined onClick={() => handleCopy(item.content)} />
                              {
                                index === historyConv.length - 1 && (
                                  <RedoOutlined onClick={() => handleReSendQuestion()} />
                                )
                              }
                            </div>
                          )
                        }
                      </div>
                    </div>
                  )
                })
              }
            </div>
          )
        }

        <div className={styles.footer}>
          <div className={styles.guide}>
            <div>
              我们提供了一些问题，可以点击进行快速提问
            </div>
            {
              !isEmpty(historyConv) && !processing && (
                <div className={styles.resetConv}>
                  <a onClick={resetConv}>+ 新建对话</a>
                </div>
              )
            }
          </div>

          <div className={styles.demoQuestionWrapper}>
            <div className={styles.demoQuestion}>
              {demoQuestion.map((item, index) => {
                return <Button key={index} onClick={() => handleClickDemoQuestion(item)}>{item}</Button>
              })}
            </div>
          </div>

          <div className={styles.questionWrapper}>
            <TextArea
              placeholder='来说点什么吧（Alt+Enter 发送  Enter 换行）'
              onKeyDown={handleKeyDown}
              rows={2}
              disabled={processing}
              value={modelParams.question}
              onChange={(val) => updateParams({ question: val.target.value })}
            />

            <div className={styles.sendBtnWrapper}>
              {
                processing ? (
                  <Button
                    type="ghost"
                    icon={<StopIcon
                      style={{
                        fontSize: '40px',
                        color: '#00F0FF',
                      }}
                    />}
                    onClick={() => handleStopConv()}
                  />
                ) : (
                  <Button
                    type="ghost"
                    icon={<SendIcon
                      style={{
                        fontSize: '40px',
                        color: (!modelParams.question) ? 'gray' : '#00F0FF',
                      }}
                    />}
                    disabled={!modelParams.question}
                    onClick={() => handleSendQuestion()}
                  />
                )
              }
            </div>
          </div>

        </div>
      </div>
    </>
  );
};

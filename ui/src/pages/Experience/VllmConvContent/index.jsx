import { useRef, useState, useEffect, useMemo } from 'react';
import { Button, Form, Select, Input, App, Avatar, Image, Upload, Spin, message, Breadcrumb } from 'antd';
import { UserOutlined, CopyOutlined, RedoOutlined } from '@ant-design/icons';
import { useVllmConvStore } from '@/store/vllmConvStore'
import { useModelServiceTypeStore, getModelCategoryDisplay } from '@/store/modelServiceTypeStore'
import { createEventSource } from '@/utils/eventSource'
import { SendIcon, StopIcon, AiIcon, UploadImageIcon } from '@/utils/icons';
import { SEND_VLLM_QUESTION_URL } from '@/constant'
import { isEmpty, isArray } from 'lodash'
import classNames from 'classnames';
import styles from './index.less'
import { uploadFilePreSignApi } from '@/request/experience'
import { useGlobalStore } from '@/store/useGlobalStore'
import eventBus from '@/utils/eventBus';
import PrettyMarkdown from '@/components/PrettyMarkdown';
import { UpOutlined, DownOutlined } from '@ant-design/icons';
import copy from 'clipboard-copy';

const { Option } = Select;
const { TextArea } = Input;
export default () => {
  const [form] = Form.useForm();
  const { modelParams, updateParams, historyConv, addHistoryConv, popHistoryConv, updateAnswer, updateThinkContent, finishAnswer, hasSendImg, setHasSendImg, resetConv, processing, setProcessing } = useVllmConvStore()
  const abortControllerRef = useRef(null)
  const uploadBtnRef = useRef(null);
  const { serviceType } = useModelServiceTypeStore()
  const modelCategoryDisplay = useMemo(() => getModelCategoryDisplay(serviceType), [serviceType])

  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState('');
  const [fileList, setFileList] = useState([])
  const { profile, setLoginType } = useGlobalStore()
  const [showThinkContent, setShowThinkContent] = useState(true)

  useEffect(() => {
    eventBus.on('vllm:modelChange', () => resetConv());
    return () => {
      eventBus.off('vllm:modelChange');
    };
  }, [])

  const handleSendQuestion = ({questionInParam, imageInParam, convLen=0}={}) => {
    // 登录拦截
    if (!profile) {
      message.warning('请先登录')
      setLoginType('login')
      return
    }

    const { question: questionInState, systemPrompt, model } = modelParams;
    let question = questionInParam || questionInState;
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

    // 添加系统提示语到消息列表最前面
    if (systemPrompt) {
      sendParams.messages.unshift({ role: 'system', content: [{ type: 'text', text: systemPrompt }] })
    }

    // 添加图片到当前这次发送的content中，如果有的话
    const content = []
    if (!isEmpty(fileList)) {
      fileList.forEach(item => {
        content.push({ type: 'image_url', image_url: { url: item.response.url } })
      })
    }
    if (!isEmpty(imageInParam)) {
      imageInParam.forEach(item => {
        content.push({ type: 'image_url', image_url: { url: item } })
      })
    }

    // 一定存在问题，附带在图片的后面
    content.push({ type: 'text', text: question })

    // 构造发送的消息列表，包括系统提示次，历史对话，当前这次发送的内容
    sendParams.messages = [
      ...sendParams.messages,
      ...historyConv.filter(item => !item.isLoading).map(item => ({ role: item.role, content: item.content })).slice(0, convLen !== 0 ? convLen : historyConv.length),
      { role: 'user', content: content }
    ]

    doProcessTextConv(sendParams, content)
  }

  const handleSendQuestionFinished = (answer, thinkContent) => {
    abortControllerRef.current = null
    setProcessing(false)
    finishAnswer(answer)
    updateThinkContent(thinkContent)
  }

  const doProcessTextConv = async (params, content) => {
    // 将发送的内容添加到历史记录中
    addHistoryConv(content)
    // 清空发送的内容
    updateParams({ question: '' })
    if (fileList.length > 0) {
      setHasSendImg(true)
    }
    setFileList([])
    setProcessing(true)

    let respAnswer = ''
    let thinkContent = ''
    const abortController = new AbortController();
    abortControllerRef.current = abortController;
    await createEventSource(
      {
        route: SEND_VLLM_QUESTION_URL,
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

  const getBase64 = (file) =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = (error) => reject(error);
    });

  const handlePreview = async (file) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj);
    }
    setPreviewImage(file.url || file.preview);
    setPreviewOpen(true);
  };

  const handleChange = ({ fileList }) => {
    let newFileList = [...fileList]
    // newFileList = newFileList.map((file) => {
    //   if (file.response) {
    //     file.url = file.response.url;
    //   }
    //   return file;
    // });
    setFileList(newFileList);

    // 上传图片后滚动到上传按钮位置
    setTimeout(() => {
      uploadBtnRef.current?.scrollIntoView({ behavior: 'smooth' })
    }, 100);
  }

  const uploadButton = (
    <button
      style={{
        border: 0,
        background: 'none',
      }}
      ref={uploadBtnRef}
      type="button"
    >
      <UploadImageIcon
        style={{
          fontSize: '40px',
          color: '#00F0FF',
        }}
      />
    </button>
  );

  const handleKeyDown = (event) => {
    // 检测 Alt + Enter
    if (event.key === 'Enter' && event.altKey) {
      event.preventDefault();
      if (modelParams.question) {
        handleSendQuestion()
      }
    }
  };

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
    let questionInParam = ''
    let imageInParam = []
    for (const c of lastQuestionSend) {
      if (c.type === 'text') {
        questionInParam = c.text
      }
      if (c.type === 'image_url') {
        imageInParam.push(c.image_url.url)
      }
    }
    handleSendQuestion({questionInParam, imageInParam, convLen: -2})
  };

  const handleStopConv = () => {
    if (abortControllerRef.current) {
      abortControllerRef.current?.abort()
    }
  }

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
              <p>暂无内容，开始你的创意吧～</p>
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
                          item.isLoading && isEmpty(item.content) ? <Spin /> : isArray(item.content) ? item.content.map((subItem, subIndex) => {
                            if (subItem.type === 'image_url') {
                              return <img src={subItem.image_url.url} alt="" key={subIndex} className={styles.userImg} />
                            } else if (subItem.type === 'text') {
                              return (
                                <PrettyMarkdown content={subItem.text} key={subIndex} loading={item.isLoading}/>
                              )
                            }
                          }) : (
                            <PrettyMarkdown content={item.content} loading={item.isLoading}/>
                          )
                        }
                        {
                          !item.isLoading && (
                            <div className={styles.convAction}>
                              <CopyOutlined onClick={() => handleCopy(item.content?.[0]?.text)} />
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

        {
          !isEmpty(historyConv) && !processing && (
            <div className={styles.clearConv}>
              <a onClick={resetConv}>+ 新建对话</a>
            </div>
          )
        }

        <div className={styles.footer}>
          {/* <div className={styles.uploadImgWrapper}>
          <div className={styles.uploadImg}>
            <Upload
              accept='image/png, image/jpeg'
              listType="picture-card"
              fileList={fileList}
              onPreview={handlePreview}
              onChange={handleChange}
              customRequest={async (option) => {
                const { onProgress, onError, onSuccess, file } = option;

                const signData = {}
                try {
                  const resp = await uploadFilePreSignApi({ filename: file.name, scene: 'experience' })
                  if (resp.code !== 0) {
                    message.warning(msg)
                    onError('获取文件签名失败')
                    return
                  }
                  Object.assign(signData, resp.data)
                } catch (err) {
                  onError('获取文件签名异常')
                  return
                }

                const xhr = new XMLHttpRequest();
                if (xhr.upload) {
                  xhr.upload.onprogress = function (event) {
                    let percent;

                    if (event.total > 0) {
                      percent = (event.loaded / event.total) * 100;
                    }

                    onProgress(parseInt(percent, 10), event);
                  };
                }

                xhr.onerror = function error(e) {
                  onError(e);
                };

                xhr.onload = function onload() {
                  if (xhr.status < 200 || xhr.status >= 300) {
                    return onError(xhr.responseText);
                  }

                  // onSuccess(xhr.responseText, xhr);
                  // 上传成功，服务端不返回任何东西，这个时候把签名中的url返回给前端
                  onSuccess({ url: signData.url }, xhr);
                };

                const fileData = Buffer.from(await file.arrayBuffer())
                xhr.open('put', signData.uploadUrl, true);
                xhr.send(fileData);

                return {
                  abort() {
                    xhr.abort();
                  },
                };
              }}
            >
              {
                (fileList.length > 0 || hasSendImg) ? null : uploadButton
              }
            </Upload>
          </div>
        </div> */}

          <div className={styles.questionWrapper}>
            <TextArea
              placeholder='来说点什么吧（Alt+Enter 发送  Enter 换行）'
              onKeyDown={handleKeyDown}
              rows={2}
              disabled={processing}
              value={modelParams.question}
              onChange={(val) => updateParams({ question: val.target.value })}
            />

            <div className={styles.uploadIcon}>
              <Upload
                accept='image/png, image/jpeg'
                listType="picture-card"
                fileList={fileList}
                disabled={processing}
                onPreview={handlePreview}
                onChange={handleChange}
                customRequest={async (option) => {
                  const { onProgress, onError, onSuccess, file } = option;

                  const signData = {}
                  try {
                    const resp = await uploadFilePreSignApi({ filename: file.name, scene: 'experience' })
                    if (resp.code !== 0) {
                      message.warning(msg)
                      onError('获取文件签名失败')
                      return
                    }
                    Object.assign(signData, resp.data)
                  } catch (err) {
                    onError('获取文件签名异常')
                    return
                  }

                  const xhr = new XMLHttpRequest();
                  if (xhr.upload) {
                    xhr.upload.onprogress = function (event) {
                      let percent;

                      if (event.total > 0) {
                        percent = (event.loaded / event.total) * 100;
                      }

                      onProgress(parseInt(percent, 10), event);
                    };
                  }

                  xhr.onerror = function error(e) {
                    onError(e);
                  };

                  xhr.onload = function onload() {
                    if (xhr.status < 200 || xhr.status >= 300) {
                      return onError(xhr.responseText);
                    }

                    // onSuccess(xhr.responseText, xhr);
                    // 上传成功，服务端不返回任何东西，这个时候把签名中的url返回给前端
                    onSuccess({ url: signData.url }, xhr);
                  };

                  const fileData = Buffer.from(await file.arrayBuffer())
                  xhr.open('put', signData.uploadUrl, true);
                  xhr.send(fileData);

                  return {
                    abort() {
                      xhr.abort();
                    },
                  };
                }}
              >
                {
                  (fileList.length > 0 || hasSendImg) ? null : uploadButton
                }
              </Upload>
            </div>

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

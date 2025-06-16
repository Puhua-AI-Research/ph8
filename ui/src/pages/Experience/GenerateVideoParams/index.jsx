import React, { useState, useMemo, useEffect, useCallback } from 'react';
import { Button, Form, Col, InputNumber, Row, Slider, Select, Input, App, Image, message } from 'antd';
import { isEmpty } from 'lodash'
import { useModelServiceTypeStore, getModelCategory } from '@/store/modelServiceTypeStore'
import { useGenerateVideoStore } from '@/store/generateVideoStore'
import { useModelListStore } from '@/store/modelListStore'
import { generateVideoApi, generateVideoApiV2, queryVideoApi } from '@/request/experience'
import styles from './index.less'
import { useRequest } from 'ahooks';
import { useNavigate, useSearchParams } from 'umi';
import { useGlobalStore } from '@/store/useGlobalStore'
import eventBus from '@/utils/eventBus';

const { Option } = Select;
const { TextArea } = Input;
export default ({ type }) => {
  const [form] = Form.useForm();
  const { serviceType, setServiceType } = useModelServiceTypeStore()
  const modelCategory = useMemo(() => getModelCategory(serviceType), [serviceType])
  const { modelParams, updateParams, respVideos, setRespVideos, setProcessing, processing: videoProcessing } = useGenerateVideoStore()
  const { modelList, modelDetailId } = useModelListStore()
  const [searchParams] = useSearchParams();
  const { profile, setLoginType } = useGlobalStore()
  const navigate = useNavigate();
  const [videoQueryId, setVideoQueryId] = useState(null)

  const { loading: processing, run: generateVideo } = useRequest(generateVideoApi, {
    manual: true,
    onBefore: () => {
      setProcessing(true)
      setRespVideos([])
    },
    onFinally: () => {
      setProcessing(false)
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      const respVideos = []
      if (data?.url) {
        respVideos.push({ url: data.url })
      }
      setRespVideos(respVideos)
    },
  });

  const { run: generateVideoTask } = useRequest(generateVideoApiV2, {
    manual: true,
    onBefore: () => {
      setProcessing(true)
      setVideoQueryId(null)
      setRespVideos([])
    },
    onFinally: () => {
    },
    onSuccess({ id } = {}) {
      if (!id) {
        message.warning('生成视频失败，请稍后重试')
        setProcessing(false)
        return
      }
      setVideoQueryId(id)
    },
  });

  useRequest(() => queryVideoApi({ id: videoQueryId }), {
    ready: !!videoQueryId,
    pollingInterval: 1000,
    onSuccess({ url, status } = {}) {
      if (status !== 'running') {
        if (url) {
          setVideoQueryId(null)
          setProcessing(false)

          setRespVideos([{ url }])
        } else {
          message.warning('生成视频失败，请稍后重试')
          setVideoQueryId(null)
          setProcessing(false)
        }
      }
    },
  });

  useEffect(() => {
    if (type === serviceType && !isEmpty(modelList) && !isEmpty(modelList[modelCategory])) {
      let modelName = modelParams.model
      if (searchParams.get('modelName')) {
        modelName = searchParams.get('modelName')
      }
      if (!modelName || !modelList[modelCategory].includes(modelName)) {
        modelName = modelList[modelCategory][0]
      }
      updateParams({ model: modelName })
    }
  }, [modelList, modelCategory])

  const handleGenerateVideo = useCallback(() => {
    // 登录拦截
    if (!profile) {
      message.warning('请先登录')
      setLoginType('login')
      return
    }

    const { prompt, model } = modelParams
    if (!model) {
      message.warning('请选择模型')
      return
    }
    if (!prompt) {
      message.warning('请输入prompt')
      return
    }

    generateVideoTask(modelParams)
  }, [modelParams])

  useEffect(() => {
    eventBus.on('text2video:generate', handleGenerateVideo);
    return () => {
      eventBus.off('text2video:generate');
    };
  }, [handleGenerateVideo])

  const onModelChange = (val) => {
    updateParams({ model: val })
    eventBus.emit('text2video:modelChange', val)
  }

  const gotoModelDetail = () => {
    const { model } = modelParams
    if (!model) return
    const idx = modelList[modelCategory]?.findIndex(item => item === model);
    if (idx === -1) return
    const detailId = modelDetailId[modelCategory]?.[idx]
    navigate(`/modelDetail/${detailId}`)
  }

  return (
    <div className={styles.formContent}>
      <Form
        layout='vertical'
        form={form}
      >
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          width: '100%',
          paddingBottom: 8
        }}>
          <span>model</span>
          <a onClick={gotoModelDetail}>
            查看模型介绍
          </a>
        </div>
        <Form.Item
        >
          <Select
            showSearch
            placeholder="请选择模型"
            value={modelParams.model}
            onChange={onModelChange}
            disabled={processing || videoProcessing}
          >
            {
              (modelList?.[modelCategory] || []).map((item, idx) => {
                return <Option value={item} key={idx}>{item}</Option>
              })
            }
          </Select>
        </Form.Item>

        <Form.Item label="prompt">
          <TextArea
            rows={2}
            placeholder='请输入prompt'
            value={modelParams.prompt}
            onChange={(val) => updateParams({ prompt: val.target.value })}
          />
        </Form.Item>

        <Form.Item label="num_frames">
          <Row>
            <Col span={15}>
              <Slider
                min={1}
                max={48}
                onChange={(val) => updateParams({ num_frames: val })}
                value={modelParams.num_frames}
                step={1}
              />
            </Col>
            <Col span={8} style={{ marginLeft: 'auto' }}>
              <InputNumber
                min={1}
                max={48}
                style={{
                  width: '100%',
                }}
                step={1}
                value={modelParams.num_frames}
                onChange={(val) => updateParams({ num_frames: val })}
              />
            </Col>
          </Row>
        </Form.Item>

        <Form.Item label="width">
          <Row>
            <Col span={15}>
              <Slider
                min={64}
                max={1024}
                onChange={(val) => updateParams({ width: val })}
                value={modelParams.width}
                step={1}
              />
            </Col>
            <Col span={8} style={{ marginLeft: 'auto' }}>
              <InputNumber
                min={64}
                max={1024}
                style={{
                  width: '100%',
                }}
                step={1}
                value={modelParams.width}
                onChange={(val) => updateParams({ width: val })}
              />
            </Col>
          </Row>
        </Form.Item>

        <Form.Item label="height">
          <Row>
            <Col span={15}>
              <Slider
                min={64}
                max={1024}
                onChange={(val) => updateParams({ height: val })}
                value={modelParams.height}
                step={1}
              />
            </Col>
            <Col span={8} style={{ marginLeft: 'auto' }}>
              <InputNumber
                min={64}
                max={1024}
                style={{
                  width: '100%',
                }}
                step={1}
                value={modelParams.height}
                onChange={(val) => updateParams({ height: val })}
              />
            </Col>
          </Row>
        </Form.Item>

        <Form.Item
          label="seed"
        >
          <InputNumber
            min={0}
            step={1}
            style={{
              width: '100%'
            }}
            value={modelParams.seed}
            onChange={(val) => updateParams({ seed: val })}
          />
        </Form.Item>
      </Form>
    </div>
  );
};

import React, { useState, useMemo, useEffect, useCallback } from 'react';
import { Button, Form, Col, InputNumber, Row, Slider, Select, Input, App, Image, message } from 'antd';
import { isEmpty } from 'lodash'
import { useModelServiceTypeStore, getModelCategory } from '@/store/modelServiceTypeStore'
import { useSD15GenImageStore } from '@/store/generateImageStore'
import { useModelListStore } from '@/store/modelListStore'
import { generateImageApi } from '@/request/experience'
import styles from './index.less'
import { useRequest } from 'ahooks';
import { useNavigate, useSearchParams } from 'umi';
import { useGlobalStore } from '@/store/useGlobalStore'
import eventBus from '@/utils/eventBus';
import { UpOutlined, DownOutlined } from '@ant-design/icons';

const { Option } = Select;
const { TextArea } = Input;
export default ({ type }) => {
  const [form] = Form.useForm();
  const { serviceType, setServiceType } = useModelServiceTypeStore()
  const modelCategory = useMemo(() => getModelCategory(serviceType), [serviceType])
  const { modelParams, updateParams, respImages, setRespImages, setProcessing } = useSD15GenImageStore()
  const { modelList, modelDetailId } = useModelListStore()
  const [searchParams] = useSearchParams();
  const { profile, setLoginType } = useGlobalStore()
  const [showAdvanced, setShowAdvanced] = useState(false)
  const navigate = useNavigate();

  const sampleNames = 'DPM++ 2M, DPM++ SDE, DPM++ 2M SDE, DPM++ 2M SDE Heun, DPM++ 2S a, DPM++ 3M SDE, Euler a, Euler, LMS, Heun, DPM2, DPM2 a, DPM fast, DPM adaptive, Restart, DDIM, DDIM CFG++, PLMS, UniPC, LCM'.split(',').map(item => item.trim()).filter(item => item !== '')
  const schedulerNames = 'Automatic, Uniform, Karras, Exponential, Polyexponential, SGM Uniform, KL Optimal, Align Your Steps, Simple, Normal, DDIM, Beta'.split(',').map(item => item.trim()).filter(item => item !== '')

  const { loading: processing, run: generateImage } = useRequest(generateImageApi, {
    manual: true,
    onBefore: () => {
      setProcessing(true)
      setRespImages([])
    },
    onFinally: () => {
      setProcessing(false)
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      setRespImages(data?.data || [])
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

  const isControllNet = useMemo(() => {
    const { model } = modelParams
    if (!model) {
      return false
    }
    if (model.includes('SD15MultiControlnetGenerateImage')) {
      return true
    }
    return false
  }, [modelParams])

  const handleGenerateImg = useCallback(() => {
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

    generateImage(modelParams)
  }, [modelParams])

  useEffect(() => {
    eventBus.on('text2img:generate', handleGenerateImg);
    return () => {
      eventBus.off('text2img:generate');
    };
  }, [handleGenerateImg])

  const onModelChange = (val) => {
    updateParams({ model: val })
    eventBus.emit('text2img:modelChange', val)
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
            disabled={processing}
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

        <Form.Item label="negative_prompt">
          <TextArea
            rows={2}
            placeholder='请输入negative_prompt'
            value={modelParams.negative_prompt}
            onChange={(val) => updateParams({ negative_prompt: val.target.value })}
          />
        </Form.Item>

        <Form.Item
          label="batch_size"
        >
          <Select
            placeholder="请选择batch_size"
            value={modelParams.batch_size}
            onChange={(val) => updateParams({ batch_size: val })}
          >
            <Option value={1}>1</Option>
            <Option value={2}>2</Option>
            <Option value={4}>4</Option>
          </Select>
        </Form.Item>

        <Form.Item label="width">
          <Row>
            <Col span={15}>
              <Slider
                min={32}
                max={2048}
                onChange={(val) => updateParams({ width: val })}
                value={modelParams.width}
                step={1}
              />
            </Col>
            <Col span={8} style={{ marginLeft: 'auto' }}>
              <InputNumber
                min={32}
                max={2048}
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
                min={32}
                max={2048}
                onChange={(val) => updateParams({ height: val })}
                value={modelParams.height}
                step={1}
              />
            </Col>
            <Col span={8} style={{ marginLeft: 'auto' }}>
              <InputNumber
                min={32}
                max={2048}
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

        <Form.Item>
          {
            showAdvanced ?
              <a onClick={() => setShowAdvanced(!showAdvanced)}>
                高级设置 &nbsp;
                <UpOutlined />
              </a>
              :
              <a onClick={() => setShowAdvanced(!showAdvanced)}>
                高级设置 &nbsp;
                <DownOutlined />
              </a>
          }
        </Form.Item>

        {
          showAdvanced && (
            <>
              <Form.Item
                label="steps"
              >
                <InputNumber
                  min={0}
                  max={50}
                  step={1}
                  style={{
                    width: '100%'
                  }}
                  value={modelParams.steps}
                  onChange={(val) => updateParams({ steps: val })}
                />
              </Form.Item>

              {
                isControllNet && (
                  <>
                    <Form.Item
                      label="sampler_name"
                    >
                      <Select
                        showSearch
                        placeholder="请选择sampler_name"
                        value={modelParams.sampler_name}
                        onChange={(val) => updateParams({ sampler_name: val })}
                      >
                        {
                          sampleNames.map((item, idx) => {
                            return <Option value={item} key={idx}>{item}</Option>
                          })
                        }
                      </Select>
                    </Form.Item>

                    <Form.Item
                      label="scheduler"
                    >
                      <Select
                        showSearch
                        placeholder="请选择scheduler"
                        value={modelParams.scheduler}
                        onChange={(val) => updateParams({ scheduler: val })}
                      >
                        {
                          schedulerNames.map((item, idx) => {
                            return <Option value={item} key={idx}>{item}</Option>
                          })
                        }
                      </Select>
                    </Form.Item>
                  </>
                )
              }

              <Form.Item label="cfg">
                <Row>
                  <Col span={15}>
                    <Slider
                      min={1}
                      max={30}
                      onChange={(val) => updateParams({ cfg: val })}
                      value={modelParams.cfg}
                      step={1}
                    />
                  </Col>
                  <Col span={8} style={{ marginLeft: 'auto' }}>
                    <InputNumber
                      min={1}
                      max={30}
                      style={{
                        width: '100%',
                      }}
                      step={1}
                      value={modelParams.cfg}
                      onChange={(val) => updateParams({ cfg: val })}
                    />
                  </Col>
                </Row>
              </Form.Item>

              <Form.Item label="denoising_strength">
                <Row>
                  <Col span={15}>
                    <Slider
                      min={0.0}
                      max={1.0}
                      onChange={(val) => updateParams({ denoising_strength: val })}
                      value={modelParams.denoising_strength}
                      step={0.1}
                    />
                  </Col>
                  <Col span={8} style={{ marginLeft: 'auto' }}>
                    <InputNumber
                      min={0.0}
                      max={1.0}
                      style={{
                        width: '100%',
                      }}
                      step={0.1}
                      value={modelParams.denoising_strength}
                      onChange={(val) => updateParams({ denoising_strength: val })}
                    />
                  </Col>
                </Row>
              </Form.Item>
            </>
          )
        }
      </Form>
    </div>
  );
};

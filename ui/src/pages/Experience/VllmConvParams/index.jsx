import React, { useState, useMemo, useEffect } from 'react';
import { Button, Form, Col, InputNumber, Row, Slider, Select, Input } from 'antd';
import { isEmpty } from 'lodash'
import { useModelServiceTypeStore, getModelCategory } from '@/store/modelServiceTypeStore'
import { useVllmConvStore } from '@/store/vllmConvStore'
import { useModelListStore } from '@/store/modelListStore'
import styles from './index.less'
import { useNavigate, useSearchParams } from 'umi';
import eventBus from '@/utils/eventBus';

const { Option } = Select;
const { TextArea } = Input;
export default ({ type }) => {
  const [form] = Form.useForm();
  const { serviceType, setServiceType } = useModelServiceTypeStore()
  const modelCategory = useMemo(() => getModelCategory(serviceType), [serviceType])
  const { modelParams, updateParams, processing } = useVllmConvStore()
  const { modelList, modelDetailId } = useModelListStore()
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

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

  const onModelChange = (val) => {
    updateParams({ model: val })
    eventBus.emit('vllm:modelChange', val)
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

        <Form.Item label="系统 Prompt">
          <TextArea
            rows={2}
            placeholder='请输入系统人设，例如“你是一个AI助手”'
            value={modelParams.systemPrompt}
            onChange={(val) => updateParams({ systemPrompt: val.target.value })}
          />
        </Form.Item>

        <Form.Item
          label="max_tokens"
          tooltip={{ title: '体验区max tokens为10K，需要设置更大的token请通过api调用。' }}
        >
          <InputNumber
            min={0}
            max={10240}
            step={1}
            style={{
              width: '100%'
            }}
            value={modelParams.max_tokens}
            onChange={(val) => updateParams({ max_tokens: val })}
          />
        </Form.Item>

        <Form.Item label="temperature">
          <Row>
            <Col span={15}>
              <Slider
                min={0}
                max={1}
                onChange={(val) => updateParams({ temperature: val })}
                value={modelParams.temperature}
                step={0.1}
              />
            </Col>
            <Col span={8} style={{ marginLeft: 'auto' }}>
              <InputNumber
                min={0}
                max={1}
                style={{
                  width: '100%',
                }}
                step={0.1}
                value={modelParams.temperature}
                onChange={(val) => updateParams({ temperature: val })}
              />
            </Col>
          </Row>
        </Form.Item>

        <Form.Item label="top_p">
          <Row>
            <Col span={15}>
              <Slider
                min={0}
                max={1}
                onChange={(val) => updateParams({ top_p: val })}
                value={modelParams.top_p}
                step={0.1}
              />
            </Col>
            <Col span={8} style={{ marginLeft: 'auto' }}>
              <InputNumber
                min={0}
                max={1}
                style={{
                  width: '100%',
                }}
                step={0.1}
                value={modelParams.top_p}
                onChange={(val) => updateParams({ top_p: val })}
              />
            </Col>
          </Row>
        </Form.Item>

        <Form.Item label="presence_penalty">
          <Row>
            <Col span={15}>
              <Slider
                min={0}
                max={2}
                onChange={(val) => updateParams({ presence_penalty: val })}
                value={modelParams.presence_penalty}
                step={0.1}
              />
            </Col>
            <Col span={8} style={{ marginLeft: 'auto' }}>
              <InputNumber
                min={0}
                max={2}
                style={{
                  width: '100%',
                }}
                step={0.1}
                value={modelParams.presence_penalty}
                onChange={(val) => updateParams({ presence_penalty: val })}
              />
            </Col>
          </Row>
        </Form.Item>

        <Form.Item label="frequency_penalty">
          <Row>
            <Col span={15}>
              <Slider
                min={0}
                max={2}
                onChange={(val) => updateParams({ frequency_penalty: val })}
                value={modelParams.frequency_penalty}
                step={0.1}
              />
            </Col>
            <Col span={8} style={{ marginLeft: 'auto' }}>
              <InputNumber
                min={0}
                max={2}
                style={{
                  width: '100%',
                }}
                step={0.1}
                value={modelParams.frequency_penalty}
                onChange={(val) => updateParams({ frequency_penalty: val })}
              />
            </Col>
          </Row>
        </Form.Item>
      </Form>
    </div>
  );
};

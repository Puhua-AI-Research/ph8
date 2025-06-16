import { useEffect, useRef, useState } from 'react';
import { Button, Modal, Space, Table, Tag, Form, Input, message } from 'antd';
import { ExclamationCircleFilled } from '@ant-design/icons';
import styles from './index.less'
import { formatTime } from '@/utils'
import { useRequest } from 'ahooks';
import { apiKeyListApi, createApiKeyApi, updateApiKeyApi, deleteApiKeyApi } from '@/request/apiKey'
import { withAuth } from '@/hocs/withAuth';
import _ from 'lodash'
import copy from 'clipboard-copy';

const { confirm } = Modal;

const ApiKey = () => {
  const [data, setData] = useState([])
  const [form] = Form.useForm();
  const inputRef = useRef(null);

  const [visible, setVisible] = useState(false);
  const [editData, setEditData] = useState(null)

  useEffect(() => {
    getApiKeyList()
  }, [])

  const { loading, run: getApiKeyList } = useRequest(apiKeyListApi, {
    manual: true,
    onBefore: () => {
      setData([])
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      setData(data?.list || [])
    },
  });

  const showNotify = (apiKey) => {
    Modal.warning({
      title: 'API Key保存提醒',
      content: (
        <div>
          <div>
            请保存您的API Key，否则将无法再次查看！
          </div>
          <div>
            {apiKey}
          </div>
        </div>
      )
    });
  };

  const { loading: creating, run: createApiKey } = useRequest(createApiKeyApi, {
    manual: true,
    onBefore: () => {
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      setVisible(false)
      getApiKeyList()
      if (data && _.get(data, 'apiKey')) {
        const apiKey = data.apiKey
        showNotify(apiKey)
        copy(apiKey)
        message.success('API Key已复制到剪贴板', 6)
      } else {
        message.success('创建成功')
      }
    },
  });

  const { loading: editing, run: updateApiKey } = useRequest(updateApiKeyApi, {
    manual: true,
    onBefore: () => {
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      message.success('修改成功')
      setVisible(false)
      getApiKeyList()
    },
  });

  const { loading: deleting, run: deleteApiKey } = useRequest(deleteApiKeyApi, {
    manual: true,
    onBefore: () => {
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      message.success('删除成功')
      getApiKeyList()
    },
  });

  const deleteConfirm = (record) => {
    confirm({
      title: `确认删除`,
      icon: <ExclamationCircleFilled />,
      content: `确认删除这个API Key（${record.name}）吗?`,
      onOk() {
        deleteApiKey({ id: record.id })
      },
      onCancel() {
      },
    });
  };

  const columns = [
    {
      title: '调用ID',
      dataIndex: 'id',
      width: 180
    },
    {
      title: '名称',
      dataIndex: 'name',
      width: 150,
      render: (_, { name }) => {
        return (
          <div style={{
            maxWidth: 150,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap'
          }}>
            {name}
          </div>
        )
      },
    },
    {
      title: 'API密钥',
      width: 150,
      dataIndex: 'apiKey',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 160,
      render: (_, { createTime }) => {
        return <span>{formatTime(createTime)}</span>;
      },
    },
    {
      title: '最后使用时间',
      dataIndex: 'lastUseTime',
      width: 160,
      render: (_, { lastUseTime }) => {
        return <span>{lastUseTime ? formatTime(lastUseTime) : '-'}</span>;
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      render: (_, { status }) => {
        if (status === 0) return <Tag color="green">启用</Tag>;
        if (status === 1) return <Tag color="red">未启用</Tag>;
      }
    },
    {
      title: '操作',
      key: 'action',
      align: 'center',
      width: 150,
      render: (_, record) => (
        <Space size="middle">
          {
            record.status === 0 ? <a onClick={() => updateApiKey({ id: record.id, status: 1 })}>禁用</a> : <a onClick={() => updateApiKey({ id: record.id, status: 0 })}>启用</a>
          }
          <a onClick={() => handleEditName(record)}>编辑</a>
          <a onClick={() => deleteConfirm(record)}>删除</a>
        </Space>
      ),
    },
  ];

  const onFinish = (values) => {
    if (editData) {
      updateApiKey({ ...values, id: editData.id });
    } else {
      createApiKey(values);
    }
  }

  const onCancel = () => {
    setVisible(false);
  };

  const onOk = () => {
    form.submit();
  };

  const handleCreateApiKey = () => {
    setVisible(true);
    setEditData(null)

    setTimeout(() => {
      inputRef.current?.focus();
    }, 300);
  };

  const handleEditName = (data) => {
    setVisible(true);
    setEditData(data)
    form.setFieldsValue({
      name: data.name,
    });

    setTimeout(() => {
      inputRef.current?.focus();
    }, 300);
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.description}>
          下方列表内是您的全部 API keys，请注意保护您的 API Key 的安全。请不要与他人共享您的 API Keys，避免将其暴露在浏览器和其他客户端代码中。 为了保护您帐户的安全，我们还可能会自动更换我们发现已公开泄露的密钥信息。
        </div>

        <div className={styles.action}>
          <Button type='primary' onClick={handleCreateApiKey}>创建API密钥</Button>
        </div>
      </div>

      <div className={styles.tableWrapper}>
        <Table
          columns={columns}
          dataSource={data}
          loading={loading}
          rowKey="id"
        />
      </div>

      <Modal
        title="创建API密钥"
        maskClosable={false}
        open={visible}
        onCancel={onCancel}
        onOk={onOk}
        destroyOnClose={true}
        okButtonProps={{
          loading: creating,
          disabled: !!creating,
          htmlType: 'submit',
        }}
      >
        <Form
          form={form}
          onFinish={onFinish}
          preserve={false}
        >
          <Form.Item
            label='名称'
            name={'name'}
            rules={[{ required: true, message: '请输入名称！' }]}
          >
            <Input ref={(e) => inputRef.current = e}></Input>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
};

export default withAuth(ApiKey)
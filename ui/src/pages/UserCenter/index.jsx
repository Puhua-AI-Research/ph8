import { useEffect, useState } from 'react';
import { Table, message, Avatar, Button, Modal, Form, Input, Select } from 'antd';
import { EditFilled, UserOutlined, CloseCircleOutlined } from '@ant-design/icons';
import styles from './index.less'
import { formatTime } from '@/utils'
import { useRequest } from 'ahooks';
import { useGlobalStore } from '@/store/useGlobalStore'
import { useOrderStore } from '@/store/useOrderStore'
import { mobileRegex } from '@/constant'
import { getMobileCodeApi } from '@/request/login'
import { editUserInfoApi, getUserBalanceLogApi } from '@/request/user'
import { changeTypeMap, businessModeMap } from '@/utils';
import { withAuth } from '@/hocs/withAuth';

const UserCenter = () => {
  const { profile, fetchProfile } = useGlobalStore()
  const { setShowOrderDialog } = useOrderStore()

  const [editUserInfoVisible, setEditUserInfoVisible] = useState(false)
  const [hasMobile, setHasMobile] = useState(false)
  const [editUserId, setEditUserId] = useState(null)
  const [form] = Form.useForm();

  const [userBalanceLog, setUserBalanceLog] = useState([])
  const [chargeType, setChargeType] = useState('all')
  const [tableParams, setTableParams] = useState({
    pagination: {
      current: 1,
      pageSize: 10,
    },
  });

  const [countdown, setCountdown] = useState(0);
  const [isCounting, setIsCounting] = useState(false);

  useEffect(() => {
    let timer;

    if (isCounting && countdown > 0) {
      timer = setTimeout(() => {
        setCountdown(countdown - 1);
      }, 1000);
    } else if (countdown === 0) {
      setIsCounting(false);
    }

    return () => clearTimeout(timer);
  }, [countdown, isCounting]);

  const startCountdown = () => {
    setIsCounting(true);
    setCountdown(60);
  }

  useEffect(() => {
    const params = {
      pageNo: tableParams.pagination.current,
      pageSize: tableParams.pagination.pageSize,
    }

    if (chargeType !== 'all') {
      params.changeType = chargeType
    }

    getUserBalanceLog(params)
  }, [
    tableParams.pagination?.current,
    tableParams.pagination?.pageSize,
    tableParams?.sortOrder,
    tableParams?.sortField,
    JSON.stringify(tableParams.filters),
    chargeType
  ]);

  const { loading: getMobileCodeLoading, run: getMobileCode } = useRequest(getMobileCodeApi, {
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
      message.info('验证码已发送，请注意查收')
    },
  });

  const { loading: editing, run: editUserInfo } = useRequest(editUserInfoApi, {
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
      fetchProfile()
      setEditUserInfoVisible(false)
    },
  });

  const { loading, run: getUserBalanceLog } = useRequest(getUserBalanceLogApi, {
    manual: true,
    onBefore: () => {
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        setUserBalanceLog([])
        return
      }
      setUserBalanceLog(data?.list || [])

      setTableParams({
        ...tableParams,
        pagination: {
          ...tableParams.pagination,
          total: data?.total || 0,
        },
      });
    },
  });

  const columns = [
    {
      title: '账单编号',
      dataIndex: 'id',
    },
    {
      title: '账单时间',
      dataIndex: 'logTime',
      render: (_, { logTime }) => {
        return <span>{formatTime(logTime)}</span>;
      },
    },
    {
      title: '交易类型',
      dataIndex: 'changeType',
      render: (_, { changeType }) => {
        return <span>{changeTypeMap?.[changeType] || '--'}</span>;
      },
    },
    {
      title: '账单积分',
      dataIndex: 'changeBalance',
      render: (_, { changeBalance }) => {
        return <span>{changeBalance ? `${changeBalance}` : '--'}</span>;
      },
    },
    {
      title: '业务方式',
      dataIndex: 'businessMode',
      render: (_, { businessMode }) => {
        return <span>{businessModeMap?.[businessMode] || '--'}</span>;
      },
    },
  ];

  const handleTableChange = (pagination, filters, sorter) => {
    setTableParams({
      pagination,
      filters,
      sortOrder: Array.isArray(sorter) ? undefined : sorter.order,
      sortField: Array.isArray(sorter) ? undefined : sorter.field,
    });
  };

  const onChargeTypeChange = (val) => {
    setChargeType(val)

    // 日期变化，重置到第一页
    setTableParams({
      ...tableParams,
      pagination: {
        ...tableParams.pagination,
        current: 1,
      },
    });
  };

  const onFinish = (values) => {
    const { code } = values
    if (!code) {
      message.warning('请输入验证码')
      return
    }
    const params = {
      ...values,
      id: editUserId,
    }
    editUserInfo(params)
  }

  const onValuesChange = (changedValues) => {
    if ('mobile' in changedValues) {
      if (mobileRegex.test(changedValues.mobile)) {
        setHasMobile(true)
      } else {
        setHasMobile(false)
      }
    }
  };

  const handleEditUserInfo = () => {
    form.setFieldsValue({
      nickname: profile?.nickname,
      email: profile?.email,
      mobile: profile?.mobile,
    })
    onValuesChange({ 'mobile': profile?.mobile })
    setEditUserId(profile?.id)
    setEditUserInfoVisible(true)
  }

  const handleSendCode = () => {
    if (!hasMobile || isCounting) return
    startCountdown()
    getMobileCode({ mobile: form.getFieldValue('mobile'), scene: 3 })
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.userInfoWrapper}>
          <div className={styles.userInfoHeader}>
            <Avatar size={58} icon={<UserOutlined />} />
            <div className={styles.memberPrivilege}>
              <div>
                {`余额：${profile?.balance || 0}积分`}
              </div>
            </div>
          </div>

          <div className={styles.userInfoBody}>
            <div className={styles.mobileWrapper}>
              <span>
                {`手机号：${profile?.mobile || '--'}`}
              </span>
              <EditFilled onClick={handleEditUserInfo} className={styles.editIcon} />
            </div>
            <div className={styles.emailWrapper}>
              <span>
                {`邮箱号：${profile?.email || '--'}`}
              </span>
              <EditFilled onClick={handleEditUserInfo} className={styles.editIcon} />
            </div>
          </div>

          <div className={styles.userInfoFooter}>
            <Button onClick={() => setShowOrderDialog(true)} type='primary'>充值</Button>
          </div>
        </div>
      </div>

      <div className={styles.tableWrapper}>
        <div className={styles.tableHeaderWrapper}>
          <div className={styles.tableTitle}>
            账单记录
          </div>
          <div className={styles.tableFilter}>
            <span>
              交易类型：
            </span>
            <Select
              showSearch
              placeholder="请选择交易类型"
              optionFilterProp="label"
              value={chargeType}
              onChange={onChargeTypeChange}
              style={{ width: 130 }}
              options={[
                {
                  value: 'all',
                  label: '全部',
                },
                {
                  value: 'token_cost',
                  label: '消费',
                },
                {
                  value: 'recharge',
                  label: '充值',
                },
              ]}
            />
          </div>
        </div>

        <Table
          columns={columns}
          dataSource={userBalanceLog}
          loading={loading}
          pagination={tableParams.pagination}
          onChange={handleTableChange}
          rowKey="id"
        />
      </div>

      <Modal
        open={editUserInfoVisible}
        className={styles.modalContainer}
        width={540}
        onCancel={() => setEditUserInfoVisible(false)}
        footer={null}
        maskClosable={false}
        destroyOnClose={true}
        closable={{ closeIcon: <CloseCircleOutlined style={{ fontSize: '18px' }} /> }}
      >
        <div className={styles.modalContent}>
          <div className={styles.header}>
            <div className={styles.title}>
              个人资料设置
            </div>
            <Avatar size={80} icon={<UserOutlined />} className={styles.userAvatar} />
            <Button>修改头像</Button>
          </div>

          <Form
            form={form}
            onFinish={onFinish}
            onValuesChange={onValuesChange}
            layout='vertical'
            preserve={false}
          >
            <Form.Item
              label='用户名'
              name='nickname'
            >
              <Input></Input>
            </Form.Item>

            <Form.Item
              label="密码"
              name="password"
            >
              <Input.Password placeholder='请输入密码' />
            </Form.Item>

            <Form.Item
              label='邮箱'
              name='email'
            >
              <Input placeholder='请输入邮箱'></Input>
            </Form.Item>

            <Form.Item
              label="手机号"
              name="mobile"
            >
              <Input placeholder='请输入手机号' />
            </Form.Item>

            <Form.Item
              label="验证码"
              name="code"
            >
              <Input
                placeholder="请输入验证码"
                suffix={
                  <Button color="cyan" variant="link" onClick={handleSendCode} disabled={!hasMobile || isCounting} loading={getMobileCodeLoading} style={{ height: '20px' }}>
                    {isCounting ? `${countdown}秒后重新发送` : '发送验证码'}
                  </Button>
                }
              />
            </Form.Item>

            <div className={styles.formFooter}>
              <Button color="default" variant="filled" onClick={() => setEditUserInfoVisible(false)}>暂不修改</Button>
              <Button type="primary" onClick={() => form.submit()} loading={editing}>确认修改</Button>
            </div>
          </Form>
        </div>
      </Modal>
    </div>
  )
};

export default withAuth(UserCenter);
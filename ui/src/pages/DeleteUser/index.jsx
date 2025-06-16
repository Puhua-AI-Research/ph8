import { message, Button, Modal, Form, Input } from 'antd';
import { CloseCircleOutlined, ExclamationCircleFilled } from '@ant-design/icons';
import styles from './index.less'
import { useRequest } from 'ahooks';
import { useGlobalStore } from '@/store/useGlobalStore'
import { mobileRegex } from '@/constant'
import { getMobileCodeApi, removeUserApi } from '@/request/login'
import { useEffect, useState } from 'react';
const { confirm } = Modal;


const DeleteUser = () => {
  const { profile, showDeleteUserModal, setShowDeleteUserModal } = useGlobalStore()
  const [hasMobile, setHasMobile] = useState(false)
  const [form] = Form.useForm();

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

  const { loading: deleting, run: removeUser } = useRequest(removeUserApi, {
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
      message.success('注销账号成功')
      setShowDeleteUserModal(false)
      setTimeout(() => {
        window.location.reload()
      }, 500);
    },
  });

  useEffect(() => {
    if (showDeleteUserModal && profile?.mobile) {
      form.setFieldsValue({ mobile: profile.mobile })
      setHasMobile(true)
    }
  }, [showDeleteUserModal, profile])

  const onFinish = (values) => {
    const { code } = values
    if (!code) {
      message.warning('请输入验证码')
      return
    }
    removeUser({ code })
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

  const showConfirm = () => {
    confirm({
      title: '确认注销账户？',
      icon: <ExclamationCircleFilled />,
      content: '注销账户后，你的个人数据将被删除，请谨慎操作。',
      onOk() {
        form.submit();
      },
      onCancel() {
      },
    });
  };

  const handleSendCode = () => {
    if (!hasMobile || isCounting) return
    startCountdown()
    getMobileCode({ mobile: form.getFieldValue('mobile'), scene: 6 })
  }

  return (
    <Modal
      open={showDeleteUserModal}
      className={styles.modalContainer}
      width={540}
      onCancel={() => setShowDeleteUserModal(false)}
      footer={null}
      maskClosable={false}
      destroyOnClose={true}
      closable={{ closeIcon: <CloseCircleOutlined style={{ fontSize: '18px' }} /> }}
    >
      <div className={styles.modalContent}>
        <div className={styles.header}>
          <div className={styles.title}>
            注销账户
          </div>
        </div>

        <Form
          form={form}
          onFinish={onFinish}
          onValuesChange={onValuesChange}
          layout='vertical'
          preserve={false}
        >
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
            <Button color="default" variant="filled" onClick={() => setShowDeleteUserModal(false)}>暂不注销</Button>
            <Button type="primary" onClick={showConfirm} loading={deleting}>确认注销</Button>
          </div>
        </Form>
      </div>
    </Modal>
  )
};

export default DeleteUser;
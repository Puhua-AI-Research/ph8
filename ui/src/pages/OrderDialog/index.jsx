import { App, Button, Modal, Space, Radio, Spin, message } from 'antd';
import { useOrderStore } from '@/store/useOrderStore'
import { useGlobalStore } from '@/store/useGlobalStore'
import { useEffect, useMemo, useRef, useState } from 'react';
import { CloseCircleOutlined } from '@ant-design/icons';
import styles from './index.less';
import { StarIcon } from '@/utils/icons'
import classNames from 'classnames';
import { useRequest } from 'ahooks';
import { getProductListApi, createOrderApi, checkOrderApi } from '@/request/order'
import dayjs from 'dayjs';
import { QRCodeSVG } from 'qrcode.react';
import AlipaySvg from '@/assets/alipay.svg';
import WxpaySvg from '@/assets/wxpay.svg';

export default () => {
  const { profile, fetchProfile } = useGlobalStore()
  const { showOrderDialog, setShowOrderDialog } = useOrderStore()

  const [productList, setProductList] = useState([])

  const [selProduct, setSelProduct] = useState(null)
  const [purchaseProduct, setPurchaseProduct] = useState(null)
  const [payChannelCode, setPayChannelCode] = useState('wx')

  const [orderTime, setOrderTime] = useState(null)
  const [orderUrl, setOrderUrl] = useState(null)
  const [orderId, setOrderId] = useState(null)
  const [createOrderFail, setCreateOrderFail] = useState(false)

  const [currentTime, setCurrentTime] = useState(null)
  const countdownRef = useRef(null)

  const hasBuy = purchaseProduct !== null

  useEffect(() => {
    if (!showOrderDialog) {
      setProductList([])
      setSelProduct(null)
      setPurchaseProduct(null)
      setPayChannelCode('wx')

      setOrderTime(null)
      setOrderUrl(null)
      setOrderId(null)
      setCreateOrderFail(false)
      setCurrentTime(null)
      if (countdownRef.current) clearInterval(countdownRef.current)
    } else {
      getProductList()
    }
  }, [showOrderDialog])

  const { loading, run: getProductList } = useRequest(getProductListApi, {
    manual: true,
    onBefore: () => {
      setProductList([])
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      setProductList(data || [])
    },
  });

  const { loading: creating, run: createOrder } = useRequest(createOrderApi, {
    manual: true,
    onBefore: () => {
      setCreateOrderFail(false)
      setOrderId(null)
      setOrderUrl(null)
      setOrderTime(null)
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        setCreateOrderFail(true)
        setOrderId(null)
        setOrderUrl(null)
        setOrderTime(null)
        return
      }
      message.success('订单创建成功，请尽快完成支付！')
      setOrderId(data.orderId)
      setOrderUrl(data.displayContent)
      setOrderTime(dayjs())
      if (countdownRef.current) clearInterval(countdownRef.current)
      countdownRef.current = setInterval(() => {
        setCurrentTime(dayjs())
      }, 1000);
    },
  });

  const orderExpired = useMemo(() => {
    if (orderTime && currentTime) {
      const finishTime = orderTime.add(1, 'hour');
      if (currentTime.isAfter(finishTime)) {
        return true
      }
    }
    return false
  }, [orderTime, currentTime])

  useRequest(() => checkOrderApi(orderId), {
    ready: !!orderId && !orderExpired,
    pollingInterval: 2000,
    onBefore: () => {
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        return
      }
      if (data?.payStatus === 10) {
        message.success('支付成功！')
        fetchProfile()
        setShowOrderDialog(false)
      }
    },
  });

  const handleCancel = () => {
    setShowOrderDialog(false)
  };

  const handlePurchase = () => {
    if (!selProduct) {
      message.warning('请选择商品')
      return
    }
    setPurchaseProduct(selProduct)
    createOrder({
      spuId: selProduct.productId,
      payChannelCode: payChannelCode
    })
  }

  const handlePayChannelChange = (code) => {
    setPayChannelCode(code)
    createOrder({
      spuId: purchaseProduct.productId,
      payChannelCode: code
    })
  }

  const leftTimeDisplay = useMemo(() => {
    if (orderTime && currentTime) {
      const finishTime = orderTime.add(1, 'hour');
      if (finishTime.isAfter(currentTime)) {
        const leftSeconds = finishTime.diff(currentTime, 'second')
        return [Math.floor(leftSeconds / 60), leftSeconds % 60]
      } else {
        if (countdownRef.current) clearInterval(countdownRef.current)
        return [0, 0]
      }
    }
    return []
  }, [orderTime, currentTime])

  const QrPlacement = () => {
    if (creating) {
      return <div className={styles.qrPlacement}><Space>订单创建中<Spin /></Space></div>
    }
    if (createOrderFail) {
      return <div className={styles.qrPlacement}>订单创建失败，请重试</div>
    }
    if (orderExpired) {
      return <div className={styles.qrPlacement}>订单已过期</div>
    }
    if (orderUrl) {
      return (
        <QRCodeSVG
          value={orderUrl}
          size={200}
          marginSize={2}
        />
      )
    }
    return (
      <div className={styles.qrPlacement}>
      </div>
    )
  }

  return (
    <>
      <Modal
        open={showOrderDialog}
        className={styles.modalContainer}
        width={1000}
        onCancel={handleCancel}
        footer={null}
        maskClosable={false}
        destroyOnClose={true}
        closable={{ closeIcon: <CloseCircleOutlined style={{ fontSize: '18px' }} /> }}
      >
        <div className={styles.modalContent}>
          <div className={styles.title}>
            充值
          </div>

          <div className={styles.header}>
            <Space>
              <StarIcon
                style={{
                  fontSize: '16px',
                  color: '#00F0FF',
                }} />
              <span>
                {`当前剩余：${profile?.balance || 0}积分`}
              </span>
            </Space>
          </div>

          <div className={styles.productList}>
            {
              productList.map((item) => {
                return (
                  <div
                    className={classNames(styles.productCard, { [styles.selected]: selProduct?.id === item.id, [styles.disabled]: hasBuy })}
                    key={item.id}
                    onClick={() => {
                      if (hasBuy) {
                        return
                      }
                      setSelProduct(item)
                    }
                    }
                  >
                    <div className={styles.iconWrapper}>
                      <StarIcon
                        style={{
                          fontSize: '40px',
                          color: '#00F0FF',
                        }}
                        className={styles.icon}
                      />
                    </div>

                    <div className={styles.content}>
                      <div className={styles.name}>{item.name}</div>
                      <div className={styles.price}>{`¥${item.price}`}</div>
                    </div>
                  </div>
                )
              })
            }
          </div>

          <div className={styles.purchaseWarning}>
            * 您充值的积分永久有效/会员服务属于虚拟商品，一经支付无法退款，请您谅解
          </div>

          {
            selProduct && !purchaseProduct && (
              <div className={styles.buyBtnWrapper}>
                <Button
                  type='primary'
                  onClick={handlePurchase}
                >
                  立即购买
                </Button>
              </div>
            )
          }

          {
            purchaseProduct && (
              <div className={styles.payWrapper}>
                <QrPlacement />

                <div className={styles.payContent}>
                  <div className={styles.payPrice}>
                    {`支付金额：¥${purchaseProduct.price}元`}
                  </div>

                  <div>
                    <Radio.Group
                      onChange={(e) => handlePayChannelChange(e.target.value)}
                      value={payChannelCode}
                      options={[
                        {
                          value: 'wx',
                          label: (
                            <div className={styles.payChannel}>
                              <img src={WxpaySvg} style={{ width: '18px' }}></img>
                              <span>微信支付</span>
                            </div>
                          ),
                        },
                        {
                          value: 'alipay',
                          label: (
                            <div className={styles.payChannel}>
                              <img src={AlipaySvg} style={{ width: '18px' }}></img>
                              <span>支付宝支付</span>
                            </div>
                          ),
                        },
                      ]}
                    />
                  </div>

                  <div>
                    请使用支付宝/微信扫码支付，开通即代表同意<a>《API服务条款》</a>
                  </div>

                  {
                    orderTime && currentTime && (
                      <div className={styles.payReminder}>
                        <span>
                          请在倒计时结束前完成支付
                          {
                            leftTimeDisplay.length === 2 && (
                              <>
                                <span className={styles.timeDigit}>{leftTimeDisplay[0]}</span>分
                                <span className={styles.timeDigit}>{leftTimeDisplay[1]}</span>秒
                              </>
                            )
                          }
                        </span>
                      </div>
                    )
                  }
                </div>
              </div>
            )
          }
        </div>
      </Modal>
    </>
  );
}

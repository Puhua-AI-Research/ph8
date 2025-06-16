import { useEffect, useState } from 'react';
import { Table, message, Popover, DatePicker, Modal } from 'antd';
import { CloseCircleOutlined } from '@ant-design/icons';
import styles from './index.less'
import { formatTime } from '@/utils'
import { useRequest } from 'ahooks';
import { taskStatisticsApi, taskListApi } from '@/request/callStatistics'
import totalApiTimesSvg from '@/assets/totalApiTimes.svg'
import totalFeeSvg from '@/assets/totalFee.svg'
import totalModelsSvg from '@/assets/totalModels.svg'
import totalTokensSvg from '@/assets/totalTokens.svg'
import { modelCategoryMap, chargeModeMap, formatJson } from '@/utils';
import { withAuth } from '@/hocs/withAuth';
import SyntaxHighlighter from 'react-syntax-highlighter';
import { a11yDark } from 'react-syntax-highlighter/dist/esm/styles/hljs'
import PrettyMarkdown from '@/components/PrettyMarkdown';


const { RangePicker } = DatePicker;

const CallStatistics = () => {
  const [taskListData, setTaskListData] = useState([])
  const [taskStatisData, setTaskStatisData] = useState({})
  const [tableParams, setTableParams] = useState({
    pagination: {
      current: 1,
      pageSize: 10,
    },
  });
  const [dateRangeStr, setDateRangeStr] = useState(null)

  const [showModal, setShowModal] = useState(false)
  const [modalTitle, setModalTitle] = useState(null)
  const [modalContentType, setModalContentType] = useState(null)
  const [modalContent, setModalContent] = useState('')

  useEffect(() => {
    getTaskStatistics()
  }, [])

  useEffect(() => {
    const params = {
      pageNo: tableParams.pagination.current,
      pageSize: tableParams.pagination.pageSize,
    }

    if (dateRangeStr && dateRangeStr.length === 2 && dateRangeStr[0] && dateRangeStr[1]) {
      params.callTime = [`${dateRangeStr[0]} 00:00:00`, `${dateRangeStr[1]} 23:59:59`]
    }

    getTaskList(params)
  }, [
    tableParams.pagination?.current,
    tableParams.pagination?.pageSize,
    tableParams?.sortOrder,
    tableParams?.sortField,
    JSON.stringify(tableParams.filters),

    dateRangeStr
  ]);

  const { loading: taskStatisLoading, run: getTaskStatistics } = useRequest(taskStatisticsApi, {
    manual: true,
    onBefore: () => {
      setTaskStatisData({})
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      setTaskStatisData(data)
    },
  });

  const { loading: taskListLoading, run: getTaskList } = useRequest(taskListApi, {
    manual: true,
    onBefore: () => {
    },
    onFinally: () => {
    },
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        setTaskListData([])
        return
      }
      setTaskListData(data?.list || [])

      setTableParams({
        ...tableParams,
        pagination: {
          ...tableParams.pagination,
          total: data?.total || 0,
        },
      });
    },
  });

  const handleShowModal = (title, contentType, content) => {
    setModalTitle(title)
    setModalContentType(contentType)
    setModalContent(content)
    setShowModal(true)
  }

  const TaskStatisCard = ({ cardType, data }) => {
    let title = ''
    let value = ''
    let icon = ''

    switch (cardType) {
      case 'totalApiTimes':
        title = '调用服务总次数'
        value = data?.totalApiTimes || 0
        icon = totalApiTimesSvg
        break;
      case 'totalFee':
        title = '调用总积分消耗'
        value = data?.totalFee || 0
        value = `${value}`
        icon = totalFeeSvg
        break;
      case 'totalModels':
        title = '调用模型总数'
        value = data?.totalModels || 0
        icon = totalModelsSvg
        break;
      case 'totalTokens':
        title = '调用token总数'
        value = data?.totalTokens || 0
        icon = totalTokensSvg
        break;
    }

    return (
      <div className={styles.card}>
        <div className={styles.cardContent}>
          <div className={styles.cardTitle}>
            {title}
          </div>
          <div className={styles.value}>
            {value}
          </div>
        </div>

        <div className={styles.iconWrapper}>
          <img src={icon}></img>
        </div>
      </div>
    )
  }

  const columns = [
    {
      title: '调用ID',
      dataIndex: 'id',
    },
    {
      title: '模型名称',
      dataIndex: 'modelName',
      width: 150,
      render: (_, { modelName }) => {
        return (
          <Popover
            content={
              <div style={{
                maxWidth: '300px',
                wordBreak: 'break-all',
                overflowWrap: 'break-word',
              }}>
                {modelName}
              </div>
            }
            title="模型名称"
            trigger="click"
          >
            <div style={{
              maxWidth: 150,
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
              cursor: 'pointer'
            }}>
              {modelName}
            </div>
          </Popover>
        )
      },
    },
    {
      title: '模型类型',
      dataIndex: 'modelType',
      render: (_, { modelType }) => <span>{modelCategoryMap[modelType]}</span>
    },
    {
      title: '版本号',
      dataIndex: 'version',
    },
    {
      title: '请求内容',
      dataIndex: 'requestBody',
      width: 150,
      render: (_, { requestBody }) => {
        return (
          <div
            style={{
              maxWidth: 150,
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
              cursor: 'pointer'
            }}
            onClick={() => handleShowModal('请求内容', 'json', formatJson(requestBody))}
          >
            {requestBody}
          </div>
        )
      },
    },
    {
      title: '返回结果',
      dataIndex: 'responseBody',
      width: 150,
      render: (_, { responseBody }) => {
        return (
          <div
            style={{
              maxWidth: 150,
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
              cursor: 'pointer'
            }}
            onClick={() => handleShowModal('返回结果', 'md', responseBody)}
          >
            {responseBody}
          </div>
        )
      },
    },
    {
      title: '计费方式',
      dataIndex: 'chargeMode',
      render: (_, { chargeMode }) => <span>{chargeModeMap[chargeMode] || chargeMode}</span>
    },
    {
      title: '计费数量',
      dataIndex: 'tokens',
    },
    {
      title: '调用单价',
      dataIndex: 'unitPrice',
    },
    {
      title: '调用时间',
      dataIndex: 'callTime',
      render: (_, { callTime }) => {
        return <span>{formatTime(callTime)}</span>;
      },
    },
    {
      title: '消耗积分',
      dataIndex: 'totalFee',
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

  const onDateChange = (date, dateString) => {
    setDateRangeStr(dateString);

    // 日期变化，重置到第一页
    setTableParams({
      ...tableParams,
      pagination: {
        ...tableParams.pagination,
        current: 1,
      },
    });
  };

  return (
    <>
      <div className={styles.container}>
        <div className={styles.statisContainer}>
          <TaskStatisCard
            cardType="totalModels"
            data={taskStatisData}
          />
          <TaskStatisCard
            cardType="totalTokens"
            data={taskStatisData}
          />
          <TaskStatisCard
            cardType="totalApiTimes"
            data={taskStatisData}
          />
          <TaskStatisCard
            cardType="totalFee"
            data={taskStatisData}
          />
        </div>

        <div className={styles.tableWrapper}>
          <div className={styles.tableHeaderWrapper}>
            <RangePicker onChange={onDateChange} />
          </div>

          <Table
            columns={columns}
            dataSource={taskListData}
            loading={taskListLoading}
            pagination={tableParams.pagination}
            onChange={handleTableChange}
            rowKey="id"
          />
        </div>
      </div>

      <Modal
        open={showModal}
        className={styles.modalContainer}
        onCancel={() => setShowModal(false)}
        width={800}
        title={modalTitle}
        style={{
          top: 50,
        }}
        styles={{
          header: {
            textAlign: 'center',
          },
          body: {
            minHeight: '100px',
            maxHeight: 'calc(100vh - 200px)',
            padding: '0 10px 10px 10px',
            overflow: 'auto',
          }
        }}
        footer={null}
        maskClosable={true}
        destroyOnClose={true}
        closable={{ closeIcon: <CloseCircleOutlined style={{ fontSize: '18px' }} /> }}
      >
        <div className={styles.modalMain}>
          <div className={styles.content}>
            {
              modalContentType === 'json' && (
                <SyntaxHighlighter language={'json'} style={a11yDark}>
                  {modalContent}
                </SyntaxHighlighter>
              )
            }
            {
              modalContentType === 'md' && (
                <PrettyMarkdown content={modalContent} />
              )
            }
            {
              modalContentType !== 'json' && modalContentType !== 'md' && (
                { modalContent }
              )
            }
          </div>
        </div>
      </Modal>
    </>
  )
};

export default withAuth(CallStatistics);
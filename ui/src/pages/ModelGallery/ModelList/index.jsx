import { App, Button, Input, message, Carousel } from 'antd';
import { useModelGalleryStore } from '@/store/useModelGalleryStore'
import { useRequest } from 'ahooks';
import modelGalleryBanner from '@/assets/modelGalleryBanner.svg'
import { getModelGalleryListApi, getModelTagListApi } from '@/request/modelGallery';
import classNames from 'classnames';
import styles from './index.less';
import { useMemo, useState } from 'react';
import { SearchOutlined } from '@ant-design/icons';
import { useNavigate } from 'umi';


export default () => {
  const { modelList, setModelList } = useModelGalleryStore()
  const [modelCategory, setModelCategory] = useState('all')
  const [allModelCategory, setAllModelCategory] = useState([])
  const [searchText, setSearchText] = useState('')
  const navigate = useNavigate();

  const filteredModelList = useMemo(() => {
    return searchText === '' ? modelList : modelList.filter(item => item.name.toLocaleLowerCase().includes(searchText.toLocaleLowerCase()))
  }, [modelList, searchText])

  useRequest(() => getModelGalleryListApi({ type: modelCategory === 'all' ? '' : modelCategory }), {
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      setModelList(data || [])
    },
    refreshDeps: [modelCategory],
  })

  useRequest(getModelTagListApi, {
    manual: false,
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      const newData = [
        {
          "name": "全部",
          "type": "all"
        }
        , ...(data || [])]
      setAllModelCategory(newData)
    },
  })

  const onClickModelItem = (id) => {
    navigate(`/modelDetail/${id}`)
  }

  return (
    <div className={styles.flexWrapper}>
      <div className={styles.container}>
        <div className={styles.banner}>
          <Carousel autoplay arrows>
            <div>
              <img src={modelGalleryBanner}></img>
            </div>
            <div>
              <img src={modelGalleryBanner}></img>
            </div>
            <div>
              <img src={modelGalleryBanner}></img>
            </div>
          </Carousel>
        </div>

        <div className={styles.header}>
          <div className={styles.modelCategory}>
            {allModelCategory.map(({ type: item, name }) => {
              return (
                <Button
                  onClick={() => setModelCategory(item)}
                  key={item}
                  className={modelCategory === item ? styles.active : ''}
                >
                  {name}
                </Button>
              )
            })}
          </div>

          <div className={styles.searchBox}>
            <Input value={searchText} onChange={e => setSearchText(e.target.value)} prefix={<SearchOutlined />} placeholder='请输入模型名称'></Input>
          </div>
        </div>

        <div className={styles.main}>
          {
            filteredModelList.map(item => {
              return (
                <div
                  className={styles.modelItem}
                  key={item.id}
                >
                  <div
                    className={styles.cover}
                    onClick={() => onClickModelItem(item.id)}
                  >
                    <img src={item.coverUrl}></img>
                  </div>

                  <div className={styles.content}>
                    <div className={styles.title}>
                      {item.name}
                    </div>

                    <div className={classNames(styles.desc, styles.multiLineEllipsis)}>
                      {item.briefIntroduction}
                    </div>
                  </div>
                </div>)
            })
          }
        </div>
      </div>
    </div>
  )
}

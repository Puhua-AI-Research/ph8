import { App, Button, Input, message, Carousel, Tag } from 'antd';
import { useModelGalleryStore } from '@/store/useModelGalleryStore'
import { useRequest } from 'ahooks';
// import modelGalleryBanner from '@/assets/modelGalleryBanner.svg'
import banner1 from '@/assets/banner1@2x.png'
import banner2 from '@/assets/banner2@2x.png'
import banner3 from '@/assets/banner3@2x.png'
import { getModelGalleryListApi, getModelTagListV2Api as getModelTagListApi } from '@/request/modelGallery';
import classNames from 'classnames';
import styles from './index.less';
import { useMemo, useState } from 'react';
import { SearchOutlined } from '@ant-design/icons';
import { useNavigate } from 'umi';
import freeTag from '@/assets/freeTag.svg';
import newTag from '@/assets/newTag.svg';
import hotTag from '@/assets/hotTag.svg';
import discountTag from '@/assets/discountTag.svg';

export default () => {
  const { modelList, setModelList } = useModelGalleryStore()

  const [selAll, setSelAll] = useState(true)
  const [selModelCategory, setSelModelCategory] = useState('')
  const [allModelCategory, setAllModelCategory] = useState([])
  const [selModelTag, setSelModelTag] = useState('')
  const [allModelTag, setAllModelTag] = useState([])

  const [searchText, setSearchText] = useState('')
  const navigate = useNavigate();

  const filteredModelList = useMemo(() => {
    return searchText === '' ? modelList : modelList.filter(item => item.name.toLocaleLowerCase().includes(searchText.toLocaleLowerCase()))
  }, [modelList, searchText])

  useRequest(() => getModelGalleryListApi({ type: selAll ? '' : selModelCategory, tags: selAll ? '' : selModelTag }), {
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      setModelList(data || [])
    },
    refreshDeps: [selModelCategory, selModelTag, selAll],
  })

  useRequest(getModelTagListApi, {
    manual: false,
    onSuccess({ code, msg, data }) {
      if (code !== 0) {
        message.warning(msg)
        return
      }
      for (const item of data) {
        if (item.name === 'charge') {
          setAllModelTag(item?.list || [])
        }

        if (item.name === 'modelType') {
          setAllModelCategory(item?.list || [])
        }
      }
    },
  })

  const onClickModelItem = (id) => {
    navigate(`/modelDetail/${id}`)
  }

  const getTagImage = (tag) => {
    switch (tag) {
      case 'free':
        return freeTag
      case 'discount':
        return discountTag
      case 'new':
        return newTag
      case 'hot':
        return hotTag
    }
  }

  const handlClickAll = () => {
    setSelAll(true)
    setSelModelCategory('')
    setSelModelTag('')
  }

  const handlClickModelTag = (tag) => {
    setSelAll(false)
    setSelModelTag(tag)
  }

  const handlClickModelCategory = (category) => {
    setSelAll(false)
    setSelModelCategory(category)
  }

  return (
    <div className={styles.flexWrapper}>
      <div className={styles.container}>
        <div className={styles.banner}>
          <Carousel autoplay arrows>
            <div>
              <img src={banner1}></img>
            </div>
            <div>
              <img src={banner2}></img>
            </div>
            <div>
              <img src={banner3}></img>
            </div>
          </Carousel>
        </div>

        <div className={styles.header}>
          <div className={styles.modelFilter}>
            <Button
              onClick={handlClickAll}
              className={selAll ? styles.active : ''}
            >
              全部
            </Button>

            {allModelTag.map(({ type: item, name }) => {
              return (
                <Button
                  onClick={() => handlClickModelTag(item)}
                  key={item}
                  className={selModelTag === item ? styles.active : ''}
                >
                  {name}
                </Button>
              )
            })}

            <div className={styles.divider}>
            </div>

            {allModelCategory.map(({ type: item, name }) => {
              return (
                <Button
                  onClick={() => handlClickModelCategory(item)}
                  key={item}
                  className={selModelCategory === item ? styles.active : ''}
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
                    <img src={item.coverUrl} className={styles.coverImg}></img>
                    {
                      item.tag && getTagImage(item.tag) && (
                        <img src={getTagImage(item.tag)} className={styles.tag}></img>
                      )
                    }
                  </div>

                  <div className={styles.content}>
                    <div className={styles.labels}>
                      {item.labelList && item.labelList.map(label => (<Tag key={label} color='rgba(0,240,255,0.09)' className={styles.label}>{label}</Tag>))}
                    </div>

                    <div className={classNames(styles.desc, styles.multiLineEllipsis)}>
                      {item.briefIntroduction}
                    </div>
                  </div>

                  <div className={styles.footer}>
                    {
                      item.originalPrice === 0 && item.inferencePrice === 0 ? (
                        <div>限免</div>
                      ) : item.originalPrice === item.inferencePrice ? (
                        <div>模型调用：{item.originalPriceStr}</div>
                      ) : item.inferencePrice === 0 ? (
                        <>
                          <div>模型调用：<span className={styles.noUse}>{item.originalPriceStr}</span></div>
                          <div>限免</div>
                        </>
                      ) : (
                        <>
                          <div>模型调用：<span className={styles.noUse}>{item.originalPriceStr}</span></div>
                          <div>折后：{item.inferencePriceStr}</div>
                        </>
                      )
                    }
                  </div>
                </div>)
            })
          }
        </div>
      </div>
    </div>
  )
}

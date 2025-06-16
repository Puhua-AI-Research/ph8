package com.puhua.module.infra.dal.mysql.db;

import com.puhua.framework.mybatis.core.mapper.BaseMapperX;
import com.puhua.module.infra.dal.dataobject.db.DataSourceConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 *
 * @author 中航普华
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigDO> {
}

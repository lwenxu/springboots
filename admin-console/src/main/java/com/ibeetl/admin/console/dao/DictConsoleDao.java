package com.ibeetl.admin.console.dao;

import java.util.List;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;

import com.ibeetl.admin.core.entity.CoreDict;

/**
 * CoreDict Dao
 */
@SqlResource("console.dict")
public interface DictConsoleDao extends BaseMapper<CoreDict>{
    public PageQuery<CoreDict> queryByCondition(@Param("query") PageQuery query);
    public void batchDelCoreDictByIds(@Param("ids") List<Long> ids);
}
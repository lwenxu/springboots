package com.ibeetl.admin.console.dao;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;

import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.entity.CoreUserRole;
import com.ibeetl.admin.core.util.enums.GeneralStateEnum;

import java.util.List;

@SqlResource("console.user")
public interface UserConsoleDao extends BaseMapper<CoreUser> {

    PageQuery<CoreUser> queryByCondtion(@Param("query") PageQuery<CoreUser> query);

    void batchDelUserByIds(@Param("ids") List<Long> ids);

    void batchUpdateUserState(@Param("ids") List<Long> ids,@Param("state") GeneralStateEnum state);
    
    List<CoreUserRole> queryUserRole(@Param("id") Long id,@Param("orgId") Long orgId,@Param("roleId") Long roleId);

}


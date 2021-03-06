package com.atguigu.gmall.ums.dao;

import com.atguigu.gmall.ums.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:06:38
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}

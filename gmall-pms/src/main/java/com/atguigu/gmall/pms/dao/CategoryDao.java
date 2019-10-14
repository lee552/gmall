package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:03:31
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

    public List<CategoryVO> queryCatesAndSubs(Long pid);
	
}

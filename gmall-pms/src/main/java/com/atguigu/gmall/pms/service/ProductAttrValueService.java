package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * spu属性值
 *
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:03:31
 */
@Transactional
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageVo queryPage(QueryCondition params);

    List<ProductAttrValueEntity> querySearchAttrValueBySpuId(Long spuId);
}


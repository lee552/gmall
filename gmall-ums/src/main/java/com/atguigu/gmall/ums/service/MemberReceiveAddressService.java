package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 会员收货地址
 *
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:06:38
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageVo queryPage(QueryCondition params);

    List<MemberReceiveAddressEntity> queryReciveAddress(Long userId);
}


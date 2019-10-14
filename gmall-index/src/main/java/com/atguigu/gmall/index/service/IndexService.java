package com.atguigu.gmall.index.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;

import java.util.List;

public interface IndexService {
    List<CategoryEntity> queryLevel1Cates();

    Resp<List<CategoryVO>> queryCatesAndSubs(Long pid);
}

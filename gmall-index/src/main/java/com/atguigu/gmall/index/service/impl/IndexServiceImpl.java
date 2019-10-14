package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.annotation.GamllCache;
import com.atguigu.gmall.index.feign.GmallPmsFeign;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private GmallPmsFeign gmallPmsFeign;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public static final String MUNE_PREFIX = "INDEX:CAT:";

    @Override
    public List<CategoryEntity> queryLevel1Cates() {
        Resp<List<CategoryEntity>> categoryTree = gmallPmsFeign.categoryTree(1, 0);
        return categoryTree.getData();

    }

    @Override
    @GamllCache(MUNE_PREFIX)
    public Resp<List<CategoryVO>> queryCatesAndSubs(Long pid) {

        /*String categoryVOsString = redisTemplate.opsForValue().get(MUNE_PREFIX + pid);

        if(StringUtils.isEmpty(categoryVOsString)){*/
            Resp<List<CategoryVO>> listResp = gmallPmsFeign.queryCatesAndSubs(pid);

            return listResp;
       /* }

        List<CategoryVO> categoryVOS = JSON.parseArray(categoryVOsString, CategoryVO.class);


        return Resp.ok(categoryVOS);*/
    }
}

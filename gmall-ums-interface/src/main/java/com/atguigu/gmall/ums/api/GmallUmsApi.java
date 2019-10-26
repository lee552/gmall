package com.atguigu.gmall.ums.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GmallUmsApi {
    @GetMapping("ums/member/query")
    public Resp<MemberEntity> query(@RequestParam("username")String username, @RequestParam("password")String password);

    /**
     * 根据用户id查询用户收货地址
     * @param userId 用户id
     * @return 收货地址的List集合
     */
    @GetMapping("ums/memberreceiveaddress/{userId}")
    public Resp<List<MemberReceiveAddressEntity>> queryReciveAddress(@PathVariable("userId")Long userId);


    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    @GetMapping("ums/member/info/{id}")
    public Resp<MemberEntity> info(@PathVariable("id") Long id);
}

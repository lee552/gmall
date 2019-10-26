package com.atguigu.gmall.ums.controller;

import java.util.Arrays;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;




/**
 * 会员
 *
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:06:38
 */
@Api(tags = "会员 管理")
@RestController
@RequestMapping("ums/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("regist")
    public Resp<Object> regist(@RequestBody MemberEntity memberEntity,@RequestParam String code){
        Boolean result = memberService.regist(memberEntity,code);

        return Resp.ok(result);
    }

    @GetMapping("check/{data}/{type}")
    public Resp<Object> check(@PathVariable("data")String data,@PathVariable("type")Integer type){
        Boolean result = memberService.check(data,type);

        return Resp.ok(result);
    }

    @GetMapping("code/{phoneNum}")
    public Resp<Object> makeCode(@PathVariable("phoneNum")String phoneNum) throws IllegalAccessException {
        memberService.makeCode(phoneNum);

        return Resp.ok(null);
    }

    @GetMapping("query")
    public Resp<MemberEntity> query(@RequestParam("username")String username,@RequestParam("password")String password){

        MemberEntity memberEntity = memberService.query(username,password);

        return Resp.ok(memberEntity);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ums:member:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = memberService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('ums:member:info')")
    public Resp<MemberEntity> info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return Resp.ok(member);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ums:member:save')")
    public Resp<Object> save(@RequestBody MemberEntity member){
		memberService.save(member);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ums:member:update')")
    public Resp<Object> update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ums:member:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}

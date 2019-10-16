package com.atguigu.gmall.ums.service.impl;

import com.atguigu.core.utils.DateUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import com.atguigu.core.utils.SendSmsUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;



@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Autowired
    private MemberDao memberDao;

    @Override
    public Boolean check(String data, Integer type) {
        QueryWrapper<MemberEntity> queryWrapper = new QueryWrapper<>();
        if(type == 1){
            queryWrapper.eq("username",data);
        }else if(type == 2){
            queryWrapper.eq("mobile",data);
        }else  if(type == 3){
            queryWrapper.eq("email",data);
        }else{
            return false;
        }

        Integer count = memberDao.selectCount(queryWrapper);
        return count == 0;
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void makeCode(String phoneNum) throws IllegalAccessException {
        String ifExistCode = redisTemplate.opsForValue().get(SendSmsUtil.CODE_PREFIX + phoneNum + ":");
        if(!StringUtils.isEmpty(ifExistCode)){
            throw new IllegalAccessException("请不要重复申请注册码");
        }
        Integer code = (int) ((Math.random() * 9 + 1) * 100000);
        SendSmsUtil.SendSms(phoneNum,code.toString());
        redisTemplate.opsForValue().set(SendSmsUtil.CODE_PREFIX+phoneNum+":",code.toString(),15, TimeUnit.MINUTES);

    }

    @Override
    public Boolean regist(MemberEntity memberEntity, String code) {
        MemberEntity one = this.getOne(new QueryWrapper<MemberEntity>().eq("username", memberEntity.getUsername()).or().eq("email",memberEntity.getEmail()));
        if(one !=null){
            return false;
        }
        String mobile = memberEntity.getMobile();
        if(StringUtils.isEmpty(mobile)){
            return false;
        }
        String key = SendSmsUtil.CODE_PREFIX+mobile+":";
        String redisCode = redisTemplate.opsForValue().get(key);
        if(StringUtils.isEmpty(redisCode)){
            return false;
        }
        if(!StringUtils.equals(code,redisCode)){
            return false;
        }
        String salt = StringUtils.replace(UUID.randomUUID().toString(),"-","");
        memberEntity.setSalt(salt);
        memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword()+salt));
        memberEntity.setCreateTime(new Date());
        this.save(memberEntity);
        return null;
    }


    @Override
    public MemberEntity query(String username, String password) {
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return null;
        }

        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", username));
        if(memberEntity == null){
            return null;
        }
        String salt = memberEntity.getSalt();
        return this.getOne(new QueryWrapper<MemberEntity>().eq("username", username).eq("password", DigestUtils.md5Hex(password + salt)));
    }


}
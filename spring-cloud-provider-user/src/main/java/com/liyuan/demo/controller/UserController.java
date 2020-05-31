package com.liyuan.demo.controller;
import com.liyuan.demo.domain.User;
import com.liyuan.demo.req.UserAddDto;
import com.liyuan.demo.res.BaseResVo;
import com.liyuan.demo.res.UserVo;
import com.liyuan.demo.utils.CopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(value = "/user", description = "")
public class UserController{

    @ApiOperation(value = "查询",notes = "根据ID查询",httpMethod = "GET")
    @GetMapping(value = "/queryByName")
    public BaseResVo query(@ApiParam(value = "", required = true)@RequestParam String name, HttpServletRequest request) {
        log.info("login,ip={},port={}",request.getLocalAddr(),request.getLocalPort());
        UserVo vo = UserVo.builder().id((int)(Math.random() + 10)).name(name).age(30).build();
        return BaseResVo.builder().resCode(200).resMsg("查询成功").data(vo).build();
    }

    @ApiOperation(value = "新增",notes = "新增",httpMethod = "POST")
    @PostMapping(value = "/add")
    public UserVo queryCount(@RequestBody @Valid UserAddDto form) {
        User user = CopyUtil.transfer(form,User.class);
        return CopyUtil.transfer(user,UserVo.class);
    }

}
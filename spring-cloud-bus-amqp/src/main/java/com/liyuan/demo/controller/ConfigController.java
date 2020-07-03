package com.liyuan.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/config")
@Api(value = "/config", description = "")
public class ConfigController{

    @Value("${from}")
    private String from;

    @ApiOperation(value = "获取from参数配置",notes = "获取from参数配置",httpMethod = "GET")
    @GetMapping
    public String getFrom(){
        return from;
    }
}
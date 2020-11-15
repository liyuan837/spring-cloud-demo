/**
 * KAYO APPLIANCE CHAINS.
 * Copyright (c) 2016-2016 All Rights Reserved.
 */
package com.liyuan.demo.rest.base;

import com.alibaba.fastjson.JSON;
import com.liyuan.demo.res.BaseResVo;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 功能描述: rest服务基类
 *
 * @author suiying
 * @created 2018年9月6日
 * @version 1.0.0
 */
@Service
public class BaseRest {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 
     * 功能描述: 处理get请求
     *
     * @param url
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    protected String dealGetServer(String url,Map<String,Object> paramVariables) {

        StringBuilder sb = new StringBuilder(64);

        // 封装必要参数
        sb.append(url).append("?timestamp=").append(System.currentTimeMillis());

        if(MapUtils.isNotEmpty(paramVariables)){
            paramVariables.entrySet().stream().forEach(entry -> sb.append("&").append(entry.getKey()).append("=").append(entry.getValue()));
        }

        return restTemplate.getForObject(sb.toString(), String.class);
    }

    /**
     * 
     * 功能描述: 处理post请求
     *
     * @param url
     * @param params
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    protected String dealPostServer(String url, MultiValueMap<String, String> params) {

        return restTemplate.postForEntity(url, new HttpEntity<MultiValueMap<String, String>>(params, new HttpHeaders()),
                String.class).getBody();
    }

    /**
     *
     * 功能描述: 微服务请求异常
     *
     * @param code
     * @param msg
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public String restError(int code, String msg) {
        BaseResVo resVo = BaseResVo.builder().resCode(code).resMsg(msg).build();

        return JSON.toJSONString(resVo);
    }
}

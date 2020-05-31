package com.liyuan.demo.controller;

import com.liyuan.demo.exception.BusinessException;
import com.liyuan.demo.res.BaseResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName BaseController
 * @Description TODO
 * @Author liyuan
 * @Date 2020/2/29 22:31
 **/
@Slf4j
public class BaseController {
    /**
     * 业务异常控制
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public BaseResVo ExceptionHandler(BusinessException e) {
        log.warn(e.getLocalizedMessage());
        return BaseResVo.builder().resCode(1007).resMsg("业务异常").data(e.getLocalizedMessage()).build();
    }

    /**
     * 运行期异常控制
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public
    @ResponseBody
    BaseResVo runtimeExceptionHandler(RuntimeException e) {
        log.error("系统异常", e);
        return BaseResVo.builder().resCode(1009).resMsg("系统异常").data(e.getLocalizedMessage()).build();
    }
}

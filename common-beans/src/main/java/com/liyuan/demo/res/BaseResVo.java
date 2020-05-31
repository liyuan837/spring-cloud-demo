package com.liyuan.demo.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BaseResVo
 * @Description TODO
 * @Author liyuan
 * @Date 2020/2/29 21:53
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResVo {
    private Integer resCode;

    private String resMsg;

    private Object data;
}

package com.liyuan.demo.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName UserVo
 * @Description TODO
 * @Author liyuan
 * @Date 2020/2/28 13:39
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {
    private Integer id;
    private String name;
    private Integer age;
}

package com.liyuan.demo.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginDto extends BaseReqDto {
	private String name;
	private String password;
}
package com.liyuan.demo.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAddDto implements Serializable {
	private Integer id;
	private String name;
	private Integer age;
}
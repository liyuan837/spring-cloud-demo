package com.liyuan.demo.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CopyUtil {
    public CopyUtil() {
    }

    public static <T> T transfer(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        } else {
            try {
                T t = targetClass.newInstance();
                BeanUtils.copyProperties(source, t);
                return t;
            } catch (InstantiationException var3) {
                var3.printStackTrace();
                return null;
            } catch (IllegalAccessException var4) {
                var4.printStackTrace();
                return null;
            }
        }
    }

    public static <T> List<T> transfer(List<?> sourceList, Class<T> targetClass) {
        return (List)(CollectionUtils.isEmpty(sourceList) ? new ArrayList() : (List)sourceList.stream().map((source) -> {
            return transfer(source, targetClass);
        }).collect(Collectors.toList()));
    }

    public static <T> T mapTransferBean(Map map, Class<T> targetClass) {
        if (map == null) {
            return null;
        } else {
            try {
                T t = targetClass.newInstance();
                org.apache.commons.beanutils.BeanUtils.populate(t, map);
                return t;
            } catch (InstantiationException var3) {
                var3.printStackTrace();
                return null;
            } catch (IllegalAccessException var4) {
                var4.printStackTrace();
                return null;
            } catch (InvocationTargetException var5) {
                var5.printStackTrace();
                return null;
            }
        }
    }
}

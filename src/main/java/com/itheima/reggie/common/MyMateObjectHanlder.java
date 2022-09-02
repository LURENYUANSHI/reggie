package com.itheima.reggie.common;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

//自定义源数据对象处理器
@Component
@Slf4j
public class MyMateObjectHanlder implements MetaObjectHandler {
    @Resource
    private HttpSession session;

    //插入是自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        long id = Thread.currentThread().getId();
        log.info("insertFill线程id为:{}", id);
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("userId", (Long) session.getAttribute("user"));
        metaObject.setValue("updateUser", (Long) session.getAttribute("user"));
        metaObject.setValue("createUser", new Long(1));

    }

    //修改时自动填充
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", (Long) session.getAttribute("user"));
    }
}

package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.pojo.Dish;

public interface DishService extends IService<Dish>  {

    //新增菜品插入口味数据，需要操作两张表, dish和disflavor
    void saveWithFlavor(DishDto dto);

    /**
     * 根据id查询菜品信息和口味信息
     * @param id
     * @return
     */
    DishDto getByIDWithFlavor(Long id);

    /**
     * 修改菜品信息和口味信息
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);

}

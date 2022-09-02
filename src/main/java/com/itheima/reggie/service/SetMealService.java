package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.pojo.Setmeal;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {
    /**
     * 新增套餐,同时需要保存套餐和菜品的名称
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐,同时删除套餐和菜品的关联数据
     * @param ids
     */
    void removeWithDish(List<Long> ids);
}

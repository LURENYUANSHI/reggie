package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.mapper.SetMealMapper;
import com.itheima.reggie.pojo.Dish;
import com.itheima.reggie.pojo.Setmeal;
import com.itheima.reggie.pojo.SetmealDish;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {

    @Resource
    private SetMealDishService setMealDishService;

    /**
     * 新增套餐,同时需要保存套餐和菜品的名称
     *
     * @param setmealDto
     */
    @Transactional(rollbackFor = SQLException.class)
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息,操作setmeal,执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());


        //保存菜品和套餐的关联信息,操作setmeal_dish表,执行insert事务
        setMealDishService.saveBatch(setmealDishes);


    }

    /**
     * 删除套餐,同时删除套餐和菜品的关联数据
     *
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐数据是否可用
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.in(Setmeal::getId,ids);

        queryWrapper.eq(Setmeal::getStatus,1);

        //如果不能删除抛出异常
        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("套餐正在售卖中,不能删除");
        }

        //如果可以删除,先删除套餐表中的数据
        this.removeByIds(ids);

        //  删除关系表中的数据setmeal_dish
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setMealDishService.remove(dishLambdaQueryWrapper);




    }
}

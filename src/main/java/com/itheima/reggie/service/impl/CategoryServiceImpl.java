package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.mapper.CategoryDao;
import com.itheima.reggie.pojo.Category;
import com.itheima.reggie.pojo.Dish;
import com.itheima.reggie.pojo.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao,Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService mealService;
    //根据id删除分类,删除之前要进行判断
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件,根据分类ID进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品,如果已经关联,那么抛出业务异常
        if(count>0){
            //如果已经关联,那么抛出业务异常
            throw new CustomException("当前分类下关联了菜品,不能删除");

        }
        //查询当前分类是否关联了套餐,如果已经关联,那么抛出异常
    LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int mealcount = mealService.count(setmealLambdaQueryWrapper);
        if(mealcount>0){
            throw new CustomException("当前分类下关联了套餐,不能删除");
        }

        //如果都没有关联,正常删除分类
        super.removeById(id);
    }
}

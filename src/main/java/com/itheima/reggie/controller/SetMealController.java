package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.pojo.Category;
import com.itheima.reggie.pojo.Setmeal;
import com.itheima.reggie.pojo.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
/**
 * 套餐管理
 */
public class SetMealController {
    @Resource
    private SetMealService setMealService;
    @Resource
    private SetMealDishService setMealDishService;
    @Resource
    private CategoryService categoryService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setMealService.saveWithDish(setmealDto);

        return R.success("新增信息成功");
    }

    /**
     * 查询套餐
     * @param categoryId
     * @param status
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(@RequestParam Long categoryId,@RequestParam int status) {
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(Setmeal::getStatus,status);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setMealService.list(queryWrapper);
        return R.success(list);

    }

    @GetMapping("/page")
    public R<Page> pages(int page, int pageSize, String name) {
        log.info("{page}", page, "{pageSize}", pageSize, "{name}", name);

        Page<Setmeal> setmealPage = new Page<>(page, pageSize);

        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null, Setmeal::getName, name);

        queryWrapper.orderByAsc(Setmeal::getUpdateTime);

        setMealService.page(setmealPage, queryWrapper);

        BeanUtils.copyProperties(setmealPage, dtoPage, "records");

        List<Setmeal> records = setmealPage.getRecords();


        List<SetmealDto> list = records.stream().map((item) -> {

            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);
            if (category != null) {

                String categoryname = category.getName();

                setmealDto.setCategoryName(categoryname);
            }
            return setmealDto;

        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 套餐删除以及批量删除功能
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setMealService.removeWithDish(ids);

        return R.success("套餐删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status, @RequestParam List<Long> ids) {
        Setmeal setmeal = new Setmeal();

        setmeal.setStatus(status);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(Setmeal::getId, ids);

        setMealService.update(setmeal, queryWrapper);

        return R.success("更改起售状态成功");


    }
}

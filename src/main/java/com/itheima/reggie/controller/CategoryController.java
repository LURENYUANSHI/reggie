package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.pojo.Category;
import com.itheima.reggie.pojo.Employee;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<IPage> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);

        IPage page1 = new Page(page, pageSize);
        //条件过滤器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //排序
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(page1, queryWrapper);

        return R.success(page1);
    }

    //删除分类
    @DeleteMapping
    public R<String> delete(long ids) {

        categoryService.remove(ids);

        return null;
    }

    //根据id修改分类信息
    @PutMapping
    public R<String> update(@RequestBody Category category) {

        categoryService.updateById(category);

        return R.success("修改分类信息成功");
    }

    //根据条件查询分类数据
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //添加排序条件

        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);

        return  R.success(list);
    }

}

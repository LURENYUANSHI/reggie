package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.pojo.Category;
import com.itheima.reggie.pojo.Dish;
import com.itheima.reggie.pojo.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dto) {
        dishService.saveWithFlavor(dto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageinfo = new Page<>(page, pageSize);
        Page<DishDto> dtopage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getUpdateTime);

        dishService.page(pageinfo, queryWrapper);

        BeanUtils.copyProperties(pageinfo, dtopage, "records");

        List<Dish> records = pageinfo.getRecords();
//        records.stream().map(new Function<Dish, Object>() {
//            @Override
//            public Object apply(Dish dish) {
//                return null;
//            }
//        }).collect(Collectors.toList());
        /**
         * item是Dish集合,这段lambada表达式的意思是一个有参构造方法
         * 将Dish对象传进去之后进行处理
         */
        List<DishDto> list = records.stream().map((item) -> {

            DishDto dishDto = new DishDto();
            /**
             * 把Dish的参数传给Dishdto,多余的参数没有接受的地方会被自动消除
             */
            BeanUtils.copyProperties(item, dishDto);
            /**
             * 获取菜单分类id
             */
            Long categoryId = item.getCategoryId();
            /**
             * 根据id查询分类对象并且封装
             */
            Category category = categoryService.getById(categoryId);
            /**
             * 判断DISH中的菜单分类id是否为空,如果不为空将菜单id传给Dishdto
             */
            if (category.getName() != null) {
                String categoryname = category.getName();
                dishDto.setCategoryName(categoryname);
            }
            /**
             * 返回dishDto对象并包装成List
             */
            return dishDto;

        }).collect(Collectors.toList());
        /**
         * Page page1=page.setRecirds(list)
         * 用于给Page对象传输参数
         */
        dtopage.setRecords(list);
        return R.success(dtopage);
    }

    /**
     * 根据id查询菜品信息和口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable long id) {
        DishDto dto = dishService.getByIDWithFlavor(id);

        return R.success(dto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);


        return R.success("修改成功");
    }

    /**
     * 根据条件查询菜品数据
     *
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//
//        queryWrapper.eq(dish.getCategoryId() != null,
//                Dish::getCategoryId, dish.getCategoryId()).eq(Dish::getStatus, 1);
//
//
//        //添加排序条件
//
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list1 = dishService.list(queryWrapper);
//
//
//        return R.success(list1);
//
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }
    /**
     * 批量起售停售
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> state(@PathVariable int status, @RequestParam List<Long> ids) {
        /**
         * update status from setmeal where id=#{id};
         */
        log.info("ids" + ids);
        List<Dish> dishes = dishService.listByIds(ids).stream().peek((item) -> {
            item.setStatus(status);
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishes);

        return R.success("菜品停售起售成功");

    }

    /**
     * 删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        dishService.removeByIds(ids);

        return R.success("菜品删除成功");
    }


}

package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.pojo.Orders;
import com.itheima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private HttpSession session;

    /**
     * 用户下单
     *
     * @return
     * @Param orders
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据:{}", orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }
    @GetMapping("/page")
    public R<Page> pageBack(int page,int pageSize){
        Page<Orders> page1=new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders>queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Orders::getOrderTime);

        Page<Orders> page2 = orderService.page(page1, queryWrapper);
        return R.success(page2);
    }


    /**
     * 根据id查询订单信息给前台
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize, String name) {
        Page<Orders> page1 = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        long userId = (long) session.getAttribute("user");

        queryWrapper.eq(Orders::getUserId, userId);

        Page<Orders> page2 = orderService.page(page1, queryWrapper);

        return R.success(page2);
    }

}

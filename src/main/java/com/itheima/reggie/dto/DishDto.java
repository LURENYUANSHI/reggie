package com.itheima.reggie.dto;


import com.itheima.reggie.pojo.Dish;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import com.itheima.reggie.pojo.DishFlavor;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}

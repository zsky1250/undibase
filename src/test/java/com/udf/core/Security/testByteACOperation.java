package com.udf.core.Security;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by 张未然 on 2015/9/12.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class testByteACOperation {

    //添加
    int addOP = 8;// ...1000=2^4
    //删除
    int deleteOP = 4;// ...0100=2^3
    // 修改
    int updateOP = 2;// ...0010=2^1
    // 查询
    int viewOP = 1;// ...0001=2^0

    int userPermission = 0;

    int addResource = 8;
    int deleteResource = 4;
    int updateResource = 2;
    int viewResource = 1;

    @Before
    public void initPermission(){
        initUserPermission();
    }

    private void initUserPermission(){
        userPermission = addOP | deleteOP ;
    }


    @Test
    public void accessControl(){
        System.out.println("初始化：用户可增删");
        System.out.print("模拟查看资源操作:");
        if((userPermission&viewResource) == viewResource){
            System.out.println("允许访问");
        }else{
            System.out.println("无权访问");
        }

        System.out.println("--> 用户添加查看权限");
        userPermission = userPermission | viewOP;

        System.out.print("模拟查看资源操作:");
        if((userPermission&viewResource) == viewResource){
            System.out.println("允许访问");
        }else{
            System.out.println("无权访问");
        }

        System.out.println("--> 用户减少增删权限");
        userPermission = userPermission & ~(addOP | deleteResource);

        System.out.print("模拟增加资源操作:");
        if((userPermission&addResource) == addResource){
            System.out.println("允许访问");
        }else{
            System.out.println("无权访问");
        }

        System.out.print("模拟删除资源操作:");
        if((userPermission&deleteResource) == deleteResource){
            System.out.println("允许访问");
        }else{
            System.out.println("无权访问");
        }
    }

    @Test
    public void testConvert(){
        System.out.print("-->int值7转为bit串打印:");
        int bit = 7;
        System.out.println(Integer.toBinaryString(bit)); // 十进制转二进制
        System.out.println();
        System.out.print("-->读2进制形式字符串'111'，转换为int打印:");
        Integer it = Integer.valueOf("111", 2);
        System.out.println(it);// 转换为10进制结果

    }


}
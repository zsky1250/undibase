package com.udf.core.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by 张未然 on 2015/8/20.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({
        @ContextConfiguration(name = "parent", locations = "classpath:config/applicationContext.xml"),
        @ContextConfiguration(name = "child", locations = "classpath:config/webApplicationContext.xml")
})
public class TreeTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testWholeStr() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tree.json")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)) //执行请求
                //.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))//使用Json path验证JSON 请参考http://goessner.net/articles/JsonPath/
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testNodeStr() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/treeNode")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)) //执行请求
                //.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)) //验证响应contentType
                //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))//使用Json path验证JSON 请参考http://goessner.net/articles/JsonPath/
                .andDo(MockMvcResultHandlers.print());
    }
}

package com.udf.core.web.view.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 继承{@link MappingJackson2JsonView} 实现了Jackson2的mixin功能。
 * <p>从配置级别决定一个类应被生成什么样子的json串，避免了将Jackson2的注解写到Entity中造成耦合的情形。
 * 这里有一些关于mixin的参考：
 * <a href="http://wiki.fasterxml.com/JacksonMixInAnnotations">#1</a>
 * <a href="http://www.cowtowncoder.com/blog/archives/2009/08/entry_305.html">#2</a>
 * <a href="http://www.studytrails.com/java/json/java-jackson-mix-in-annotation.jsp">#3</a>
 * <p>新增一个 {@code mixinMap} 属性 以键值对的形式存储一个类应该被映射成什么样子
 * 初始化完成后通过 {@code @PostConstruct},最后调用一个{@link #addMixinMapIntoObjectMapper}
 * 设置{@link ObjectMapper#setMixInAnnotations(Map)}.
 *
 *
 * Created by 张未然 on 2015/8/19.
 *
 * 目前来看 完全可以用MappingJackson2JsonView 替代 没必要自己再搞一个。
 */


@Deprecated
public class UndiMappingJackson2JsonView extends MappingJackson2JsonView {

    private Map<Class<?>,Class<?>> mixinMap;


    public Map<Class<?>,Class<?>> getMixinMap() {
        return mixinMap;
    }

    public void setMixinMap(Map<Class<?>,Class<?>> mixinMap) {
        this.mixinMap = mixinMap;
    }

    @PostConstruct
    private void addMixinMapIntoObjectMapper(){
        if(mixinMap==null||mixinMap.isEmpty()) return;
        getObjectMapper().setMixInAnnotations(mixinMap);

    }
}

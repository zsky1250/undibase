package com.udf.core.web.view.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Locale;

/**
 * Created by zwr on 2014/12/18.
 * 这个是自定义的针对Jackson2view的viewResolver
 * 在ContentnegoationViewResolver中存在两种配置
 * 1.把viewResolver和view都写到这个bean里 （推荐）
 * 2.只配置一个空bean 然后把其他的viewResolver写到外面，ContentnegoationViewResolver会自动把其他的viewResolver加进去。
 */
@Deprecated
public class UndiMappingJackson2ViewResolver implements ViewResolver {

    private static Logger log = LoggerFactory.getLogger(UndiMappingJackson2ViewResolver.class);

    public MappingJackson2JsonView getView() {
        return view;
    }

    public void setView(MappingJackson2JsonView view) {
        this.view = view;
    }

    private MappingJackson2JsonView view;



    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        if(view==null){
            view = new MappingJackson2JsonView();
            log.debug("未发现注入view，填充默认的view：{}",view.getClass());
        }
        return view;
    }
}

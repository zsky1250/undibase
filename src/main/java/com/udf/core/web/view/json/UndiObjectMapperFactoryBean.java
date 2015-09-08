package com.udf.core.web.view.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by 张未然 on 2015/8/25.
 *
 * 如果可以，尽量使用 org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean
 */
@Deprecated
public class UndiObjectMapperFactoryBean implements FactoryBean<ObjectMapper>,InitializingBean {

    private ObjectMapper objectMapper;

    public ObjectMapper getObject() throws Exception {
        return objectMapper;
    }

    public Class<?> getObjectType() {
        return ObjectMapper.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,true);
    }
}

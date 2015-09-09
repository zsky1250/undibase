package com.udf.core.orm.nestedSet.view;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.udf.core.orm.nestedSet.entity.NestedSetEntity;

/**
 *
 * 支持MappingJackson 转换属性
 * Created by zwr on 2015/9/9.
 */
@JsonFilter("treeView")
@JsonIgnoreProperties({"batchInsert","parentIDBeforeUpdate"})
@JsonPropertyOrder({"name","id","order","children"})
public abstract class NestedSetMixin extends NestedSetEntity {

     @JsonProperty("order")
     private int seq;
}

package com.udf.showcase.web.view.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.udf.showcase.entity.Catalog;

/**
 * Created by 张未然 on 2015/8/20.
 */
@JsonIgnoreProperties({"lft","rgt","parent","parentIDBeforeUpdate"})
@JsonPropertyOrder(value = {"id","name","order","parent"})
public abstract class CatalogJsonView extends Catalog {


}

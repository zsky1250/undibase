package com.udf.core.web.mvc.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.udf.core.entity.Catalog;

/**
 * Created by 张未然 on 2015/8/20.
 */
@JsonIgnoreProperties({"lft","rgt","parent","parentIDBeforeUpdate"})
public abstract class CatalogJsonView extends Catalog {


}

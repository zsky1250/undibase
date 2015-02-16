package com.udf.core.web.mvc.freeMarker;

import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by zwr on 2014/12/18.
 *
 *  目前仅仅暴露几个公共变量
 *  以后如有重构需求，可以把这个类搞成Abstract。
 */
public class UndiFreeMarkerView extends FreeMarkerView{

    /**
     * 部署路径名
     */
    public static final String CONTEXT_PATH = "base";
    public static final String RES_PATH = "res";

    /**
     * 在model中增加基础变量
     * 参数中必须使用Map
     */
    protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
        String basePath = request.getContextPath();
        String resPath = basePath + "/" + RES_PATH + "/default";
        model.put(CONTEXT_PATH, basePath);
        model.put(RES_PATH, resPath);
    }

}

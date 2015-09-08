package com.udf.core.web.support;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by 张未然 on 2015/8/24.
 */
public class DataTablesWrapper {

    public static <T>DataTables createDTInstance(Page<T> result, int draw){
        DataTables<T> dt = new DataTables<T>();
        dt.setData(result.getContent());
        dt.setDraw(draw + 1);
        dt.setRecordsTotal(result.getTotalElements());
        dt.setRecordsFiltered(result.getTotalElements());
        return dt;
    }

}

class DataTables<T>{

    private int draw;

    private List<T> data;

    private long recordsTotal;

    private long recordsFiltered;

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
}

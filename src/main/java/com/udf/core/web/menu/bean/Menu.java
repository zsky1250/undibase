package com.udf.core.web.menu.bean;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by zwr on 2014/12/16.
 */
@XmlRootElement(name="Menu")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name","items"})

public class Menu {
    @XmlAttribute
    private String name;

    @XmlElementWrapper(name="Menus")
    @XmlElement(name = "MenuItem")
    private List<MenuItem> items;

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

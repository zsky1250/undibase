package com.udf.cms.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created by 张未然 on 2015/9/20.
 */
@Entity
public class Role {
    @Id
    @GeneratedValue
    private Long id;

    private String roleName;

    //后期更改为Enum
    private String roleType;

    @OneToMany(mappedBy = "role",fetch = FetchType.LAZY)
    private List<User> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}

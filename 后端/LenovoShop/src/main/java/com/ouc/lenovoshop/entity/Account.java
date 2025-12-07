package com.ouc.lenovoshop.entity;

/**
 * 角色用户父类
 */
public class Account {
    private Integer id;
    /** 用户名 */
    private String username;
    /** 显示名称（用于展示：商家店铺名或管理员昵称），兼容替代旧的 nickname */
    private String displayName;
    /** 密码 */
    private String password;
    /** 角色标识 */
    private String role;
    /** 新密码 */
    private String newPassword;
    /** 头像 */
    private String avatar;

    private String token;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 兼容：返回用户名（原先用于昵称/显示名）
     * @return username
     */
    @Deprecated
    public String getName() {
        return this.displayName != null && !this.displayName.isEmpty() ? this.displayName : this.username;
    }

    /**
     * 兼容：设置用户名（原先用于昵称/显示名）
     */
    @Deprecated
    public void setName(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

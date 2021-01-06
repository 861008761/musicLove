package com.mylovin.music.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class UserInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer uid;
    @Column(unique = true)
    private String username;//帐号
    private String name;//名称（昵称或者真实姓名，不同系统不同定义）
    private String password; //密码;
    private String salt;//加密密码的盐
    private byte state;//用户状态,0:创建未认证（比如没有激活，没有输入验证码等等）--等待验证的用户 , 1:正常状态,2：用户被锁定.
    @ManyToMany(fetch = FetchType.EAGER)//立即从数据库中进行加载数据;
    @JoinTable(name = "SysUserRole", joinColumns = {@JoinColumn(name = "uid")}, inverseJoinColumns = {@JoinColumn(name = "roleId")})
    private List<com.mylovin.music.model.SysRole> roleList;// 一个用户具有多个角色

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //级联保存、更新、删除、刷新;延迟加载。当删除用户，会级联删除该用户的所有音乐
    //拥有mappedBy注解的实体类为关系被维护端
    //mappedBy="userInfo"中的userInfo是UserMusic中的userInfo属性
    private List<UserMusic> musicList;//音乐列表

    /**
     * 用户邮箱
     */
    private String useremail;
    /**
     * 利用UUID生成一段数字，发动到用户邮箱，当用户点击链接时
     * 在做一个校验如果用户传来的code跟我们发生的code一致，更改状态为“1”来激活用户
     */
    private String  code;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public List<com.mylovin.music.model.SysRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<com.mylovin.music.model.SysRole> roleList) {
        this.roleList = roleList;
    }

    public void setMusicList(List<UserMusic> musicList) {
        this.musicList = musicList;
    }

    public List<UserMusic> getMusicList() {
        return musicList;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 密码盐.
     * 重新对盐重新进行了定义，用户名+salt，这样就更加不容易被破解
     *
     * @return
     */
    public String getCredentialsSalt() {
        return this.username + this.salt;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", salt='" + salt + '\'' +
                ", state=" + state +
                ", roleList=" + roleList +
                ", musicList=" + musicList +
                ", useremail='" + useremail + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
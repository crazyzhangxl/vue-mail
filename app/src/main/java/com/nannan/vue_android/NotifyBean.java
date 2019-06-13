package com.nannan.vue_android;

public class NotifyBean {
    public String command;
    public String msg;

    public String id;
    public String token;
    public String username;
    public String name;
    public String mobile;
    public String emailDomain;
    public String departmentId;
    public String departmentName;
    public String companyId;
    public String companyName;
    public String account;
    public String password;
    public String employeeId;
    public String positionId;
    public String positionName;
    public String identity;
    public String companyType;


    public NotifyBean(String command, String msg) {
        this.command = command;
        this.msg = msg;
    }

    public NotifyBean(String id,
                      String token,
                      String username,
                      String name,
                      String mobile,
                      String emailDomain,
                      String departmentId,
                      String departmentName,
                      String companyId,
                      String companyName,
                      String account,
                      String password,
                      String employeeId,
                      String positionId) {
        this.id = id;
        this.token = token;
        this.username = username;
        this.name = name;
        this.mobile = mobile;
        this.emailDomain = emailDomain;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.companyId = companyId;
        this.companyName = companyName;
        this.account = account;
        this.password = password;
        this.employeeId = employeeId;
        this.positionId = positionId;
    }
}

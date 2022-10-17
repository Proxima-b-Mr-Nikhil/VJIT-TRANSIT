package com.vjit.vjittransit;

public class User {
    private  boolean istyping;
    private String id;
    private String name;
    private String phone;
    private String duration;
    private String imageurl;
    private String status;
    private String chatstatus;
    private String email;
    private String editprofile;
    private String job;
    private  String password;
    private  boolean login;
    private  String count;
    private  String busno;
    private  String driving;
    public User(boolean istyping, String id, String name, String phone, String duration, String imageurl, String status, String chatstatus, String email, String editprofile, String job, String password, boolean login, String count,String busno,String driving) {
        this.istyping = istyping;
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.duration = duration;
        this.imageurl = imageurl;
        this.status = status;
        this.chatstatus = chatstatus;
        this.email = email;
        this.editprofile = editprofile;
        this.job = job;
        this.password = password;
        this.login = login;
        this.count = count;
        this.busno=busno;
        this.driving=driving;
    }

    public User() {
    }

    public String getDriving() {
        return driving;
    }

    public void setDriving(String driving) {
        this.driving = driving;
    }

    public String getBusno() {
        return busno;
    }

    public void setBusno(String busno) {
        this.busno = busno;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public boolean isIstyping() {
        return istyping;
    }

    public void setIstyping(boolean istyping) {
        this.istyping = istyping;
    }

    public String getChatstatus() {
        return chatstatus;
    }

    public void setChatstatus(String chatstatus) {
        this.chatstatus = chatstatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getEditprofile() {
        return editprofile;
    }

    public void setEditprofile(String editprofile) {
        this.editprofile = editprofile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

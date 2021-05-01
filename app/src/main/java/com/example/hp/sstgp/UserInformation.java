package com.example.hp.sstgp;

import java.util.jar.Attributes;

public class UserInformation {
    public String name;
    public String address;
    public String image;
    public String thumbImage;
    public String online;
    UserInformation(){

    }
    UserInformation(String name, String address){
        this.name=name;
        this.address=address;

    }
    UserInformation(String image, String thumbImage,int i){
        this.image=image;
        this.thumbImage=thumbImage;

    }
    UserInformation(String name, String address ,String image ){
        this.name=name;
        this.address=address;
        this.image=image;

    }
    UserInformation(String name, String address ,String image , String thumbImage ){
        this.name=name;
        this.address=address;
        this.image=image;
        this.thumbImage=thumbImage;
    }
    UserInformation(String name, String address ,String image , String thumbImage , String online ){
        this.name=name;
        this.address=address;
        this.image=image;
        this.thumbImage=thumbImage;
        this.online= online;
    }
    public String getName(){
        return name;
    }
    public String getAddress(){
        return address;
    }
    public String getThumbImage(){
        return thumbImage;
    }


}

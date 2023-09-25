package com.example.aidltest;

public class Person {
    public  String name;
    private String age;
    private Address address;

    public Person(String name,String age,Address address){
        this.name=name;
        this.age = age;
        this.address = address;
    }
}

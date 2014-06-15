package com.ruenzuo.through.models.enums;

/**
 * Created by renzocrisostomo on 14/06/14.
 */
public enum ServiceType {

    SERVICE_TYPE_NONE ("None"), SERVICE_TYPE_TWITTER ("Twitter"), SERVICE_TYPE_FACEBOOK ("Facebook");

    private final String print;

    ServiceType(String prt) {
        print = prt;
    }

    public String toString(){
        return print;
    }

}

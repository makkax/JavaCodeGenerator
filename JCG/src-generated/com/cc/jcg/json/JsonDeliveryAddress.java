package com.cc.jcg.json;

import com.cc.jcg.MGenerated;

@MGenerated
public class JsonDeliveryAddress
        implements JsonBean {
    
    private String firstName;
    private String lastName;
    private String address1;
    private String address2;
    private String address3;
    private String postalCode;
    private String city;
    private String state;
    private String countryCode;
    private String telephoneNumber;
    private JsonToken token;
    
    public JsonDeliveryAddress() {
        super();
    }
    
    public final synchronized String getFirstName() {
        return firstName;
    }
    
    public final synchronized void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public final synchronized String getLastName() {
        return lastName;
    }
    
    public final synchronized void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public final synchronized String getAddress1() {
        return address1;
    }
    
    public final synchronized void setAddress1(String address1) {
        this.address1 = address1;
    }
    
    public final synchronized String getAddress2() {
        return address2;
    }
    
    public final synchronized void setAddress2(String address2) {
        this.address2 = address2;
    }
    
    public final synchronized String getAddress3() {
        return address3;
    }
    
    public final synchronized void setAddress3(String address3) {
        this.address3 = address3;
    }
    
    public final synchronized String getPostalCode() {
        return postalCode;
    }
    
    public final synchronized void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public final synchronized String getCity() {
        return city;
    }
    
    public final synchronized void setCity(String city) {
        this.city = city;
    }
    
    public final synchronized String getState() {
        return state;
    }
    
    public final synchronized void setState(String state) {
        this.state = state;
    }
    
    public final synchronized String getCountryCode() {
        return countryCode;
    }
    
    public final synchronized void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    public final synchronized String getTelephoneNumber() {
        return telephoneNumber;
    }
    
    public final synchronized void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
    
    public final synchronized JsonToken getToken() {
        return token;
    }
    
    public final synchronized void setToken(JsonToken token) {
        this.token = token;
    }
}

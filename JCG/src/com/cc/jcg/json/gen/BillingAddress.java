package com.cc.jcg.json.gen;

import com.cc.jcg.MGenerated;

@MGenerated
public class BillingAddress {
    
    private String address1;
    private String postalCode;
    private String city;
    private String countryCode;
    
    public BillingAddress() {
        super();
    }
    
    public final synchronized String getAddress1() {
        return address1;
    }
    
    public final synchronized void setAddress1(String address1) {
        this.address1 = address1;
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
    
    public final synchronized String getCountryCode() {
        return countryCode;
    }
    
    public final synchronized void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}

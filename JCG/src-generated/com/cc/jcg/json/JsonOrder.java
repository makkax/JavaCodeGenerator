package com.cc.jcg.json;

import com.cc.jcg.MGenerated;
import java.util.List;

@MGenerated
public class JsonOrder
        implements JsonBean {
    
    private JsonToken token;
    private String orderType;
    private Double amount;
    private Boolean authorizeOnly;
    private String currencyCode;
    private String orderDescription;
    private String customerOrderCode;
    private String name;
    private JsonBillingAddress billingAddress;
    private JsonDeliveryAddress deliveryAddress;
    private String shopperEmailAddress;
    private String shopperIpAddress;
    private String shopperSessionId;
    private List<String> items;
    private List<Double> taxes;
    
    public JsonOrder() {
        super();
    }
    
    public final synchronized JsonToken getToken() {
        return token;
    }
    
    public final synchronized void setToken(JsonToken token) {
        this.token = token;
    }
    
    public final synchronized String getOrderType() {
        return orderType;
    }
    
    public final synchronized void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public final synchronized Double getAmount() {
        return amount;
    }
    
    public final synchronized void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public final synchronized Boolean isAuthorizeOnly() {
        return authorizeOnly;
    }
    
    public final synchronized void setAuthorizeOnly(Boolean authorizeOnly) {
        this.authorizeOnly = authorizeOnly;
    }
    
    public final synchronized String getCurrencyCode() {
        return currencyCode;
    }
    
    public final synchronized void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
    public final synchronized String getOrderDescription() {
        return orderDescription;
    }
    
    public final synchronized void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }
    
    public final synchronized String getCustomerOrderCode() {
        return customerOrderCode;
    }
    
    public final synchronized void setCustomerOrderCode(String customerOrderCode) {
        this.customerOrderCode = customerOrderCode;
    }
    
    public final synchronized String getName() {
        return name;
    }
    
    public final synchronized void setName(String name) {
        this.name = name;
    }
    
    public final synchronized JsonBillingAddress getBillingAddress() {
        return billingAddress;
    }
    
    public final synchronized void setBillingAddress(JsonBillingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }
    
    public final synchronized JsonDeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public final synchronized void setDeliveryAddress(JsonDeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public final synchronized String getShopperEmailAddress() {
        return shopperEmailAddress;
    }
    
    public final synchronized void setShopperEmailAddress(String shopperEmailAddress) {
        this.shopperEmailAddress = shopperEmailAddress;
    }
    
    public final synchronized String getShopperIpAddress() {
        return shopperIpAddress;
    }
    
    public final synchronized void setShopperIpAddress(String shopperIpAddress) {
        this.shopperIpAddress = shopperIpAddress;
    }
    
    public final synchronized String getShopperSessionId() {
        return shopperSessionId;
    }
    
    public final synchronized void setShopperSessionId(String shopperSessionId) {
        this.shopperSessionId = shopperSessionId;
    }
    
    public final synchronized List<String> getItems() {
        return items;
    }
    
    public final synchronized void setItems(List<String> items) {
        this.items = items;
    }
    
    public final synchronized List<Double> getTaxes() {
        return taxes;
    }
    
    public final synchronized void setTaxes(List<Double> taxes) {
        this.taxes = taxes;
    }
}

package com.project.hadar.AcadeMovie.Model;

/** Copyright 2018 Ofir Uzan. All Rights Reserved. **/

public class MoviePurchase
{
    private String m_time;
    private String m_sku;
    private String m_orderId;
    private String m_token;
    private int m_amount;

    public String getSku()
    {
        return m_sku;
    }

    public void setSku(String i_sku)
    {
        this.m_sku = i_sku;
    }

    public String getOrderId()
    {
        return m_orderId;
    }

    public void setOrderId(String i_orderId)
    {
        this.m_orderId = i_orderId;
    }

    public String getToken()
    {
        return m_token;
    }

    public void setToken(String i_token)
    {
        this.m_token = i_token;
    }

    public int getAmount()
    {
        return m_amount;
    }

    public void setAmount(int i_amount)
    {
        this.m_amount = i_amount;
    }

    public String getTime()
    {

        return m_time;
    }

    public void setTime(String i_time)
    {
        this.m_time = i_time;
    }
}

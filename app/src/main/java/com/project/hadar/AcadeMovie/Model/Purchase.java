package com.project.hadar.AcadeMovie.Model;

import java.io.Serializable;
import java.util.Map;

public class Purchase implements Serializable {
    private String m_movieId;
    private Map<String,Integer> m_mapOfTypeTicketsAndQuantity;
    private double m_purchaseAmount;

    public Purchase() {}

    public Purchase(String i_movieId, Map<String, Integer> i_mapOfTypeTicketsAndQuantity, double i_purchaseAmount)
    {
        this.m_movieId = i_movieId;
        this.m_mapOfTypeTicketsAndQuantity = i_mapOfTypeTicketsAndQuantity;
        this.m_purchaseAmount = i_purchaseAmount;
    }

    public String getM_movieId()
    {
        return m_movieId;
    }

    public Map<String, Integer> getM_mapOfTypeTicketsAndQuantity()
    {
        return m_mapOfTypeTicketsAndQuantity;
    }

    public void setM_purchaseAmount(double m_purchaseAmount) {
        this.m_purchaseAmount = m_purchaseAmount;
    }

    public double getM_purchaseAmount()
    {
        return m_purchaseAmount;
    }
}

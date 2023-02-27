package com.driver;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;

@Repository
public class OrderRepository {

    private HashMap<String, Order> OrderMap;

    private HashMap<String, DeliveryPartner> DPartnerMap;

    private HashMap<String, HashSet<String>> partnerToOrderMap;

    private HashMap<String,String> orderToPartnerMap;


    public OrderRepository(){
        this.OrderMap = new HashMap<String, Order>();
        this.DPartnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerToOrderMap = new HashMap<String, HashSet<String>>();

    }

    public String addOrder(Order order){

        OrderMap.put(order.getId(),order);

        return "New order added successfully";
    }

    public String addPartner(String partnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        DPartnerMap.put(partnerId,deliveryPartner);
        return "New delivery partner added successfully";
    }

    public Order getOrderById(String orderId){
        return OrderMap.get(orderId);
    }

    public  DeliveryPartner getPartnerById(String partnerId){
        return DPartnerMap.get(partnerId);
    }

    public void addOrderPartnerPair(String orderId, String partnerId){
        //checking part
        if(OrderMap.containsKey(orderId) && DPartnerMap.containsKey(partnerId)){

            // Add the order in the partner id's orderList/ orderSet
            HashSet<String> currentOrders = new HashSet<String>();

            if(partnerToOrderMap.containsKey(partnerId))
                currentOrders = partnerToOrderMap.get(partnerId); //getting the list
            currentOrders.add(orderId); // adding the orderId
            partnerToOrderMap.put(partnerId,currentOrders); // update the HashMap

            //Increase the count of partner

            DeliveryPartner partner = DPartnerMap.get(partnerId);
            partner.setNumberOfOrders(currentOrders.size());

            //Assign partner to this order
            orderToPartnerMap.put(orderId,partnerId); // Maintaining every HashMaps


        }
    }



}

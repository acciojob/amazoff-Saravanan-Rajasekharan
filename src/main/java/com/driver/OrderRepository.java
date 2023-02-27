package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    private HashMap<String, Order> OrderMap;

    private HashMap<String, DeliveryPartner> DPartnerMap;

    private HashMap<String, List<String>> partnerToOrderMap;

    private HashMap<String,String> orderToPartnerMap;


    public OrderRepository(){
        this.OrderMap = new HashMap<String, Order>();
        this.DPartnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerToOrderMap = new HashMap<String, List<String>>();
        this.orderToPartnerMap = new HashMap<String,String>();

    }

    public void addOrder(Order order){
        OrderMap.put(order.getId(),order);
    }

    public void addPartner(String partnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        DPartnerMap.put(partnerId,deliveryPartner);
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
            List<String> currentOrders = new ArrayList<String>();

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

    public Integer getOrderCountByPartnerId(String partnerId){
        Integer count = 0;
        if(DPartnerMap.containsKey(partnerId)){
            count = DPartnerMap.get(partnerId).getNumberOfOrders();
        }
        return count;
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orderList = new ArrayList<>();
        if(partnerToOrderMap.containsKey(partnerId))
            orderList = partnerToOrderMap.get(partnerId);
        return orderList;
    }

    public List<String> getAllOrders(){
        return new ArrayList<>(OrderMap.keySet());
    }

    public Integer getCountOfUnassignedOrders (){

        int totalOrders = OrderMap.size();
        int assignedOrders = orderToPartnerMap.size();
         return totalOrders- assignedOrders;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId( String time, String partnerId){
        Integer HH = Integer.parseInt(time.substring(0,2));
        Integer MM = Integer.parseInt(time.substring(3,5));
        Integer t = HH*60+MM;

        Integer countOfOrders = 0;
        if(partnerToOrderMap.containsKey(partnerId)){
            List<String> orders = partnerToOrderMap.get(partnerId);
            for(String order: orders){
                if(OrderMap.containsKey(order)){
                    Order currOrder = OrderMap.get(order);
                    if(t < currOrder.getDeliveryTime()){
                        countOfOrders += 1;
                    }
                }
            }
        }
        return countOfOrders;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int ldt = 0;
        if(partnerToOrderMap.containsKey(partnerId)){
            List<String> orderlist = partnerToOrderMap.get(partnerId);
            for(String orderId: orderlist){
                int time = OrderMap.get(orderId).getDeliveryTime();
                ldt = Math.max(ldt,time);
            }
        }
        int HH = ldt /60;
        int MM = ldt %60;

        String lastDeliveryTime = HH+":"+MM;

        return lastDeliveryTime;
    }

    public void deletePartnerById(String partnerId){
        List<String> orders = new ArrayList<>();
        if(partnerToOrderMap.containsKey(partnerId)){
            orders = partnerToOrderMap.get(partnerId);
            for(String order : orders){
                if(orderToPartnerMap.containsKey(order)){
                    // Free all the orders assigned to the partner
                    orderToPartnerMap.remove(order);
                }
            }
            partnerToOrderMap.remove(partnerId);
        }
        if(DPartnerMap.containsKey(partnerId)){
            DPartnerMap.remove(partnerId);
        }
    }

    public void deleteOrderById(String orderId){
        if(orderToPartnerMap.containsKey(orderId)){
            String partnerId = orderToPartnerMap.get(orderId);
            List<String> orders = partnerToOrderMap.get(partnerId);
            orders.remove(orderId);

            partnerToOrderMap.put(partnerId,orders);

            //change the number of Orders in the partner
            DeliveryPartner partner = DPartnerMap.get(partnerId);
            partner.setNumberOfOrders(orders.size());

            if(OrderMap.containsKey(orderId)){
                OrderMap.remove(orderId);
            }
        }
    }



}

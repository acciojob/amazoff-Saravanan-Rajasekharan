package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    private HashMap<String, Order> OrderMap  = new HashMap<String, Order>();

    private HashMap<String, DeliveryPartner> DPartnerMap = new HashMap<String, DeliveryPartner>();;

    private HashMap<String, List<String>> partnerToOrderMap = new HashMap<String, List<String>>();

    private HashMap<String,String> orderToPartnerMap = new HashMap<String,String>();



    public String addOrder(Order order){
        OrderMap.put(order.getId(),order);
        return "Added successfully";
    }

    public String addPartner(String partnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        DPartnerMap.put(partnerId,deliveryPartner);
        return "Added successfully";
    }

    public Order getOrderById(String orderId){
        if(OrderMap.containsKey(orderId))
            return OrderMap.get(orderId);
        return null;
    }

    public  DeliveryPartner getPartnerById(String partnerId){
        if(DPartnerMap.containsKey(partnerId))
            return DPartnerMap.get(partnerId);
        return null;
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
        String hour ="";

        if(HH<10) { hour="0"+ HH;
        } else{ hour = String.valueOf(HH);}


        int MM = ldt %60;
        String min ="";

        if(MM<10) { min="0"+ MM;
        } else{ min = String.valueOf(MM); }


        String lastDeliveryTime = hour+":"+min;

        return lastDeliveryTime;
    }

    public void deletePartnerById(String partnerId){
        DPartnerMap.remove(partnerId);
        List<String> orderList = partnerToOrderMap.getOrDefault(partnerId, new ArrayList<>());
        ListIterator<String> itr = orderList.listIterator();

        while (itr.hasNext()){
            String s = itr.next();
            orderToPartnerMap.remove(s);
        }
        partnerToOrderMap.remove(partnerId);

    }

    public void deleteOrderById(String orderId){
       OrderMap.remove(orderId); // Remove the order from the OrderMap
       String partnerId = orderToPartnerMap.get(orderId); // Get the partnerId from orderId
        //Remove it from the orderId-partnerId map
        orderToPartnerMap.remove(orderId);

        List<String> orderList = partnerToOrderMap.get(partnerId);
        ListIterator<String> itr = orderList.listIterator();

        while(itr.hasNext()){
            String s = itr.next();
            if(s.equals(orderId)){
                itr.remove();
            }
        }
        partnerToOrderMap.put(partnerId,orderList);

    }



}

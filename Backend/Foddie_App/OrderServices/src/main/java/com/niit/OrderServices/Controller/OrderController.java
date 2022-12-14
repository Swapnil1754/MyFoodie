package com.niit.OrderServices.Controller;

import UserDefinedException.OrderAlreadyExistsException;
import UserDefinedException.OrderNotFoundException;
import com.niit.OrderServices.Model.Menu;
import com.niit.OrderServices.Model.Order;
import com.niit.OrderServices.Model.OrderDetails;
import com.niit.OrderServices.Repository.OrderDetailsRepository;
import com.niit.OrderServices.Repository.UserRepository;
import com.niit.OrderServices.Services.OrderService;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.razorpay.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order-services")
public class OrderController {

    private ResponseEntity responseEntity;
    private OrderService orderService;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    //  generate An Order
    @PostMapping(path = "/{email}",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateOrderCall(@RequestBody List<Menu> menuList,@PathVariable("email") String email) throws OrderNotFoundException {
        System.out.println(email);
        Order order = orderService.placeOrder(menuList,email);
            responseEntity = new ResponseEntity(order, HttpStatus.CREATED);


        return responseEntity;
    }

    //  delete Order
    @DeleteMapping("order/delete/{orderId}")
    public ResponseEntity<?> cancelOrderCall(@PathVariable long orderId) throws OrderNotFoundException{
        try{
            orderService.cancelOrder(orderId);
            responseEntity = new ResponseEntity("Order is Successfully canceled!", HttpStatus.OK);
        }catch (OrderNotFoundException ex){
            throw ex;
        }catch (Exception e){
            responseEntity = new ResponseEntity("Error while cancelling order! Please Try again!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    //  editing Order
    @PutMapping("order/update")
    public ResponseEntity<?> updateOrderCall(@RequestBody Order order) throws OrderNotFoundException{
        try{
            orderService.updateOrder(order);
            responseEntity = new ResponseEntity("Updated!", HttpStatus.OK);
        }catch (OrderNotFoundException ex){
            throw ex;
        }catch (Exception e){
            responseEntity = new ResponseEntity("Error while Updating! Please Try again!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
    @GetMapping("/get/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable("orderId") long orderId)throws OrderNotFoundException{

       return responseEntity = new ResponseEntity<>(orderService.getOrder(orderId),HttpStatus.OK);
    }
    @PostMapping("/create_order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String,Object> data, Principal principal) throws Exception {
       // System.out.println("Order Done...");
        System.out.println(data);
        //int amt = Integer.parseInt(data.get("amount").toString());
        double amt = Double.parseDouble(data.get("amount").toString());
        var client = new RazorpayClient("rzp_test_57YwJ8uRgM4FPo","PZHKcLLBzYzT02eAvmiLANWq");
        JSONObject ob = new JSONObject();
        ob.put("amount",amt*100);
        ob.put("currency","INR");
        ob.put("receipt","txn_235425");
        com.razorpay.Order ord= client.orders.create(ob);
        System.out.println(ord);
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setAmount(ord.get("amount")+"");
        orderDetails.setOrderId(ord.get("order_id"));
        orderDetails.setPaymentId(null);
        orderDetails.setStatus("created");
        orderDetails.setEmail(ord.get("email"));
        orderDetails.setReceipt(ord.get("receipt"));
        System.out.println("receipt"+orderDetails);
        this.orderDetailsRepository.save(orderDetails);
        return ord.toString();
    }
}

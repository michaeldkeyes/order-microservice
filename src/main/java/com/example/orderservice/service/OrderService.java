package com.example.orderservice.service;

import com.example.orderservice.client.InventoryClient;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final InventoryClient inventoryClient;
    private final OrderRepository orderRepository;

    public void placeOrder(final OrderRequest orderRequest) {
        final boolean isInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if (isInStock) {
            final Order order = mapToOrder(orderRequest);

            orderRepository.save(order);
        } else {
            throw new RuntimeException("Product with sku code " + orderRequest.skuCode() + " is out of stock");
        }

    }

    private static Order mapToOrder(final OrderRequest orderRequest) {
        final Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderRequest.price());
        order.setQuantity(orderRequest.quantity());
        order.setSkuCode(orderRequest.skuCode());

        return order;
    }
}

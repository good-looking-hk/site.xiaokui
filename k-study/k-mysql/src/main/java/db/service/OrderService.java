package db.service;

import db.dao.OrderDao;
import db.entity.OrderDTO;
import db.entity.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HK
 * @date 2021-03-17 17:49
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderDao orderDao;

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<OrderDTO> queryByScrollingPagination(String paymentDateTimeStart,
                                                     String paymentDateTimeEnd,
                                                     long lastBatchMaxId,
                                                     int limit) {
        LocalDateTime start = LocalDate.parse(paymentDateTimeStart, F).atStartOfDay();
        LocalDateTime end = LocalDate.parse(paymentDateTimeEnd, F).atStartOfDay();
        return orderDao.queryByScrollingPagination(lastBatchMaxId, limit, start, end).stream().map(order -> {
            OrderDTO dto = new OrderDTO();
            dto.setId(order.getId());
            dto.setAmount(order.getAmount());
            dto.setOrderId(order.getOrderId());
            dto.setPaymentTime(order.getPaymentTime().format(F));
            dto.setOrderStatus(OrderStatus.fromStatus(order.getOrderStatus()).getDescription());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<OrderDTO> queryOptimize(String paymentDateTimeStart,
                                        String paymentDateTimeEnd,
                                        int offset, int size) {
        LocalDateTime start = LocalDate.parse(paymentDateTimeStart, F).atStartOfDay();
        LocalDateTime end = LocalDate.parse(paymentDateTimeEnd, F).atStartOfDay();
        return orderDao.queryOptimize(start, end, offset, size).stream().map(order -> {
            OrderDTO dto = new OrderDTO();
            dto.setId(order.getId());
            dto.setAmount(order.getAmount());
            dto.setOrderId(order.getOrderId());
            dto.setPaymentTime(order.getPaymentTime().format(F));
            dto.setOrderStatus(OrderStatus.fromStatus(order.getOrderStatus()).getDescription());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<OrderDTO> queryAll(String paymentDateTimeStart,
                                   String paymentDateTimeEnd) {
        LocalDateTime start = LocalDate.parse(paymentDateTimeStart, F).atStartOfDay();
        LocalDateTime end = LocalDate.parse(paymentDateTimeEnd, F).atStartOfDay();
        return orderDao.queryAll(start, end).stream().map(order -> {
            OrderDTO dto = new OrderDTO();
            dto.setId(order.getId());
            dto.setAmount(order.getAmount());
            dto.setOrderId(order.getOrderId());
            dto.setPaymentTime(order.getPaymentTime().format(F));
            dto.setOrderStatus(OrderStatus.fromStatus(order.getOrderStatus()).getDescription());
            return dto;
        }).collect(Collectors.toList());
    }
}

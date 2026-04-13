package com.yanluwuyou.config;

import com.yanluwuyou.dto.OrderDTO;
import com.yanluwuyou.entity.Address;
import com.yanluwuyou.entity.CartItem;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.service.AddressService;
import com.yanluwuyou.service.CartItemService;
import com.yanluwuyou.service.OrderService;
import com.yanluwuyou.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

@Aspect
@Component
public class DisabledUserGuardAspect {

    @Autowired
    private UserService userService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private OrderService orderService;

    @Before("within(com.yanluwuyou.controller..*)")
    public void guardDisabledUser(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Long userId = resolveUserId(method, joinPoint.getArgs());
        if (userId == null) {
            userId = resolveUserIdFromResource(method, joinPoint.getArgs());
        }
        if (userId == null) {
            return;
        }
        User user = userService.getById(userId);
        if (user != null && user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }
    }

    private Long resolveUserId(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (isUserIdRequestParam(parameters[i]) && i < args.length) {
                Long val = toLong(args[i]);
                if (val != null) {
                    return val;
                }
            }
        }
        for (Object arg : args) {
            Long val = extractUserIdFromArg(arg);
            if (val != null) {
                return val;
            }
        }
        return null;
    }

    private Long resolveUserIdFromResource(Method method, Object[] args) {
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        if ("CartItemController".equals(className) && "delete".equals(methodName) && args.length > 0) {
            Long cartItemId = toLong(args[0]);
            if (cartItemId != null) {
                CartItem cartItem = cartItemService.getById(cartItemId);
                if (cartItem != null) {
                    return cartItem.getUserId();
                }
            }
        }
        if ("AddressController".equals(className) && "delete".equals(methodName) && args.length > 0) {
            Long addressId = toLong(args[0]);
            if (addressId != null) {
                Address address = addressService.getById(addressId);
                if (address != null) {
                    return address.getUserId();
                }
            }
        }
        if ("OrderController".equals(className) && args.length > 0) {
            if ("pay".equals(methodName) || "cancel".equals(methodName)) {
                Long orderId = toLong(args[0]);
                if (orderId != null) {
                    com.yanluwuyou.entity.Order order = orderService.getById(orderId);
                    if (order != null) {
                        return order.getUserId();
                    }
                }
            }
            if ("getByOrderNo".equals(methodName)) {
                String orderNo = String.valueOf(args[0]);
                OrderDTO order = orderService.getByOrderNo(orderNo);
                if (order != null) {
                    return order.getUserId();
                }
            }
        }
        return null;
    }

    private boolean isUserIdRequestParam(Parameter parameter) {
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        if (requestParam == null) {
            return false;
        }
        String name = requestParam.name();
        if (name == null || name.isEmpty()) {
            name = requestParam.value();
        }
        if (name == null || name.isEmpty()) {
            name = parameter.getName();
        }
        return "userId".equals(name);
    }

    private Long extractUserIdFromArg(Object arg) {
        if (arg == null) {
            return null;
        }
        if (arg instanceof Map<?, ?> map) {
            return toLong(map.get("userId"));
        }
        try {
            Method getter = arg.getClass().getMethod("getUserId");
            return toLong(getter.invoke(arg));
        } catch (Exception ignored) {
            return null;
        }
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception ignored) {
            return null;
        }
    }
}

package tn.esprit.spring.marketplaceservice.controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.marketplaceservice.services.IMPL.PaymentService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession() throws StripeException {
        Session session = paymentService.createCheckoutSession(
                "http://localhost:4200/marketplacefront/user/overview",
                "http://localhost:4200/marketplacefront/user/overview"
        );

        Map<String, String> response = new HashMap<>();
        response.put("checkoutUrl", session.getUrl());
        return response;
    }
}


package TiCatch.backend.global.constant;

public class PaymentConstants {
    public static final String KAKAOPAY_BASE_URL = "https://open-api.kakaopay.com/online/v1/payment";
    public static final String PAYMENT_READY_URL = KAKAOPAY_BASE_URL + "/ready";
    public static final String PAYMENT_APPROVE_URL = KAKAOPAY_BASE_URL + "/approve";

    public static final String PAYMENT_SUCCESS_URL = "/order/pay/completed";
    public static final String PAYMENT_CANCEL_URL = "/order/pay/cancel";
    public static final String PAYMENT_FAIL_URL = "/order/pay/fail";

    public static final String PARTNER_ORDER_ID = "1234567890";
    public static final String PARTNER_USER_ID = "ticatch";
}

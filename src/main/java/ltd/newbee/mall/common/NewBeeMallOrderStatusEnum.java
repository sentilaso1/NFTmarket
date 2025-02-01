package ltd.newbee.mall.common;

public enum NewBeeMallOrderStatusEnum {

    DEFAULT(-9, "ERROR"),
    ORDER_PRE_PAY(0, "Pending payment"),
    ORDER_PAID(1, "Paid"),
    ORDER_PACKAGED(2, "Packaging completed"),
    ORDER_EXPRESS(3, "Shipped"),
    ORDER_SUCCESS(4, "Transaction successful"),
    ORDER_CLOSED_BY_MALLUSER(-1, "Manually closed"),
    ORDER_CLOSED_BY_EXPIRED(-2, "Closed due to timeout"),
    ORDER_CLOSED_BY_JUDGE(-3, "Closed by merchant");

    private int orderStatus;

    private String name;

    NewBeeMallOrderStatusEnum(int orderStatus, String name) {
        this.orderStatus = orderStatus;
        this.name = name;
    }

    public static NewBeeMallOrderStatusEnum getNewBeeMallOrderStatusEnumByStatus(int orderStatus) {
        for (NewBeeMallOrderStatusEnum newBeeMallOrderStatusEnum : NewBeeMallOrderStatusEnum.values()) {
            if (newBeeMallOrderStatusEnum.getOrderStatus() == orderStatus) {
                return newBeeMallOrderStatusEnum;
            }
        }
        return DEFAULT;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

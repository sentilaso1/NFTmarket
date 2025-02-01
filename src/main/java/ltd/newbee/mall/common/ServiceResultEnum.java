package ltd.newbee.mall.common;
public enum ServiceResultEnum {
    ERROR("error"),

    SUCCESS("success"),

    DATA_NOT_EXIST("Record not found!"),
            
    SAME_CATEGORY_EXIST("A category with the same level and name already exists!"),
            
    SAME_LOGIN_NAME_EXIST("Username already exists!"),
            
    LOGIN_NAME_NULL("Please enter the login name!"),
            
    LOGIN_PASSWORD_NULL("Please enter the password!"),
            
    LOGIN_VERIFY_CODE_NULL("Please enter the verification code!"),
            
    LOGIN_VERIFY_CODE_ERROR("Incorrect verification code!"),
            
    SAME_INDEX_CONFIG_EXIST("An identical homepage configuration item already exists!"),
            
    GOODS_CATEGORY_ERROR("Category data exception!"),
            
    SAME_GOODS_EXIST("Identical product information already exists!"),
            
    GOODS_NOT_EXIST("Product does not exist!"),
            
    GOODS_PUT_DOWN("Product has been delisted!"),
            
    SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR("Exceeded the maximum purchase quantity for a single product!"),
            
    SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR("Exceeded the maximum capacity of the shopping cart!"),
            
    LOGIN_ERROR("Login failed!"),

    LOGIN_USER_LOCKED("User is banned from logging in!"),
            
    ORDER_NOT_EXIST_ERROR("Order does not exist!"),
            
    ORDER_ITEM_NOT_EXIST_ERROR("Order item does not exist!"),
            
    NULL_ADDRESS_ERROR("Address cannot be empty!"),
            
    ORDER_PRICE_ERROR("Order price exception!"),
            
    ORDER_GENERATE_ERROR("Error generating order!"),
            
    SHOPPING_ITEM_ERROR("Shopping cart data exception!"),
            
    SHOPPING_ITEM_COUNT_ERROR("Insufficient stock!"),
            
    ORDER_STATUS_ERROR("Order status exception!"),
            
    CLOSE_ORDER_ERROR("Failed to close the order!"),
            
    OPERATE_ERROR("Operation failed!"),
            
    NO_PERMISSION_ERROR("No permission!"),

    DB_ERROR("database error");

    private String result;

    ServiceResultEnum(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
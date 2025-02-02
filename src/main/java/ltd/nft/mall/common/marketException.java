package ltd.nft.mall.common;

public class marketException extends RuntimeException {

    public marketException() {
    }

    public marketException(String message) {
        super(message);
    }

    /**
     * Throws an exception
     *
     * @param message
     */
    public static void fail(String message) {
        throw new marketException(message);
    }

}

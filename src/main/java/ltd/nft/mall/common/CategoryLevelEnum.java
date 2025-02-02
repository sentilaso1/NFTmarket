package ltd.nft.mall.common;

public enum CategoryLevelEnum {

    DEFAULT(0, "ERROR"),
    LEVEL_ONE(1, "First-level category"),
    LEVEL_TWO(2, "Second-level category"),
    LEVEL_THREE(3, "Third-level category");

    private int level;

    private String name;

    CategoryLevelEnum(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public static CategoryLevelEnum getOrderStatusEnumByLevel(int level) {
        for (CategoryLevelEnum CategoryLevelEnum : CategoryLevelEnum.values()) {
            if (CategoryLevelEnum.getLevel() == level) {
                return CategoryLevelEnum;
            }
        }
        return DEFAULT;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

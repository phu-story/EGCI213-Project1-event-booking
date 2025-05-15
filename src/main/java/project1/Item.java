package project1;

public class Item {
    public enum ItemType {
        ROOM,
        MEAL
    }

    private String code;
    private String name;
    private double unitPrice;
    private ItemType itemType;

    public Item(String code, String name, double unitPrice, ItemType itemType) {
        this.code = code;
        this.name = name;
        this.itemType = itemType;

        this.unitPrice = unitPrice;
        // if(itemType == ItemType.ROOM) {
        // this.unitPrice = (unitPrice * 1.1) * 1.07;
        // } else {
        // this.unitPrice = unitPrice;
        // }
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public ItemType getItemType() {
        return itemType;
    }
}

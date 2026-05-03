package ru.nsu.tumilevich;

import java.util.List;

public class PizzeriaConfig {
    private int stockCapacity;
    private List<BakerConfig> bakers;
    private List<CourierConfig> couriers;

    public int getStockCapacity() { return stockCapacity; }
    public void setStockCapacity(int stockCapacity) { this.stockCapacity = stockCapacity; }
    public List<BakerConfig> getBakers() { return bakers; }
    public void setBakers(List<BakerConfig> bakers) { this.bakers = bakers; }
    public List<CourierConfig> getCouriers() { return couriers; }
    public void setCouriers(List<CourierConfig> couriers) { this.couriers = couriers; }

    public static class BakerConfig {
        private int cookingTime;
        public int getCookingTime() { return cookingTime; }
        public void setCookingTime(int cookingTime) { this.cookingTime = cookingTime; }
    }

    public static class CourierConfig {
        private int trunkCapacity;
        private int deliveryTime;

        public int getTrunkCapacity() { return trunkCapacity; }
        public void setTrunkCapacity(int trunkCapacity) { this.trunkCapacity = trunkCapacity; }
        public int getDeliveryTime() { return deliveryTime; }
        public void setDeliveryTime(int deliveryTime) { this.deliveryTime = deliveryTime; }
    }
}
public class Road {

    private double distance;
    private int startCityId, arrivalCityId;

    public Road(double distance, int startCityId, int arrivalCityId) {
        this.distance = distance;
        this.startCityId = startCityId;
        this.arrivalCityId = arrivalCityId;
    }

    public double getDistance() {
        return distance;
    }

    public int getStartCityId() {
        return startCityId;
    }

    public int getArrivalCityId() {
        return arrivalCityId;
    }
}

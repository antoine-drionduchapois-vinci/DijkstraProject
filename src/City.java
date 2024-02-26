public class City {
    private int id;
    private String cityName;
    private Double longitude, latitude;

    public City(int id, String cityName, Double longitude, Double latitude) {
        this.id = id;
        this.cityName = cityName;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
}

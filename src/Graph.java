import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Graph {
    private static final int EXPECTED_CITY_DATA_LENGTH = 4;
    private static final int EXPECTED_ROAD_DATA_LENGTH = 2;

    private Map<Integer, List<Road>> cityMap; // Map to store roads for each city
    private Map<Integer, City> cityFinder; // Map to quickly find city details by ID
    private Map<String,Integer> cityIdFinder; // Map to quickly find city ID by name

    // Constructor to initialize the graph with city and road data from files
    public Graph(File cityFile, File roadFile){
        cityMap = new HashMap<>();
        cityFinder = new HashMap<>();
        cityIdFinder = new HashMap<>();
        try{
            constructCitiesFromTxt(cityFile); // Construct cities from file
            constructRoadFromTxt(roadFile); // Construct roads from file
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Method to construct cities from a text file
    public void constructCitiesFromTxt(File file) throws FileNotFoundException {
        try{
            Scanner scanner = new Scanner(file);
            // here code city extraction
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == EXPECTED_CITY_DATA_LENGTH) {
                    int cityId = Integer.parseInt(parts[0].trim());
                    String cityName = parts[1].trim();
                    double latitude = Double.parseDouble(parts[2].trim());
                    double longitude = Double.parseDouble(parts[3].trim());

                    City city = new City(cityId, cityName, latitude, longitude);
                    cityMap.put(cityId,new ArrayList<>());
                    cityFinder.put(cityId, city);
                    cityIdFinder.put(cityName,cityId);
                } else {
                    System.err.println("Invalid line: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Method to construct roads from a text file
    public void constructRoadFromTxt(File file) {
        try{
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == EXPECTED_ROAD_DATA_LENGTH) {
                    int cityIdStart = Integer.parseInt(parts[0].trim());
                    int cityIdEnd = Integer.parseInt(parts[1].trim());

                    addRoad(cityIdStart, cityIdEnd);
                    addRoad(cityIdEnd, cityIdStart);
                } else {
                    System.err.println("Invalid line: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Method to add a road between two cities
    private void addRoad(int cityId1, int cityId2) {
        if (!cityMap.containsKey(cityId1)) {
            cityMap.put(cityId1, new ArrayList<>());
        }
        double latitude1 = cityFinder.get(cityId1).getLatitude();
        double longitude1 = cityFinder.get(cityId1).getLongitude();
        double latitude2 = cityFinder.get(cityId2).getLatitude();
        double longitude2 = cityFinder.get(cityId2).getLongitude();

        double distance = Util.distance(longitude1,latitude1,longitude2,latitude2);

        Road road = new Road(distance, cityId1, cityId2);
        cityMap.get(cityId1).add(road);
    }

    // Method to calculate the shortest path minimizing the number of routes
    public void calculerItineraireMinimisantNombreRoutes(String city1, String city2) throws NoSuchElementException {
        int startCityId = cityIdFinder.get(city1);
        int endCityId = cityIdFinder.get(city2);

        if (!cityIdFinder.containsKey(city1) || !cityIdFinder.containsKey(city2)) {
            throw new NoSuchElementException("City not found");
        }

        Set<Integer> visited = new HashSet<>();
        LinkedList<Integer> queue = new LinkedList<Integer>();
        HashMap <Integer, Integer>  pathHistory = new HashMap<>();

        queue.add(startCityId);
        visited.add(startCityId);

        boolean found = false;

        while (!queue.isEmpty()) {
            int currentCityId = queue.poll();
            if (currentCityId == endCityId) {
                found = true;
                break;
            }
            List<Road> roads = cityMap.getOrDefault(currentCityId, new ArrayList<>());
            if (roads != null) {
                for (Road road : roads) {
                    int neighbor = road.getArrivalCityId();
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                        pathHistory.put(neighbor, currentCityId);
                    }
                }
            }
        }
        if (!found) {
            throw new NoSuchElementException("No path found");
        }
        // Reconstruct the path
        reconstructPath(pathHistory, startCityId, endCityId);
    }

    // Method to calculate the shortest path minimizing the distance
    public void calculerItineraireMinimisantKm(String city1, String city2) throws NoSuchElementException {
        int startCityId = cityIdFinder.get(city1);
        int endCityId = cityIdFinder.get(city2);

        if (!cityIdFinder.containsKey(city1) || !cityIdFinder.containsKey(city2)) {
            throw new NoSuchElementException("City not found");
        }

        // Initialize data structures for Dijkstra's algorithm
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();

        // Initialize priority queue to manage cities to explore
        PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        queue.add(startCityId);
        distances.put(startCityId, 0.0);

        // Dijkstra's algorithm
        while (!queue.isEmpty()) {
            int currentCityId = queue.poll();
            if (currentCityId == endCityId) {
                break;
            }
            if (visited.contains(currentCityId)) {
                continue;
            }
            visited.add(currentCityId);

            // Traverse outgoing roads from current city
            for (Road road : cityMap.getOrDefault(currentCityId, new ArrayList<>())) {
                int neighbor = road.getArrivalCityId();
                double distanceToNeighbor = distances.get(currentCityId) + road.getDistance();
                if (!distances.containsKey(neighbor) || distanceToNeighbor < distances.get(neighbor)) {
                    distances.put(neighbor, distanceToNeighbor);
                    previous.put(neighbor, currentCityId);
                    queue.add(neighbor);
                }
            }
        }

        // Reconstruct the path
        reconstructPath(previous, startCityId, endCityId);
    }

    // Method to reconstruct the path based on previous node information
    private void reconstructPath(Map<Integer, Integer> previous, int startCityId, int endCityId) {
        List<String> path = new ArrayList<>();
        double totalDistance = 0.0;
        int current = endCityId;
        while (current != startCityId) {
            int previousCityId = previous.get(current);
            double distanceToPrevious = 0.0;
            for (Road road : cityMap.get(previousCityId)) {
                if (road.getArrivalCityId() == current) {
                    distanceToPrevious = road.getDistance();
                    totalDistance += distanceToPrevious;
                    break;
                }
            }
            City prevCity = cityFinder.get(previousCityId);
            City currentCity = cityFinder.get(current);
            path.add(prevCity.getCityName() + " -> " + currentCity.getCityName() + " (" + distanceToPrevious + "km)");
            current = previousCityId;
        }
        Collections.reverse(path);
        System.out.println("Itinéraire de " + cityFinder.get(startCityId).getCityName() + " à " + cityFinder.get(endCityId).getCityName() + " : " + path.size() + " routes et " + totalDistance + " km.");
        for (String step : path) {
            System.out.println(step);
        }
    }
}

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Graph {
    private static final int EXPECTED_CITY_DATA_LENGTH = 4;
    private static final int EXPECTED_ROAD_DATA_LENGTH = 2;

    private Map<Integer, List<Road>> cityMap;
    private Map<Integer, City> cityFinder;
    private Map<String,Integer> cityIdFinder;
    public Graph(File cityFile, File roadFile){
        cityMap = new HashMap<>();
        cityFinder = new HashMap<>();
        cityIdFinder = new HashMap<>();
        try{
            constructCitiesFromTxt(cityFile);
            constructRoadFromTxt(roadFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

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

    public void calculerItineraireMinimisantKm(String city1, String city2) throws NoSuchElementException {
        int startCityId = cityIdFinder.get(city1);
        int endCityId = cityIdFinder.get(city2);

        if (!cityIdFinder.containsKey(city1) || !cityIdFinder.containsKey(city2)) {
            throw new NoSuchElementException("City not found");
        }

        // Initialisation des structures de données pour l'algorithme de Dijkstra
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();

        // Initialisation de la file de priorité pour gérer les villes à explorer
        PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        queue.add(startCityId);
        distances.put(startCityId, 0.0);

        // Algorithme de Dijkstra
        while (!queue.isEmpty()) {
            int currentCityId = queue.poll();
            if (currentCityId == endCityId) {
                break;
            }
            if (visited.contains(currentCityId)) {
                continue;
            }
            visited.add(currentCityId);

            // Parcourir les routes sortantes de la ville actuelle
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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Graph {

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
                if (parts.length == 4) {
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
                if (parts.length == 2) {
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
        List<String> path = new ArrayList<>();
        int count = 0;
        double total_dist = 0.0;
        int current = endCityId;
        while (current != startCityId) {
            int previous = pathHistory.get(current);
            double distance = 0.0;
            for (Road road : cityMap.get(previous)) {
                if (road.getArrivalCityId() == current) {
                    distance = road.getDistance();
                    total_dist += distance;
                    break;
                }

            }
            count++;
            City prevCity = cityFinder.get(previous);
            City currentCity = cityFinder.get(current);
            path.add(prevCity.getCityName() + " -> " + currentCity.getCityName() + " (" + distance + "km)");
            current = previous;
        }

        Collections.reverse(path);
        System.out.println("Itinéraire de " + cityFinder.get(startCityId).getCityName() + " à " + cityFinder.get(endCityId).getCityName() + " : " + count + " routes et " + total_dist + " km.");

        for (String step : path) {
            System.out.println(step);
        }
    }

    public void calculerItineraireMinimisantKm(String city1, String city2){
        int startCityId = cityIdFinder.get(city1);
        int endCityId = cityIdFinder.get(city2);

        City cityFirst = cityFinder.get(startCityId);
        City cityEnd = cityFinder.get(endCityId);

        // comment retenir les noeuds precendents ??

        ArrayList<City> visited = new ArrayList<City>();
        ArrayList<City> unvisited = new ArrayList<City>();
        Map<City,Double> distance = new HashMap<City,Double>();
        Map<City,Double> mapProvisoir = new HashMap<City,Double>();     // Faire un triset pour l optimisation
        Map<City,City> parent = new HashMap<City,City>();

        // we fill up the unvisited array with all the cities?
        for (Map.Entry<Integer,City> entry : cityFinder.entrySet()){
            unvisited.add(entry.getValue());
            // We put infinity distance in every node
            distance.put(entry.getValue(),Double.MAX_VALUE);
        }
        // First city1 node should  have a distance of 0;
        distance.put(cityFinder.get(startCityId),0.0);
        // We add the parent to the city1 which is null?
        parent.put(cityFirst,null);

        // Start boucle from city1
        int currentNodeId = startCityId;

        while (!unvisited.isEmpty() && !visited.contains(cityEnd)) {
            City currentNodeCity = cityFinder.get(currentNodeId);
            List<Road> routes = cityMap.get(currentNodeId);
            for (Road myRoad : routes) {
                if (myRoad.getDistance() < distance.get(currentNodeCity)) {
                    distance.put(currentNodeCity, myRoad.getDistance());
                    parent.put(cityFinder.get(myRoad.getArrivalCityId()),currentNodeCity);
                }
            }
            visited.add(currentNodeCity);
            unvisited.remove(currentNodeCity);
            currentNodeId = unvisited.
        }






    }
}

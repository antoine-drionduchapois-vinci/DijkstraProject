import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Graph {

    protected Set<Integer> city;
    private Map<Integer, Set<Integer>> cityMap;
    public Graph(File cityFile, File roadFile){
        city = new HashSet<>();
        cityMap = new HashMap<>();
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
                    cityMap.put(cityId,new HashSet<>());
                } else {
                    System.err.println("Invalid line: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void constructRoadFromTxt (File file) {
        try{
            Scanner scanner = new Scanner(file);
            // here code for road extaraction
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    int cityIdStart = Integer.parseInt(parts[0].trim());
                    int cityIdEnd = Integer.parseInt(parts[1].trim());

                } else {
                    System.err.println("Invalid line: " + line);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void calculerItineraireMinimisantNombreRoutes(String city1,String city2){

    }

    public void calculerItineraireMinimisantKm(String city1, String city2){

    }
}

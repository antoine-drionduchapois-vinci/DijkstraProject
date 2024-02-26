import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Graph {

    protected Set<Integer> city;
    protected Map<Integer, Set<Integer>> cityMap;
    public Graph(File cityFile, File roadFile){
        city = new HashSet<>();
        cityMap = new HashMap<>();
        try{
            constructCitiesFromTxt(cityFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void constructCitiesFromTxt(File file) throws FileNotFoundException {
        if (file.getPath().equals("cities.txt")){
            try{
                Scanner scanner = new Scanner(file);
                // here code city extraction
                City myCity;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        int cityId = Integer.parseInt(parts[0].trim());
                        String cityName = parts[1].trim();
                        double latitude = Double.parseDouble(parts[2].trim());
                        double longitude = Double.parseDouble(parts[3].trim());

                        City city = new City(cityId, cityName, latitude, longitude);
                    } else {
                        System.err.println("Invalid line: " + line);
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            try{
                Scanner scanner = new Scanner(file);
                // here code for road extaraction
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    public void calculerItineraireMinimisantNombreRoutes(String city1,String city2){

    }

    public void calculerItineraireMinimisantKm(String city1, String city2){

    }
}

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {

    protected Set<Integer> city;
    protected Map<Integer, Set<Integer>> cityMap;
    public Graph(File cityFile, File roadFile){
        city = new HashSet<>();
        cityMap = new HashMap<>();


    }

    public void constructFromTxt(File file){

    }

    public void calculerItineraireMinimisantNombreRoutes(String city1,String city2){

    }

    public void calculerItineraireMinimisantKm(String city1, String city2){

    }
}

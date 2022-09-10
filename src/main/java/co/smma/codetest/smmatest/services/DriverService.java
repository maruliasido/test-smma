package co.smma.codetest.smmatest.services;

import co.smma.codetest.smmatest.objects.request.BasicRequest;
import co.smma.codetest.smmatest.objects.request.ClostestDriverRequest;
import co.smma.codetest.smmatest.objects.response.BadResponse;
import co.smma.codetest.smmatest.entities.Driver;
import co.smma.codetest.smmatest.respositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class DriverService {

    @Autowired private DriverRepository driverRepository;

    @Transactional
    public ResponseEntity<?> activateDriver(BasicRequest activateDriverRequest){

        BadResponse badResponse = basicValidation(activateDriverRequest);
        if (badResponse != null){
            return ResponseEntity.badRequest().body(badResponse);
        }

        Long idLong = Long.parseLong(activateDriverRequest.getDriver_id());
        driverRepository.setDriverToActive(idLong);
        return ResponseEntity.ok(driverRepository.getDriverById(idLong));
    }

    @Transactional
    public ResponseEntity<?> deassignDriver(BasicRequest request){

        BadResponse badResponse = basicValidation(request);
        if (badResponse != null){
            return ResponseEntity.badRequest().body(badResponse);
        }

        Long idLong = Long.parseLong(request.getDriver_id());
        Driver driver = driverRepository.getDriverById(idLong);
        if (!driver.getStatus().equalsIgnoreCase("OnJob")){
            BadResponse driverNotOnjob = new BadResponse();
            driverNotOnjob.setResponse("Driver status is not OnJob");
            driverNotOnjob.setUpdatingDataStatus("FAILED");
            return ResponseEntity.badRequest().body(driverNotOnjob);
        }

        driverRepository.deassignDriver(idLong);
        return ResponseEntity.ok(driverRepository.getDriverById(idLong));
    }

    @Transactional
    public ResponseEntity<?> deactivedDriver(BasicRequest request){

        BadResponse badResponse = basicValidation(request);
        if (badResponse != null){
            return ResponseEntity.badRequest().body(badResponse);
        }


        Long idLong = Long.parseLong(request.getDriver_id());
        Driver driver = driverRepository.getDriverById(idLong);
        if (driver.getStatus().equalsIgnoreCase("Off")){
            BadResponse driverNotOnjob = new BadResponse();
            driverNotOnjob.setResponse("Driver status is already off");
            driverNotOnjob.setUpdatingDataStatus("FAILED");
            return ResponseEntity.badRequest().body(driverNotOnjob);
        }

        driverRepository.setDriverToInctive(idLong);
        return ResponseEntity.ok(driverRepository.getDriverById(idLong));
    }

    public ResponseEntity<?> getClostestDriver(ClostestDriverRequest clostestDriverRequest){
        BadResponse badResponse = new BadResponse();
        if (clostestDriverRequest.getLat() == null || clostestDriverRequest.getLon() == null){
            badResponse.setResponse("Field can't be empty");
            return ResponseEntity.badRequest().body(badResponse);
        }

        try {
            Double.parseDouble(clostestDriverRequest.getLat());
            Double.parseDouble(clostestDriverRequest.getLon());
        }catch (Exception e){
            badResponse.setResponse("Wrong input value");
            return ResponseEntity.badRequest().body(badResponse);
        }

        List<Driver> driverList = driverRepository.findAll();
        HashMap<Long, Double> mapList = new HashMap<>();

        //dapatkan jarak, masukan ke maplist
        for (Driver driver : driverList){
            Double driverDistance =
                    distance(Double.parseDouble(clostestDriverRequest.getLat()),
                            Double.parseDouble(clostestDriverRequest.getLon()),
                            driver.getLat(), driver.getLon());
            mapList.put(driver.getId(), driverDistance);
        }

        Map<Long, Double> sortedHashMap = sortByValue(mapList);

        //get id driver yang paling dekat
        Long clostestDriver = sortedHashMap.entrySet().stream().findFirst().get().getKey();
        Driver driver = driverRepository.getDriverById(clostestDriver);

        return ResponseEntity.ok(driver);

    }

    private HashMap<Long, Double> sortByValue(HashMap<Long, Double> hm){
        // Creating a list from elements of HashMap
        List<Map.Entry<Long, Double> > list
                = new LinkedList<Map.Entry<Long, Double> >(
                hm.entrySet());

        // Sorting the list using Collections.sort() method
        // using Comparator
        Collections.sort(
                list,
                new Comparator<Map.Entry<Long, Double> >() {
                    public int compare(
                            Map.Entry<Long, Double> object1,
                            Map.Entry<Long, Double> object2)
                    {
                        return (object1.getValue())
                                .compareTo(object2.getValue());
                    }
                });

        // putting the  data from sorted list back to hashmap
        HashMap<Long, Double> result
                = new LinkedHashMap<Long, Double>();
        for (Map.Entry<Long, Double> me : list) {
            result.put(me.getKey(), me.getValue());
        }

        // returning the sorted HashMap
        return result;
    }

    private Double distance(double lat1, double lon1, double lat2, double lon2) {
        // haversine great circle distance approximation, returns meters
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60; // 60 nautical miles per degree of seperation
        dist = dist * 1852; // 1852 meters per nautical mile
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private BadResponse basicValidation(BasicRequest basicRequest){
        BadResponse emptyIdResponse = new BadResponse();
        emptyIdResponse.setResponse("Driver id cant be empty");
        emptyIdResponse.setUpdatingDataStatus("FAILED");

        BadResponse badResponse = new BadResponse();
        badResponse.setResponse("Driver is not found");
        badResponse.setUpdatingDataStatus("FAILED");

        if (basicRequest.getDriver_id() == null){
            return emptyIdResponse;
        }

        try {
            Long.parseLong(basicRequest.getDriver_id());
            return null;
        }catch (Exception e){
            return badResponse;
        }
    }

}

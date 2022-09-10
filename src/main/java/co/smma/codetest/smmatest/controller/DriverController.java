package co.smma.codetest.smmatest.controller;

import co.smma.codetest.smmatest.objects.request.BasicRequest;
import co.smma.codetest.smmatest.objects.request.ClostestDriverRequest;
import co.smma.codetest.smmatest.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping("/activate")
    public ResponseEntity<?> activate(@RequestBody BasicRequest activateDriverRequest){
        return driverService.activateDriver(activateDriverRequest);
    }

    @PostMapping("/find-clostest-driver")
    public ResponseEntity<?> findDriver(@RequestBody ClostestDriverRequest request){
        return driverService.getClostestDriver(request);
    }

    @PostMapping("/deassign-driver")
    public ResponseEntity<?> deassignDriver(@RequestBody BasicRequest request){
        return driverService.deassignDriver(request);
    }

    @PostMapping("/deactive-driver")
    public ResponseEntity<?> findDriver(@RequestBody BasicRequest request){
        return driverService.deactivedDriver(request);
    }

}

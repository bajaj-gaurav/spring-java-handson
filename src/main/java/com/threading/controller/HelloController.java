package com.threading.controller;


import static com.threading.variables.Constants.GET_CONFIG_API;
import static com.threading.variables.Constants.GET_CONFIG_API_2;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.threading.configService.configCallsInterface;
import com.threading.configService.configServiceSddc.SddcResourceConfig;
import com.threading.configService.configServiceSddc.SddcResourceConfigConfiguration;
import com.threading.configService.configServiceVhs.ConfigOpsOld;
import com.threading.configService.configServiceVhs.ConfigurationSource;
import com.threading.persistence.Employee;
import com.threading.persistence.EmployeeRepository;
import com.threading.resolver.resolverMain;
//import com.threading.scheduledAnnotation.TestScheduler;
import com.threading.service.AsynchronousService;


@RestController
public class HelloController {

    @Autowired
    resolverMain resol;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    private AsynchronousService anAsynchronousService;

    @Autowired
    private configCallsInterface config;

    @Qualifier("configServiceAsSource")
    @Autowired
    ConfigurationSource cm;

    @Autowired
    ConfigOpsOld co;

    @Autowired
    SddcResourceConfig sddcResourceConfig;


    //@Autowired
    //private TestScheduler scheduler;



    private static final Logger LOGGER = LoggerFactory.getLogger(HelloController.class);


    @RequestMapping("/runTask")
    public String executeAsync() {

        anAsynchronousService.executeAsynchronously();

        return "OK";
    }

    @RequestMapping("/employees")
    public List<Employee> employees() throws Exception {

        return anAsynchronousService.fetchEmployess().get();
    }

    @RequestMapping(value = "/employee",method = RequestMethod.POST)
    public void add(@RequestBody Employee employee) {

        employeeRepository.save(employee);
    }

    @RequestMapping(value = "/httpCall",method = RequestMethod.GET)
    public String http() {

        return anAsynchronousService.httpCallOutput();
    }

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String home() {

        return "Hello Gaurav!!!";
    }

    @RequestMapping(value = "/resolve",method = RequestMethod.GET)
    public String resolve() throws IOException{
        return resol.getResolver();

    }

    /**
     * Update the data in config service.
     */
    @RequestMapping(method = RequestMethod.GET, value = GET_CONFIG_API)
    public String callUpdateConfigServiceData(@RequestParam(value = "action") String action) {
        String s = "templist";

        config.testConfig();
        return action;


    }

    @RequestMapping(method = RequestMethod.GET, value = GET_CONFIG_API_2)
    public String callUpdateConfigServiceData2() {
        String s = "templist";

        cm.getCollectorConfigurations();
        return "hello2";


    }

    @RequestMapping(method = RequestMethod.GET, value = "/getCon")
    public String callUpdateConfigServiceData3() {
        String s = "templist";

        co.getConfig();
        return "hello2";


    }

    @RequestMapping(method = RequestMethod.GET, value = "/resourceConfig")
    public String sddcResourceConfig() {
        Optional<SddcResourceConfigConfiguration> resourceConfigParams
                = sddcResourceConfig.get();

        return "hello";
    }


/*    @RequestMapping(value = "/scheduler",method = RequestMethod.GET)
    public List schedule() {

        List sc = scheduler.search();
        sc.add(sc.size());
        return sc;
    }

    @RequestMapping(value = "/scheduler2",method = RequestMethod.GET)
    public List schedule2() {

        List sc = scheduler.search();
        sc.add(sc.size());
        return sc;
    }*/
}


package com.qrapids.forecast_rest.services;

import Forecast.Common;
import Forecast.Elastic_RForecast;
import Forecast.ForecastDTO;
import com.qrapids.forecast_rest.configuration.Connection;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ForecastService {

    @Autowired
    private Connection connection;

    @RequestMapping("api/ForecastTechniques")
    public synchronized Common.ForecastTechnique[] getForecastTechniques (@RequestParam("host") String host, @RequestParam("port") String port, @RequestParam("path") String path, @RequestParam("user") String user, @RequestParam("pwd") String pwd) throws Exception {
        return connection.getConnection(host, port, path, user, pwd).getForecastTechniques();
    }

    @RequestMapping("api/Train")
    public void train(@RequestParam("host") String host, @RequestParam("port") String port, @RequestParam("path") String path, @RequestParam("user") String user, @RequestParam("pwd") String pwd, @RequestParam("index") String index, @RequestParam("elements") List<String> elements, @RequestParam("frequency") String freq, @RequestParam(value = "technique", required = false) String technique) {
        Elastic_RForecast elastic_rForecast = connection.getConnection(host, port, path, user, pwd);
        if (technique != null) {
            Common.ForecastTechnique forecastTechnique = Common.ForecastTechnique.valueOf(technique);
            trainTechnique(elastic_rForecast, elements, index, freq, forecastTechnique);
        } else {
            for (Common.ForecastTechnique forecastTechnique : Common.ForecastTechnique.values()) {
                trainTechnique(elastic_rForecast, elements, index, freq, forecastTechnique);
            }
        }
    }

    private synchronized void trainTechnique (Elastic_RForecast elastic_rForecast, List<String> elements, String index, String freq, Common.ForecastTechnique forecastTechnique) {
        try {
            elastic_rForecast.multipleElementTrain(elements.toArray(new String[0]), index, freq, forecastTechnique);
        } catch (Exception e) {
        }
    }

    @RequestMapping("/api/Metrics/Forecast")
    public List<ForecastDTO> getForecastMetric(@RequestParam("host") String host, @RequestParam("port") String port, @RequestParam("path") String path, @RequestParam("user") String user, @RequestParam("pwd") String pwd, @RequestParam("index_metrics") String indexMetrics, @RequestParam("metric") List<String> metric, @RequestParam("frequency") String freq, @RequestParam("horizon") String horizon, @RequestParam("technique") String technique) throws Exception {
        return getForecast(host, port, path, user, pwd, indexMetrics, metric, freq, horizon, technique);
    }

    @RequestMapping("/api/QualityFactors/Forecast")
    public List<ForecastDTO> getForecastFactor(@RequestParam("host") String host, @RequestParam("port") String port, @RequestParam("path") String path, @RequestParam("user") String user, @RequestParam("pwd") String pwd, @RequestParam("index_factors") String indexFactors, @RequestParam("factor") List<String> factor, @RequestParam("frequency") String freq, @RequestParam("horizon") String horizon, @RequestParam("technique") String technique) throws  Exception {
        return getForecast(host, port, path, user, pwd, indexFactors, factor, freq, horizon, technique);
    }

    @RequestMapping("/api/SIs/Forecast")
    public List<ForecastDTO> getForecastSI(@RequestParam("host") String host, @RequestParam("port") String port, @RequestParam("path") String path, @RequestParam("user") String user, @RequestParam("pwd") String pwd, @RequestParam("index_strategic_indicators") String indexSIs, @RequestParam("si") List<String> si, @RequestParam("frequency") String freq, @RequestParam("horizon") String horizon, @RequestParam("technique") String technique) throws Exception {
        return getForecast(host, port, path, user, pwd, indexSIs, si, freq, horizon, technique);
    }

    private synchronized List<ForecastDTO> getForecast(String host, String port, String path, String user, String pwd, String indexFactors, List<String> factor, String freq, String horizon, String technique) throws REXPMismatchException, REngineException {
        try {
            Common.ForecastTechnique forecastTechnique = Common.ForecastTechnique.valueOf(technique);
            return connection.getConnection(host, port, path, user, pwd).multipleElementForecast(factor.toArray(new String[0]), indexFactors, freq, horizon, forecastTechnique);
        }
        catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Forecast technique not supported");
        }
    }
}

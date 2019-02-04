package com.qrapids.forecast_rest.services;

import Forecast.Common;
import Forecast.ForecastDTO;
import com.qrapids.forecast_rest.configuration.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ForecastService {

    @Autowired
    private Connection connection;

    @RequestMapping("/api/Metrics/Forecast")
    public List<ForecastDTO> getForecastMetric(@RequestParam("host") String host, @RequestParam("port") String port, @RequestParam("path") String path, @RequestParam("user") String user, @RequestParam("pwd") String pwd, @RequestParam("index_metrics") String indexMetrics, @RequestParam("metric") List<String> metric, @RequestParam("frequency") String freq, @RequestParam("horizon") String horizon) {
        if (metric.size() < 2)
            return new ArrayList<ForecastDTO>() {{
                add(connection.getConnection(host, port, path, user, pwd).metricForecast(indexMetrics, metric.get(0), freq, horizon, Common.ForecastTechnique.ETS));
            }};
        else
            return connection.getConnection(host, port, path, user, pwd).multipleMetricsForecast(indexMetrics, metric.toArray(new String[0]), freq, horizon, Common.ForecastTechnique.ETS);
    }

    @RequestMapping("/api/QualityFactors/Forecast")
    public List<ForecastDTO> getForecastFactor(@RequestParam("host") String host, @RequestParam("port") String port, @RequestParam("path") String path, @RequestParam("user") String user, @RequestParam("pwd") String pwd, @RequestParam("index_factors") String indexFactors, @RequestParam("factor") List<String> factor, @RequestParam("frequency") String freq, @RequestParam("horizon") String horizon) {
        if (factor.size() < 2)
            return new ArrayList<ForecastDTO>() {{
                add(connection.getConnection(host, port, path, user, pwd).factorForecast(indexFactors, factor.get(0), freq, horizon, Common.ForecastTechnique.ETS));
            }};
        else
            return connection.getConnection(host, port, path, user, pwd).multipleFactorsForecast(indexFactors, factor.toArray(new String[0]), freq, horizon, Common.ForecastTechnique.ETS);
    }
}

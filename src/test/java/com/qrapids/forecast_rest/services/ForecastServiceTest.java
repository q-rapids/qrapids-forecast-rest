package com.qrapids.forecast_rest.services;

import Forecast.Common;
import Forecast.Elastic_RForecast;
import Forecast.ForecastDTO;
import com.qrapids.forecast_rest.configuration.Connection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class ForecastServiceTest {

    private MockMvc mockMvc;

    @Mock
    private Connection connection;

    @InjectMocks
    private ForecastService forecastService;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(forecastService)
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void getForecastTechniques() throws Exception {
        String host = "host";
        String port = "8080";
        String path = "path";
        String user = "user";
        String pwd = "pwd";

        Elastic_RForecast elastic_rForecast = mock(Elastic_RForecast.class);
        when(connection.getConnection(host,port,path,user,pwd)).thenReturn(elastic_rForecast);
        when(elastic_rForecast.getForecastTechniques()).thenReturn(Common.ForecastTechnique.values());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/ForecastTechniques")
                .param("host", host)
                .param("port", port)
                .param("path", path)
                .param("user", user)
                .param("pwd", pwd);

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0]", is(Common.ForecastTechnique.ARIMA.toString())))
                .andExpect(jsonPath("$[1]", is(Common.ForecastTechnique.ARIMA_FORCE_SEASONALITY.toString())))
                .andExpect(jsonPath("$[2]", is(Common.ForecastTechnique.THETA.toString())))
                .andExpect(jsonPath("$[3]", is(Common.ForecastTechnique.ETS.toString())))
                .andExpect(jsonPath("$[4]", is(Common.ForecastTechnique.ETSDAMPED.toString())))
                .andExpect(jsonPath("$[5]", is(Common.ForecastTechnique.BAGGEDETS.toString())))
                .andExpect(jsonPath("$[6]", is(Common.ForecastTechnique.STL.toString())))
                .andExpect(jsonPath("$[7]", is(Common.ForecastTechnique.NN.toString())))
                .andExpect(jsonPath("$[8]", is(Common.ForecastTechnique.HYBRID.toString())))
                .andExpect(jsonPath("$[9]", is(Common.ForecastTechnique.PROPHET.toString())))
                .andDo(document("ForecastTechniques",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]")
                                        .description("Forecast techiques"))));

        verify(elastic_rForecast, times(1)).getForecastTechniques();
        verifyNoMoreInteractions(elastic_rForecast);
        verify(connection, times(1)).getConnection(host,port,path,user,pwd);
        verifyNoMoreInteractions(connection);
    }

    @Test
    public void train() throws Exception {
        String host = "host";
        String port = "8080";
        String path = "path";
        String user = "user";
        String pwd = "pwd";
        String index = "indexMetrics";
        String[] elements = {"testperformance"};
        String frequency = "7";

        Elastic_RForecast elastic_rForecast = mock(Elastic_RForecast.class);
        when(connection.getConnection(host,port,path,user,pwd)).thenReturn(elastic_rForecast);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/Train")
                .param("host", host)
                .param("port", port)
                .param("path", path)
                .param("user", user)
                .param("pwd", pwd)
                .param("index", index)
                .param("elements", elements)
                .param("frequency", frequency);

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(document("Train",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("host")
                                        .description("ElasticSearch host"),
                                parameterWithName("port")
                                        .description("ElasticSearch port"),
                                parameterWithName("path")
                                        .description("ElasticSearch path"),
                                parameterWithName("user")
                                        .description("ElasticSearch user"),
                                parameterWithName("pwd")
                                        .description("ElasticSearch password"),
                                parameterWithName("index")
                                        .description("ElasticSearch name of metrics or factors index"),
                                parameterWithName("elements")
                                        .description("List of elements to forecast"),
                                parameterWithName("frequency")
                                        .description("Amount of days conforming the natural time period of the data samples")
                        )));

        verify(elastic_rForecast, times(Common.ForecastTechnique.values().length)).multipleElementTrain(eq(elements),eq(index),eq(frequency), any(Common.ForecastTechnique.class));
        verifyNoMoreInteractions(elastic_rForecast);
        verify(connection, times(1)).getConnection(host,port,path,user,pwd);
        verifyNoMoreInteractions(connection);
    }

    @Test
    public void getForecastMetric() throws Exception {
        String host = "host";
        String port = "8080";
        String path = "path";
        String user = "user";
        String pwd = "pwd";
        String indexMetrics = "indexMetrics";
        String[] metric = {"testperformance"};
        String frequency = "7";
        String horizon = "10";
        String technique = "ETS";

        ForecastDTO forecastDTO = new ForecastDTO();
        forecastDTO.setId("testperformance");
        double[] lower80 = {0.6414524692520523,0.6310027287500249,0.6229788194502033,0.6162098305036225,0.6102422881085776,0.6048436667711347,0.5998758631263202};
        forecastDTO.setLower80(lower80);
        double[] lower95 = {0.6281052189200053,0.612123719944535,0.5998522096396154,0.5894999343947172,0.5803733658781132,0.5721168870347814,0.564519286994423};
        forecastDTO.setLower95(lower95);
        double[] mean = {0.6666660253129496,0.6666660253129496,0.6666660253129496,0.6666660253129496,0.6666660253129496,0.6666660253129496,0.6666660253129496};
        forecastDTO.setMean(mean);
        double[] upper80 = {0.6918795813738469,0.7023293218758743,0.7103532311756959,0.7171222201222767,0.7230897625173216,0.7284883838547646,0.733456187499579};
        forecastDTO.setUpper80(upper80);
        double[] upper95 = {0.705226831705894,0.7212083306813643,0.7334798409862838,0.7438321162311821,0.752958684747786,0.7612151635911178,0.7688127636314762};
        forecastDTO.setUpper95(upper95);
        ArrayList<ForecastDTO> forecastDTOArrayList = new ArrayList<>();
        forecastDTOArrayList.add(forecastDTO);

        Elastic_RForecast elastic_rForecast = mock(Elastic_RForecast.class);
        when(connection.getConnection(host,port,path,user,pwd)).thenReturn(elastic_rForecast);
        when(elastic_rForecast.multipleElementForecast(metric,indexMetrics,frequency,horizon, Common.ForecastTechnique.ETS)).thenReturn(forecastDTOArrayList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/Metrics/Forecast")
                .param("host", host)
                .param("port", port)
                .param("path", path)
                .param("user", user)
                .param("pwd", pwd)
                .param("index_metrics", indexMetrics)
                .param("metric", metric)
                .param("frequency", frequency)
                .param("horizon", horizon)
                .param("technique", technique);

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("testperformance")))
                .andExpect(jsonPath("$[0].lower80", hasSize(7)))
                .andExpect(jsonPath("$[0].lower80[0]", is(0.6414524692520523)))
                .andExpect(jsonPath("$[0].lower95", hasSize(7)))
                .andExpect(jsonPath("$[0].lower95[0]", is(0.6281052189200053)))
                .andExpect(jsonPath("$[0].mean", hasSize(7)))
                .andExpect(jsonPath("$[0].mean[0]", is(0.6666660253129496)))
                .andExpect(jsonPath("$[0].upper80", hasSize(7)))
                .andExpect(jsonPath("$[0].upper80[0]", is(0.6918795813738469)))
                .andExpect(jsonPath("$[0].upper95", hasSize(7)))
                .andExpect(jsonPath("$[0].upper95[0]", is(0.705226831705894)))
                .andDo(document("Metrics/Forecast",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("host")
                                        .description("ElasticSearch host"),
                                parameterWithName("port")
                                        .description("ElasticSearch port"),
                                parameterWithName("path")
                                        .description("ElasticSearch path"),
                                parameterWithName("user")
                                        .description("ElasticSearch user"),
                                parameterWithName("pwd")
                                        .description("ElasticSearch password"),
                                parameterWithName("index_metrics")
                                        .description("ElasticSearch name of metrics index"),
                                parameterWithName("metric")
                                        .description("List of metrics to forecast"),
                                parameterWithName("frequency")
                                        .description("Amount of days conforming the natural time period of the data samples"),
                                parameterWithName("horizon")
                                        .description("Amount of days that the forecasting will cover"),
                                parameterWithName("technique")
                                        .description("Forecasting technique")
                        ),
                        responseFields(
                                fieldWithPath("[].id")
                                        .description("Metric id"),
                                fieldWithPath("[].lower80")
                                        .description("Lower 80 confidence predicted interval"),
                                fieldWithPath("[].lower95")
                                        .description("Lower 95 confidence predicted interval"),
                                fieldWithPath("[].mean")
                                        .description("Mean confidence predicted interval"),
                                fieldWithPath("[].upper80")
                                        .description("Upper 80 confidence predicted interval"),
                                fieldWithPath("[].upper95")
                                        .description("Upper 95 confidence predicted interval"),
                                fieldWithPath("[].error")
                                        .description("Description of the forecasting error")
                        )));

        verify(elastic_rForecast, times(1)).multipleElementForecast(metric,indexMetrics,frequency,horizon, Common.ForecastTechnique.ETS);
        verifyNoMoreInteractions(elastic_rForecast);
        verify(connection, times(1)).getConnection(host,port,path,user,pwd);
        verifyNoMoreInteractions(connection);
    }

    @Test
    public void getForecastMetricWrongTechnique () throws Exception {
        String host = "host";
        String port = "8080";
        String path = "path";
        String user = "user";
        String pwd = "pwd";
        String indexMetrics = "indexMetrics";
        String[] metric = {"testperformance"};
        String frequency = "7";
        String horizon = "10";
        String technique = "wrong";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/Metrics/Forecast")
                .param("host", host)
                .param("port", port)
                .param("path", path)
                .param("user", user)
                .param("pwd", pwd)
                .param("index_metrics", indexMetrics)
                .param("metric", metric)
                .param("frequency", frequency)
                .param("horizon", horizon)
                .param("technique", technique);

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is("Forecast technique not supported")));
    }

    @Test
    public void getForecastFactor() throws Exception {
        String host = "host";
        String port = "8080";
        String path = "path";
        String user = "user";
        String pwd = "pwd";
        String indexFactors = "indexFactors";
        String[] factor = {"blockingcode"};
        String frequency = "7";
        String horizon = "10";
        String technique = "ETS";

        ForecastDTO forecastDTO = new ForecastDTO();
        forecastDTO.setId("blockingcode");
        double[] lower80 = {0.6414524692520523,0.6310027287500249,0.6229788194502033,0.6162098305036225,0.6102422881085776,0.6048436667711347,0.5998758631263202};
        forecastDTO.setLower80(lower80);
        double[] lower95 = {0.6281052189200053,0.612123719944535,0.5998522096396154,0.5894999343947172,0.5803733658781132,0.5721168870347814,0.564519286994423};
        forecastDTO.setLower95(lower95);
        double[] mean = {0.6666660253129496,0.6666660253129496,0.6666660253129496,0.6666660253129496,0.6666660253129496,0.6666660253129496,0.6666660253129496};
        forecastDTO.setMean(mean);
        double[] upper80 = {0.6918795813738469,0.7023293218758743,0.7103532311756959,0.7171222201222767,0.7230897625173216,0.7284883838547646,0.733456187499579};
        forecastDTO.setUpper80(upper80);
        double[] upper95 = {0.705226831705894,0.7212083306813643,0.7334798409862838,0.7438321162311821,0.752958684747786,0.7612151635911178,0.7688127636314762};
        forecastDTO.setUpper95(upper95);
        ArrayList<ForecastDTO> forecastDTOArrayList = new ArrayList<>();
        forecastDTOArrayList.add(forecastDTO);

        Elastic_RForecast elastic_rForecast = mock(Elastic_RForecast.class);
        when(connection.getConnection(host,port,path,user,pwd)).thenReturn(elastic_rForecast);
        when(elastic_rForecast.multipleElementForecast(factor,indexFactors,frequency,horizon, Common.ForecastTechnique.ETS)).thenReturn(forecastDTOArrayList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/QualityFactors/Forecast")
                .param("host", host)
                .param("port", port)
                .param("path", path)
                .param("user", user)
                .param("pwd", pwd)
                .param("index_factors", indexFactors)
                .param("factor", factor)
                .param("frequency", frequency)
                .param("horizon", horizon)
                .param("technique", technique);

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("blockingcode")))
                .andExpect(jsonPath("$[0].lower80", hasSize(7)))
                .andExpect(jsonPath("$[0].lower80[0]", is(0.6414524692520523)))
                .andExpect(jsonPath("$[0].lower95", hasSize(7)))
                .andExpect(jsonPath("$[0].lower95[0]", is(0.6281052189200053)))
                .andExpect(jsonPath("$[0].mean", hasSize(7)))
                .andExpect(jsonPath("$[0].mean[0]", is(0.6666660253129496)))
                .andExpect(jsonPath("$[0].upper80", hasSize(7)))
                .andExpect(jsonPath("$[0].upper80[0]", is(0.6918795813738469)))
                .andExpect(jsonPath("$[0].upper95", hasSize(7)))
                .andExpect(jsonPath("$[0].upper95[0]", is(0.705226831705894)))
                .andDo(document("Factors/Forecast",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("host")
                                        .description("ElasticSearch host"),
                                parameterWithName("port")
                                        .description("ElasticSearch port"),
                                parameterWithName("path")
                                        .description("ElasticSearch path"),
                                parameterWithName("user")
                                        .description("ElasticSearch user"),
                                parameterWithName("pwd")
                                        .description("ElasticSearch password"),
                                parameterWithName("index_factors")
                                        .description("ElasticSearch name of factors index"),
                                parameterWithName("factor")
                                        .description("List of factors to forecast"),
                                parameterWithName("frequency")
                                        .description("Amount of days conforming the natural time period of the data samples"),
                                parameterWithName("horizon")
                                        .description("Amount of days that the forecasting will cover"),
                                parameterWithName("technique")
                                        .description("Forecasting technique")
                        ),
                        responseFields(
                                fieldWithPath("[].id")
                                        .description("Factor id"),
                                fieldWithPath("[].lower80")
                                        .description("Lower 80 confidence predicted interval"),
                                fieldWithPath("[].lower95")
                                        .description("Lower 95 confidence predicted interval"),
                                fieldWithPath("[].mean")
                                        .description("Mean confidence predicted interval"),
                                fieldWithPath("[].upper80")
                                        .description("Upper 80 confidence predicted interval"),
                                fieldWithPath("[].upper95")
                                        .description("Upper 95 confidence predicted interval"),
                                fieldWithPath("[].error")
                                        .description("Description of the forecasting error")
                        )));

        verify(elastic_rForecast, times(1)).multipleElementForecast(factor,indexFactors,frequency,horizon, Common.ForecastTechnique.ETS);
        verifyNoMoreInteractions(elastic_rForecast);
        verify(connection, times(1)).getConnection(host,port,path,user,pwd);
        verifyNoMoreInteractions(connection);
    }

    @Test
    public void getForecastFactorWrongTechnique () throws Exception {
        String host = "host";
        String port = "8080";
        String path = "path";
        String user = "user";
        String pwd = "pwd";
        String indexFactors = "indexFactors";
        String[] factor = {"blockingcode"};
        String frequency = "7";
        String horizon = "10";
        String technique = "wrong";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/QualityFactors/Forecast")
                .param("host", host)
                .param("port", port)
                .param("path", path)
                .param("user", user)
                .param("pwd", pwd)
                .param("index_factors", indexFactors)
                .param("factor", factor)
                .param("frequency", frequency)
                .param("horizon", horizon)
                .param("technique", technique);

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is("Forecast technique not supported")));
    }
}
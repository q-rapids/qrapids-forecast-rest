package com.qrapids.forecast_rest.configuration;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import Forecast.Elastic_RForecast;

@Configuration
@PropertySource(value="classpath:application.properties", encoding="UTF-8")
public class Connection {

    private boolean init;

    private Elastic_RForecast eForecast;

    private String host;
    private String port;
    private String path;
    private String user;
    private String pwd;

    @Value("${Rserve.host}")
    private String Rhost;

    @Value("${Rserve.port}")
    private String Rport;

    @Value("${Rscripts.location}")
    private String RLocation;

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public String getRhost() {
        return Rhost;
    }

    public String getRport() {
        return Rport;
    }

    public String getRLocation() {
        return RLocation;
    }

    public Connection() {
        this.init = false;
    }

    public Elastic_RForecast getConnection(String host, String port, String path, String user, String pwd) {
        if (!init || !host.equals(this.host) || !port.equals(this.port) || !path.equals(this.path) || !user.equals(this.user) || !pwd.equals(this.pwd)) {
            try {
                this.host = host;
                this.port = port;
                this.path = path;
                this.user = user;
                this.pwd = pwd;
                eForecast = new Elastic_RForecast(host, Integer.parseInt(port), path, user, pwd, Rhost, Integer.parseInt(Rport), RLocation);
                this.init = true;
                return eForecast;
            } catch (REXPMismatchException|REngineException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return eForecast;
        }

    }
}

package dev.alex.standardizer.config;

import java.util.TimeZone;

public class SpringZoneConfig {
    public void setTime(){
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }
}

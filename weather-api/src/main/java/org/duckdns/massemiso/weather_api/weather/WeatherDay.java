package org.duckdns.massemiso.weather_api.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class WeatherDay{
    String datetime;
    Float temp;
    Float feelslike;
    Float humidity;
    Float precipprob;
    Float windspeed;
    Float pressure;
    Float visibility;
    String sunrise;
    String sunset;
    String conditions;
    String description;
}

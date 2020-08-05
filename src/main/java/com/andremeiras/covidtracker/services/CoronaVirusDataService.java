package com.andremeiras.covidtracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.andremeiras.covidtracker.models.LocationsStats;

@Service
public class CoronaVirusDataService {

	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

	private List<LocationsStats> allStats = new ArrayList<>();

	// HEADER of CSV: Province/State,Country/Region,Lat,Long
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *") // SS:MM:HH DD/MM/AA -- it will run each one hour
	public void fetchVirusData() throws IOException, InterruptedException {
		List<LocationsStats> newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

		StringReader csvBodyReader = new StringReader(httpResponse.body());

		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			LocationsStats locationsStat = new LocationsStats();
			locationsStat.setState(record.get("Province/State"));
			locationsStat.setCountry(record.get("Country/Region"));
			int latestCases = Integer.parseInt(record.get(record.size() - 1));
			int prevDayCases = Integer.parseInt(record.get(record.size() - 2));

			locationsStat.setLatestTotalCases(latestCases);
			locationsStat.setDiffFromPrevDay(latestCases - prevDayCases);

			System.out.println(locationsStat);

			newStats.add(locationsStat);
		}
		this.allStats = newStats;

	}

	public List<LocationsStats> getAllStats() {
		return allStats;
	}

	public void setAllStats(List<LocationsStats> allStats) {
		this.allStats = allStats;
	}
}

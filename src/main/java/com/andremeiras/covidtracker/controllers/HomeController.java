package com.andremeiras.covidtracker.controllers;

import java.util.List;

import com.andremeiras.covidtracker.models.LocationsStats;
import com.andremeiras.covidtracker.services.CoronaVirusDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@Autowired
	CoronaVirusDataService coronaVirusDataService;

	@GetMapping("/")
	public String home(final Model model) {

		final List<LocationsStats> allStats = coronaVirusDataService.getAllStats();
		final int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
		final int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();

		model.addAttribute("locationStats", allStats);
		model.addAttribute("totalReportedCases", totalReportedCases);

		model.addAttribute("totalNewCases", totalNewCases);

		return "home";
	}
}

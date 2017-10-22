package com.karens.coding.server.stats.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.karens.coding.server.stats.health.ServerStatistics;
import com.karens.coding.server.stats.rest.RestResponse;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController("ServerStatisticsRestController")
@Validated
public class ServerStatisticsRestController {

	/** Logger */
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(ServerStatisticsRestController.class);

	
	@RequestMapping(value = "/rest/server/statistics", produces = "application/json")
	public RestResponse getServerDiskUsage() {
		RestResponse ajaxResponse = new RestResponse();

		ServerStatistics serverUsage = new ServerStatistics();
		ajaxResponse.setResult(serverUsage);
		ajaxResponse.setMessage("Success");
		ajaxResponse.setStatusCode(200);
		return ajaxResponse;
	}
	
	
	
}

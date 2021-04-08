package com.lti.cast.imaging.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

import com.lti.cast.imaging.dao.CastImagingDAO;
import com.lti.cast.imaging.request.RefreshToken;
import com.lti.cast.imaging.request.UserDetails;
import com.lti.cast.imaging.response.external.ExternalRoot;

import com.lti.cast.imaging.response.level.Edge;
import com.lti.cast.imaging.response.level.ExternalNodeDendency;
import com.lti.cast.imaging.response.level.Node;
import com.lti.cast.imaging.response.level.Nodes;
import com.lti.cast.imaging.response.level.Root;
import com.lti.cast.imaging.security.ImagingURLs;
import com.lti.cast.imaging.security.SecurityConstants;
import com.lti.cast.imaging.util.ImagingServiceUtil;

@Repository

public class CastImagingService implements CastImagingDAO {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ImagingServiceUtil imaginService;

	@Autowired
	Environment environmentVariable;

	String service_url = new String();

	private static final Logger LOGGER = LoggerFactory.getLogger(CastImagingService.class);

	@Override
	public String genrateToken() {
		try {
			SecurityConstants.SERVICE_URL = environmentVariable.getProperty("cast.SERVICE_URL").trim();

			final String secretKey = environmentVariable.getProperty("cast.key").trim();// "castimagingapi";// ;
			UserDetails userDetails = new UserDetails();
			userDetails.setPassword(
					imaginService.decrypt(environmentVariable.getProperty("cast.password").trim(), secretKey));
			userDetails.setUsername(
					imaginService.decrypt(environmentVariable.getProperty("cast.loginname").trim(), secretKey));

			HttpHeaders headers = new HttpHeaders();
			// You can use more methods of HttpHeaders to set additional information
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

			service_url = SecurityConstants.SERVICE_URL + ImagingURLs.CAST_AUTH;
			HttpEntity<UserDetails> requestEntity = new HttpEntity<>(userDetails, headers);

			ResponseEntity<UserDetails> response = restTemplate.exchange(service_url, HttpMethod.POST, requestEntity,
					UserDetails.class);

			SecurityConstants.AUTH_TOKEN = response.getHeaders().get("Authentication").get(0);
			return SecurityConstants.AUTH_TOKEN;
		} catch (Exception e) {
			return e.toString();
		}
	}

	@Override
	public String genrateEncryption(String encryptionString) {
		try {
			final String secretKey = environmentVariable.getProperty("cast.key").trim();
			return imaginService.encrypt(encryptionString, secretKey);
		} catch (Exception e) {
			return e.toString();
		}
	}

	@Override
	public String levelExtration(String appName) {
		try {
			service_url = SecurityConstants.SERVICE_URL + "api/app/" + appName + ImagingURLs.CAST_LEVEL;

			HttpHeaders headers = getHeader();

			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

			ResponseEntity<String> result = restTemplate.exchange(service_url, HttpMethod.GET, entity, String.class);

			Gson googleJson = new Gson();
			Root rootJson = googleJson.fromJson(result.getBody(), Root.class);

			List<String> externalName = new ArrayList<String>();
			List<Integer> nodeIds = new ArrayList<Integer>();
			List<Integer> edgesIds = new ArrayList<Integer>();

			if (rootJson.getSuccess().getGraph().getNodes() != null)
				for (Node node : rootJson.getSuccess().getGraph().getNodes()) {
					nodeIds.add(node.getId());
					externalName.add(node.getData().getName());
				}

			if (rootJson.getSuccess().getGraph().getEdges() != null)
				for (Edge edge : rootJson.getSuccess().getGraph().getEdges())
					edgesIds.add(edge.getId());

			List<List<Integer>> nodeEdgesIdesExternal = extrnalLevelExtration(appName, externalName);
			String fileName = appName;
			try {
				ResponseEntity<String> response = downloadLevelExternalFile(appName, nodeIds, edgesIds);

				ResponseEntity<String> responseExternal = downloadLevelExternalFile(appName,
						nodeEdgesIdesExternal.get(0), nodeEdgesIdesExternal.get(1));

				List<ResponseEntity<String>> responses = new ArrayList<>();
				responses.add(response);
				responses.add(responseExternal);
				// Download External and Level dependency
				fileDownload(appName, responses);

			} catch (JsonProcessingException e) {
				LOGGER.error(e.getMessage());
				return e.getMessage();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				return e.getMessage();
			}
			return fileName;
		} catch (Exception e) {
			return e.toString();
		}
	}
	
	public void validateFileEixtence(String applicationName) {
		File file = null;		
		try {
			file = new File(applicationName+ImagingURLs.CAST_DOWNLOAD_FILE_TYPE);			
			if (file.exists()) 
				file.delete();			
		} catch (Exception e) {			
		}
	}

	public String fileDownload(String applicationName, List<ResponseEntity<String>> responses) {
		validateFileEixtence(applicationName);
		try {
			int count = 0;
			String sheetName = new String();
			for (ResponseEntity<String> responseEntity : responses) {
				if (count == 0)
					sheetName = ImagingURLs.CAST_LEVEL_EXTRACTION;
				else
					sheetName = ImagingURLs.CAST_EXTERNAL_LEVEL_EXTRACTION;
				try {
					imaginService.generateXlsFiles(applicationName, sheetName, responseEntity);
					count++;
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
					return e.toString();
				}
			}
	
			return applicationName;
		}catch (Exception e) {
			return e.toString();
		}
	}

	public List<List<Integer>> extrnalLevelExtration(String clientAppName, List<String> dependencys) {
		try {
			String result = "";
			// Node and edges Combination
			List<List<Integer>> nodesEdgeIds = new ArrayList<>();
			List<Integer> nodeIds = new ArrayList<Integer>();
			List<Integer> edgesIds = new ArrayList<Integer>();
	
			for (String dependentAppName : dependencys) {
				result = extrnalDependencyName(clientAppName, dependentAppName);
	
				Gson googleJson = new Gson();
				ExternalRoot rootJson = googleJson.fromJson(result, ExternalRoot.class);
	
				if (rootJson.getSuccess().getGraph().getNodes() != null)
					for (com.lti.cast.imaging.response.external.Node node : rootJson.getSuccess().getGraph().getNodes())
						nodeIds.add(node.getId());
	
				if (rootJson.getSuccess().getGraph().getEdges() != null)
					for (com.lti.cast.imaging.response.external.Edge edge : rootJson.getSuccess().getGraph().getEdges())
						edgesIds.add(edge.getId());
	
			}
			nodesEdgeIds.add(nodeIds);
			nodesEdgeIds.add(edgesIds);
			return nodesEdgeIds;
		}catch (Exception e) {
			return null;
		}
	}

	// Response for external(Dependent Nodes) Nodes
	public String extrnalDependencyName(String clientName, String appName) {
		try {
			service_url = SecurityConstants.SERVICE_URL + "api/app/" + clientName + ImagingURLs.CAST_EXTERNAL_LEVEL;
			HttpHeaders headers = getHeader();
			ExternalNodeDendency externalNode = new ExternalNodeDendency();
			externalNode.setName(appName);
	
			HttpEntity<ExternalNodeDendency> requestEntity = new HttpEntity<ExternalNodeDendency>(externalNode, headers);
	
			ResponseEntity<String> response = restTemplate.exchange(service_url, HttpMethod.POST, requestEntity,
					String.class);
	
			return response.getBody();
		}catch (Exception e) {
			return e.toString();
		}
	}

	// Download files CSV / XLS for External and Level
	public ResponseEntity<String> downloadLevelExternalFile(String applicationName, List<Integer> nodeIds,
			List<Integer> edgesIds) throws JsonProcessingException {
		try { 
			service_url = SecurityConstants.SERVICE_URL + "api/app/" + applicationName
					+ ImagingURLs.CAST_DOWNLOAD_FILE_TYPE_CSV;
	
			Nodes nodes = new Nodes();
			nodes.setNodes(nodeIds);
			nodes.setEdges(edgesIds);
	
			HttpHeaders headers = getHeader();
	
			HttpEntity<Nodes> requestEntity = new HttpEntity<>(nodes, headers);
	
			ResponseEntity<String> response = restTemplate.exchange(service_url, HttpMethod.POST, requestEntity,
					String.class);
			return response;
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	// Set Header with Auth Key
	private HttpHeaders getHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("authentication", SecurityConstants.AUTH_TOKEN);
		return headers;
	}

}

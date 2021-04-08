package com.lti.cast.imaging.controller;

import javax.ws.rs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.lti.cast.imaging.dao.CastImagingDAO;

@RestController
@RequestMapping("/imaging")
@CrossOrigin

public class CastController {
	

	@Autowired
	CastImagingDAO castImagingDao;
	
	
	@PostMapping(path = "/castauth")
	public ResponseEntity<String> generateEncryption(@QueryParam("name") String name) throws Exception {		
		if (name == null || name.length() == 0)
			return ResponseEntity.badRequest().body("\n Please provide value to encrypt");		
		// Token Generation
		String generateToken = castImagingDao.genrateEncryption(name);		
		if (generateToken != null || generateToken.length() <= 0)
			return ResponseEntity.ok().body(generateToken);
		return ResponseEntity.noContent().build();

	}
	
	
	@PostMapping(path = "/extract")
	public ResponseEntity<String> generateAuthorizationToken( @QueryParam("name") String name) throws Exception {		
		if (name == null || name.length() == 0) 			
			return ResponseEntity.badRequest().body("\n Please Provide Application Name");		
		// Token Generation
		castImagingDao.genrateToken();

		// Extraction
		String response = castImagingDao.levelExtration(name);
		if (response != null || response.length() <= 0)
			return ResponseEntity.ok().body("\n Component and Component mapping data extraction file download for Application : " + name);
		return ResponseEntity.noContent().build();
	}
	
}

package com.lti.cast.imaging.dao;


import org.springframework.stereotype.Service;

import com.lti.cast.imaging.request.RefreshToken;
import com.lti.cast.imaging.request.UserDetails;

@Service
public interface CastImagingDAO {	
	public String genrateToken();	
	public String levelExtration(String appName);
	public String genrateEncryption(String encryptionString);
}

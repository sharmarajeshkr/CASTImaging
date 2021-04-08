package com.lti.cast.imaging.response.external;

import java.util.List;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Data{   
    public String AipId;     
    public String Color;    
    public String DrillDown;     
    public boolean External;    
    public String FullName;    
    public String Level;    
    public String Name;    
    public String Objects;    
    public String Type;
    public List<String> labels;   
    public String Parent;    
    public boolean IsOnlyEscalated;
    public List<String> details;
    public String links;
	
}
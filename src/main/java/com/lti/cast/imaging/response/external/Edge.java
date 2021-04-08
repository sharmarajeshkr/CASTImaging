package com.lti.cast.imaging.response.external;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Edge{
    public int id;
    public int source;
    public int target;
    public String type;
    public Data data;
    public Attributes attributes;
    
    
    
}


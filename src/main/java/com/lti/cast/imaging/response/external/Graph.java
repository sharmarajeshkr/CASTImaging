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

public class Graph{
    public List<Node> nodes;
    public List<Edge> edges;
    
}

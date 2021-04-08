package com.lti.cast.imaging.response.level;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor

public class Node{
    public int id;
    public Data data;
    public Attributes attributes;     
}

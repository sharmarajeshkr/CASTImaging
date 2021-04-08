package com.lti.cast.imaging.response.level;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Attributes{
    public Badges badges;
    public Image image;
    public InnerStroke innerStroke;
    public OuterStroke outerStroke;
    public double radius;
    public String shape;
    public String color;
    public Text text;
    public double width;
    public int strokeWidth;
    
}

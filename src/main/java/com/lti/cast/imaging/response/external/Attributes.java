package com.lti.cast.imaging.response.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Attributes {
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

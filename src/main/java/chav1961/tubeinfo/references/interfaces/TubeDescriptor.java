package chav1961.tubeinfo.references.interfaces;


import javax.swing.Icon;

import chav1961.purelib.ui.swing.useful.svg.SVGPainter;

public interface TubeDescriptor {
	TubesType getType();
	String getAbbr();
	String getDescription();
	SVGPainter getScheme();
	SVGPainter getCorpusDraw();
	TubePanelType getPanelType();
	TubeConnector[] getConnectors();
	TubeCorpusType getCorpus();
	TubeParameter[] getParameters();
	float[] getValues();
	TubeParameter[] getParameters(int lampNo);
	float[] getValues(int lampNo);
	Graphic[] getGraphics();
	Graphic[] getGraphics(int lampNo);
}

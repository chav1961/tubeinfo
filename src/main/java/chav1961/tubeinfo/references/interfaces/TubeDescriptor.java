package chav1961.tubeinfo.references.interfaces;


import chav1961.purelib.i18n.interfaces.LocalizerOwner;
import chav1961.purelib.ui.swing.useful.svg.SVGPainter;

public interface TubeDescriptor extends LocalizerOwner {
	TubesType getType();
	String getAbbr();
	String getDescription();
	String getUsage();
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

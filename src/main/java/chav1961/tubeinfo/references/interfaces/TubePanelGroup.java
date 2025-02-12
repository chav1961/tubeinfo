package chav1961.tubeinfo.references.interfaces;

import java.net.URL;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.references.interfaces.TubePanelGroup/chav1961/tubeinfo/i18n/i18n.xml")
public enum TubePanelGroup {
	@LocaleResource(value="chav1961.calc.references.tubes.tubePanelGroup.pin7",tooltip="chav1961.calc.references.tubes.tubePanelGroup.pin7.tt",icon="root://chav1961.tubeinfo.references.interfaces.TubePanelGroup/chav1961/tubeinfo/references/interfaces/pin7.png")
	PIN7("pin7.svg"),
	@LocaleResource(value="chav1961.calc.references.tubes.tubePanelGroup.pin9",tooltip="chav1961.calc.references.tubes.tubePanelGroup.pin9.tt",icon="root://chav1961.tubeinfo.references.interfaces.TubePanelGroup/chav1961/tubeinfo/references/interfaces/pin9.png")
	PIN9("pin7.svg");
	
	private final String	svgDraw;
	
	private TubePanelGroup(final String svgDraw) {
		this.svgDraw = svgDraw;
	}
	
	public URL getSvgURL() {
		return this.getClass().getResource(svgDraw);
	}
}

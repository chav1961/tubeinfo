package chav1961.tubeinfo.references.interfaces;

import java.awt.Point;
import java.net.URL;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.tubeinfo.references.interfaces.TubePanelGroup/chav1961/tubeinfo/i18n/i18n.xml")
public enum TubePanelGroup {
	@LocaleResource(value="chav1961s.tubes.tubePanelGroup.pin7",tooltip="chav1961s.tubes.tubePanelGroup.pin7.tt",icon="root://chav1961.tubeinfo.references.interfaces.TubePanelGroup/chav1961/tubeinfo/references/interfaces/pin7.png")
	PIN7("pin7.svg", new Pin(1, 54, 148), new Pin(2, 34, 100), new Pin(3, 54, 52), new Pin(4, 100, 32), new Pin(5, 146, 52), new Pin(6, 166, 100), new Pin(7, 146, 148)),
	@LocaleResource(value="chav1961s.tubes.tubePanelGroup.pin8",tooltip="chav1961s.tubes.tubePanelGroup.pin8.tt",icon="root://chav1961.tubeinfo.references.interfaces.TubePanelGroup/chav1961/tubeinfo/references/interfaces/pin9.png")
	PIN8("pin9.svg", new Pin(1, 61, 154), new Pin(2, 37, 121), new Pin(3, 36, 79), new Pin(4, 61, 45), new Pin(5, 100, 34), new Pin(6, 139, 46), new Pin(7, 164, 80), new Pin(8, 163, 121), new Pin(9, 139, 154)),
	@LocaleResource(value="chav1961s.tubes.tubePanelGroup.pin9",tooltip="chav1961s.tubes.tubePanelGroup.pin9.tt",icon="root://chav1961.tubeinfo.references.interfaces.TubePanelGroup/chav1961/tubeinfo/references/interfaces/pin9.png")
	PIN9("pin9.svg", new Pin(1, 61, 154), new Pin(2, 37, 121), new Pin(3, 36, 79), new Pin(4, 61, 45), new Pin(5, 100, 34), new Pin(6, 139, 46), new Pin(7, 164, 80), new Pin(8, 163, 121), new Pin(9, 139, 154)),
	;
	
	private final String	svgDraw;
	private final Pin[]		pins;
	
	private TubePanelGroup(final String svgDraw, final Pin... pins) {
		this.svgDraw = svgDraw;
		this.pins = pins;
	}
	
	public URL getSvgURL() {
		return this.getClass().getResource(svgDraw);
	}
	
	public Pin[] getPins() {
		return pins;
	}
	
	public static class Pin {
		public final int	pinNumber;
		public final Point	point;
		
		public Pin(final int pinNumber, final int x, final int y) {
			this.pinNumber = pinNumber;
			this.point = new Point(x, y);
		}

		@Override
		public String toString() {
			return "Pin [pinNumber=" + pinNumber + ", point=" + point + "]";
		}
	}
}

package chav1961.tubeinfo.references.interfaces;

import java.net.URL;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.tubeinfo.references.interfaces.TubesType/chav1961/tubeinfo/i18n/i18n.xml")
public enum TubesType {
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.all",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.all.tt",icon="all.png")
	ALL(1, 1, "double_triode.svg"), 
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.diode",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.diode.tt",icon="diode.png")
	DIODE(1, 1, "double_triode.svg"), 
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.doubleDiode",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.doubleDiode.tt",icon="doubleDiode.png")
	DOUBLE_DIODE(1, 2, "double_triode.svg"),
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.triode",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.triode.tt",icon="triode.png")
	TRIODE(1, 1, "double_triode.svg"),
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.doubleTriode",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.doubleTriode.tt",icon="doubleTriode.png")
	DOUBLE_TRIODE(1, 2, "double_triode.svg"),
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.tetrode",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.tetrode.tt",icon="tethrode.png")
	TETRODE(1, 1, "double_triode.svg"),
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.beamTetrode",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.beamTetrode.tt",icon="beamTethrode.png")
	BEAM_TETRODE(1, 1, "beam_tetrode.svg"),
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.doubleTetrode",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.doubleTetrode.tt",icon="doubleTethrode.png")
	DOUBLE_TETRODE(1, 2, "double_triode.svg"),
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.pentode",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.pentode.tt",icon="pentode.png")
	PENTODE(1, 1, "pentode.svg"),
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.doublePentode",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.doublePentode.tt",icon="doublePentode.png")
	DOUBLE_PENTODE(1, 2, "double_triode.svg"),
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.triodePentode",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.triodePentode.tt",icon="triodePentode.png")
	TRIODE_PENTODE(2, 1, "triode_pentode.svg"),
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.heptode",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.heptode.tt",icon="heptode.png")
	HEPTODE(1, 1, "heptode.svg"),
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.triodeHeptode",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.triodeHeptode.tt",icon="triodeHeptode.png")
	TRIODE_HEPTODE(2, 1, "triode_heptode.svg"),
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesType.special",tooltip="chav1961.tubeinfo.references.interfaces.tubesType.special.tt",icon="special.png")
	SPECIAL(2, 1, "double_triode.svg");
	
	private final URL	reference;
	private final int	numberOfLampTypes;
	private final int	numberOfLamps;
	
	TubesType(final int numberOfLampTypes, final int numberOfLamps, final String svgReference) {
		this.numberOfLampTypes = numberOfLampTypes;
		this.numberOfLamps = numberOfLamps;
		this.reference = getClass().getResource(svgReference);
	}
	
	public int getNumberOfLampTypes() {
		return numberOfLampTypes;
	}

	public int getNumberOfLamps() {
		return numberOfLamps;
	}
	
	public URL getSvgURL() {
		return reference;
	}
	
	public static int getMaximumNumberOfLampTypes() {
		int	result = -1;
		
		for(TubesType item : values()) {
			if (result < item.getNumberOfLampTypes()) {
				result = item.getNumberOfLampTypes();
			}
		}
		return result;
	}
}

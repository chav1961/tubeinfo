package chav1961.tubeinfo.references.interfaces;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.tubeinfo.references.interfaces.TubeParameterUnit/chav1961/tubeinfo/i18n/i18n.xml")
public enum TubeParameterUnit {
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.none",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.none.tt")
	NONE(TubeParameterUnitBase.NONE, 1.0f), 
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.amper",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.amper.tt")
	AMPER(TubeParameterUnitBase.AMPER, 1.0f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.milliamper",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.milliamper.tt")
	MILLIAMPER(TubeParameterUnitBase.AMPER, 1e-3f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.microamper",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.microamper.tt")
	MICROAMPER(TubeParameterUnitBase.AMPER, 1e-6f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.volt",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.volt.tt")
	VOLT(TubeParameterUnitBase.VOLT, 1.0f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.millivolt",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.millivolt.tt")
	MILLIVOLT(TubeParameterUnitBase.VOLT, 1e-3f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.kilovolt",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.kilovolt.tt")
	KILOVOLT(TubeParameterUnitBase.VOLT, 1e3f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.ohm",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.ohm.tt")
	OHM(TubeParameterUnitBase.OHM, 1.0f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.kiloohm",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.kiloohm.tt")
	KOHM(TubeParameterUnitBase.OHM, 1e3f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.megaohm",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.megaohm.tt")
	MOHM(TubeParameterUnitBase.OHM, 1e6f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.picofarada",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.picofarada.tt")
	PICOFARADA(TubeParameterUnitBase.PICOFARADA, 1.0f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.ma_v",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.ma_v.tt")
	MA_V(TubeParameterUnitBase.A_V, 1.0f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.celsius",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.celsius.tt")
	CELSIUS(TubeParameterUnitBase.CELSIUS, 1.0f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.megahertz",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.megahertz.tt")
	MEGAHERTZ(TubeParameterUnitBase.HERTZ, 1000000.0f),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.watt",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.watt.tt")
	WATT(TubeParameterUnitBase.WATT, 1.0f);
	
	private final TubeParameterUnitBase	base;
	private final float 	scale;
	
	private TubeParameterUnit(final TubeParameterUnitBase base, final float scale) {
		this.base = base;
		this.scale = scale;
	}
	
	public TubeParameterUnitBase getBase() {
		return base;
	}
	
	public float getScale() {
		return scale;
	}
}

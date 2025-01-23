package chav1961.tubeinfo.references.interfaces;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.tubeinfo.references.interfaces.TubeParameterUnit/chav1961/tubeinfo/i18n/i18n.xml")
public enum TubeParameterUnit {
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.none",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.none.tt")
	NONE,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.amper",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.amper.tt")
	AMPER,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.milliamper",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.milliamper.tt")
	MILLIAMPER,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.microamper",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.microamper.tt")
	MICROAMPER,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.volt",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.volt.tt")
	VOLT,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.millivolt",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.millivolt.tt")
	MILLIVOLT,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.kilovolt",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.kilovolt.tt")
	KILOVOLT,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.ohm",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.ohm.tt")
	OM,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.kiloohm",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.kiloohm.tt")
	KOM,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.megaohm",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.megaohm.tt")
	MOM,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.picofarada",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.picofarada.tt")
	PICOFARADA,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.ma_v",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.ma_v.tt")
	MA_V,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.celsius",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.celsius.tt")
	CELSIUS,
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameterUnit.watt",tooltip="chav1961.tubeinfo.references.tubes.tubeParameterUnit.watt.tt")
	WATT;
}

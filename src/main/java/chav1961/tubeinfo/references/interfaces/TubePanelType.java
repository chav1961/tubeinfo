package chav1961.tubeinfo.references.interfaces;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.references.interfaces.TubePanelType/chav1961/tubeinfo/i18n/i18n.xml")
public enum TubePanelType {
	@LocaleResource(value="chav1961.calc.references.tubes.tubePanelType.pin7",tooltip="chav1961.calc.references.tubes.tubePanelType.pin7.tt")
	PIN_FLEX10(TubePanelGroup.PIN_FLEX10),
	@LocaleResource(value="chav1961.calc.references.tubes.tubePanelType.pin7",tooltip="chav1961.calc.references.tubes.tubePanelType.pin7.tt")
	PIN_NOUV(TubePanelGroup.PIN_NOUV),
	@LocaleResource(value="chav1961.calc.references.tubes.tubePanelType.pin7",tooltip="chav1961.calc.references.tubes.tubePanelType.pin7.tt")
	PIN7(TubePanelGroup.PIN7),
	@LocaleResource(value="chav1961.calc.references.tubes.tubePanelType.pin8",tooltip="chav1961.calc.references.tubes.tubePanelType.pin8.tt")
	PIN8(TubePanelGroup.PIN8),
	@LocaleResource(value="chav1961.calc.references.tubes.tubePanelType.pin9",tooltip="chav1961.calc.references.tubes.tubePanelType.pin9.tt")
	PIN9(TubePanelGroup.PIN9),
	@LocaleResource(value="chav1961.calc.references.tubes.tubePanelType.pin9",tooltip="chav1961.calc.references.tubes.tubePanelType.pin9.tt")
	PIN10(TubePanelGroup.PIN10),
	@LocaleResource(value="chav1961.calc.references.tubes.tubePanelType.pin9",tooltip="chav1961.calc.references.tubes.tubePanelType.pin9.tt")
	PIN10_F(TubePanelGroup.PIN10_F),
	;
	
	private final TubePanelGroup	group;
	
	private TubePanelType(final TubePanelGroup group) {
		this.group = group;
	}
	
	public TubePanelGroup getGroup() {
		return group;
	}
}

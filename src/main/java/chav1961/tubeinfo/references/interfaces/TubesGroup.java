package chav1961.tubeinfo.references.interfaces;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.tubeinfo.references.interfaces.TubesGroup/chav1961/tubeinfo/i18n/i18n.xml")
public enum TubesGroup {
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesGroup.electron",tooltip="chav1961.tubeinfo.references.interfaces.tubesGroup.electron.tt",icon="electron.png")
	GROUP_ELECTRON,
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesGroup.ion",tooltip="chav1961.tubeinfo.references.interfaces.tubesGroup.ion.tt",icon="ion.png")
	GROUP_ION,
	@LocaleResource(value="chav1961.tubeinfo.references.interfaces.tubesGroup.images",tooltip="chav1961.tubeinfo.references.interfaces.tubesGroup.images.tt",icon="images.png")
	GROUP_IMAGES;
}

package chav1961.tubeinfo.references.interfaces;

import java.net.URL;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.references.interfaces.TubeCorpusGroup/chav1961/calculator/i18n/i18n.xml")
public enum TubeCorpusGroup {
	@LocaleResource(value="chav1961.calc.references.tubes.tubeCorpusGroup.tg",tooltip="chav1961.calc.references.tubes.tubeCorpusGroup.tg.tt",icon="type.png")
	TG("double_triode.svg");
	
	private final URL	reference;
	
	private TubeCorpusGroup(final String svgreference) {
		this.reference = getClass().getResource(svgreference);
	}
	
	public URL getSvgURL() {
		return reference;
	}
}

package chav1961.tubeinfo.references.interfaces;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.references.interfaces.TubeCorpusType/chav1961/calculator/i18n/i18n.xml")
public enum TubeCorpusType {
	@LocaleResource(value="chav1961.calc.references.tubes.tubeCorpusType.tg",tooltip="chav1961.calc.references.tubes.tubeCorpusType.tg.tt",icon="type.png")
	TG("", TubeCorpusGroup.TG);
	
	private final String			corpusName;
	private final TubeCorpusGroup	corpusGroup;
	
	private TubeCorpusType(final String corpusName, final TubeCorpusGroup group) {
		this.corpusName = corpusName;
		this.corpusGroup = group;
	}
	
	public String getCorpusName() {
		return corpusName;
	}
	
	public Icon getCorpus() {
		return new ImageIcon(this.getClass().getResource(getCorpusName()));
	}
	
	public TubeCorpusGroup getGroup() {
		return corpusGroup;
	}
}

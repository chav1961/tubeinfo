package chav1961.tubeinfo.references.tubes;

import java.io.IOException;

import javax.swing.JSplitPane;

import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.tubeinfo.utils.InternalUtils;

public class ElectronicTubesScreen extends JSplitPane {
	private static final long serialVersionUID = -3893101411523333611L;

	private final TubesPreview	preview;
	private final TubesTabs		tabs;
	
	public ElectronicTubesScreen(final Localizer localizer) throws IOException {
		super(JSplitPane.VERTICAL_SPLIT);
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null"); 
		}
		else {
			this.preview = new TubesPreview(localizer);
			this.tabs = new TubesTabs(localizer, (d)->preview.refreshDesc(d), InternalUtils.getTubeDescriptor(XMLBasedTube.class.getResourceAsStream("test.xml")));
			
			setLeftComponent(preview);
			setRightComponent(tabs);
			this.setDividerLocation(350);
		}		
	}
}

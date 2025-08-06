package chav1961.tubeinfo.references.tubes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.tubeinfo.references.interfaces.TubeDescriptor;
import chav1961.tubeinfo.utils.InternalUtils;
import chav1961.tubeinfo.utils.ParallelManager;

public class ElectronicTubesScreen extends JSplitPane {
	private static final long serialVersionUID = -3893101411523333611L;

	private final TubesPreview	preview;
	private final TubesTabs		tabs;
	
	public ElectronicTubesScreen(final Localizer localizer, final File contentDir, final int numberOfThreads) throws IOException {
		super(JSplitPane.VERTICAL_SPLIT);
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null"); 
		}
		else {
			final Vector<TubeDescriptor>	list = new Vector<>();

			try(final ParallelManager	loader = new ParallelManager()) {
				final URI		builtin = XMLBasedTube.class.getResource("/chav1961/tubeinfo/builtin/").toURI();

				for (String item : loadBuiltin(URIUtils.appendRelativePath2URI(builtin, "index.txt"))) {
					if (!Utils.checkEmptyOrNullString(item)) {
						final URI	uri = URIUtils.appendRelativePath2URI(builtin, item);
						
						loader.addTask(()->{
							try(final InputStream	is2 = uri.toURL().openStream()) {
								list.add(InternalUtils.getTubeDescriptor(uri, is2));
							}
						});
					}
				}
			} catch (URISyntaxException e) {
				throw new IOException(e.getLocalizedMessage(), e);
			} 
			final TubeDescriptor[] descList = list.toArray(new TubeDescriptor[list.size()]);
			
			Arrays.sort(descList, (o1,o2)->o1.getAbbr().compareTo(o2.getAbbr()));
			this.preview = new TubesPreview(localizer);
			this.tabs = new TubesTabs(localizer, (d)->preview.refreshDesc(d), descList);
			
			setLeftComponent(preview);
			setRightComponent(tabs);
			SwingUtils.assignActionKey(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, SwingUtils.KS_FIND, (e)->{tabs.selectFilter();}, SwingUtils.ACTION_FIND);
			this.setDividerLocation(350);
		}		
	}

	private String[] loadBuiltin(final URI uri) throws IOException {
		return Utils.fromResource(uri.toURL()).replace("\r", "").split("\n");
	}
}

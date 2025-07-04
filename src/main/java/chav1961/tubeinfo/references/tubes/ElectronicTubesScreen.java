package chav1961.tubeinfo.references.tubes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import chav1961.purelib.basic.URIUtils;
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
				final URI		ext = contentDir.toURI();
				final String[]	externalSet = loadExternal(contentDir);
				final URI		builtin = XMLBasedTube.class.getResource("/chav1961/tubeinfo/builtin/").toURI();
				final String[]	builtinSet = loadBuiltin(URIUtils.appendRelativePath2URI(builtin, "index.txt"));

				for (String item : externalSet) {
					final URI	uri = URIUtils.appendRelativePath2URI(ext, item);
					loader.addTask(()->{
						try(final InputStream	is2 = uri.toURL().openStream()) {
							list.add(InternalUtils.getTubeDescriptor(uri, is2));
						}
					});
				}
				for (String item : builtinSet) {
					final URI	uri = URIUtils.appendRelativePath2URI(builtin, item);
					loader.addTask(()->{
						try(final InputStream	is2 = uri.toURL().openStream()) {
							list.add(InternalUtils.getTubeDescriptor(uri, is2));
						}
					});
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

	private String[] loadExternal(final File contentDir) {
		final List<String>	result = new ArrayList<>();
		
		loadExternal(contentDir, "", result);
		return result.toArray(new String[result.size()]);
	}

	private void loadExternal(final File current, final String path, final List<String> result) {
		if (current.exists()) {
			if (current.isFile() && current.getName().endsWith(".xml")) {
				result.add(path + '/' + current.getName());
			}
			else if (current.isDirectory()) {
				final File[]	list = current.listFiles();
				
				if (list != null) {
					for(File item : list) {
						loadExternal(item, path + '/' + item.getName(), result);
					}
				}
			}
		}
	}
	
	private String[] loadBuiltin(final URI uri) throws IOException {
		final List<String>	result = new ArrayList<>();
	
		try(final InputStream	is = uri.toURL().openStream();
			final Reader		rdr = new InputStreamReader(is);
			final BufferedReader	brdr = new BufferedReader(rdr)) {
			
			String	line;
			while ((line = brdr.readLine()) != null) {
				result.add(line);
			}
		}
		return result.toArray(new String[result.size()]);
	}
	
}

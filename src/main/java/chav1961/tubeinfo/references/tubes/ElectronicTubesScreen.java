package chav1961.tubeinfo.references.tubes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

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
				try {
					final URI			std = XMLBasedTube.class.getResource("/chav1961/tubeinfo/builtin/").toURI();
	
					if ("jar".equals(std.getScheme())) {
						final Map<String, String> 	env = new HashMap<>();
						
				        try(final FileSystem fs = FileSystems.newFileSystem(std, env)) {
							
				        	loadContent(fs, fs.getPath("chav1961", "tubeinfo", "builtin"), list, loader);
				        }
					}
					else {
						loadContent(std.toURL(), list, loader);
					}
					
					if (contentDir.exists() && contentDir.isDirectory() && contentDir.canRead()) {
						loadContent(contentDir, list, loader);
					}
				} catch (URISyntaxException e) {
					throw new IOException(e.getLocalizedMessage(), e);
				} 
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

	private void loadContent(final FileSystem fs, final Path path, final List<TubeDescriptor> list, final ParallelManager loader) throws IOException {
		if (path.getFileName() != null) {
			if (path.getFileName().toString().endsWith(".xml")) {
				loader.addTask(()->{
					try(final InputStream	is = Files.newInputStream(path, StandardOpenOption.READ)) {
						list.add(InternalUtils.getTubeDescriptor(path.toAbsolutePath().toUri(), is));
					}
				});
			}
			else if (!path.getFileName().toString().contains(".")) {	// Possibly directory???
				try (final DirectoryStream<Path>	stream = Files.newDirectoryStream(path)) {
					for (Path dir : stream) {
						loader.addTask(()->{
							loadContent(fs, dir, list, loader);
						});
					}
				}
			}
		}
		else {
			try (final DirectoryStream<Path>	stream = Files.newDirectoryStream(path)) {
				for (Path dir : stream) {
					loader.addTask(()->{
						loadContent(fs, dir, list, loader);
					});
				}
			}
		}
	}

	private void loadContent(final URL root, final List<TubeDescriptor> list, final ParallelManager loader) throws IOException {
		if (root.getPath().endsWith(".xml")) {
			loader.addTask(()->{
				try(final InputStream		is = root.openStream()) {
					list.add(InternalUtils.getTubeDescriptor(root.toURI(), is));
				} catch (URISyntaxException e) {
					throw new IOException(e.getLocalizedMessage(), e);
				}
			});
		}
		else {
			final String	path = root.getPath();
			
			if (!path.substring(path.lastIndexOf("/")+1).contains(".")) {	// Possibly directory???
				loader.addTask(()->{
					try(final InputStream		is = root.openStream();
						final Reader			rdr = new InputStreamReader(is);
						final BufferedReader	brdr = new BufferedReader(rdr)) {
						String	line;
						
						while ((line = brdr.readLine()) != null) {
							loadContent(URI.create(root.toExternalForm()+"/"+line).toURL(), list, loader);
						}
					}
				});
			}
		}
	}	
	
	private void loadContent(final File root, final List<TubeDescriptor> list, final ParallelManager loader) throws IOException {
		if (root.exists() && root.canRead()) {
			if (root.isDirectory()) {
				loader.addTask(()->{
					final File[]	content = root.listFiles();
					
					if (content != null) {
						for(File item : content) {
							loadContent(item, list, loader);
						}
					}
				});
			}
			else if (root.getName().endsWith(".xml")) {
				loader.addTask(()->{
					try(final InputStream	is = new FileInputStream(root)) {
						list.add(InternalUtils.getTubeDescriptor(root.toURI(), is));
					}
				});
			}
		}
	}
}

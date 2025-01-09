package chav1961.tubeinfo.references;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.ui.swing.useful.svg.SVGPainter;
import chav1961.purelib.ui.swing.useful.svg.SVGParser;
import chav1961.tubeinfo.references.interfaces.TubeDescriptor;
import chav1961.tubeinfo.utils.InternalUtils;

public class TubesRepo {
	public static enum ContentType {
		DESCRIPTOR,
		VECTOR_GRAPHICS,
		RASTER_GRAPHICS
	}
	
	private final Map<String, Content>	content = new HashMap<>();
	private final EnumMap<ContentType, Map<String, Content>>	contentByType = new EnumMap<>(ContentType.class);
	
	public TubesRepo(final File... content) throws IOException {
		if (content == null || Utils.checkArrayContent4Nulls(content) >= 0) {
			throw new IllegalArgumentException("Content is null or contains nulls inside");
		}
		else {
			for (File item : content) {
				if (!item.exists() || !item.canRead()) {
					throw new IOException("File ["+item.getAbsolutePath()+"] not exists or can't be accessed for you");
				}
				else if (item.isDirectory()) {
					loadDir(item, "");
				}
				else {
					loadZip(item);
				}
			}
		}
	}

	
	
	private void loadDir(final File current, final String name) throws IOException {
		if (current.exists() && current.canRead()) {
			if (current.isFile()) {
				try (final InputStream	is = new FileInputStream(new File(current, name))) {
					loadContent(name, is);
				}
			}
			else {
				final File[]	content = current.listFiles();
				
				if (content != null) {
					for(File item : content) {
						loadDir(current, name + '/' +item.getName());
					}
				}
			}
		}
		
	}

	private void loadZip(final File zip) throws IOException {
		try(final InputStream		is = new FileInputStream(zip);
			final ZipInputStream	zis = new ZipInputStream(is)) {
			
			ZipEntry	ze;
			while ((ze =zis.getNextEntry()) != null) {
				loadContent(ze.getName(), zis);
			}
		}
	}

	private void loadContent(final String name, final InputStream is) throws IOException {
		final String	tail = name.substring(name.lastIndexOf('.') + 1);
		final Content	item;
		
		switch (tail) {
			case "xml"	:
				item = new Content(InternalUtils.getTubeDescriptor(is));
				break;
			case "png"	:
				item = new Content(ImageIO.read(is));
				break;
			case "svg"	:
				try {
					item = new Content(SVGParser.parse(is));
					
					break;
				} catch (ContentException e) {
					throw new IOException(e);
				}
			default :
				throw new IOException("Name ["+name+"] has unidentivied content"); 
		}
		if (!contentByType.containsKey(item.type)) {
			contentByType.put(item.type, new HashMap<>());
		}
		contentByType.get(item.type).put(name, item);
		content.put(name, item);
	}

	private static class Content {
		private final ContentType		type;
		private final TubeDescriptor	desc;
		private final SVGPainter		painter;
		private final BufferedImage		image;
	
		private Content(final TubeDescriptor desc) {
			this.type = ContentType.DESCRIPTOR;
			this.desc = desc;
			this.painter = null;
			this.image = null;
		}
		
		private Content(final SVGPainter painter) {
			this.type = ContentType.VECTOR_GRAPHICS;
			this.desc = null;
			this.painter = painter;
			this.image = null;
		}
		
		private Content(final BufferedImage image) {
			this.type = ContentType.RASTER_GRAPHICS;
			this.desc = null;
			this.painter = null;
			this.image = image;
		}

	}

}

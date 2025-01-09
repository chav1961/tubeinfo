package chav1961.tubeinfo.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.tubeinfo.references.interfaces.TubeDescriptor;
import chav1961.tubeinfo.references.tubes.XMLBasedTube;

public class InternalUtils {
	public static LocaleResource getLocaleResource(final Enum<?> item) {
		if (item == null) {
			throw new NullPointerException("Item to get resource can't be null");
		}
		else {
			try {
				return item.getClass().getField(item.name()).getAnnotation(LocaleResource.class);
			} catch (NoSuchFieldException | SecurityException e) {
				throw new IllegalArgumentException(e.getLocalizedMessage(), e);
			}
		}
	}
	
	public static TubeDescriptor getTubeDescriptor(final InputStream is) throws IOException {
		if (is == null) {
			throw new NullPointerException("Input stream to parse can't be null");
		}
		else {
			try {
				final DocumentBuilder	builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				final Document 			doc = builder.parse(is);
				
				doc.getDocumentElement().normalize();
				return new XMLBasedTube((Element)doc.getElementsByTagName("tube").item(0));
			} catch (ParserConfigurationException | SAXException e) {
				throw new IOException(e.getLocalizedMessage(), e);
			}
		}
	}
}

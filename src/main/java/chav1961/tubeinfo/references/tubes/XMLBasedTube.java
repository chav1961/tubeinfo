package chav1961.tubeinfo.references.tubes;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Locale;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import chav1961.purelib.basic.CharUtils.SubstitutionSource;
import chav1961.purelib.basic.MimeType;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.i18n.KeyValueLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;
import chav1961.purelib.streams.char2char.CreoleWriter;
import chav1961.purelib.ui.swing.useful.svg.SVGPainter;
import chav1961.purelib.ui.swing.useful.svg.SVGParser;
import chav1961.tubeinfo.references.interfaces.Graphic;
import chav1961.tubeinfo.references.interfaces.PinType;
import chav1961.tubeinfo.references.interfaces.TubeConnector;
import chav1961.tubeinfo.references.interfaces.TubeConnectorType;
import chav1961.tubeinfo.references.interfaces.TubeCorpusType;
import chav1961.tubeinfo.references.interfaces.TubeDescriptor;
import chav1961.tubeinfo.references.interfaces.TubePanelType;
import chav1961.tubeinfo.references.interfaces.TubeParameter;
import chav1961.tubeinfo.references.interfaces.TubesType;

/*
 * <tube type="TubesType" abbr="1a1a" panel="TubePanelType" corpus="TubeCorpusType" description="localizedKey" usage="localizedKey">
 *   <localizedKeys>
 *   	<lang name="en">
 *   		<key name="name">text</key>
 *   		<help name="name">text</help>
 *   	</lang>
 *   </localizedKeys>
 * 	 <parms>
 *   	<parm name="TubeParameter" number="0|1|2">value</parm>
 * 	 </parms>
 *   <scheme href="scheme.svg"> 	// if href is missing, href from TubesType will be used.
 *   	<parm name="p1">1</parm>  	// Optional
 *   </scheme>
 *   <corpus href="corpus.svg">		// if href is missing, href from TubesCorpusType will be used.
 *   	<parm name="p1">1</parm>	// Optional
 *   </corpus>
 *   <connectors>
 *   	<connector number="0|1|2" type="TubeConnector.TypeConnectorType" pin="pinNumber" pinType="TubeConnector.PinType"/>
 *   </connectors>
 *   <graphics>
 *   	<graphic name="localizedKey" tooltip="localizedKey" number="0|1|2">base64Image</graphic>
 *   </graphics>
 * </tube>
 */

public class XMLBasedTube implements TubeDescriptor, LocalizerOwner {
	private static final String			TAG_PARMS = "parms";
	private static final String			TAG_PARM = "parm";
	private static final String			TAG_SCHEME = "scheme";
	private static final String			TAG_CORPUS = "corpus";
	private static final String			TAG_CONNECTOR = "connector";
	private static final String			TAG_GRAPHIC = "graphic";
	private static final String			TAG_LOCALIZED_KEYS = "localizedKeys";
	private static final String			TAG_LANG = "lang";
	private static final String			TAG_KEY = "key";
	private static final String			TAG_HELP = "help";
	private static final String			ATTR_TYPE = "type";
	private static final String			ATTR_ABBR = "abbr";
	private static final String			ATTR_CORPUS = "corpus";
	private static final String			ATTR_PANEL = "panel";
	private static final String			ATTR_DESCRIPTION = "description";
	private static final String			ATTR_USAGE = "usage";
	private static final String			ATTR_NAME = "name";
	private static final String			ATTR_NUMBER = "number";
	private static final String			ATTR_PIN = "pin";
	private static final String			ATTR_PINTYPE = "pinType";
	private static final String			ATTR_TOOLTIP = "tooltip";
	private static final String			ATTR_HREF = "href";

	private final URI					currentURI;
	private final TubesType				type;
	private final String				abbr;
	private final String				description;
	private final String				usage;
	private final TubeCorpusType		corpus;
	private final TubePanelType			panel;
	private final SVGPainter			scheme;
	private final SVGPainter			corpusDraw;
	private final TubeParmDescriptor[]	parms;
	private final TubeConnectorImpl[]	connectors;
	private final GraphicImpl[]			graphics;
	private final KeyValueLocalizer		localizer = new KeyValueLocalizer();
	
	public XMLBasedTube(final Element root, final URI currentURI) throws IOException {
		if (root == null) {
			throw new NullPointerException("Root element can't be null");
		}
		else if (currentURI == null) {
			throw new NullPointerException("Current URI can't be null");
		}
		else {
			try {
				final NodeList parms = ((Element)root.getElementsByTagName(TAG_PARMS).item(0)).getElementsByTagName(TAG_PARM);
				final NodeList scheme = root.getElementsByTagName(TAG_SCHEME);
				final NodeList connectors = root.getElementsByTagName(TAG_CONNECTOR);
				final NodeList graphics = root.getElementsByTagName(TAG_GRAPHIC);
				final NodeList localizedSection = root.getElementsByTagName(TAG_LOCALIZED_KEYS);

				if (localizedSection.getLength() > 0) {
					final NodeList 	localizedKeys = ((Element)localizedSection.item(0)).getElementsByTagName(TAG_LANG);
					
					for(int langIndex = 0; langIndex < localizedKeys.getLength(); langIndex++) {
						final Locale	locale = Locale.forLanguageTag(((Element)localizedKeys.item(langIndex)).getAttribute(ATTR_NAME));
						final NodeList	keys = ((Element)localizedKeys.item(langIndex)).getElementsByTagName(TAG_KEY);
						final NodeList	helps = ((Element)localizedKeys.item(langIndex)).getElementsByTagName(TAG_HELP);
	
						for(int index = 0; index < keys.getLength(); index++) {
							localizer.addKey(((Element)keys.item(index)).getAttribute(ATTR_NAME), 
										  locale, 
										  ((Element)keys.item(index)).getTextContent());
							
						}
						for(int index = 0; index < helps.getLength(); index++) {
							localizer.addHelp(((Element)helps.item(index)).getAttribute(ATTR_NAME), 
										  locale, 
										  ((Element)helps.item(index)).getTextContent());
							
						}
					}
				}
				
				this.currentURI = currentURI; 
				this.type = TubesType.valueOf(root.getAttribute(ATTR_TYPE));
				this.abbr = root.getAttribute(ATTR_ABBR);
				this.corpus = TubeCorpusType.valueOf(root.getAttribute(ATTR_CORPUS));
				this.panel = TubePanelType.valueOf(root.getAttribute(ATTR_PANEL));
				
				final ParmSubstitutor	psScheme = new ParmSubstitutor(((Element)scheme.item(0)).getElementsByTagName(TAG_PARM));
						
				if (((Element)scheme.item(0)).hasAttribute(ATTR_HREF)) {
					this.scheme = loadSVG(URI.create(((Element)scheme.item(0)).getAttribute(ATTR_HREF)), psScheme);
				}
				else {
					this.scheme = loadSVG(type.getSvgURL().toURI(), psScheme);
				}

				final NodeList 			corpusRef = root.getElementsByTagName(TAG_CORPUS);
				final ParmSubstitutor	psCorpus = new ParmSubstitutor(((Element)corpusRef.item(0)).getElementsByTagName(TAG_PARM));
				
				if (((Element)scheme.item(0)).hasAttribute(ATTR_HREF)) {
					this.corpusDraw = loadSVG(URI.create(((Element)scheme.item(0)).getAttribute(ATTR_HREF)), psCorpus);
				}
				else {
					this.corpusDraw = loadSVG(corpus.getGroup().getSvgURL().toURI(), psCorpus);
				}
//				final String	strDesc = Utils.fromResource(getLocalizer().getContent(root.getAttribute(ATTR_DESCRIPTION), MimeType.MIME_CREOLE_TEXT, MimeType.MIME_HTML_TEXT)); 
//				final String	strUsage = root.hasAttribute(ATTR_USAGE) ? Utils.fromResource(getLocalizer().getContent(root.getAttribute(ATTR_USAGE), MimeType.MIME_CREOLE_TEXT, MimeType.MIME_HTML_TEXT)) : ""; 
//				
				this.parms = new TubeParmDescriptor[parms.getLength()];
				this.description = root.getAttribute(ATTR_DESCRIPTION); //strDesc.substring(strDesc.indexOf("<html>"));
				this.usage = root.hasAttribute(ATTR_USAGE) ? root.getAttribute(ATTR_USAGE) : "";  

				for(int index = 0; index < this.parms.length; index++) {
					final int			number = ((Element)parms.item(index)).hasAttribute(ATTR_NUMBER) ? Integer.valueOf(((Element)parms.item(index)).getAttribute(ATTR_NUMBER)) : 0;
					final TubeParameter	key = TubeParameter.valueOf(((Element)parms.item(index)).getAttribute(ATTR_NAME));
					final float			value = Float.valueOf(parms.item(index).getTextContent().trim());
					
					this.parms[index] = new TubeParmDescriptor(number, key, value);
				}
				
				this.graphics = new GraphicImpl[graphics.getLength()];
				
				for(int index = 0; index < this.graphics.length; index++) {
					final int		number = ((Element)graphics.item(index)).hasAttribute(ATTR_NUMBER) ? Integer.valueOf(((Element)graphics.item(index)).getAttribute(ATTR_NUMBER)) : 0;
					final String	name = ((Element)graphics.item(index)).getAttribute(ATTR_NAME);
					final String	tooltip = ((Element)graphics.item(index)).getAttribute(ATTR_TOOLTIP);
					final Icon		picture;
					
					if (((Element)graphics.item(index)).hasAttribute(ATTR_HREF)) {
						final URI	ref = URI.create(((Element)graphics.item(index)).getAttribute(ATTR_HREF)); 	
						
						if (ref.isAbsolute()) {
							picture = new ImageIcon(ref.toURL());
						}
						else {
							picture = new ImageIcon(currentURI.resolve(ref).toURL());
						}
					}
					else {
						picture = new ImageIcon(URIUtils.convert2selfURI(graphics.item(index).getTextContent().trim().getBytes()).toURL());
					}
					this.graphics[index] = new GraphicImpl(number, name, tooltip, picture);
				}

				this.connectors = new TubeConnectorImpl[connectors.getLength()];
				
				for(int index = 0; index < this.connectors.length; index++) {
					final int				number = ((Element)connectors.item(index)).hasAttribute(ATTR_NUMBER) ? Integer.valueOf(((Element)connectors.item(index)).getAttribute(ATTR_NUMBER)) : 0;
					final int				pin = Integer.valueOf(((Element)connectors.item(index)).getAttribute(ATTR_PIN));
					final TubeConnectorType	type = TubeConnectorType.valueOf(((Element)connectors.item(index)).getAttribute(ATTR_TYPE));
					final PinType			pinType = ((Element)connectors.item(index)).hasAttribute(ATTR_PINTYPE) ? PinType.valueOf(((Element)connectors.item(index)).getAttribute(ATTR_PINTYPE)) : PinType.ORDINAL;
					
					this.connectors[index] = new TubeConnectorImpl(number, type, pin, pinType);
				}

			} catch (DOMException | URISyntaxException exc) {
				throw new IOException(exc.getLocalizedMessage(), exc);
			}
		}
	}
	
	@Override
	public Localizer getLocalizer() {
		return localizer;
	}
	
	@Override
	public TubesType getType() {
		return type;
	}

	@Override
	public String getAbbr() {
		return abbr;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getUsage() {
		return usage;
	}
	
	@Override
	public SVGPainter getScheme() {
		return scheme;
	}

	@Override
	public SVGPainter getCorpusDraw() {
		return corpusDraw;
	}
	
	@Override
	public TubeConnector[] getConnectors() {
		return connectors;
	}
	
	@Override
	public TubeCorpusType getCorpus() {
		return corpus;
	}

	@Override
	public TubePanelType getPanelType() {
		return panel;
	}
	
	@Override
	public TubeParameter[] getParameters() {
		return getParameters(0);
	}

	@Override
	public float[] getValues() {
		return getValues(0);
	}

	@Override
	public TubeParameter[] getParameters(final int lampNo) {
		int	count = 0;
		
		for(TubeParmDescriptor item : parms) {
			if (item.lampNo == lampNo) {
				count++;
			}
		}
		final TubeParameter[]	result = new TubeParameter[count];
		
		count = 0;
		for(TubeParmDescriptor item : parms) {
			if (item.lampNo == lampNo) {
				result[count++] = item.parm;
			}
		}
		return result;
	}

	@Override
	public float[] getValues(final int lampNo) {
		int	count = 0;
		
		for(TubeParmDescriptor item : parms) {
			if (item.lampNo == lampNo) {
				count++;
			}
		}
		final float[]	result = new float[count];
		
		count = 0;
		for(TubeParmDescriptor item : parms) {
			if (item.lampNo == lampNo) {
				result[count++] = item.value;
			}
		}
		return result;
	}

	@Override
	public Graphic[] getGraphics() {
		return getGraphics(0);
	}

	@Override
	public Graphic[] getGraphics(final int lampNo) {
		int	count = 0;
		
		for(GraphicImpl item : graphics) {
			if (item.lampNo == lampNo) {
				count++;
			}
		}
		final Graphic[]	result = new Graphic[count];
		
		count = 0;
		for(GraphicImpl item : graphics) {
			if (item.lampNo == lampNo) {
				result[count++] = item;
			}
		}
	
		return result;
	}

	private SVGPainter loadSVG(final URI href, final SubstitutionSource ss) {
		try {
			return SVGParser.parse(href.toURL().openStream(), ss);
		} catch (ContentException | IOException e) {
			return null;
		}
	}

	private static class ParmSubstitutor implements SubstitutionSource {
		private final Properties	parms = new Properties();

		private ParmSubstitutor(final NodeList list) {
			for(int index = 0; index < list.getLength(); index++) {
				final Element	el = (Element) list.item(index);
				
				parms.setProperty(el.getAttribute(ATTR_NAME), el.getTextContent());
			}
		}
		
		@Override
		public String getValue(final String key) {
			return parms.getProperty(key, "???");
		}

		@Override
		public String toString() {
			return "ParmSubstitutor [parms=" + parms + "]";
		}
	}
	
	private static class TubeParmDescriptor {
		final int			lampNo;
		final TubeParameter	parm;
		final float			value;

		private TubeParmDescriptor(final int lampNo, final TubeParameter parm, final float value) {
			this.lampNo = lampNo;
			this.parm = parm;
			this.value = value;
		}

		@Override
		public String toString() {
			return "TubeParmDescriptor [lampNo=" + lampNo + ", parm=" + parm + ", value=" + value + "]";
		}
	}

	private static class TubeConnectorImpl implements TubeConnector {
		private final int				lampNo;
		private final TubeConnectorType type;
		private final int				pin;
		private final PinType			pinType;
		
		private TubeConnectorImpl(int lampNo, TubeConnectorType type, int pin, PinType pinType) {
			this.lampNo = lampNo;
			this.type = type;
			this.pin = pin;
			this.pinType = pinType;
		}

		@Override
		public int getLampNo() {
			return lampNo;
		}

		@Override
		public int getPin() {
			return pin;
		}

		@Override
		public PinType getPinType() {
			return pinType;
		}

		@Override
		public TubeConnectorType getType() {
			return type;
		}

		@Override
		public String toString() {
			return "TubeConnectorImpl [lampNo=" + lampNo + ", type=" + type + ", pin=" + pin + ", pinType=" + pinType + "]";
		}
	}
	
	private static class GraphicImpl implements Graphic {
		private final int		lampNo;
		private final String	title;
		private final String	tooltip;
		private final Icon		picture;

		private GraphicImpl(final int lampNo, final String title, final String tooltip, final Icon picture) {
			this.lampNo = lampNo;
			this.title = title;
			this.tooltip = tooltip;
			this.picture = picture;
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public String getTooltip() {
			return tooltip;
		}

		@Override
		public Icon getPicture() {
			return picture;
		}

		@Override
		public String toString() {
			return "GraphicImpl [title=" + title + ", tooltip=" + tooltip + "]";
		}
	}
}

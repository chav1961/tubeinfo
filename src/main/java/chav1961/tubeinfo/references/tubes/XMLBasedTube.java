package chav1961.tubeinfo.references.tubes;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import chav1961.purelib.basic.CharUtils.SubstitutionSource;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.i18n.KeyValueLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;
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
 *   	<parm name="TubeParameter" number="0|1|2" ref="ref(mode)">value</parm>
 * 	 </parms>
 *	 <modes>
 *		<mode ref="1">
 *			<parm name="TubeParameter">6.3</parm>
 *		</mode>
 * 	 </modes> 
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
	private static final String			TAG_MODES = "modes";
	private static final String			TAG_MODE = "mode";
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
	private static final String			ATTR_REF = "ref";
	private static final String			ATTR_PIN = "pin";
	private static final String			ATTR_PINTYPE = "pinType";
	private static final String			ATTR_TOOLTIP = "tooltip";
	private static final String			ATTR_HREF = "href";

	private final TubesType				type;
	private final String				abbr;
	private final String				description;
	private final String				usage;
	private final TubeCorpusType		corpus;
	private final TubePanelType			panel;
	private final SVGPainter			scheme;
	private final SVGPainter			corpusDraw;
	private final TubeParmDescriptor[]	parms;
	private final TubeParmDescriptor[][]modes;
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
				final NodeList modes = root.getElementsByTagName(TAG_MODES);
				final NodeList scheme = root.getElementsByTagName(TAG_SCHEME);
				final NodeList connectors = root.getElementsByTagName(TAG_CONNECTOR);
				final NodeList graphics = root.getElementsByTagName(TAG_GRAPHIC);
				final NodeList localizedSection = root.getElementsByTagName(TAG_LOCALIZED_KEYS);

				if (localizedSection.getLength() > 0) {
					final NodeList 	localizedKeys = ((Element)localizedSection.item(0)).getElementsByTagName(TAG_LANG);
					
					for(int langIndex = 0; langIndex < localizedKeys.getLength(); langIndex++) {
						final Element	current = (Element)localizedKeys.item(langIndex);
						final Locale	locale = Locale.forLanguageTag(current.getAttribute(ATTR_NAME));
						final NodeList	keys = current.getElementsByTagName(TAG_KEY);
						final NodeList	helps = current.getElementsByTagName(TAG_HELP);
	
						for(int index = 0; index < keys.getLength(); index++) {
							final Element	pair = (Element)keys.item(index);
							
							localizer.addKey(pair.getAttribute(ATTR_NAME), locale, pair.getTextContent());
						}
						for(int index = 0; index < helps.getLength(); index++) {
							final Element	pair = (Element)helps.item(index);
							
							localizer.addHelp(pair.getAttribute(ATTR_NAME), locale, pair.getTextContent());
						}
					}
				}
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
				
				if (((Element)corpusRef.item(0)).hasAttribute(ATTR_HREF)) {
					this.corpusDraw = loadSVG(URI.create(((Element)corpusRef.item(0)).getAttribute(ATTR_HREF)), psCorpus);
				}
				else {
					this.corpusDraw = loadSVG(corpus.getGroup().getSvgURL().toURI(), psCorpus);
				}
				this.parms = new TubeParmDescriptor[parms.getLength()];
				this.description = root.getAttribute(ATTR_DESCRIPTION);
				this.usage = root.hasAttribute(ATTR_USAGE) ? root.getAttribute(ATTR_USAGE) : "";  

				for(int index = 0; index < this.parms.length; index++) {
					final Element		current = (Element)parms.item(index);
					final int			number = current.hasAttribute(ATTR_NUMBER) ? Integer.valueOf(current.getAttribute(ATTR_NUMBER)) : 0;
					final String		ref = current.hasAttribute(ATTR_REF) ? current.getAttribute(ATTR_REF) : null;
					final TubeParameter	key = TubeParameter.valueOf(current.getAttribute(ATTR_NAME));
					final float			value = Float.valueOf(current.getTextContent().trim());
					
					this.parms[index] = new TubeParmDescriptor(number, key, value, ref);
				}
				if (modes != null && modes.getLength() > 0) {
					final NodeList	modeList = ((Element)modes.item(0)).getElementsByTagName(TAG_MODE);
	
					this.modes = new TubeParmDescriptor[modeList.getLength()][];
					for(int index = 0; index < this.modes.length; index++) {
						final Element		current = (Element)modeList.item(index);
						final String		ref = current.getAttribute(ATTR_REF);
						final NodeList		list = current.getElementsByTagName(TAG_PARM);
						final TubeParmDescriptor[]	items = new TubeParmDescriptor[list.getLength()]; 
								
						for(int pIndex = 0; pIndex < items.length; pIndex++) {
							final Element		pCurrent = (Element)list.item(pIndex);
							final int			number = pCurrent.hasAttribute(ATTR_NUMBER) ? Integer.valueOf(pCurrent.getAttribute(ATTR_NUMBER)) : 0;
							final TubeParameter	key = TubeParameter.valueOf(pCurrent.getAttribute(ATTR_NAME));
							final float			value = Float.valueOf(pCurrent.getTextContent().trim());
							
							items[pIndex] = new TubeParmDescriptor(number, key, value, ref);
						}
						this.modes[index] = items; 
					}
				}
				else {
					this.modes = new TubeParmDescriptor[0][];
				}
				
				this.graphics = new GraphicImpl[graphics.getLength()];
				
				for(int index = 0; index < this.graphics.length; index++) {
					final Element	current = (Element)graphics.item(index);
					final int		number = current.hasAttribute(ATTR_NUMBER) ? Integer.valueOf(current.getAttribute(ATTR_NUMBER)) : 0;
					final String	name = current.getAttribute(ATTR_NAME);
					final String	tooltip = current.getAttribute(ATTR_TOOLTIP);
					final Icon		picture;
					
					if (current.hasAttribute(ATTR_HREF)) {
						final URI	ref = URI.create(current.getAttribute(ATTR_HREF)); 	
						
						if (ref.isAbsolute()) {
							picture = new ImageIcon(ref.toURL());
						}
						else {
							picture = new ImageIcon(URIUtils.appendRelativePath2URI(currentURI, ref.toString()).toURL());
						}
					}
					else {
						picture = new ImageIcon(URIUtils.convert2selfURI(current.getTextContent().trim().getBytes()).toURL());
					}
					this.graphics[index] = new GraphicImpl(number, name, tooltip, picture);
				}
				this.connectors = new TubeConnectorImpl[connectors.getLength()];
				
				for(int index = 0; index < this.connectors.length; index++) {
					final Element			current = (Element)connectors.item(index);
					final int				number = current.hasAttribute(ATTR_NUMBER) ? Integer.valueOf(current.getAttribute(ATTR_NUMBER)) : 0;
					final int				pin = Integer.valueOf(current.getAttribute(ATTR_PIN));
					final TubeConnectorType	type = TubeConnectorType.valueOf(current.getAttribute(ATTR_TYPE));
					final PinType			pinType = current.hasAttribute(ATTR_PINTYPE) ? PinType.valueOf(current.getAttribute(ATTR_PINTYPE)) : PinType.ORDINAL;
					
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
	public int numberOfModes() {
		return modes.length;
	}


	@Override
	public TubeParameter[] getMode(final int modeNo) {
		if (modeNo < 0 || modeNo >= modes.length) {
			throw new IllegalArgumentException("Mode number ["+modeNo+"] out of range 0.."+(modes.length-1));
		}
		else {
			final TubeParameter[]	result = new TubeParameter[modes[modeNo].length];
			
			for(int index = 0; index < result.length; index++) {
				result[index] = modes[modeNo][index].parm;
			}
			return result;
		}
	}

	@Override
	public float[] getModeValue(final int modeNo) {
		if (modeNo < 0 || modeNo >= modes.length) {
			throw new IllegalArgumentException("Mode number ["+modeNo+"] out of range 0.."+(modes.length-1));
		}
		else {
			final float[]	result = new float[modes[modeNo].length];
			
			for(int index = 0; index < result.length; index++) {
				result[index] = modes[modeNo][index].value;
			}
			return result;
		}
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
	public String[] getModes(final int lampNo) {
		int	count = 0;
		
		for(TubeParmDescriptor item : parms) {
			if (item.lampNo == lampNo) {
				count++;
			}
		}
		final String[]	result = new String[count];
		
		count = 0;
		for(TubeParmDescriptor item : parms) {
			if (item.lampNo == lampNo) {
				result[count++] = item.ref;
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
		final String		ref;

		private TubeParmDescriptor(final int lampNo, final TubeParameter parm, final float value, final String ref) {
			this.lampNo = lampNo;
			this.parm = parm;
			this.value = value;
			this.ref = ref;
		}

		@Override
		public String toString() {
			return "TubeParmDescriptor [lampNo=" + lampNo + ", parm=" + parm + ", value=" + value + ", ref=" + ref + "]";
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

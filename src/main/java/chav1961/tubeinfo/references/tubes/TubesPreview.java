package chav1961.tubeinfo.references.tubes;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.HyperlinkEvent;
import javax.swing.table.DefaultTableModel;

import chav1961.purelib.basic.MimeType;
import chav1961.purelib.basic.NamedValue;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.svg.SVGPainter;
import chav1961.tubeinfo.references.interfaces.Graphic;
import chav1961.tubeinfo.references.interfaces.TubeDescriptor;
import chav1961.tubeinfo.references.interfaces.TubeParameter;
import chav1961.tubeinfo.references.interfaces.TubeParameterUnit;
import chav1961.tubeinfo.utils.InternalUtils;

class TubesPreview extends JPanel implements LocaleChangeListener {
	private static final long 	serialVersionUID = -2153048973150602961L;
	private static final int	PARM_COUNT = 2;
	
	private static final String	COL_NAME = "chav1961.tubesReference.preview.table.name"; 
	private static final String	COL_ABBR = "chav1961.tubesReference.preview.table.abbr"; 
	private static final String	COL_VALUE = "chav1961.tubesReference.preview.table.value"; 
	private static final String	COL_MODE = "chav1961.tubesReference.preview.table.mode"; 
	private static final String	TAB_PARAMETERS = "chav1961.tubesReference.preview.tab.parameters"; 
	private static final String	TAB_MAXIMUM = "chav1961.tubesReference.preview.tab.maximum"; 
	private static final String	TAB_GRAPHICS = "chav1961.tubesReference.preview.tab.graphics"; 
	private static final String	TAB_USAGE = "chav1961.tubesReference.preview.tab.usage"; 

	private final Localizer		localizer;
	private final JTabbedPane	tabArea = new JTabbedPane();
	private final List<TabDesc>	tabDesc = new ArrayList<>();
	private final JScrollPane[]	scrollsOrdinal = new JScrollPane[PARM_COUNT];
	private final JScrollPane[]	scrollsMaximum = new JScrollPane[PARM_COUNT];

	private SchemePainter		scheme = new SchemePainter();
	private CorpusPainter		corpus = new CorpusPainter();
	private JLabel				abbr = new JLabel("", JLabel.CENTER);
	private JEditorPane			description = new JEditorPane("text/html","");
	private JEditorPane			usage = new JEditorPane("text/html","");
	private final JScrollPane	usagePane = new JScrollPane(usage);
	private ParametersX[]		parmsOrdinal = new ParametersX[PARM_COUNT];
	private Parameters[]		parmsMaximum = new Parameters[PARM_COUNT];
	private JTable[]			tablesOrdinal = new JTable[PARM_COUNT];
	private JTable[]			tablesMaximum = new JTable[PARM_COUNT];
	private JLabel[][]			graphics = new JLabel[PARM_COUNT][];
	private TubeDescriptor		desc = null;
	
	TubesPreview(final Localizer localizer) {
		super(new BorderLayout(5, 5));
		this.localizer = localizer;

		final JPanel		pictures = new JPanel(new GridLayout(2, 1, 5, 5));
//		final BoxLayout		picturesLayout = new BoxLayout(pictures, BoxLayout.Y_AXIS);
		final JPanel		center = new JPanel(new BorderLayout(5, 5));
		
//		pictures.setLayout(picturesLayout);

		for(int index = 0; index < PARM_COUNT; index++) {
			parmsOrdinal[index] = new ParametersX();
			tablesOrdinal[index] = new JTable(parmsOrdinal[index]);
			tablesOrdinal[index].getColumnModel().getColumn(1).setMinWidth(80);
			tablesOrdinal[index].getColumnModel().getColumn(1).setMaxWidth(80);
			tablesOrdinal[index].getColumnModel().getColumn(2).setMaxWidth(200);
			tablesOrdinal[index].setRowHeight(20);
			scrollsOrdinal[index] = new JScrollPane(tablesOrdinal[index]);
			parmsMaximum[index] = new Parameters();
			tablesMaximum[index] = new JTable(parmsMaximum[index]);
			tablesMaximum[index].getColumnModel().getColumn(1).setMinWidth(80);
			tablesMaximum[index].getColumnModel().getColumn(1).setMaxWidth(80);
			tablesMaximum[index].getColumnModel().getColumn(2).setMaxWidth(200);
			tablesMaximum[index].setRowHeight(20);
			scrollsMaximum[index] = new JScrollPane(tablesMaximum[index]);
		}
		add(abbr, BorderLayout.NORTH);
		pictures.add(scheme);
		pictures.add(corpus);
		add(pictures, BorderLayout.WEST);
		prepareEditorPane(description);
		prepareEditorPane(usage);
		center.add(description, BorderLayout.NORTH);
		center.add(tabArea, BorderLayout.CENTER);
		add(center, BorderLayout.CENTER);
		fillLocalizedStrings();
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		for(int index = 0; index < parmsOrdinal.length; index++) {
			parmsOrdinal[index].fireTableStructureChanged();
		}
		for(int index = 0; index < parmsMaximum.length; index++) {
			parmsMaximum[index].fireTableStructureChanged();
		}
		fillLocalizedStrings();
	}

	public void refreshDesc(final TubeDescriptor desc) {
		if (this.desc != null) {
			localizer.remove(this.desc.getLocalizer());
		}
		this.desc = desc;
		if (desc != null) {
			localizer.add(this.desc.getLocalizer());
			fillContent();
		}
	}
	
	private void fillContent() {
		int	tabNo = 0;
		
		tabArea.removeAll();
		tabDesc.clear();
		for(int index = 0; index < PARM_COUNT; index++) {
			if (index < desc.getType().getNumberOfLampTypes()) {
				final JPanel		panel = new JPanel(new BorderLayout(0, 10));
				final JEditorPane	modes = new JEditorPane("text/html", buildModeDescriptor(desc));
				
				modes.setOpaque(false);
				panel.add(scrollsOrdinal[index], BorderLayout.CENTER);
				panel.add(modes, BorderLayout.SOUTH);
				tabArea.addTab("", panel);
				tabDesc.add(new TabDesc(tabNo++, TabType.PARAMETERS, index+1));
				parmsOrdinal[index].setContent(buildListX(desc.getParameters(index+1), desc.getValues(index+1), desc.getModes(index+1)));

				tabArea.addTab("", scrollsMaximum[index]);
				tabDesc.add(new TabDesc(tabNo++, TabType.ABSOLUTE_MAXIMUM, index+1));
				parmsMaximum[index].setContent(buildList(desc.getParameters(index+1), desc.getValues(index+1), true));
				final Graphic[] g = desc.getGraphics(index+1);
				
				if (g != null && g.length > 0) {						
					final JPanel gallery = new JPanel(new GridLayout(1, g.length, 10, 10));
					
					this.graphics[index] = new JLabel[g.length]; 
					for(int gIndex = 0; gIndex < g.length; gIndex++) {
						final JLabel	label = new JLabel(g[gIndex].getPicture());
	
						label.setVerticalTextPosition(JLabel.BOTTOM);
						label.setHorizontalTextPosition(JLabel.CENTER);
						this.graphics[index][gIndex] = label;
						gallery.add(label);
					}
					tabArea.addTab("", new JScrollPane(gallery));
					tabDesc.add(new TabDesc(tabNo++, TabType.GRAPHICS, index+1));
				}
			}
		}
		if (!desc.getUsage().isEmpty()) {
			tabArea.addTab("", usagePane);
			tabDesc.add(new TabDesc(tabNo++, TabType.USAGE, 0));
		}
		final int	preferredX = Math.min(desc.getScheme().getWidth(), desc.getCorpusDraw().getWidth());
				
		scheme.setPreferredSize(new Dimension(preferredX, 1));
		scheme.setPainter(desc.getScheme());
		corpus.setPreferredSize(new Dimension(preferredX, 1));
		corpus.setPainter(desc.getCorpusDraw());
		try {
			final URL	ref = URI.create(InternalUtils.getLocaleResource(desc.getPanelType().getGroup()).icon()).toURL();
			
			abbr.setIcon(new ImageIcon(ref));
		} catch (SecurityException | MalformedURLException e) {
			abbr.setIcon(null);
		}
		fillLocalizedStrings();
	}
	
	private String buildModeDescriptor(final TubeDescriptor desc) {
		final StringBuilder	sb = new StringBuilder("<html><body><p>");
		
		for(int index = 0; index < desc.numberOfModes(); index++) {
			final TubeParameter[]	p = desc.getMode(index);
			final float[]			v = desc.getModeValue(index);
			
			for(int pIndex = 0; pIndex < p.length; pIndex++) {
				sb.append(index+1).append("). <b>").append(p[pIndex].getAbbr()).append("</b> = ").append(v[pIndex]).append(' ');
				try {
					sb.append(localizer.getValue(TubeParameterUnit.class.getField(p[pIndex].getUnit().name()).getAnnotation(LocaleResource.class).value()));
				} catch (LocalizationException | NoSuchFieldException e) {
					e.printStackTrace();
				}
				sb.append("&nbsp;&nbsp;&nbsp;");
			}
		}
		return sb.append("</p></body></html>").toString();
	}

	private Collection<NamedValue<Float>> buildList(final TubeParameter[] parameters, final float[] values, final boolean isMaximum) {
		final List<NamedValue<Float>>	result = new ArrayList<>();

		for(int index = 0; index < parameters.length; index++) {
			if (parameters[index].isMaxAvailable() == isMaximum) {
				result.add(new NamedValue<Float>(parameters[index].name(), values[index]));
			}
		}
		return result;
	}

	private Collection<NamedValueX<Float>> buildListX(final TubeParameter[] parameters, final float[] values, final String[] modes) {
		final List<NamedValueX<Float>>	result = new ArrayList<>();

		for(int index = 0; index < parameters.length; index++) {
			if (!parameters[index].isMaxAvailable()) {
				result.add(new NamedValueX<Float>(parameters[index].name(), values[index], modes[index]));
			}
		}
		return result;
	}
	
	private void fillLocalizedStrings() {
		if (desc != null) {
			try {
				final String		strDesc = Utils.fromResource(localizer.getContent(desc.getDescription(), MimeType.MIME_CREOLE_TEXT, MimeType.MIME_HTML_TEXT));
				
				description.setText(strDesc.substring(strDesc.indexOf("<html>")));
				if (!desc.getUsage().isEmpty()) {
					final String	strUsage = Utils.fromResource(localizer.getContent(desc.getUsage(), MimeType.MIME_CREOLE_TEXT, MimeType.MIME_HTML_TEXT)); 

					usage.setText(strUsage.substring(strUsage.indexOf("<html>")));
				}
				else {
					usage.setText("");
				}
			} catch (LocalizationException | IOException e) {
				e.printStackTrace();
			} 
			final LocaleResource	anno = InternalUtils.getLocaleResource(desc.getType());
			
			abbr.setText("<html><body><h1>" + desc.getAbbr()+" - "+localizer.getValue(anno.value()) +"</h1></body></html>");
			for (TabDesc item : tabDesc) {
				tabArea.setTitleAt(item.tabIndex, localizer.getValue(item.type.getTitle(), item.lampNo));
			}
			for(int index = 0; index < graphics.length; index++) {
				if (index < desc.getType().getNumberOfLampTypes()) {
					final Graphic[]	g = desc.getGraphics(index+1);
					
					if (graphics[index] != null) {
						for(int gIndex = 0; gIndex < graphics[index].length; gIndex++) {
							graphics[index][gIndex].setText(localizer.getValue(g[gIndex].getTitle()));
							graphics[index][gIndex].setToolTipText(localizer.getValue(g[gIndex].getTooltip()));
						}
					}
				}
			}
		}
	}

	private void prepareEditorPane(final JEditorPane pane) {
		pane.setEditable(false);
		pane.setOpaque(false);
		pane.addHyperlinkListener((e)->{
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	            if (Desktop.isDesktopSupported()) {
	                try {
	                    Desktop.getDesktop().browse(e.getURL().toURI());
	                } catch (IOException | URISyntaxException exc) {
	                	SwingUtils.getNearestLogger(this).message(Severity.warning, exc.getLocalizedMessage());
	                }
	            }	
			}
		});
	}

	private static void resizeContent(final Graphics g, final int width, final int height, final int width2, final int height2) {
		final Graphics2D		g2d = (Graphics2D)g;
		final AffineTransform	at = new AffineTransform(g2d.getTransform());
		final double			kW = 1.0 * width / width2;
		final double			kH = 1.0 * height / height2;
		
		if (kW < kH) {
			at.scale(kW, kW);
		}
		else {
			at.scale(kH, kH);
		}
		g2d.setTransform(at);
	}
	
	private class Parameters extends DefaultTableModel {
		private static final long serialVersionUID = 5633892490065085578L;
		
		private final CopyOnWriteArrayList<NamedValue<Float>>	content = new CopyOnWriteArrayList<>();

		@Override
		public int getRowCount() {
			return content == null ? 0 : content.size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(final int columnIndex) {
			switch (columnIndex) {
				case 0 :
					return localizer.getValue(COL_NAME);
				case 1 :
					return localizer.getValue(COL_ABBR);
				case 2 :
					return localizer.getValue(COL_VALUE);
				default :
					return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
				case 0 :
					return String.class;
				case 1 :
					return String.class;
				case 2 :
					return Float.class;
				default :
					return null;
			}
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			final String name = content.get(rowIndex).getName();

			switch (columnIndex) {
				case 0 :
					final TubeParameterUnit	unit = TubeParameter.valueOf(name).getUnit();
					
					try {
						return localizer.getValue(TubeParameter.class.getField(name).getAnnotation(LocaleResource.class).value())
								+", "+
								localizer.getValue(TubeParameterUnit.class.getField(unit.name()).getAnnotation(LocaleResource.class).value());
					} catch (LocalizationException | NoSuchFieldException | SecurityException e) {
						return name;
					}
				case 1 :
					return "<html><body><p><b>" + TubeParameter.valueOf(name).getAbbr() + "</b></p></body></html>";
				case 2 :
					return content.get(rowIndex).getValue();
				default :
					return null;
			}
		}

		public void setContent(final Collection<NamedValue<Float>> values) {
			if (values == null) {
				throw new NullPointerException("Values to add can't be null");
			}
			else {
				content.clear();
				content.addAll(values);
				fireTableDataChanged();
			}
		}
			
	}

	private class ParametersX extends DefaultTableModel {
		private static final long serialVersionUID = 5633892490065085578L;
		
		private final CopyOnWriteArrayList<NamedValueX<Float>>	content = new CopyOnWriteArrayList<>();

		@Override
		public int getRowCount() {
			return content == null ? 0 : content.size();
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public String getColumnName(final int columnIndex) {
			switch (columnIndex) {
				case 0 :
					return localizer.getValue(COL_NAME);
				case 1 :
					return localizer.getValue(COL_ABBR);
				case 2 :
					return localizer.getValue(COL_VALUE);
				case 3 :
					return localizer.getValue(COL_MODE);
				default :
					return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
				case 0 :
					return String.class;
				case 1 :
					return String.class;
				case 2 :
					return Float.class;
				case 3 :
					return String.class;
				default :
					return null;
			}
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			final String name = content.get(rowIndex).getName();

			switch (columnIndex) {
				case 0 :
					final TubeParameterUnit	unit = TubeParameter.valueOf(name).getUnit();
					
					try {
						return localizer.getValue(TubeParameter.class.getField(name).getAnnotation(LocaleResource.class).value())
								+", "+
								localizer.getValue(TubeParameterUnit.class.getField(unit.name()).getAnnotation(LocaleResource.class).value());
					} catch (LocalizationException | NoSuchFieldException | SecurityException e) {
						return name;
					}
				case 1 :
					return "<html><body><p><b>" + TubeParameter.valueOf(name).getAbbr() + "</b></p></body></html>";
				case 2 :
					return content.get(rowIndex).getValue();
				case 3 :
					return content.get(rowIndex).getMode();
				default :
					return null;
			}
		}

		public void setContent(final Collection<NamedValueX<Float>> values) {
			if (values == null) {
				throw new NullPointerException("Values to add can't be null");
			}
			else {
				content.clear();
				content.addAll(values);
				fireTableDataChanged();
			}
		}
			
	}
	
	private static class NamedValueX<T> extends NamedValue<T> {
		private final String	mode;
		
		private NamedValueX(final String name, final T value, final String mode) {
			super(name, value);
			this.mode = mode;
		}
		
		public String getMode() {
			return mode;
		}
	}
	
	private static class SchemePainter extends JComponent {
		private static final long serialVersionUID = -6183021481289137700L;

		private SVGPainter	painter = null;
		
		private void setPainter(final SVGPainter painter) {
			this.painter = painter;
			repaint();
		}
		
		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			if (painter != null) {
				resizeContent(g, getWidth(), getHeight(), painter.getWidth(), painter.getHeight());
				painter.paint((Graphics2D)g);
			}
		}
	}

	private static class CorpusPainter extends JComponent {
		private static final long serialVersionUID = -6183021481289137700L;

		private SVGPainter	painter = null;
		
		private void setPainter(final SVGPainter painter) {
			this.painter = painter;
			repaint();
		}
		
		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			if (painter != null) {
				resizeContent(g, getWidth(), getHeight(), painter.getWidth(), painter.getHeight());
				painter.paint((Graphics2D)g);
			}
		}
	}

	private static enum TabType {
		PARAMETERS(TAB_PARAMETERS),
		ABSOLUTE_MAXIMUM(TAB_MAXIMUM),
		GRAPHICS(TAB_GRAPHICS),
		USAGE(TAB_USAGE);
		
		private final String	title;
		
		private TabType(final String title) {
			this.title = title;
		}
		
		public String getTitle() {
			return title;
		}
	}
	
	private static class TabDesc {
		private final int		tabIndex;
		private final TabType	type;
		private final int		lampNo;
		
		private TabDesc(int tabIndex, TabType type, int lampNo) {
			this.tabIndex = tabIndex;
			this.type = type;
			this.lampNo = lampNo;
		}

		@Override
		public String toString() {
			return "TabDesc [tabIndex=" + tabIndex + ", type=" + type + ", lampNo=" + lampNo + "]";
		}
	}

}

package chav1961.tubeinfo.references.tubes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import chav1961.purelib.basic.NamedValue;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.useful.svg.SVGPainter;
import chav1961.tubeinfo.references.interfaces.Graphic;
import chav1961.tubeinfo.references.interfaces.TubeDescriptor;
import chav1961.tubeinfo.references.interfaces.TubePanelGroup;
import chav1961.tubeinfo.references.interfaces.TubeParameter;
import chav1961.tubeinfo.references.interfaces.TubeParameterUnit;
import chav1961.tubeinfo.references.interfaces.TubesType;
import chav1961.tubeinfo.utils.InternalUtils;

class TubesPreview extends JPanel implements LocaleChangeListener {
	private static final long 	serialVersionUID = -2153048973150602961L;
	private static final int	PARM_COUNT = 2;
	private static final String	COL_NAME = "chav1961.tubesReference.preview.table.name"; 
	private static final String	COL_ABBR = "chav1961.tubesReference.preview.table.abbr"; 
	private static final String	COL_VALUE = "chav1961.tubesReference.preview.table.value"; 

	private final Localizer	localizer;
	
	private SchemePainter		scheme = new SchemePainter();
	private CorpusPainter		corpus = new CorpusPainter();
	private JLabel				abbr = new JLabel("", JLabel.CENTER);
	private JEditorPane			description = new JEditorPane("text/html","");
	private final JTabbedPane	tabArea = new JTabbedPane();
	private final JScrollPane[]	scrollsOrdinal = new JScrollPane[PARM_COUNT];
	private final JScrollPane[]	scrollsMaximum = new JScrollPane[PARM_COUNT];
	private Parameters[]		parmsOrdinal = new Parameters[PARM_COUNT];
	private Parameters[]		parmsMaximum = new Parameters[PARM_COUNT];
	private JTable[]			tablesOrdinal = new JTable[PARM_COUNT];
	private JTable[]			tablesMaximum = new JTable[PARM_COUNT];
	private JLabel[][]			graphics = new JLabel[PARM_COUNT][];
	private TubeDescriptor		desc = null;
	
	TubesPreview(final Localizer localizer) {
		super(new BorderLayout(5, 5));
		this.localizer = localizer;

		final JPanel		pictures = new JPanel(new GridLayout(2, 1, 5, 5));
		final BoxLayout		picturesLayout = new BoxLayout(pictures, BoxLayout.Y_AXIS);
		final JPanel		center = new JPanel(new BorderLayout(5, 5));
		
		pictures.setLayout(picturesLayout);

		for(int index = 0; index < PARM_COUNT; index++) {
			parmsOrdinal[index] = new Parameters();
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
		description.setEditable(false);
		description.setOpaque(false);
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
		tabArea.removeAll();
		for(int index = 0; index < tablesOrdinal.length; index++) {
			if (index < desc.getType().getNumberOfLampTypes()) {
				tabArea.addTab("#"+(index+1), scrollsOrdinal[index]);
				parmsOrdinal[index].setContent(buildList(desc.getParameters(index+1), desc.getValues(index+1), false));
			}
		}
		for(int index = 0; index < tablesMaximum.length; index++) {
			if (index < desc.getType().getNumberOfLampTypes()) {
				tabArea.addTab("#"+(index+1)+"(max)", scrollsMaximum[index]);
				parmsMaximum[index].setContent(buildList(desc.getParameters(index+1), desc.getValues(index+1), true));
			}
		}
		for(int index = 0; index < tablesMaximum.length; index++) {
			if (index < desc.getType().getNumberOfLampTypes()) {
				final Graphic[] g = desc.getGraphics(index+1);
				final JPanel gallery = new JPanel(new GridLayout(1, g.length, 10, 10));
				
				this.graphics[index] = new JLabel[g.length]; 
				for(int gIndex = 0; gIndex < g.length; gIndex++) {
					final JLabel	label = new JLabel(g[gIndex].getPicture());

					label.setVerticalTextPosition(JLabel.BOTTOM);
					label.setHorizontalTextPosition(JLabel.CENTER);
					this.graphics[index][gIndex] = label;
					gallery.add(label);
				}
				tabArea.addTab("#"+(index+1)+"(grf)", gallery);
			}
		}
		scheme.setPainter(desc.getScheme());
		corpus.setPainter(desc.getCorpusDraw());
		try {
			final URL	ref = URI.create(InternalUtils.getLocaleResource(desc.getPanelType().getGroup()).icon()).toURL();
			
			abbr.setIcon(new ImageIcon(ref));
		} catch (SecurityException | MalformedURLException e) {
			abbr.setIcon(null);
		}
		fillLocalizedStrings();
	}

	
	private Collection<NamedValue<Float>> buildList(final TubeParameter[] parameters, final float[] values, final boolean isMaximum) {
		int	count = 0;
		
		for (TubeParameter item : parameters) {
			if (item.isMaxAvailable() == isMaximum) {
				count++;
			}
		}
		final NamedValue<Float>[]	result = new NamedValue[count];

		count = 0;
		for(int index = 0; index < parameters.length; index++) {
			if (parameters[index].isMaxAvailable() == isMaximum) {
				result[count++] = new NamedValue<Float>(parameters[index].name(), values[index]);
			}
		}
		return Arrays.asList(result);
	}

	private void fillLocalizedStrings() {
		if (desc != null) {
			final LocaleResource	anno = InternalUtils.getLocaleResource(desc.getType());
			
			abbr.setText("<html><body><h1>" + desc.getAbbr()+" - "+localizer.getValue(anno.value()) +"</h1></body></html>");
			description.setText(desc.getDescription());
			for(int index = 0; index < graphics.length; index++) {
				if (index < desc.getType().getNumberOfLampTypes()) {
					final Graphic[]	g = desc.getGraphics(index+1);
					
					for(int gIndex = 0; gIndex < graphics[index].length; gIndex++) {
						graphics[index][gIndex].setText(localizer.getValue(g[gIndex].getTitle()));
						graphics[index][gIndex].setToolTipText(localizer.getValue(g[gIndex].getTooltip()));
					}
				}
			}
		}
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
	
	private static class SchemePainter extends JComponent {
		private static final long serialVersionUID = -6183021481289137700L;

		private SVGPainter	painter = null;
		
		private void setPainter(final SVGPainter painter) {
			this.painter = painter;
			if (painter != null) {
				this.setPreferredSize(new Dimension(painter.getWidth(), painter.getHeight()));
			}
			repaint();
		}
		
		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			if (painter != null) {
				painter.paint((Graphics2D)g);
			}
		}
	}

	private static class CorpusPainter extends JComponent {
		private static final long serialVersionUID = -6183021481289137700L;

		private SVGPainter	painter = null;
		
		private void setPainter(final SVGPainter painter) {
			this.painter = painter;
			if (painter != null) {
				this.setPreferredSize(new Dimension(painter.getWidth(), painter.getHeight()));
			}
			repaint();
		}
		
		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			if (painter != null) {
				painter.paint((Graphics2D)g);
			}
		}
	}
}

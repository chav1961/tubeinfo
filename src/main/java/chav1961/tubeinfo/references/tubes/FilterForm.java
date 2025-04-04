package chav1961.tubeinfo.references.tubes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import chav1961.purelib.basic.CharUtils;
import chav1961.purelib.basic.CharUtils.RelevanceFunction;
import chav1961.purelib.basic.MimeType;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;
import chav1961.purelib.ui.swing.useful.JStateString;
import chav1961.tubeinfo.references.interfaces.TubeConnector;
import chav1961.tubeinfo.references.interfaces.TubeConnectorType;
import chav1961.tubeinfo.references.interfaces.TubeDescriptor;
import chav1961.tubeinfo.references.interfaces.TubePanelGroup;
import chav1961.tubeinfo.references.interfaces.TubeParameter;
import chav1961.tubeinfo.references.interfaces.TubesType;
import chav1961.tubeinfo.utils.InternalUtils;

public class FilterForm extends JPanel implements LoggerFacadeOwner {
	private static final long 	serialVersionUID = 2249124198926610489L;
	private static final String	COL_NAME = "chav1961.tubesReference.preview.table.name"; 
	private static final String	COL_VALUE = "chav1961.tubesReference.preview.table.value"; 
	private static final String	LABEL_ABBR_NAME = "chav1961.tubesReference.filterForm.abbr.name"; 
	private static final String	LABEL_DESCR_NAME = "chav1961.tubesReference.filterForm.descr.name"; 
	private static final String	BORDER_TYPES_NAME = "chav1961.tubesReference.filterForm.types.title"; 
	private static final String	BORDER_PANELS_NAME = "chav1961.tubesReference.filterForm.panels.title"; 
	private static final String	PINOUT_TITLE = "chav1961.tubesReference.filterForm.pinout.title";
	private static final Icon	PINOUTS_ICON = new ImageIcon(FilterForm.class.getResource("pinouts.png"));
	private static final String	PINOUTS_TT = "chav1961.tubesReference.filterForm.pinout.tt";
	private static final Icon	PLUS_ICON = new ImageIcon(FilterForm.class.getResource("plus.png"));
	private static final String	PLUS_TT = "chav1961.tubesReference.filterForm.addParam.tt";
	private static final Icon	MINUS_ICON = new ImageIcon(FilterForm.class.getResource("minus.png"));
	private static final String	MINUS_TT = "chav1961.tubesReference.filterForm.removeParam.tt";

	private final Localizer				localizer;
	private final ParmTableModel		model;
	private final JTable				parms;
	private final JLabel				abbrLabel = new JLabel();
	private final JTextField			abbrValue = new JTextField();
	private final JLabel				descLabel = new JLabel();
	private final JTextField			descValue = new JTextField();
	private final JList<TubesType>		types = new JList<>();
	private final JScrollPane			typesPane = new JScrollPane(types); 
	private final JList<TubePanelGroup>	panels = new JList<>();
	private final JScrollPane			panelsPane = new JScrollPane(panels);
	private final JButton				addParam = new JButton(PLUS_ICON);
	private final JButton				removeParam = new JButton(MINUS_ICON);
	private final JButton				pinouts = new JButton(PINOUTS_ICON);
	private final JStateString			logger;
	private final List<TubeConnector>	connectors = new ArrayList<>();
	
	public FilterForm(final Localizer localizer) {
		super(new BorderLayout(5, 5));
		this.localizer = localizer;
		this.logger = new JStateString(localizer);
		
		this.model = new ParmTableModel(localizer);
		this.parms = new JTable(this.model);

		final DefaultListModel<TubesType>		typesModel = new DefaultListModel<TubesType>();
		typesModel.addAll(Arrays.asList(TubesType.values()));
		types.setModel(typesModel);
		types.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		types.setCellRenderer(SwingUtils.getCellRenderer(TubesType.class, new FieldFormat(TubesType.class), ListCellRenderer.class));
		
		final DefaultListModel<TubePanelGroup>	panelsModel = new DefaultListModel<TubePanelGroup>();

		panelsModel.addAll(Arrays.asList(TubePanelGroup.values()));
		panels.setModel(panelsModel);
		panels.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		panels.setCellRenderer(SwingUtils.getCellRenderer(TubePanelGroup.class, new FieldFormat(TubePanelGroup.class), ListCellRenderer.class));
		final JPanel	left = new JPanel(new GridLayout(2, 1, 10, 10));

		left.add(typesPane);
		left.add(panelsPane);

		add(left, BorderLayout.WEST);
		
		final SpringLayout	sl = new SpringLayout();
		final JPanel		center = new JPanel(sl);
		final JPanel		centerPanel = new JPanel(new BorderLayout(5, 5));
		final JToolBar		centerToolbar = new JToolBar(JToolBar.VERTICAL);
		final JScrollPane	pane = new JScrollPane(parms);
		
		centerToolbar.setFloatable(false);
		centerToolbar.add(addParam);
		centerToolbar.add(removeParam);
		
		centerPanel.add(pane, BorderLayout.CENTER);
		centerPanel.add(centerToolbar, BorderLayout.EAST);
		
		center.add(abbrLabel);
		center.add(abbrValue);
		center.add(descLabel);
		center.add(descValue);
		center.add(pinouts);
		center.add(centerPanel);
		sl.putConstraint(SpringLayout.NORTH, pinouts, 15, SpringLayout.NORTH, center);
		sl.putConstraint(SpringLayout.EAST, pinouts, -4, SpringLayout.EAST, center);
		
		sl.putConstraint(SpringLayout.NORTH, abbrValue, 5, SpringLayout.NORTH, center);
		sl.putConstraint(SpringLayout.NORTH, abbrLabel, 5, SpringLayout.NORTH, center);
		sl.putConstraint(SpringLayout.EAST, abbrValue, -10, SpringLayout.WEST, pinouts);
		sl.putConstraint(SpringLayout.WEST, abbrLabel, 0, SpringLayout.WEST, center);

		sl.putConstraint(SpringLayout.NORTH, descValue, 5, SpringLayout.SOUTH, abbrValue);
		sl.putConstraint(SpringLayout.NORTH, descLabel, 5, SpringLayout.SOUTH, abbrValue);
		sl.putConstraint(SpringLayout.EAST, descValue, 0, SpringLayout.EAST, abbrValue);
		sl.putConstraint(SpringLayout.WEST, descLabel, 0, SpringLayout.WEST, center);
		
		sl.putConstraint(SpringLayout.WEST, descValue, 10, SpringLayout.EAST, descLabel);
		sl.putConstraint(SpringLayout.WEST, abbrValue, 0, SpringLayout.WEST, descValue);

		sl.putConstraint(SpringLayout.NORTH, centerPanel, 10, SpringLayout.SOUTH, descValue);
		sl.putConstraint(SpringLayout.WEST, centerPanel, 10, SpringLayout.WEST, center);
		sl.putConstraint(SpringLayout.EAST, centerPanel, -10, SpringLayout.EAST, center);
		sl.putConstraint(SpringLayout.SOUTH, centerPanel, -10, SpringLayout.SOUTH, center);
		
		add(center, BorderLayout.CENTER);
		add(logger, BorderLayout.SOUTH);
	
		abbrValue.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(final JComponent input) {
				try{
					final String	value = abbrValue.getText().trim();
					
					if (!value.isEmpty()) {
						CharUtils.parseListRanges(value, Predicate.class);
					}
					return true;
				} catch (SyntaxException exc) {
					getLogger().message(Severity.error, exc.getLocalizedMessage());
					return false;
				}
			}
		});
		descValue.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(final JComponent input) {
				try{
					final String	value = descValue.getText().trim();
					
					if (!value.isEmpty()) {
						CharUtils.parsceLuceneStyledQuery(value);
					}
					return true;
				} catch (SyntaxException exc) {
					getLogger().message(Severity.error, exc.getLocalizedMessage());
					return false;
				}
			}
		});
		
		pinouts.setContentAreaFilled(false);
		pinouts.addActionListener((e)->showPinout());
		addParam.setContentAreaFilled(false);
		addParam.addActionListener((e)->insertParam());
		removeParam.setContentAreaFilled(false);
		removeParam.addActionListener((e)->removeParam());
		removeParam.setEnabled(false);
		parms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		parms.getSelectionModel().addListSelectionListener((e)->{
			removeParam.setEnabled(parms.getSelectedRow() != -1);
		});
		fillLocalizedStrings();
	}

	@Override
	public LoggerFacade getLogger() {
		return logger;
	}

	public RowFilter<TubesModel, Integer> getFilter() {
		final Set<TubesType>		types2Check = new HashSet<>();
		final Set<TubePanelGroup>	panels2Check = new HashSet<>();
		
		for(TubesType value : types.getSelectedValuesList()) {
			types2Check.add(value);
		}
		if (types2Check.isEmpty()) {
			types2Check.addAll(Arrays.asList(TubesType.values()));
		}
		
		for(TubePanelGroup value : panels.getSelectedValuesList()) {
			panels2Check.add(value);
		}
		if (panels2Check.isEmpty()) {
			panels2Check.addAll(Arrays.asList(TubePanelGroup.values()));
		}
		final Predicate<String>	testAbbr;
		final RelevanceFunction	testDescr;
		
		if (!Utils.checkEmptyOrNullString(abbrValue.getText())) {
			Predicate<String>	temp;
			
			try {
				temp = CharUtils.parseListRanges(abbrValue.getText().toUpperCase(), Predicate.class);
			} catch (SyntaxException e) {
				temp = (s)->false;
			}
			testAbbr = temp;
		}
		else {
			testAbbr = (s)->true;
		}
		
		if (!Utils.checkEmptyOrNullString(descValue.getText())) {
			RelevanceFunction	temp;
			
			try {
				temp = CharUtils.parsceLuceneStyledQuery(descValue.getText());
			} catch (SyntaxException e) {
				temp = (f)->1;
			}
			testDescr = temp;
		}
		else {
			testDescr = (f)->1;
		}
		final Predicate<TubeDescriptor>[]	parms = new Predicate[model.getRowCount()];

		for(int index = 0; index < model.getRowCount(); index++) {
			final ParmValue	item = model.content.get(index);
			
			parms[index] = buildPredicate(item.parameter, item.value);
		}
		final Predicate<TubeConnector[]>	conns;
		
		if (!connectors.isEmpty()) {
			final TubeConnector[]	conn = connectors.toArray(new TubeConnector[connectors.size()]);

			conns = (d)->testConnectors(d, conn);
		}
		else {
			conns = (d)->true;
		}

		return new RowFilter<TubesModel, Integer>() {
			@Override
			public boolean include(final Entry<? extends TubesModel, ? extends Integer> entry) {
				final TubeDescriptor	desc = entry.getModel().getDescriptor(entry.getIdentifier());
				
				return  types2Check.contains(desc.getType()) &&
						panels2Check.contains(desc.getPanelType().getGroup()) &&
						testAbbr.test(desc.getAbbr().toUpperCase()) &&
						hasParameter(desc, parms) &&
						testDescr.test((s)->fromLocalizer(desc.getLocalizer(), desc.getDescription())) > 0 &&
						conns.test(desc.getConnectors());
			}

		};
	}

	private String fromLocalizer(final Localizer localizer, final String description) {
		try {
			final String 	strDesc = Utils.fromResource(localizer.getContent(description, MimeType.MIME_CREOLE_TEXT, MimeType.MIME_HTML_TEXT));
			
			return strDesc.substring(strDesc.indexOf("<html>"));
		} catch (LocalizationException | IllegalArgumentException | NullPointerException | IOException e) {
			return description;
		}
	}
	
	private static boolean testConnectors(final TubeConnector[] current, final TubeConnector[] template) {
		final Set<TubeConnectorType>	currentSet = new HashSet<>();
		final Set<TubeConnectorType>	templateSet = new HashSet<>();
		final Set<TubeConnectorType>	temp = new HashSet<>();
		
		for(TubeConnector item : current) {
			currentSet.add(item.getType());
		}
		for(TubeConnector item : template) {
			temp.add(item.getType());
		}
		templateSet.addAll(temp);
		temp.removeAll(currentSet);
		
		if (!temp.isEmpty()) {
			return false;
		}
		else {
			int	count = 0;
			
			for(TubeConnectorType item : templateSet) {
				for (TubeConnector left : current) {
					if (left.getType() == item) {
						for (TubeConnector right : template) {
							if (right.getType() == item) {
								if (left.getPinType() == right.getPinType() &&
									left.getPin() == right.getPin()) {
									count++;
								}
							}
						}
					}
				}
			}
			return count == template.length;
		}
	}

	private void insertParam() {
		final JPopupMenu	menu = new JPopupMenu();
		
		for(TubeParameter item : TubeParameter.values()) {
			if (!item.isMaxAvailable()) {
				final JMenuItem	mi = new JMenuItem(localizer.getValue(InternalUtils.getLocaleResource(item).value()));
				
				mi.addActionListener((e)->insertParam(item));
				menu.add(mi);
			}
		}
		menu.addSeparator();
		for(TubeParameter item : TubeParameter.values()) {
			if (item.isMaxAvailable()) {
				final JMenuItem	mi = new JMenuItem(localizer.getValue(InternalUtils.getLocaleResource(item).value()));
				
				mi.addActionListener((e)->insertParam(item));
				menu.add(mi);
			}
		}
		menu.show(addParam, addParam.getWidth()/2, addParam.getHeight()/2);
	}

	private void insertParam(final TubeParameter item) {
		model.insertParameter(item);
	}

	private void removeParam() {
		model.removeParameter(parms.getSelectedRow());
	}
	
	private Predicate<TubeDescriptor> buildPredicate(final TubeParameter parameter, final String value) {
		try {
			final DoublePredicate	test = CharUtils.parseListRanges(value, DoublePredicate.class);
			
			return (d)->{
				for(int index = 0; index < d.getType().getNumberOfLamps(); index++) {
					final TubeParameter[]	p = d.getParameters(index);
					
					for(int pIndex = 0; pIndex < p.length; pIndex++) {
						if (p[pIndex] == parameter && test.test(d.getValues(index)[pIndex])) {
							return true;
						}
					}
				}
				return false;
			};
		} catch (SyntaxException e) {
			getLogger().message(Severity.error, e.getLocalizedMessage());
			return (d)->false;
		}
	}

	private void showPinout() {
		connectors.clear();
		try {
			final ConnScreen cs = new ConnScreen(localizer);
			
			if (new JLocalizedOptionPane(localizer).confirm(this, cs, PINOUT_TITLE, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				connectors.addAll(cs.getConnectors());
			}
		} catch (ContentException e) {
			getLogger().message(Severity.error, e.getLocalizedMessage());
		}
	}
	
	private static boolean hasParameter(final TubeDescriptor desc, final Predicate<TubeDescriptor>[] parms) {
		for(Predicate<TubeDescriptor> item : parms) {
			if (!item.test(desc)) {
				return false;
			}
		}
		return true;
	}
	
	private void fillLocalizedStrings() {
		abbrLabel.setText(localizer.getValue(LABEL_ABBR_NAME));
		descLabel.setText(localizer.getValue(LABEL_DESCR_NAME));
		pinouts.setToolTipText(localizer.getValue(PINOUTS_TT));
		addParam.setToolTipText(localizer.getValue(PLUS_TT));
		removeParam.setToolTipText(localizer.getValue(MINUS_TT));
		typesPane.setBorder(new TitledBorder(new LineBorder(Color.BLACK), localizer.getValue(BORDER_TYPES_NAME)));
		panelsPane.setBorder(new TitledBorder(new LineBorder(Color.BLACK), localizer.getValue(BORDER_PANELS_NAME)));
	}

	private static class ParmValue {
		private final TubeParameter	parameter;
		private String				value;

		private ParmValue(final TubeParameter parameter, final String value) {
			this.parameter = parameter;
			this.value = value;
		}
	}
	
	private class ParmTableModel extends DefaultTableModel {
		private static final long serialVersionUID = -1898375546844154443L;
		
		private final Localizer			localizer;
		private final List<ParmValue>	content = new ArrayList<>();
		
		public ParmTableModel(final Localizer localizer) {
			this.localizer = localizer;
		}

		@Override
		public int getRowCount() {
			return content == null ? 0 : content.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(final int columnIndex) {
			switch (columnIndex) {
				case 0 :
					return localizer.getValue(COL_NAME);
				case 1 :
					return localizer.getValue(COL_VALUE);
				default :
					return null;
			}
		}

		@Override
		public Class<?> getColumnClass(final int columnIndex) {
			switch (columnIndex) {
				case 0 :
					return TubeParameter.class;
				case 1 :
					return String.class;
				default :
					return null;
			}
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
			return columnIndex == 1;
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			switch (columnIndex) {
				case 0 :
					return localizer.getValue(InternalUtils.getLocaleResource(content.get(rowIndex).parameter).value());
				case 1 :
					return content.get(rowIndex).value;
				default :
					return null;
			}
		}

		@Override
		public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
			final String			value = aValue.toString();
			
			try {
				CharUtils.parseListRanges(value, DoublePredicate.class);
				content.get(rowIndex).value = value; 
				fireTableCellUpdated(rowIndex, columnIndex);
			} catch (SyntaxException e) {
				getLogger().message(Severity.error, e.getLocalizedMessage());
			}
		}
		
		public void insertParameter(final TubeParameter parm) {
			if (parm == null) {
				throw new NullPointerException("Parameter to insert can't be null");
			}
			else {
				content.add(new ParmValue(parm, "0"));
				fireTableRowsInserted(content.size()-1, content.size()-1);
			}
		}
		
		public void removeParameter(final int rowIndex) {
			if (rowIndex < 0 || rowIndex >= content.size()) {
				throw new IllegalArgumentException("Parameter index ["+rowIndex+"] out of range 0.."+(content.size()-1));
			}
			else {
				content.remove(rowIndex);
				fireTableRowsDeleted(rowIndex, rowIndex);
			}
		}
	}
}

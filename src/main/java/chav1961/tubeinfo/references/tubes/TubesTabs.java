package chav1961.tubeinfo.references.tubes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;

import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;
import chav1961.tubeinfo.references.interfaces.TubeDescriptor;
import chav1961.tubeinfo.references.interfaces.TubesType;

class TubesTabs extends JPanel implements LocaleChangeListener, LocalizerOwner {
	private static final long 	serialVersionUID = -9007080601952548368L;
	private static final Icon	FILTER_ICON = new ImageIcon(TubesTabs.class.getResource("filter.png"));
	private static final String	TITLE_FILTER_FORM = "chav1961.tubesReference.filterForm.title"; 
	
	private final Localizer		localizer;
	private final JComboBox<TubesType>	types = new JComboBox<>(TubesType.values());
	private final JToggleButton	filter = new JToggleButton(FILTER_ICON);
	private final TubesTab		tab; 

	TubesTabs(final Localizer localizer, final Consumer<TubeDescriptor> selection, final TubeDescriptor... content) {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (content == null || Utils.checkArrayContent4Nulls(content) >= 0) {
			throw new IllegalArgumentException("Content is null or contains nulls inside");
		}
		else {
			final JToolBar		toolBar = new JToolBar();
			
			this.localizer = localizer;
			this.tab = new TubesTab(localizer, selection, content);
			
			setLayout(new BorderLayout());
			types.setMaximumSize(new Dimension(250, 32));
			types.setRenderer(SwingUtils.getCellRenderer(TubesType.class, new FieldFormat(TubesType.class), ListCellRenderer.class));
			types.addActionListener((e)->{
				tab.setTypeFilter((TubesType)types.getSelectedItem());
			});
			toolBar.setFloatable(false);
			toolBar.setRollover(true);
			toolBar.add(types);
			toolBar.addSeparator();
			filter.addActionListener((e)->{
				turnFilter();
			});
			toolBar.add(filter);
			add(toolBar, BorderLayout.NORTH);
			add(tab, BorderLayout.CENTER);
			
			fillLocalizedStrings();
		}
	}

	@Override
	public Localizer getLocalizer() {
		return localizer;
	}
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings();
	}

	public boolean selectFilter() {
		final FilterForm	ff = new FilterForm(localizer);
		
		ff.setPreferredSize(new Dimension(640, 480));
		if (new JLocalizedOptionPane(localizer).confirm(this, ff, TITLE_FILTER_FORM, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			filter.setSelected(true);
			tab.setCommonFilter(ff.getFilter());
			return true;
		}
		else {
			return false;
		}
	}

	private void turnFilter() {
		if (filter.isSelected()) {
			if (!selectFilter()) {
				filter.setSelected(false);
				tab.clearCommonFilter();
			}
		}
		else {
			tab.clearCommonFilter();
		}
	}
	
	private void fillLocalizedStrings() {
		
	}
}

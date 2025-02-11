package chav1961.tubeinfo.references.tubes;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.tubeinfo.references.interfaces.TubeDescriptor;
import chav1961.tubeinfo.references.interfaces.TubePanelGroup;
import chav1961.tubeinfo.references.interfaces.TubesType;

class TubesTab extends JPanel implements LocaleChangeListener {
	private static final long 		serialVersionUID = 8720981840520863203L;

	private final Consumer<TubeDescriptor>	selection;
	private final TubesModel				model;
	private final TableRowSorter<TubesModel>sorter;
	private final JScrollPane				scroll;
	private final JTable					table;
	private TubesType						currentType = TubesType.ALL;
	private RowFilter<TubesModel, Integer> 	currentFilter = null;
	
	TubesTab(final Localizer localizer, final Consumer<TubeDescriptor> selection, final TubeDescriptor... content) {
		super(new BorderLayout(5, 5));
		this.selection = selection;
		this.model = new TubesModel(localizer, content);
		this.table = new JTable(model);
		this.sorter = new TableRowSorter<TubesModel>(model);
		this.sorter.setSortable(0, false);
		this.sorter.setSortable(1, false);
		this.sorter.setRowFilter(null);
		this.table.setRowSorter(sorter);
		this.table.setCellSelectionEnabled(true);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.getSelectionModel().addListSelectionListener((e)->fireSelection());
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.table.setRowHeight(32);
		this.table.setDefaultRenderer(Icon.class, SwingUtils.getCellRenderer(Icon.class, new FieldFormat(Icon.class), TableCellRenderer.class));
		this.table.setDefaultRenderer(TubePanelGroup.class, SwingUtils.getCellRenderer(TubePanelGroup.class, new FieldFormat(Icon.class), TableCellRenderer.class));
		this.table.setDefaultRenderer(float[].class, SwingUtils.getCellRenderer(float[].class, new FieldFormat(float[].class), TableCellRenderer.class));
		this.scroll = new JScrollPane(table);
		
		add(scroll, BorderLayout.CENTER);
	}
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		model.fireTableStructureChanged();
	}
	
	public TubesType getTypeFilter() {
		return currentType;
	}

	public void setTypeFilter(final TubesType type) {
		if (type == null) {
			throw new NullPointerException("Tubes type can't be null");
		}
		else {
			currentType = type;
			refreshFilter();
		}
	}
	
	public RowFilter<TubesModel, Integer> getCommonFilter() {
		return currentFilter;
	}
	
	public void clearCommonFilter() {
		this.currentFilter = null;
		refreshFilter();
	}
	
	public void setCommonFilter(final RowFilter<TubesModel, Integer> filter) {
		if (filter == null) {
			throw new NullPointerException("Filter to set can't be null");
		}
		else {
			this.currentFilter = filter;
			refreshFilter();
		}
	}
	
	public TubeDescriptor getSelection() {
		final int 	row = table.getSelectedRow();
		
		if (row < 0) {
			return null;
		}
		else {
			return model.getDescriptor(table.convertRowIndexToModel(row));
		}
	}

	private void refreshFilter() {
		if (currentType == TubesType.ALL) {
			if (currentFilter == null) {
				this.sorter.setRowFilter(null);
			}
			else {
				this.sorter.setRowFilter(currentFilter);
			}
		}
		else {
			final RowFilter<TubesModel, Integer> filter = new RowFilter<>(){
													@Override
													public boolean include(Entry<? extends TubesModel, ? extends Integer> entry) {
														return entry.getModel().getDescriptor(entry.getIdentifier()).getType() == currentType;
													}
												};
			if (currentFilter == null) {
				this.sorter.setRowFilter(filter);
			}
			else {
				this.sorter.setRowFilter(RowFilter.<TubesModel, Integer>andFilter(Arrays.asList(filter, currentFilter)));
			}
		}
	}
	
	private void fireSelection() {
		selection.accept(getSelection());
	}
}

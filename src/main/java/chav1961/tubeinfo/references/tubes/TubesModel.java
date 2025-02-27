package chav1961.tubeinfo.references.tubes;

import javax.swing.table.DefaultTableModel;

import chav1961.purelib.basic.Utils;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.tubeinfo.references.interfaces.TubeDescriptor;
import chav1961.tubeinfo.references.interfaces.TubePanelGroup;
import chav1961.tubeinfo.utils.InternalUtils;

class TubesModel extends DefaultTableModel {
	private static final long 		serialVersionUID = -4800780353810497969L;
	private static final int		ADVANCED_COL = 3;
	private static final String		COL_PANEL = "chav1961.tubesReference.table.panel"; 
	private static final String		COL_ABBR = "chav1961.tubesReference.table.abbr"; 
	private static final String		COL_DESCR = "chav1961.tubesReference.table.descr"; 
	
	private final Localizer			localizer;
	private final TubeDescriptor[]	content;
	
	TubesModel(final Localizer localizer, final TubeDescriptor... content) {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null"); 
		}
		else if (content == null || Utils.checkArrayContent4Nulls(content) >= 0) {
			throw new IllegalArgumentException("Content is null or contains nulls inside");
		}
		else {
			this.localizer = localizer;
			this.content = content.clone();
		}
	}

	@Override
	public int getRowCount() {
		return content == null ? 0 : content.length;
	}

	@Override
	public int getColumnCount() {
		return ADVANCED_COL;
	}

	@Override
	public String getColumnName(final int columnIndex) {
		switch (columnIndex) {
			case 0 	:
				return localizer.getValue(COL_PANEL);
			case 1 	:
				return localizer.getValue(COL_ABBR);
			case 2 	:
				return localizer.getValue(COL_DESCR);
			default :
				return null;
		}
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		switch (columnIndex) {
			case 0 	:
				return TubePanelGroup.class;
			case 1 	:
				return String.class;
			case 2 	:
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
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0 	:
				return content[rowIndex].getPanelType().getGroup();
			case 1 	:
				return content[rowIndex].getAbbr();
			case 2 	:
				return localizer.getValue(InternalUtils.getLocaleResource(content[rowIndex].getType()).value());
			default :
				return null;
		}
	}

	public TubeDescriptor getDescriptor(final int rowIndex) {
		return content[rowIndex];
	}
}

package wordpressviewer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

public class WPJSONUnitTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3171823965696657294L;

	private List<String> _columnList;
	private List<WPJSONUnit> _list = new ArrayList<WPJSONUnit>();

	public WPJSONUnitTableModel(List<WPJSONUnit> list) {
		_list = list;
		calculateColumnListFromWPJSONUnitList();
	}

	public WPJSONUnit getDataAt(int row) {
		return _list.get(row);
	}

	private void calculateColumnListFromWPJSONUnitList() {
		Set<String> set = new HashSet<String>();
		List<String> columnList = new ArrayList<String>();
		for (WPJSONUnit unit : _list) {
			List<String> list = unit.getNameList();
			for (String name : list) {
				if (set.contains(name)) {
					continue;
				}
				columnList.add(name);
				set.add(name);
			}
		}
		_columnList = columnList;
	}

	@Override
	public String getColumnName(int col) {
		return _columnList.get(col);
	}

	@Override
	public Class getColumnClass(int col) {
		return String.class;
	}

	@Override
	public int getRowCount() {
		return _list.size();
	}

	@Override
	public int getColumnCount() {
		return _columnList.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		WPJSONUnit unit = _list.get(row);
		return unit.getColumnValueAt(col);
	}

}

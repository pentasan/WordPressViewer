package wordpressviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import util.Utility;

public class WPJSONUnitPanel extends JPanel implements MouseListener, KeyListener, ActionListener {

	private static final long serialVersionUID = 4304129247566444150L;

	private static final int MIN_COLUNN_WIDTH = 100;
	private static final int MAX_COLUNN_WIDTH = 500;

	private static final String ADD_NEW_ITEM_NA_LIST[] = { "types", "users", "categories", "tags", "media" };

	private JFrame _parent;
	private List<WPJSONUnit> _list;
	private String _url;
	private String _restBase;

	private JTextField _textField;

	private JButton _addNewButton;

	private TableRowSorter<WPJSONUnitTableModel> _sorter;
	private WPJSONUnitTableModel _tableModel;
	private JTable _table;

	private JMenuItem _editMenuItem;

	public WPJSONUnitPanel(JFrame parent, List<WPJSONUnit> list, String url, String restBase) {
		_parent = parent;
		_list = list;
		_url = url;
		_restBase = restBase;

		addComponents();
	}

	private boolean canAddNewButton() {
		for (String s : ADD_NEW_ITEM_NA_LIST) {
			if (s.equals(_restBase)) {
				return false;
			}
		}
		return true;
	}

	private JLabel getLabel(String text) {
		JLabel ret = new JLabel(text);
		return ret;
	}

	private JPanel getHeaderRightPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

		if (canAddNewButton()) {
			_addNewButton = new JButton("Add new item");
			_addNewButton.addActionListener(this);
			ret.add(_addNewButton);
		}
		return ret;
	}

	private JPanel getHeaderLeftPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		ret.add(getLabel("Search:"));

		_textField = new JTextField(30);
		_textField.addKeyListener(this);
		ret.add(_textField);

		return ret;
	}

	private JPanel getHeaderPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());
		ret.add(getHeaderLeftPanel());
		ret.add(getHeaderRightPanel(), BorderLayout.EAST);
		return ret;
	}

	private JPopupMenu getPopupMenu() {
		JPopupMenu ret = new JPopupMenu();

		_editMenuItem = new JMenuItem("Edit");
		_editMenuItem.addActionListener(this);
		ret.add(_editMenuItem);

		return ret;
	}

	private void addComponents() {
		setLayout(new BorderLayout());

		add(getHeaderPanel(), BorderLayout.NORTH);

		_tableModel = new WPJSONUnitTableModel(_list);
		_table = new JTable(_tableModel);
		_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_table.addMouseListener(this);
		resizeColumnWidth(_table);

		_sorter = new TableRowSorter<WPJSONUnitTableModel>(_tableModel);
		_table.setRowSorter(_sorter);
		if (canAddNewButton()) {
			_table.setComponentPopupMenu(getPopupMenu());
		}
		JScrollPane scroll = new JScrollPane(_table);
		add(scroll);
	}

	private void resizeColumnWidth(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			int width = MIN_COLUNN_WIDTH; // Min width
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width + 1, width);
			}
			if (width > MAX_COLUNN_WIDTH) {
				width = MAX_COLUNN_WIDTH;
			}
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	private void actionTextFieldInput() {
		try {
			int count = _tableModel.getColumnCount();
			List<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < count; i++) {
				list.add(i);
			}
			RowFilter<WPJSONUnitTableModel, Object> rowFilter = RowFilter.regexFilter(_textField.getText().trim(),
					Utility.getIntArrayFromIntegerList(list));
			_sorter.setRowFilter(rowFilter);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
	}

	private void actionAddNewButton() {
		new EditContentDialog(_parent, _url, _restBase, null);
	}

	private WPJSONUnit getSelectedWPJSONUnit() {
		int row = _table.getSelectedRow();
		if (row < 0) {
			return null;
		}
		row = _table.convertRowIndexToModel(row);
		return _tableModel.getDataAt(row);
	}

	private void actionEditMenuItem() {
		WPJSONUnit unit = getSelectedWPJSONUnit();
		if (unit == null) {
			return;
		}
		new EditContentDialog(_parent, _url, _restBase, unit);
	}

	private void actionDeleteMenuItem() {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Object source = e.getSource();
		if (source == _table && e.getClickCount() == 2) {
			Point point = e.getPoint();
			int row = _table.rowAtPoint(point);
			WPJSONUnit unit = _tableModel.getDataAt(row);
			new WPJSONUnitDetailFrame(_parent, _restBase, unit);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Object source = e.getSource();
		if (source == _textField) {
			actionTextFieldInput();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == _addNewButton) {
			actionAddNewButton();
		} else if (source == _editMenuItem) {
			actionEditMenuItem();
		}
	}
}

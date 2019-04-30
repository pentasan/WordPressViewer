package wordpressviewer;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import resource.ImageProvider;
import util.Utility;

public class WPJSONUnitDetailFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 6528154326477522066L;

	private static final int FRAME_WIDTH = 500;
	private static final int FRAME_HEIGHT = 800;

	private WPJSONUnit _unit;

	private List<JButton> _buttonList = new ArrayList<JButton>();

	public WPJSONUnitDetailFrame(JFrame parent, String restBase, WPJSONUnit unit) {
		setTitle("Content detail, " + restBase + " " + unit.getId());
		setIconImage(ImageProvider.getInstance()
				.getImage(ImageProvider.ICON_WP).getImage());

		_unit = unit;

		addComponents();
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private JLabel getLabel(String text) {
		JLabel ret = new JLabel(text);
		return ret;
	}

	private JTextField getTextField(String text) {
		JTextField ret = new JTextField(text);
		ret.setCaretPosition(0);
		return ret;
	}

	private JPanel getLeftPanel() {
		JPanel ret = new JPanel();
		List<NameValuePair> list = _unit.getNameValuePairList();
		ret.setLayout(new GridLayout(list.size(), 1));
		for (NameValuePair data : list) {
			ret.add(getLabel(data.getName()));
		}
		return ret;
	}

	private JPanel getCenterPanel() {
		JPanel ret = new JPanel();
		List<NameValuePair> list = _unit.getNameValuePairList();
		ret.setLayout(new GridLayout(list.size(), 1));
		for (NameValuePair data : list) {
			ret.add(getTextField(data.getValue()));
		}
		return ret;
	}

	private JPanel getRightPanel() {
		JPanel ret = new JPanel();
		List<NameValuePair> list = _unit.getNameValuePairList();
		ret.setLayout(new GridLayout(list.size(), 1));
		for (NameValuePair data : list) {
			JButton button = new JButton("detail");
			button.addActionListener(this);
			_buttonList.add(button);
			ret.add(button);
		}
		return ret;
	}

	private void addComponents() {

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getLeftPanel(), BorderLayout.WEST);
		getContentPane().add(getCenterPanel());
		getContentPane().add(getRightPanel(), BorderLayout.EAST);
	}

	private void actionShowDetailButton(int pos) {
		NameValuePair data = _unit.getNameValuePairList().get(pos);
		String text = data.getValue();
		if (text.startsWith("http://") || text.startsWith("https://")) {
			try {
				Desktop.getDesktop().browse(new URI(text));
			} catch (Exception e) {
				Utility.showMessageDialog(this, "Error:" + e);
			}
		} else {
			new TextAreaFrame(this, text);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (!(source instanceof JButton)) {
			return;
		}
		JButton button = (JButton) source;
		for (int i = 0; i < _buttonList.size(); i++) {
			if (button == _buttonList.get(i)) {
				actionShowDetailButton(i);
				return;
			}
		}
	}
}

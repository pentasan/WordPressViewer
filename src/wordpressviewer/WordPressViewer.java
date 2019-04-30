package wordpressviewer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import resource.ImageProvider;
import util.Utility;

public class WordPressViewer extends JFrame implements ActionListener {

	private static final long serialVersionUID = 5550429248616387377L;

	private static final int PAGE_MAX_COUNT = 100;

	private static final int FRAME_WIDTH = 1000;
	private static final int FRAME_HEIGHT = 400;

	private static final String DEFAULT_TYPE_LIST[] = { "posts", "pages", "media", "blocks" };

	private JTextField _urlTextField;
	private JButton _loadButton;

	private JTabbedPane _tabbedPane;

	private JLabel _footerLabel;

	private List<String> _typeList = new ArrayList<String>();

	public WordPressViewer() {
		setTitle("Word Press Viewer");
		setIconImage(ImageProvider.getInstance().getImage(ImageProvider.ICON_WP).getImage());

		addComponents();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setVisible(true);
	}

	private JLabel getLabel(String title) {
		return new JLabel(title);
	}

	private JPanel getHeaderPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		ret.add(getLabel("URL:"));

		_urlTextField = new JTextField("");
		_urlTextField.setColumns(30);
		ret.add(_urlTextField);

		_loadButton = new JButton("Load");
		_loadButton.addActionListener(this);
		ret.add(_loadButton);

		return ret;
	}

	private JPanel getMainPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());

		_tabbedPane = new JTabbedPane();
		ret.add(_tabbedPane);

		return ret;
	}

	private JPanel getFooterPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());
		_footerLabel = getLabel(" ");
		ret.add(_footerLabel);
		return ret;
	}

	private void addComponents() {
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getHeaderPanel(), BorderLayout.NORTH);
		getContentPane().add(getMainPanel());
		getContentPane().add(getFooterPanel(), BorderLayout.SOUTH);
	}

	private void actionLoadButton() {
		String text = _urlTextField.getText().trim();
		if (Utility.isEmptyString(text)) {
			Utility.showMessageDialog(this, "URL was not input...");
			return;
		}
		if (!text.startsWith("http://") && !text.startsWith("https://")) {
			Utility.showMessageDialog(this, "URL is not valid...");
			return;
		}
		LoadDataThread thread = new LoadDataThread(text);
		thread.start();
	}

	private void setFooterLabelText(String text) {
		_footerLabel.setText(text);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == _loadButton) {
			actionLoadButton();
		}
	}

	private class LoadDataThread extends Thread {
		private String _url;

		public LoadDataThread(String url) {
			_url = url;
		}

		private String getWordPressJSONTextFromURL(String url) throws IOException {
			return Utility.getHTTPContent(url);
		}

		private String getURLHeadFromURL(String url) {
			if (url.endsWith("/")) {
				return url.substring(0, url.length() - 1);
			}
			return url;
		}

		private String getRestBaseURL(String url, String restBase) {
			return getURLHeadFromURL(url) + "/wp-json/wp/v2/" + restBase;
		}

		private boolean isDefaultType(String type) {
			for (String s : DEFAULT_TYPE_LIST) {
				if (s.equals(type)) {
					return true;
				}
			}
			return false;
		}

		private JPanel getAllWPJSONUnitPanelFromURL(String inputURL, String restBase) {
			try {
				List<WPJSONUnit> allList = new ArrayList<WPJSONUnit>();
				int page = 1;
				while (true) {
					String url = getRestBaseURL(inputURL, restBase) + "?page=" + page + "&per_page=" + PAGE_MAX_COUNT;
					System.out.println("url:" + url);
					setFooterLabelText("Loading " + url + " ...");
					String text = getWordPressJSONTextFromURL(url);
					List<WPJSONUnit> list = WPJSONUnit.parseJSONText(text);
					allList.addAll(list);
					if (list.size() < PAGE_MAX_COUNT) {
						break;
					}
					page++;
				}
				if ("types".equals(restBase)) {
					_typeList.clear();
					for (WPJSONUnit unit : allList) {
						String text = unit.getValueByName("rest_base");
						if (isDefaultType(text)) {
							continue;
						}
						_typeList.add(text);
					}
				}
				return new WPJSONUnitPanel(WordPressViewer.this, allList, inputURL, restBase);
			} catch (Exception e) {
				return new ExceptionMessagePanel(e);
			}
		}

		public void run() {
			try {
				_tabbedPane.removeAll();
				_tabbedPane.validate();

				String typeList[] = { "types", "users", "categories", "tags", "posts", "pages", "media" };
				for (String type : typeList) {
					_tabbedPane.addTab(type, getAllWPJSONUnitPanelFromURL(_url, type));
				}

				for (String restBase : _typeList) {
					_tabbedPane.addTab(restBase, getAllWPJSONUnitPanelFromURL(_url, restBase));
				}

				setFooterLabelText("Load finished.");
			} catch (Exception e) {
				e.printStackTrace();
				Utility.showMessageDialog(WordPressViewer.this, "Error:" + e);
			}
		}
	}

	public static void main(String args[]) {
		Utility.setNimbusLaf();
		new WordPressViewer();
	}
}

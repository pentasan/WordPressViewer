package wordpressviewer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import resource.ImageProvider;
import util.Utility;

public class EditContentDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -1107471099978255685L;

	private static final int DIALOG_WIDTH = 500;
	private static final int DIALOG_HEIGHT = 300;

	private final String USER_AGENT = "Mozilla/5.0";

	private String _url;
	private String _restBase;
	private WPJSONUnit _unit;

	private JTextField _titleTextField;

	private JTextArea _contentTextArea;
	private JButton _saveButton;

	public EditContentDialog(JFrame parent, String url, String restBase, WPJSONUnit unit) {
		super(parent);
		setTitle(getDialogTitleText(unit));
		setIconImage(ImageProvider.getInstance().getImage(ImageProvider.ICON_WP).getImage());

		_url = url;
		_restBase = restBase;
		_unit = unit;

		addComponents();

		setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	private String getDialogTitleText(WPJSONUnit unit) {
		if (unit == null) {
			return "Add new content";
		} else {
			return "Edit content";
		}
	}

	private JLabel getLabel(String text) {
		JLabel ret = new JLabel(text);
		return ret;
	}

	private JPanel getHeaderPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());
		ret.add(getLabel("Title:"), BorderLayout.WEST);

		_titleTextField = new JTextField();
		if (_unit != null) {
			_titleTextField.setText(_unit.getValueByName("title"));
		}
		ret.add(_titleTextField);

		return ret;
	}

	private JPanel getMainPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());
		ret.add(getLabel("content:"), BorderLayout.NORTH);

		_contentTextArea = new JTextArea();
		if (_unit != null) {
			_contentTextArea.setText(_unit.getValueByName("content"));
		}
		ret.add(new JScrollPane(_contentTextArea));
		return ret;
	}

	private JPanel getFooterPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		_saveButton = new JButton("Save");
		_saveButton.addActionListener(this);
		ret.add(_saveButton);
		return ret;
	}

	private void addComponents() {
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getHeaderPanel(), BorderLayout.NORTH);
		getContentPane().add(getMainPanel());
		getContentPane().add(getFooterPanel(), BorderLayout.SOUTH);
	}

	private void sendPost(String url, String title, String content) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = null;
		if (url.startsWith("https://")) {
			con = (HttpsURLConnection) obj.openConnection();
		} else {
			con = (HttpURLConnection) obj.openConnection();
		}

		String usernameColonPassword = "yoko:0u5m rLN0 9njO aBRj UnOk fvFT";
		String basicAuthPayload = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Authorization", basicAuthPayload);

		// String urlParameters = "title="
		// + URLEncoder.encode("‚ ‚¢‚¤“ú–{Œê‚©‚«", "UTF-8");
		String urlParameters = "title=" + URLEncoder.encode(title, "UTF-8") + "&content="
				+ URLEncoder.encode(content, "UTF-8") + "&status=publish";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());

	}

	private String getPostURL() {
		if (_unit == null) {
			return _url + "wp-json/wp/v2/" + _restBase;
		} else {
			return _url + "wp-json/wp/v2/" + _restBase + "/" + _unit.getId();
		}
	}

	private void saveContent(String title, String content) throws Exception {
		String postURL = getPostURL();
		sendPost(postURL, title, content);
	}

	private void actionSaveButton() {
		String title = _titleTextField.getText().trim();
		if (Utility.isEmptyString(title)) {
			Utility.showMessageDialog(this, "Title is empty...");
			return;
		}
		String content = _contentTextArea.getText().trim();
		if (Utility.isEmptyString(content)) {
			Utility.showMessageDialog(this, "Content is empty...");
			return;
		}
		try {
			saveContent(title, content);
			Utility.showMessageDialog(this, "Saved.");
			setVisible(false);
		} catch (Exception e) {
			Utility.showMessageDialog(this, "Error:" + e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == _saveButton) {
			actionSaveButton();
		}
	}
}

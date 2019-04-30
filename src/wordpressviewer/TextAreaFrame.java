package wordpressviewer;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import resource.ImageProvider;

public class TextAreaFrame extends JFrame {

	private static final long serialVersionUID = 4489128199307913540L;

	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 600;

	private String _text;

	public TextAreaFrame(JFrame parent, String text) {
		setTitle("Detail");
		setIconImage(ImageProvider.getInstance()
				.getImage(ImageProvider.ICON_WP).getImage());

		_text = text;

		addComponents();
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void addComponents() {
		getContentPane().setLayout(new BorderLayout());
		JTextArea textArea = new JTextArea(_text);
		textArea.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(scroll);
	}
}

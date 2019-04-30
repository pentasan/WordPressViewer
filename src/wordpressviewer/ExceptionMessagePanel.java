package wordpressviewer;

import java.awt.BorderLayout;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ExceptionMessagePanel extends JPanel {

	private static final long serialVersionUID = -6111659286154430814L;

	private Exception _exception;

	public ExceptionMessagePanel(Exception e) {
		_exception = e;
		addComponents();
	}

	private String getExceptionMessageText() {
		StringWriter stringWriter = new StringWriter();
		_exception.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	private void addComponents() {
		setLayout(new BorderLayout());
		JTextArea textArea = new JTextArea(getExceptionMessageText());
		add(new JScrollPane(textArea));
	}
}

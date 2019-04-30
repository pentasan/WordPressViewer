package wordpressviewer;

public class NameValuePair {

	private String _name;
	private String _value;

	public NameValuePair(String name, String value) {
		super();
		_name = name;
		_value = value;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getValue() {
		return _value;
	}

	public void setValue(String value) {
		_value = value;
	}

	@Override
	public String toString() {
		return "NameValuePair [_name=" + _name + ", _value=" + _value + "]";
	}

}

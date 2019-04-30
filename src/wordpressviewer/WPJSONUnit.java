package wordpressviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WPJSONUnit {

	private List<NameValuePair> _nameValuePairList;

	private List<String> _nameList;

	public WPJSONUnit(List<NameValuePair> nameValuePairList) {
		_nameValuePairList = nameValuePairList;
		calculateNameListFromNameValuePairList();
	}

	public String getId() {
		for (NameValuePair data : _nameValuePairList) {
			if ("id".equals(data.getName())) {
				return data.getValue();
			}
		}
		return "";
	}

	public String getValueByName(String name) {
		for (NameValuePair data : _nameValuePairList) {
			if (data.getName().equals(name)) {
				return data.getValue();
			}
		}
		return null;
	}

	private void calculateNameListFromNameValuePairList() {
		_nameList = new ArrayList<String>();
		for (NameValuePair data : _nameValuePairList) {
			_nameList.add(data.getName());
		}
	}

	public List<String> getNameList() {
		return _nameList;
	}

	public List<NameValuePair> getNameValuePairList() {
		return _nameValuePairList;
	}

	public void setNameValuePairList(List<NameValuePair> nameValuePairList) {
		_nameValuePairList = nameValuePairList;
	}

	public String getColumnValueAt(int col) {
		return _nameValuePairList.get(col).getValue();
	}

	private static String getTextFromJsonNode(JsonNode node) {
		if (node.isTextual()) {
			return node.textValue();
		} else {
			return node.toString();
		}
	}

	private static WPJSONUnit parseJsonNode(String name, String nameValue,
			JsonNode node) {
		List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
		if (name != null && nameValue != null) {
			nameValuePairList.add(new NameValuePair(name, nameValue));
		}
		if (node != null) {
			Iterator<Entry<String, JsonNode>> it = node.fields();
			while (it.hasNext()) {
				Entry<String, JsonNode> entry = it.next();
				String key = entry.getKey();
				String value = getTextFromJsonNode(entry.getValue());
				nameValuePairList.add(new NameValuePair(key, value));
			}
		}
		return new WPJSONUnit(nameValuePairList);
	}

	public static List<WPJSONUnit> parseJSONText(String text)
			throws IOException {
		List<WPJSONUnit> ret = new ArrayList<WPJSONUnit>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode treeNode = mapper.readTree(text);
		if (treeNode.isArray()) {
			for (int i = 0; i < treeNode.size(); i++) {
				JsonNode node = treeNode.get(i);
				WPJSONUnit unit = WPJSONUnit.parseJsonNode(null, null, node);
				if (unit == null) {
					continue;
				}
				ret.add(unit);
			}
		} else {
			// Iterator<JsonNode> it = treeNode.elements();
			Iterator<Entry<String, JsonNode>> it = treeNode.fields();
			while (it.hasNext()) {
				Entry<String, JsonNode> e = it.next();
				String name = e.getKey();
				JsonNode node = e.getValue();
				WPJSONUnit unit = WPJSONUnit.parseJsonNode("type", name, node);
				if (unit == null) {
					continue;
				}
				ret.add(unit);
			}
		}
		return ret;
	}

	@Override
	public String toString() {
		return "WPJSONUnit [_nameValuePairList=" + _nameValuePairList + "]";
	}

}

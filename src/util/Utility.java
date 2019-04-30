package util;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Utility {

	public static int[] getIntArrayFromIntegerList(List<Integer> list) {
		int ret[] = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			ret[i] = list.get(i);
		}
		return ret;
	}

	public static void saveTextFile(String fileName, String content)
			throws IOException {
		BufferedWriter bw = null;
		try {
			OutputStreamWriter osw = new OutputStreamWriter(
					new FileOutputStream(new File(fileName)), "UTF-8");
			bw = new BufferedWriter(osw);
			bw.write(content);
		} catch (IOException e) {
			throw e;
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
				}
				bw = null;
			}
		}
	}

	public static void setNimbusLaf() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}
	}

	public static void showMessageDialog(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Information",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static boolean isEmptyString(String text) {
		if (text == null || text.equals("")) {
			return true;
		}
		return false;
	}

	public static void removeAllFilesInFolder(String folderName) {
		File dir = new File(folderName);
		if (!dir.exists()) {
			return;
		}
		File files[] = dir.listFiles();
		for (File file : files) {
			String name = file.getName();
			if (name.equals(".") || name.equals("..")) {
				continue;
			}
			file.delete();
		}
	}

	public static String getHTTPContent(String urlText) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			URL url = new URL(urlText);
			if (urlText.toLowerCase().startsWith("https")) {
				HttpsURLConnection con = (HttpsURLConnection) url
						.openConnection();
				br = new BufferedReader(new InputStreamReader(
						con.getInputStream(), "UTF-8"));
			} else {
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				br = new BufferedReader(new InputStreamReader(
						con.getInputStream(), "UTF-8"));
			}
			String input;

			while ((input = br.readLine()) != null) {
				sb.append(input);
				sb.append("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
				}
			}
		}
		return sb.toString();
	}

	public static int getIntValue(String s) {
		int ret = 0;
		try {
			ret = Integer.parseInt(s);
		} catch (Exception e) {
		}
		return ret;
	}

	public static String convertStringToHexString(String text) {
		return toHexString(text.getBytes());
	}

	public static String toHexString(byte[] ba) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < ba.length; i++) {
			str.append(String.format("%x", ba[i]));
		}
		return str.toString();
	}

	public static String convertHexStringToString(String hex) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < hex.length(); i += 2) {
			str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
		}
		return str.toString();
	}

	public static byte[] loadBinaryFileContent(File file) throws Exception {
		FileInputStream in = null;
		byte b[] = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			in = new FileInputStream(file);
			while (true) {
				int len = in.read(b);
				if (len < 0) {
					break;
				}
				out.write(b, 0, len);
			}
		} catch (Exception e) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
		return out.toByteArray();
	}

	public static String loadTextFileContent(File file, String encoding)
			throws Exception {
		FileInputStream in = null;
		byte b[] = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			in = new FileInputStream(file);
			while (true) {
				int len = in.read(b);
				if (len < 0) {
					break;
				}
				out.write(b, 0, len);
			}
		} catch (Exception e) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
		return new String(out.toByteArray(), encoding);
	}

	public static String getMD5TextFromBytes(byte inputByte[]) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(inputByte);
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			// sb.append(Integer.toHexString(0xff & b));
			// sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}
}

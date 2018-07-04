package com.forte.jmeter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

public class JmeterReport {

	public String readJmeterFile(String workspace) {
		String summary = "";
		File file = new File(workspace + File.separator + "jmeter.log");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				// System.out.println(tempString);
				String pattern = ".*jmeter.reporters.Summariser.*";
				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(tempString);
				if (m.find()) {
					summary = m.group(0);
					// System.out.println(m.group(0));
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return summary;
	}

	public Map<String, String> getLogReport(String workspace , String logname) {
		Map<String, String> map = new HashMap<String, String>();
		File file = new File(workspace + File.separator + logname);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String log = "";
				String[] arr = tempString.split(",");
				if ("timeStamp".equalsIgnoreCase(arr[0]))
					continue;

				boolean b = false;
				if (b == false) {
					if (!"200".equalsIgnoreCase(arr[3])) {
						log = "responseMessage:" + arr[4] + "  ";
						b = true;
					}
					if (!"true".equalsIgnoreCase(arr[7])) {
						log = log + "failureMessage:" + arr[8];
						b = true;
					}
				}
				if (b == true) {
					map.put(arr[2], log);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return map;
	}

	public void sendReportMail(Map<String, String> map, String summary) {
		try {
			Email email = new SimpleEmail();
			email.setHostName("smtp.126.com");
			email.setSmtpPort(465);
			email.setAuthenticator(new DefaultAuthenticator("fortehlw", "hlw2016"));
			email.setSSLOnConnect(true);
			email.setFrom("fortehlw@126.com");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(new Date());
			email.setSubject(date + "接口检查");

			StringBuffer sb = new StringBuffer();
			sb.append(summary + "\n");
			for (Map.Entry<String, String> entry : map.entrySet()) {
				sb.append(entry.getKey() + "\n");
				sb.append(entry.getValue() + "\n");
			}
			email.setMsg(sb.toString());
			email.addTo("365682158@qq.com");
			email.addTo("786610927@qq.com");
			email.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		JmeterReport jr = new JmeterReport();
		String summary = jr.readJmeterFile(args[0]);
		Map<String, String> map = jr.getLogReport(args[0] , args[1]);
		System.out.println("第一参数："+args[0]+"  第二参数："+args[1]);
		if(map.isEmpty()){
			System.out.println("=====================接口检查正常======================");
		}else{
			jr.sendReportMail(map, summary);
			System.out.println("请查收邮件");
		}
	}
}

package coxCharacterBuild;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HtmlIndex {
	private static final String LOGIN_URL = "https://www.cityofheroesrebirth.com/public/login";
	private static final String MANAGE_URL = "https://www.cityofheroesrebirth.com/public/manage";
	private static final String CHAR_PAGE_URL = "https://www.cityofheroesrebirth.com/public/api/character/raw?q=";
	// Swap the two lines below if you want each character to have its own page
	//private static final String LI_CHAR = "  <li title=\"%s\"><a href=\"%s.html\" target=\"charinfo\">%s %s %s (%s) %s</a></li>\n";
	private static final String LI_CHAR = "  <li title=\"%s\"><a href=\"%s.html\">%s %s %s (%s) %s</a></li>\n";
	private static final String JSON_CHAR = " { name: '%s', level: %s, origin: '%s', alignment: '%s', at: '%s', url: '%s', title: '%s', lastActive: '%s' },\n";

	private static class CharacterFile {
		private String characterName;
		private String fileName;
		private String pathFileName;
		
		CharacterFile(String characterName, String path) {
			this.characterName = characterName;
			this.fileName = parseFileName(characterName) + ".html";
			this.pathFileName = path + "/" + this.fileName;
		}
		public String getCharacterName() {
			return characterName;
		}
		public String getFileName() {
			return fileName;
		}
		public String getPathFileName() {
			return pathFileName;
		}
		public String toString() {
			return String.format("%s %s %s", getCharacterName(), getFileName(), getPathFileName());
		}
	}

	public static void main(String[] args) throws IOException {
		Properties p = getConfig();
		Map<String, CharacterProfile> treeMap = new TreeMap<String, CharacterProfile>();
		System.out.println("START");
		System.out.println(String.format("Writing to %s", p.getProperty("character_dir")));
		long num_chars = 0;
		long total_levels = 0;
		for (RebirthAccount account : getAccounts(p)) {
	        CookieStore cookieStore = new BasicCookieStore();
	        HttpClientContext  localContext = HttpClientContext.create();
	        localContext.setCookieStore(cookieStore);
	        
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(LOGIN_URL);
			request.addHeader("User-Agent", "Apache HTTPClient");
			HttpResponse response = client.execute(request, localContext);
			HttpEntity entity1 = response.getEntity();
			String content = EntityUtils.toString(entity1);
			List<NameValuePair> parms = parseLogin(content, null);
			parms.add(new BasicNameValuePair("username", account.getUsername()));
			parms.add(new BasicNameValuePair("password", account.getPassword()));
			parms.add(new BasicNameValuePair("nextpage", "login"));
			parms.add(new BasicNameValuePair("submit", "login"));
			loginAttempt(LOGIN_URL, parms,  localContext);
			request = new HttpGet(MANAGE_URL);
			request.addHeader("User-Agent", "Apache HTTPClient");
			response = client.execute(request, localContext);
			entity1 = response.getEntity();
			content = EntityUtils.toString(entity1);
			List<String> charIds = getCharacterIds(content);
			for(String charid : charIds) {
				String apiUrl = CHAR_PAGE_URL + charid;
				client = HttpClientBuilder.create().build();
				request = new HttpGet(apiUrl);

				request.addHeader("User-Agent", "Apache HTTPClient");
				response = client.execute(request);

				HttpEntity entity = response.getEntity();
				String charContent = EntityUtils.toString(entity);
				String name = parseCharName(charContent);
				System.out.println(name);
				String charFileName = parseFileName(name);
				CharacterFile cf = new CharacterFile(name, p.getProperty("character_dir"));
				HtmlBuildInfo hbi = new HtmlBuildInfo();
				CharacterProfile cp = hbi.execute(p.getProperty("character_dir"), charFileName, charContent);
				cp.setFilename(charFileName);
				treeMap.put(name.toLowerCase(), cp);
				num_chars++;
				total_levels = total_levels + Integer.valueOf(cp.getLevel());
			}
		}
		System.out.println("INDEX");
		File file = new File(String.format("%s/index.html", p.getProperty("character_dir")));
		BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
		writer.write(getHeaderHtml(num_chars, total_levels));
        for (Map.Entry<String, CharacterProfile> entry : treeMap.entrySet()) {
        	writer.write(String.format(LI_CHAR,
        			entry.getValue().getTitle(),
        			entry.getValue().getFilename(),
        			entry.getValue().getOriginIcon(),
        			entry.getValue().getAlignmentIcon(),
        			entry.getValue().getArchitypeIcon(),
            		entry.getValue().getLevel(),
            		entry.getValue().getName()));
        }
        writer.write(getFooterHtml(treeMap));
        writer.close();
		System.out.println("END");
		System.out.println(String.format("%s characters and %s levels in total", num_chars, total_levels));
	}

	private static String extractValue(String line) {
		String regex = "value=\\\"(.+)\\\"";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
        	return matcher.group(1);
        }
		return null;
	}

	private static List<String> getCharacterIds(String manageContent) {
		List<String> ids = new ArrayList<String>();
		final String regex = "character/raw\\?q=(.+)\\\"";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(manageContent);
        while (matcher.find()) {
        	ids.add(matcher.group(1));
        }
        return ids;
	}

	private static String parseCharName(String content) {
		final String regex = "^Name \\\"(.+)\\\"";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
        	return matcher.group(1);
        }
		return null;
	}
	private static String parseFileName(String charName) {
		return charName.toLowerCase().replaceAll(" ", "_"); 
	}

	private static List<NameValuePair> parseLogin(String content, List<NameValuePair> parms) {
		if (parms == null) 
			parms = new ArrayList<>();
		Reader inputString = new StringReader(content);
		BufferedReader reader = new BufferedReader(inputString);
		String line;
		try {
			line = reader.readLine();
			while (line != null) {
				if(line.contains("name=\"csrf_name\"")) {
					parms.add(new BasicNameValuePair("csrf_name", extractValue(line)));
				} else if (line.contains("name=\"csrf_value\"")) {
					parms.add(new BasicNameValuePair("csrf_value", extractValue(line)));
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parms;
	}

	private static String loginAttempt(String url, List<NameValuePair> parms, HttpContext localContext) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parms, Consts.UTF_8);

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);

            // Create a custom response handler
            ResponseHandler < String > responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 500) {
                    HttpEntity responseEntity = response.getEntity();
                    return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String responseBody = httpclient.execute(httpPost, responseHandler, localContext);
            return responseBody;
        }
	}

	private static List<RebirthAccount> getAccounts(Properties p) {
		List<RebirthAccount> accounts = new ArrayList<>();
		if (p.getProperty("account.1.username") != null && p.getProperty("account.1.password") != null) {
			accounts.add(new RebirthAccount(p.getProperty("account.1.username"), p.getProperty("account.1.password")));
		}
		if (p.getProperty("account.2.username") != null && p.getProperty("account.2.password") != null) {
			accounts.add(new RebirthAccount(p.getProperty("account.2.username"), p.getProperty("account.2.password")));
		}
		if (p.getProperty("account.3.username") != null && p.getProperty("account.3.password") != null) {
			accounts.add(new RebirthAccount(p.getProperty("account.3.username"), p.getProperty("account.3.password")));
		}
		return accounts;
	}

	private static Properties getConfig() throws IOException {
		FileReader reader = new FileReader("properties.cfg");
		Properties p = new Properties();
		p.load(reader);
		if (p.getProperty("account.1.username") != null &&
				p.getProperty("account.1.password") != null &&
				p.getProperty("character_dir") != null) {
			return p;
		}
		throw new IOException("Invalid Config File: Missing Parameters");
	}
	private static String getHeaderHtml(final long numChars, final long numLevels) {
		return "<!DOCTYPE html>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<link rel=\"stylesheet\" href=\"css\\build.css\" type=\"text/css\" />\n"
				+ "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\">\n"
				+ "<script type=\"text/javascript\" src=\"js\\rebirth.min.js\"></script>\n"
				+ "<title>Character Index</title>"
				+ "</head>\n"
				+ "<body>\n"
				+ String.format("<h1>%s characters and %s levels</h1>\n", numChars, numLevels)
				+ "<div class=\"layout\">\n"
				+ "<ul>\n";	
	}
	private static String getFooterHtml(Map<String, CharacterProfile> treeMap) {
		String charData = "";
        for (Map.Entry<String, CharacterProfile> entry : treeMap.entrySet()) {
        	charData = charData + String.format(JSON_CHAR,
        			entry.getValue().getName(),
        			entry.getValue().getLevel(),
        			entry.getValue().getOrigin(),
        			entry.getValue().getAlignment(),
        			entry.getValue().getArchitype(),
        			entry.getValue().getFilename(),
        			entry.getValue().getTitle(),
        			entry.getValue().getLastActive());
        }
		return "</ul>\n"
				+ "</div>\n"
				+ "<div style=\"text-align: center;\">Sort Options</div>\n"
				+ "<div style=\"text-align: center;\">\n"
				+ "<select id=\"sortOrder\" name=\"sortOrder\" onchange=\"reloadChars()\">\n"
				+ "  <option value=\"name\">Name</option>\n"
				+ "  <option value=\"level\">Level</option>\n"
				+ "  <option value=\"origin\">Origin</option>\n"
				+ "  <option value=\"alignment\">Alignment</option>\n"
				+ "  <option value=\"at\">Archetype</option>\n"
				+ "  <option value=\"lastActive\">Last Active</option>\n"
				+ "</select>\n"
				+ "<select id=\"ascDesc\" name=\"ascDesc\" onchange=\"reloadChars()\">\n"
				+ "  <option value=\"asc\">Ascending</option>\n"
				+ "  <option value=\"desc\">Descending</option>\n"
				+ "</select>\n"
				+ "</div>\n"
				+ "<script type=\"text/javascript\">\n"
				+ "var characters = ["
				+ charData
				+ "];\n"
				+ "!function() {\n"
				+ "  reloadChars();\n"
				+ "}();\n"
				+ "</script>\n"
				+ "</body>\n"
				+ "</html>\n"
				+ "";
	}
}

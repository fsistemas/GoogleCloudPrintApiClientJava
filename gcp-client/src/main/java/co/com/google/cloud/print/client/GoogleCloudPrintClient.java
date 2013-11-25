package co.com.google.cloud.print.client;

import java.util.Map;

import org.apache.http.HttpResponse;

import co.com.google.cloud.print.dto.ControlInputDto;
import co.com.google.cloud.print.dto.GetJobInputDto;
import co.com.google.cloud.print.dto.GetPrinterInputDto;
import co.com.google.cloud.print.dto.RegisterPrinterInputDto;
import co.com.google.cloud.print.dto.SearchPrinterInputDto;
import co.com.google.cloud.print.dto.SubmitJobInputDto;
import co.com.google.cloud.print.dto.UpdatePrinterDto;
import co.com.google.cloud.print.http.MyHttpClient;

public interface GoogleCloudPrintClient {
	public static final String[] COOKIES_KEY = { "SID", "LSID", "HSID", "SSID" };

	public static final String CRLF = "\r\n";

	//The following are used for authentication functions.
	public static final String FOLLOWUP_HOST = "www.google.com/cloudprint";
	public static final String FOLLOWUP_URI = "select%2Fgaiaauth";
	public static final String GAIA_HOST = "www.google.com";
	public static final String LOGIN_URI = "/accounts/ServiceLoginAuth";
	public static final String LOGIN_URL = "https://www.google.com/accounts/ClientLogin";
	public static final String SERVICE = "cloudprint";

	//The following are used for general backend access.
	public static final String CLOUDPRINT_URL = "http://www.google.com/cloudprint";
	public static final String CLOUDPRINT_URL_HTTPS = "https://www.google.com/cloudprint";

	public static final String DEFAULT_PROXY_NAME = "fsistemas-google-cloud-print-proxy-java";

	//CLIENT_NAME should be some string identifier for the client you are writing.
	public static final String DEFAULT_CLIENT_NAME = "fsistemas-google-cloud-print-java";
	public static final int DEFAULT_MAX_RETRY = 5;

	public String getLogin();
	public void setLogin(String login);
	public String getPassword();
	public void setPassword(String password);
	public String getProxyName();
	public void setProxyName(String proxyName);
	public Map<String, String> getTokens();
	public void setTokens(Map<String, String> tokens);
	public void setTokens(String tokensString);

	public boolean isEnableDebug();
	public void setEnableDebug(boolean enableDebug);

	public MyHttpClient getMyHttpClient();
	public void setMyHttpClient(MyHttpClient httpClient);

	public Map<String, String> getGiaLogin() throws Exception;
	public Map<String, String> getTokensFromGoogle() throws Exception;

	public void setMaxRetryIntents(int maxRetryIntents);
	public int getMaxRetryIntents();

	public GoogleCloudPrintResponse handleRequest(String httpMethod, String url, Map<String, String> headers, Map<String, String> fields) throws Exception;
	public GoogleCloudPrintResponse handleResponse(HttpResponse response) throws Exception;

	public GoogleCloudPrintResponse submitJob(SubmitJobInputDto submitJobInputDto) throws Exception;
	public void submitJob(String printerId,String jobType,String jobsrc) throws Exception;
	public GoogleCloudPrintResponse getJobs(GetJobInputDto getJobInputDto) throws Exception;
	public GoogleCloudPrintResponse deleteJob(String jobId) throws Exception;
	public GoogleCloudPrintResponse getPrinter(GetPrinterInputDto getPrinterInputDto) throws Exception;
	public GoogleCloudPrintResponse search(SearchPrinterInputDto searchPrinterInputDto) throws Exception;

	public GoogleCloudPrintResponse share(String printerId,String scope,String role,String skipNotification) throws Exception;
	public GoogleCloudPrintResponse unshare(String printerId,String scope) throws Exception;

	public GoogleCloudPrintResponse control(ControlInputDto controlInputDto) throws Exception;
	public GoogleCloudPrintResponse deletePrinter(String printerId) throws Exception;
	public GoogleCloudPrintResponse fetchNextJob(String printerId) throws Exception;
	public GoogleCloudPrintResponse listPrinters(String proxy,String extra_fields) throws Exception;
	public GoogleCloudPrintResponse registerPrinter(RegisterPrinterInputDto registerPrinterInputDto) throws Exception;
	public GoogleCloudPrintResponse updatePrinter(UpdatePrinterDto updatePrinterDto) throws Exception;
}
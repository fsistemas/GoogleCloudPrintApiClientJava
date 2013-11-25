package co.com.google.cloud.print.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.iharder.Base64;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import co.com.google.cloud.print.GoogleCloudPrintJobType;
import co.com.google.cloud.print.dto.ControlInputDto;
import co.com.google.cloud.print.dto.GetJobInputDto;
import co.com.google.cloud.print.dto.GetPrinterInputDto;
import co.com.google.cloud.print.dto.RegisterPrinterInputDto;
import co.com.google.cloud.print.dto.SearchPrinterInputDto;
import co.com.google.cloud.print.dto.SubmitJobInputDto;
import co.com.google.cloud.print.dto.UpdatePrinterDto;
import co.com.google.cloud.print.http.MyHttpClient;

public class GoogleCloudPrintClientImpl implements GoogleCloudPrintClient {
	private String login;
	private String password;
	private Map<String,String> tokens;
	private boolean enableDebug = true;

	private int maxRetryIntents;

	private String proxyName;
	private String clientName;

	private MyHttpClient myHttpClient;

	/**
	 * https://developers.google.com/cloud-print/docs/appInterfaces?hl=es-#submit
	 * Google Cloud Print API method: /submit
	 */
	@Override
	public GoogleCloudPrintResponse submitJob(SubmitJobInputDto dto) throws Exception {
		String url = String.format("%s/submit",CLOUDPRINT_URL_HTTPS) + "?printerid=" + dto.getPrinterId();

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//fields.put("printerid", dto.getPrinterId());
		fields.put("title", dto.getTitle());

		if(dto.getTicket() != null) {
			fields.put("ticket", dto.getTicket());
		}

		if(dto.getCapabilities() != null) {
			fields.put("capabilities", dto.getCapabilities());	
		}

		//fields.put("content", dto.getContent());

		String fdata = null;

		if(GoogleCloudPrintJobType.URL.equals(dto.getJobType())) {
			fdata = dto.getJobSrc();
		} else if(GoogleCloudPrintJobType.PDF.equals(dto.getJobType())) {
			if(dto.isBase64() != true) {
				fdata = Base64.encodeFromFile(dto.getJobSrc());
			}

			//PDF files are always on base64
			headers.put("contentTransferEncoding", "base64");
			fields.put("contentTransferEncoding", "base64");
		} else if(GoogleCloudPrintJobType.PNG.equals(dto.getJobType())) {
			fdata = FileUtils.readFileToString(new File(dto.getJobSrc()));
		} else if(GoogleCloudPrintJobType.JPEG.equals(dto.getJobType())) {
			fdata = FileUtils.readFileToString(new File(dto.getJobSrc()));
		} else {
			fdata = null;
		}

		String contentType = dto.getContentType();

		if(contentType == null) {
			if(GoogleCloudPrintJobType.PDF.equals(dto.getJobType())) {
				contentType = "application/pdf";
			} else if(GoogleCloudPrintJobType.PNG.equals(dto.getJobType())) {
				contentType = "image/png";
			} else if(GoogleCloudPrintJobType.JPEG.equals(dto.getJobType())) {
				contentType = "image/jpeg";
			} else if(GoogleCloudPrintJobType.URL.equals(dto.getJobType())) {
				contentType = "url";
			} else {
				contentType = "application/xml";
			}
		}

		fields.put("content", fdata);

		if(contentType != null) {
			fields.put("contentType", contentType);
		}

		if(dto.getTag() != null) {
			fields.put("tag", dto.getTag());
		}

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;
	}
	
	/**
	 * Submit a job to printerid with content of dataUrl.
	 * @param printerId: string, the printer id to submit the job to.
	 * @param jobType: string, must match the dictionary keys in content and content_type.
	 * @param jobsrc: string, points to source for job. Could be a pathname or id string.
	 * @return <code>boolean</code> True = submitted, False = errors.
	 */
	public void submitJob(String printerId,String jobType,String jobsrc) throws Exception {
		String fdata = null;

		boolean isBase64 = false;
		boolean isFile = true;

		if(GoogleCloudPrintJobType.PDF.equals(jobType)) {
			if(isBase64 != true) {
				if(isFile == true) {
					fdata = Base64.encodeFromFile(jobsrc);
				} else {
					fdata = Base64.encodeBytes(jobsrc.getBytes());
				}
			}
		} else if(GoogleCloudPrintJobType.PNG.equals(jobType)) {
			if(isFile == true) {
				fdata = FileUtils.readFileToString(new File(jobsrc));
			} else {
				fdata = jobsrc;
			}
		} else if(GoogleCloudPrintJobType.JPEG.equals(jobType)) {
			if(isFile == true) {
				fdata = FileUtils.readFileToString(new File(jobsrc));
			} else {
				fdata = jobsrc;
			}
		} else {
			fdata = null;
		}

		/*
		The following dictionaries expect a certain kind of data in jobsrc, depending on jobtype:
	    jobtype               jobsrc
	    //===============================================
	    pdf                     pathname to the pdf file
	    png                     pathname to the png file
	    jpeg                    pathname to the jpeg file
	    url						Public URL to file
	    //===============================================
	     */

		String file_type = null;

		if(GoogleCloudPrintJobType.PDF.equals(jobType)) {
			file_type = "application/pdf";
		} else if(GoogleCloudPrintJobType.PNG.equals(jobType)) {
			file_type = "image/png";
		} else if(GoogleCloudPrintJobType.JPEG.equals(jobType)) {
			file_type = "image/jpeg";
		} else if(GoogleCloudPrintJobType.URL.equals(jobType)) {
			file_type = "url";
		} else {
			file_type = "application/xml";
		}

		String url = String.format("%s/submit",CLOUDPRINT_URL_HTTPS);
		//+ "?printerid="+printerId;

		System.err.println("URL: " + url);
		
		Map<String,String> fields = new HashMap<String, String>();
		Map<String,String> headers = new HashMap<String, String>();

		fields.put("capabilities", "{\"capabilities\":[]}");

		if(GoogleCloudPrintJobType.PDF.equals(jobType) || GoogleCloudPrintJobType.PNG.equals(jobType) || GoogleCloudPrintJobType.JPEG.equals(jobType)) {
			headers.put("content", fdata);
		}

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));
		//headers.put("X-CloudPrint-Proxy", this.getProxyName());

		headers.put("printerid", printerId);
		headers.put("title", "Please set a tittle");

		if(GoogleCloudPrintJobType.PDF.equals(jobType)) {
			headers.put("content", fdata);
		} else {
			headers.put("content", jobsrc);
		}

		if(GoogleCloudPrintJobType.PDF.equals(jobType) == true) {
			headers.put("contentTransferEncoding", "base64");
		}

		headers.put("contentType", file_type);

		//HttpResponse response = this.getMyHttpClient().postURL(url, headers, fields);

		HttpResponse response = this.getURL(url, this.getTokens(), headers,fields, false, false);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuilder sb = new StringBuilder();
		String line = "";
		while ((line = rd.readLine()) != null) {
			sb.append(line).append(CRLF);
		}

		String responseBody = sb.toString();

		System.err.println("responseBody: " + responseBody);

		/*
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = mapper.getJsonFactory();
		JsonParser jp = factory.createJsonParser(responseBody);
		JsonNode jsonResult = mapper.readTree(jp);

		String message = null;
		boolean success = false;
		String jobId = null;
		String errorCode = null;

		JsonNode successNode = jsonResult.get("success");

		if(successNode != null && successNode.asText() != null) {
			JsonNode jobNode = jsonResult.get("job");

			if(successNode.asText().toLowerCase().equals("true")) {
				success = true;
				if(jsonResult.get("message") != null) {
					message = jsonResult.get("message").asText();
				}

				if(jobNode != null && jobNode.get("id") != null) {
					jobId = jobNode.get("id").asText();
				}
			} else if(successNode.asText().toLowerCase().equals("false")) {
				success = false;
				if(jsonResult.get("message") != null) {
					message = jsonResult.get("message").asText();
				}

				if(jobNode != null && jobNode.get("errorCode") != null) {
					errorCode = jobNode.get("errorCode").asText();
				}
			}
		} else {
			message = "Error to submit Job - Response body: " + responseBody;
			success = false;
		}
		

		System.err.println("Google cloud response: \n" + "success: " + success + "\n" + "message: " + message + "\n" + "jobId: " + jobId + "errorCode: " + errorCode);
		*/

		//return success;
	}
	
	/**
	 * Get URL, with GET or POST depending data, adds Authorization header.
	 * @param url: Url to access.
	 * @param tokens: Map of authentication tokens for specific user.
	 * @param data: If a POST request, data to be sent with the request.
	 * @param cookies: True = send authentication tokens in cookie headers.
	 * @param anonymous: True = do not send login credentials.
	 * @throws Exception
	 * @return response to the HTTP request.
	 */
	public HttpResponse getURL(String url,Map<String,String> tokens,Map<String,String> headers,Map<String,String> fields,boolean cookies,boolean anonymous) throws Exception {
		//We still need to get the Auth token.
		HttpClient client = null;
		HttpPost post = null;

		HttpResponse response = null;

		try {
			client = new DefaultHttpClient();
			post = new HttpPost(url);

			if(anonymous == false) {
				if(cookies == true) {
					post.addHeader("Cookie", String.format("SID=%s; HSID=%s; SSID=%s",tokens.get("SID"), tokens.get("HSID"), tokens.get("SSID")));
				} else {
					post.addHeader("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));
				}
			}

			post.addHeader("X-CloudPrint-Proxy", "KS-CLOUD-PROXY");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);

			//Aditional headers
			/**/
			if(headers != null) {
				Set<Entry<String, String>> entrySetHeaders = headers.entrySet();

				Iterator<Entry<String, String>> itHeaders = entrySetHeaders.iterator();

				while(itHeaders.hasNext()) {
					Entry<String, String> currentHeader = itHeaders.next();

					//post.addHeader(currentHeader.getKey(), currentHeader.getValue());

					nameValuePairs.add(new BasicNameValuePair(currentHeader.getKey(), currentHeader.getValue()));
				}
			}

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			if(fields != null) {
				Set<Entry<String, String>> entrySetFields = fields.entrySet();
				Iterator<Entry<String, String>> itFields = entrySetFields.iterator();

				while(itFields.hasNext()) {
					Entry<String, String> currentField = itFields.next();

					nameValuePairs.add(new BasicNameValuePair(currentField.getKey(), currentField.getValue()));
				}
			}

			response = client.execute(post);
		} catch(Exception e) {
			e.printStackTrace();

			throw e;
		}

		return response;
	}

	/**
	 * https://developers.google.com/cloud-print/docs/appInterfaces?hl=es-#jobs
	 * Google Cloud Print API method: /jobs
	 */
	@Override
	public GoogleCloudPrintResponse getJobs(GetJobInputDto dto) throws Exception {
		String url = String.format("%s/jobs",CLOUDPRINT_URL_HTTPS);

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));

		if(dto.getPrinterId() != null) {
			fields.put("printerid", dto.getPrinterId());
		}
		
		if(dto.getOwner() != null) {
			fields.put("owner", dto.getOwner());
		}

		if(dto.getStatus() != null) {
			fields.put("status", dto.getStatus());
		}

		if(dto.getQ() != null) {
			fields.put("q", dto.getQ());
		}

		if(dto.getOffset() != null) {
			fields.put("offset", dto.getOffset());
		}

		if(dto.getLimit() != null) {
			fields.put("limit", dto.getLimit());
		}

		if(dto.getSortOrder() != null) {
			fields.put("sortorder", dto.getSortOrder());
		}

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;
	}

	/**
	 * https://developers.google.com/cloud-print/docs/appInterfaces?hl=es-#deletejob
	 * Google Cloud Print API method: /deletejob
	 */
	@Override
	public GoogleCloudPrintResponse deleteJob(String jobId) throws Exception {
		String url = String.format("%s/deletejob?jobid=%s",CLOUDPRINT_URL_HTTPS,jobId);

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));

		//fields.put("jobid", jobId);

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;
	}

	/**
	 * https://developers.google.com/cloud-print/docs/appInterfaces?hl=es-#printer
	 * Google Cloud Print API method: /printer
	 */
	@Override
	public GoogleCloudPrintResponse getPrinter(GetPrinterInputDto dto) throws Exception {
		String url = String.format("%s/printer?printerid=%s",CLOUDPRINT_URL_HTTPS,dto.getPrinterId());

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));

		//fields.put("printerid", printerId);

		if(dto.getUseCdd() != null) {
			fields.put("use_cdd", dto.getUseCdd());
		}

		if(dto.getExtraFields() != null) {
			fields.put("extra_fields", dto.getExtraFields());
		}

		if(dto.getPrinterConnectionStatus() != null) {
			fields.put("printer_connection_status", dto.getPrinterConnectionStatus());
		}

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;
	}

	/**
	 * https://developers.google.com/cloud-print/docs/appInterfaces?hl=es-#search
	 * Google Cloud Print API method: /search
	 */
	@Override
	public GoogleCloudPrintResponse search(SearchPrinterInputDto dto) throws Exception {
		String url = String.format("%s/search",CLOUDPRINT_URL_HTTPS);

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));

		if(dto.getQ() != null) {
			fields.put("q", dto.getQ());
		}

		if(dto.getType() != null) {
			fields.put("type", dto.getType());
		}

		if(dto.getConnectionStatus() != null) {
			fields.put("connection_status", dto.getConnectionStatus());
		}

		if(dto.getUseCdd() != null) {
			fields.put("use_cdd", dto.getUseCdd());
		}

		if(dto.getExtraFields() != null) {
			fields.put("extra_fields", dto.getExtraFields());
		}

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;
	}

	/**
	 * https://developers.google.com/cloud-print/docs/shareApi?hl=es-#share
	 * Google Cloud Print API method: /share
	 */
	@Override
	public GoogleCloudPrintResponse share(String printerId, String scope,String role, String skipNotification) throws Exception {
		String url = String.format("%s/share",CLOUDPRINT_URL_HTTPS);

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));

		fields.put("printerid", printerId);
		fields.put("scope", scope);
		fields.put("role", role);

		if(skipNotification  != null) {
			fields.put("skip_notification", skipNotification);
		}

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;
	}

	/**
	 * https://developers.google.com/cloud-print/docs/shareApi?hl=es-#unshare
	 * Google Cloud Print API method: /unshare
	 */
	@Override
	public GoogleCloudPrintResponse unshare(String printerId, String scope) throws Exception {
		String url = String.format("%s/unshare",CLOUDPRINT_URL_HTTPS);

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));

		fields.put("printerid", printerId);
		fields.put("scope", scope);

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;
	}

	/**
	 * https://developers.google.com/cloud-print/docs/proxyinterfaces?hl=es-#control
	 * Google Cloud Print API method: /control
	 */
	public GoogleCloudPrintResponse control(ControlInputDto dto) throws Exception {
		String url = String.format("%s/control",CLOUDPRINT_URL_HTTPS);

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));

		fields.put("jobid", dto.getJobId());

		if(dto.getSemanticStateDiff() != null) {
			fields.put("semantic_state_diff", dto.getSemanticStateDiff());
		}

		if(dto.getStatus() != null) {
			fields.put("status", dto.getStatus());
		}

		if(dto.getCode() != null) {
			fields.put("code", dto.getCode());
		}

		if(dto.getMessage() != null) {
			fields.put("message", dto.getMessage());
		}

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;		
	}

	/**
	 * https://developers.google.com/cloud-print/docs/proxyinterfaces?hl=es-#delete
	 * Google Cloud Print API method: /delete
	 */
	@Override
	public GoogleCloudPrintResponse deletePrinter(String printerId) throws Exception {
		String url = String.format("%s/delete",CLOUDPRINT_URL_HTTPS);

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));

		fields.put("printerid", printerId);

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;
	}

	/**
	 * https://developers.google.com/cloud-print/docs/proxyinterfaces?hl=es-#fetch
	 * Google Cloud Print API method: /fetch
	 */
	@Override
	public GoogleCloudPrintResponse fetchNextJob(String printerId) throws Exception {
		String url = String.format("%s/fetch",CLOUDPRINT_URL_HTTPS);

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));

		fields.put("printerid", printerId);

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;
	}

	/**
	 * https://developers.google.com/cloud-print/docs/proxyinterfaces?hl=es-#list
	 * Google Cloud Print API method: /list
	 */
	@Override
	public GoogleCloudPrintResponse listPrinters(String proxy,String extra_fields) throws Exception {
		String url = String.format("%s/list",CLOUDPRINT_URL_HTTPS);

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));

		fields.put("proxy", proxy);

		if(extra_fields != null) {
			fields.put("extra_fields", extra_fields);
		}

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;		
	}

	/**
	 * https://developers.google.com/cloud-print/docs/proxyinterfaces?hl=es-#register
	 * Google Cloud Print API method: /register
	 */
	@Override
	public GoogleCloudPrintResponse registerPrinter(RegisterPrinterInputDto dto) throws Exception {
		String url = String.format("%s/list",CLOUDPRINT_URL_HTTPS);

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));

		fields.put("printer", dto.getPrinter());

		if(dto.getDefaultDisplayName() != null) {
			fields.put("default_display_name", dto.getDefaultDisplayName());
		}

		fields.put("proxy", dto.getProxy());

		if(dto.getDescription() != null) {
			fields.put("description", dto.getDescription());
		}

		fields.put("uuid", dto.getUuid());
		fields.put("manufacturer", dto.getManufacturer());
		fields.put("model", dto.getManufacturer());
		fields.put("gcp_version", dto.getGcpVersion());
		fields.put("setup_url", dto.getSetupUrl());
		fields.put("support_url", dto.getSupportUrl());
		fields.put("update_url", dto.getUpdateUrl());
		fields.put("firmware", dto.getFirmware());

		if(dto.getLocalSettings() != null) {
			fields.put("local_settings", dto.getLocalSettings());
		}

		fields.put("semantic_state", dto.getSemanticState());
		fields.put("use_cdd", dto.getUseCdd());
		fields.put("capabilities", dto.getCapabilities());
		
		if(dto.getDefaults() != null) {
			fields.put("defaults", dto.getDefaults());
		}

		if(dto.getCapsHash() != null) {
			fields.put("capsHash", dto.getCapsHash());
		}

		if(dto.getTag() != null) {
			fields.put("tag", dto.getTag());
		}

		if(dto.getData() != null) {
			fields.put("data", dto.getData());
		}

		if(dto.getContentTypes() != null) {
			fields.put("content_types", dto.getContentTypes());
		}

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;
	}

	/**
	 * https://developers.google.com/cloud-print/docs/proxyinterfaces?hl=es-#update
	 * Google Cloud Print API method: /update
	 */
	@Override
	public GoogleCloudPrintResponse updatePrinter(UpdatePrinterDto dto) throws Exception {
		String url = String.format("%s/update",CLOUDPRINT_URL_HTTPS);

		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> fields = new HashMap<String, String>();

		//headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));

		fields.put("printerid", dto.getPrinterId());

		if(dto.getPrinter() != null) {
			fields.put("printer", dto.getPrinter());
		}

		if(dto.getDefaultDisplayName() != null) {
			fields.put("default_display_name", dto.getDefaultDisplayName());
		}

		if(dto.getProxy() != null) {
			fields.put("proxy", dto.getProxy());
		}

		if(dto.getDescription() != null) {
			fields.put("description", dto.getDescription());
		}

		if(dto.getUuid() != null) {
			fields.put("uuid", dto.getUuid());
		}

		if(dto.getManufacturer() != null) {
			fields.put("manufacturer", dto.getManufacturer());
		}

		if(dto.getManufacturer() != null) {
			fields.put("model", dto.getManufacturer());
		}

		if(dto.getGcpVersion() != null) {
			fields.put("gcp_version", dto.getGcpVersion());
		}

		if(dto.getSetupUrl() != null) {
			fields.put("setup_url", dto.getSetupUrl());
		}

		if(dto.getSupportUrl() != null) {
			fields.put("support_url", dto.getSupportUrl());
		}

		if(dto.getUpdateUrl() != null) {
			fields.put("update_url", dto.getUpdateUrl());
		}

		if(dto.getFirmware() != null) {
			fields.put("firmware", dto.getFirmware());
		}

		if(dto.getLocalSettings() != null) {
			fields.put("local_settings", dto.getLocalSettings());
		}

		if(dto.getSemanticState() != null) {
			fields.put("semantic_state", dto.getSemanticState());
		}
		
		if(dto.getSemanticStateDiff() != null) {
			fields.put("semantic_state_diff", dto.getSemanticStateDiff());
		}

		if(dto.getUseCdd() != null) {
			fields.put("use_cdd", dto.getUseCdd());
		}

		if(dto.getCapabilities() != null) {
			fields.put("capabilities", dto.getCapabilities());
		}

		if(dto.getDefaults() != null) {
			fields.put("defaults", dto.getDefaults());
		}

		if(dto.getCapsHash() != null) {
			fields.put("capsHash", dto.getCapsHash());
		}

		if(dto.getTag() != null) {
			fields.put("tag", dto.getTag());
		}

		if(dto.getRemoveTag() != null) {
			fields.put("remove_tag", dto.getRemoveTag());
		}

		if(dto.getData() != null) {
			fields.put("data", dto.getData());
		}

		if(dto.getRemoveData() != null) {
			fields.put("remove_data", dto.getRemoveData());
		}

		if(dto.getContentTypes() != null) {
			fields.put("content_types", dto.getContentTypes());
		}

		GoogleCloudPrintResponse response = this.handleRequest("POST", url, headers, fields);

		return response;
	}

	@Override
	public GoogleCloudPrintResponse handleRequest(String httpMethod, String url, Map<String, String> headers, Map<String, String> fields) throws Exception {
		httpMethod = httpMethod.toUpperCase();

		GoogleCloudPrintResponse googleCloudPrintResponse = null;
		HttpResponse httpResponse = null;

		if (!httpMethod.equals("GET") && !httpMethod.equals("POST") && !httpMethod.equals("PUT") && !httpMethod.equals("DELETE")) {
			throw new Exception("Invalid HTTP_METHOD");
		}

		headers.put("Authorization", String.format("GoogleLogin auth=%s",tokens.get("Auth")));
		headers.put("X-CloudPrint-Proxy", "KS-CLOUD-PROXY");

		int maxRetry = this.getMaxRetryIntents();

		for(int i=0; i < maxRetry; i++) {
			try {
				if (httpMethod.equals("GET")) {
					httpResponse = this.getMyHttpClient().getURL(url,headers, fields);
				} else if (httpMethod.equals("POST")) {
					httpResponse = this.getMyHttpClient().postURL(url,headers, fields);
				} else if (httpMethod.equals("PUT")) {
					httpResponse = this.getMyHttpClient().putURL(url,headers, fields);
				} else if (httpMethod.equals("DELETE")) {
					httpResponse = this.getMyHttpClient().deleteURL(url,headers, fields);
				}

				googleCloudPrintResponse = this.handleResponse(httpResponse);
			} catch(Exception e) {
				//If exceed max retry intents throw current exception
				if(i >= maxRetry ) {
					throw e;
				}

				//Wait 60 seconds before next retry
				try {
					Thread.sleep(60000);
				} catch(InterruptedException ex) {}
			}
		}

		return googleCloudPrintResponse;
	}

	public void writeStringToFile(String path,String content) throws IOException {
        FileWriter fileWriter = null;

        try {
            File newTextFile = new File(path);
            fileWriter = new FileWriter(newTextFile);
            fileWriter.write(content);
            fileWriter.close();
        } finally {
        	if(fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException ex) {
                	ex.printStackTrace();
                }
        	}
        }
	}

	@Override
	public GoogleCloudPrintResponse handleResponse(HttpResponse response) throws Exception {
		GoogleCloudPrintResponse userResponse = new GoogleCloudPrintResponse();

        if(response != null) {
                int httpStatusCode = response.getStatusLine().getStatusCode();
                String responseBody = null;

                userResponse.setHttpStatus(httpStatusCode);
                userResponse.setMessage(response.getStatusLine().getReasonPhrase());

                responseBody = this.getMyHttpClient().getResponseBodyAsString(response);

                if(isEnableDebug()) {
                	writeStringToFile("C:/Users/Usuario/Desktop/gcp/response.txt", responseBody);
                    //System.err.println("Response: " + responseBody);
                }

                ObjectMapper mapper = new ObjectMapper();
                JsonFactory factory = mapper.getJsonFactory();
                JsonParser jp = factory.createJsonParser(responseBody);
                JsonNode userJsonNode = mapper.readTree(jp);
                userResponse.setData(userJsonNode);
        } else {
                userResponse.setMessage("Error response is null");
        }

        return userResponse;
	}

	/**
	 * Get Google getGiaLogin Authentication tokens, it is for get tokens
	 */
	@Override
	public Map<String, String> getGiaLogin() throws Exception {
		Map<String, String> tokens = new HashMap<String, String>();

		String email = this.getLogin();

		String galxCookie = Base64.encodeBytes((email + System.currentTimeMillis()).getBytes());

		email = email.replace("+", "%2B");

		String url = "https://" + GAIA_HOST + ":443" + LOGIN_URI + "?" + String.format("ltmpl=login&fpui=1&rm=hide&hl=en-US&alwf=true&continue=https%%3A%%2F%%2F%s%%2F%s&followup=https%%3A%%2F%%2F%s%%2F%s&service=%s&Email=%s&Passwd=%s&GALX=%s",FOLLOWUP_HOST,FOLLOWUP_URI, FOLLOWUP_HOST, FOLLOWUP_URI, SERVICE, email, password, galxCookie);

		HttpResponse response = this.getMyHttpClient().postURL(url, null, null);

		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Header[] headers = response.getAllHeaders();

			if(headers != null && headers.length > 0) {
				int length = headers.length;

				for(int index=0; index < length; index++) {
					Header currentHeader = headers[index];

					if(currentHeader.getName() != null && currentHeader.getName().toLowerCase().equals("set-cookie")) {
						for(String cookieKey: COOKIES_KEY) {
							if ( currentHeader.getValue() != null && currentHeader.getName().trim().startsWith(cookieKey) ) {
								tokens.put(cookieKey,this.getMyHttpClient().getCookie(cookieKey,currentHeader.getValue()));
							}
						}
					}
				}
			}
		} else {
			//System.out.println("Request code " + response.getStatusLine().getStatusCode());

			throw new Exception(this.getMyHttpClient().getResponseBodyAsString(response));
		}

		return tokens;
	}

	/**
	 * Get Google Authentication token
	 */
	@Override
	public Map<String, String> getTokensFromGoogle() throws Exception {
		Map<String, String> tokens = new HashMap<String, String>();

		Map<String, String> fields = new HashMap<String, String>();

		fields.put("accountType", "GOOGLE");
		fields.put("Email", this.getLogin());
		fields.put("Passwd", this.getPassword());
		fields.put("service", SERVICE);
		fields.put("source", this.getClientName());

		HttpResponse response = this.getMyHttpClient().postURL(LOGIN_URL, null, fields);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		String line = "";
		while ((line = rd.readLine()) != null) {
			if(line.trim().startsWith("Auth=")) {
				tokens.put("Auth", line.trim().replace("Auth=", ""));
			}
		}

		return tokens;
	}

	@Override
	public MyHttpClient getMyHttpClient() {
		return myHttpClient;
	}

	@Override
	public void setMyHttpClient(MyHttpClient myHttpClient) {
		this.myHttpClient = myHttpClient;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getProxyName() {
		if(proxyName == null) {
			return DEFAULT_PROXY_NAME;
		}
		return proxyName;
	}

	@Override
	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

	@Override
	public Map<String, String> getTokens() {
		return tokens;
	}

	@Override
	public void setTokens(Map<String, String> tokens) {
		this.tokens = tokens;
	}

	@Override
	public void setTokens(String tokensString) {
		if(tokens == null) {
			tokens = new HashMap<String, String>();
		}

		if(tokensString != null) {
			String[] tokensArray = tokensString.split(CRLF);

			if(tokensArray != null && tokensArray.length > 0) {				
				int tokensSize = tokensArray.length;

				for(int j = 0; j < tokensSize; j++) {
					String currentToken = tokensArray[j];

					if(currentToken != null && currentToken.length() > 0) {
						String[] currentTokenArray = currentToken.split("=");

						if(currentTokenArray.length == 1) {
							tokens.put(currentTokenArray[0], null);
						} else if(currentTokenArray.length == 2) {
							tokens.put(currentTokenArray[0], currentTokenArray[1]);
						}
					}
				}
			}
		}
	}

	@Override
	public String getLogin() {
		return login;
	}

	@Override
	public void setLogin(String login) {
		this.login = login;
	}

	public boolean isEnableDebug() {
		return enableDebug;
	}

	public void setEnableDebug(boolean enableDebug) {
		this.enableDebug = enableDebug;
	}

	public String getClientName() {
		if(clientName == null) {
			return DEFAULT_CLIENT_NAME;
		}

		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public int getMaxRetryIntents() {
		if(maxRetryIntents <= 0) {
			maxRetryIntents = DEFAULT_MAX_RETRY;
		}

		return maxRetryIntents;
	}

	public void setMaxRetryIntents(int maxRetryIntents) {
		this.maxRetryIntents = maxRetryIntents;
	}
}
package co.com.google.cloud.print.dto;

public class SubmitJobInputDto {
	private String printerId;
	private String title;
	private String ticket;

	@Deprecated
	private String capabilities;
	
	public boolean isBase64;

	private String jobSrc;
	private String contentType;
	private String jobType;
	private String tag;

	public String getPrinterId() {
		return printerId;
	}
	public void setPrinterId(String printerId) {
		this.printerId = printerId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	/**
	 * Deprecated, please use getTicket
	 * @return
	 */
	@Deprecated
	public String getCapabilities() {
		return capabilities;
	}

	/**
	 * Deprecated, please use setTicket
	 * @return
	 */
	@Deprecated
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	public String getJobSrc() {
		return jobSrc;
	}
	public void setJobSrc(String jobSrc) {
		this.jobSrc = jobSrc;
	}
	public boolean isBase64() {
		return isBase64;
	}
	public void setBase64(boolean isBase64) {
		this.isBase64 = isBase64;
	}
}
package co.com.google.cloud.print.dto;

public class ControlInputDto {
	private String jobId;
	private String semanticStateDiff;

	@Deprecated
	private String status;

	@Deprecated
	private String code;

	@Deprecated
	private String message;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getSemanticStateDiff() {
		return semanticStateDiff;
	}

	public void setSemanticStateDiff(String semanticStateDiff) {
		this.semanticStateDiff = semanticStateDiff;
	}

	@Deprecated
	public String getStatus() {
		return status;
	}

	@Deprecated
	public void setStatus(String status) {
		this.status = status;
	}

	@Deprecated
	public String getCode() {
		return code;
	}

	@Deprecated
	public void setCode(String code) {
		this.code = code;
	}

	@Deprecated
	public String getMessage() {
		return message;
	}

	@Deprecated
	public void setMessage(String message) {
		this.message = message;
	}
}
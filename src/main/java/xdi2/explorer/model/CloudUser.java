package xdi2.explorer.model;

import java.io.Serializable;

import xdi2.client.XDIClient;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.features.linkcontracts.RootLinkContract;
import xdi2.core.xri3.CloudNumber;
import xdi2.messaging.Message;

public class CloudUser implements Serializable {
	private static final long serialVersionUID = -3527442227194320885L;
	
	private String cloudName;
	private String cloudNumber;
	private String xdiEndpointUri;
	private String secretToken;	

	public CloudUser(String cloudName, CloudNumber cloudNumber, String xdiEndpointUri, String secretToken) {
		super();
		this.cloudName = cloudName;
		this.cloudNumber = cloudNumber.toString();
		this.xdiEndpointUri = xdiEndpointUri;
		this.secretToken = secretToken;
	}

	public Message prepareMessageToCloud(Message message) {
		message.setToPeerRootXri(getCloudNumber().getPeerRootXri());
		message.setLinkContractXri(RootLinkContract.createRootLinkContractXri(getCloudNumber().getXri()));
		message.setSecretToken(this.secretToken);

		return message;
	}
	
	public XDIClient getXdiClient() {
		return new XDIHttpClient(this.xdiEndpointUri);
	}

	public CloudNumber getCloudNumber() {
		return CloudNumber.create(this.cloudNumber);
	}
	public void setCloudNumber(CloudNumber cloudNumber) {
		this.cloudNumber = cloudNumber.toString();
	}

	
	public String getCloudName() {
		return cloudName;
	}
	public void setCloudName(String cloudName) {
		this.cloudName = cloudName;
	}
	public String getSecretToken() {
		return secretToken;
	}
	public void setSecretToken(String secretToken) {
		this.secretToken = secretToken;
	}
	public String getXdiEndpointUri() {
		return xdiEndpointUri;
	}
	public void setXdiEndpointUri(String xdiEndpointUri) {
		this.xdiEndpointUri = xdiEndpointUri;
	}



}

package xdi2.explorer.service.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.ssl.XDI2X509TrustManager;
import xdi2.core.features.linkcontracts.RootLinkContract;
import xdi2.core.xri3.CloudNumber;
import xdi2.core.xri3.XDI3Segment;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.explorer.model.CloudUser;
import xdi2.messaging.Message;
import xdi2.messaging.MessageCollection;
import xdi2.messaging.MessageEnvelope;

@Component("xdiAuthenticationProvider")
public class XdiAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String cloudName = authentication.getName();
		String secretToken = (String) authentication.getCredentials();

		if (! (cloudName.startsWith("=") || cloudName.startsWith("*") || cloudName.startsWith("+")) ) {
			cloudName = "=" + cloudName;
		}
		
		// cloud name discovery
		XDI2X509TrustManager.enable();

		XDIDiscoveryResult result = null;
		try {
			result = XDIDiscoveryClient.DEFAULT_DISCOVERY_CLIENT.discoverFromRegistry(XDI3Segment.create(cloudName), null);
		} catch (Xdi2ClientException e1) {
			throw new UsernameNotFoundException(e1.getMessage());
		}
		if (result == null || result.getCloudNumber() == null) {
			throw new UsernameNotFoundException("Cloud " + cloudName + " not found.");
		}

		CloudNumber cloudNumber = result.getCloudNumber();
		String xdiEndpointUri = result.getXdiEndpointUri();

		// authentication on personal cloud
		CloudUser cloudUser = new CloudUser(cloudName, cloudNumber, xdiEndpointUri, secretToken);

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		MessageCollection messageCollection = messageEnvelope.getMessageCollection(cloudUser.getCloudNumber().getXri(), true);
		Message message = messageCollection.createMessage();
		message = cloudUser.prepareMessageToCloud(message);
		message.createGetOperation(RootLinkContract.createRootLinkContractXri(cloudUser.getCloudNumber().getXri()));
	
		try {
			cloudUser.getXdiClient().send(messageEnvelope, null);
		} catch (Xdi2ClientException e) {
			throw new BadCredentialsException(e.getMessage());
		}
		
		// what can we do here?
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		//		SimpleGrantedAuthority role = new SimpleGrantedAuthority("USER_ROLE");
		//		authorities.add(role);

		return new UsernamePasswordAuthenticationToken(cloudUser, secretToken, authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

}

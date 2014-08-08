package xdi2.explorer.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/")
public class TestController {

	
	@RequestMapping(method = RequestMethod.GET)
	public Authentication getStatus() {
		
		return SecurityContextHolder.getContext().getAuthentication();

	}
}

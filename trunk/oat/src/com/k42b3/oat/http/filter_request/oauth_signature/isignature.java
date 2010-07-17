package com.k42b3.oat.http.filter_request.oauth_signature;

public interface isignature 
{
	public String build(String base_string, String consumer_secret, String token_secret);
}

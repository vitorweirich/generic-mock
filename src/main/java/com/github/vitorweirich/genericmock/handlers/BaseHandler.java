package com.github.vitorweirich.genericmock.handlers;

import com.github.vitorweirich.genericmock.utils.ResourceLoaderUtil;

public abstract class BaseHandler implements RequestHandler {
	
	protected final ResourceLoaderUtil resourceLoaderUtil;

	public BaseHandler(ResourceLoaderUtil resourceLoaderUtil) {
		super();
		this.resourceLoaderUtil = resourceLoaderUtil;
	}

}

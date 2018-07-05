// Copyright © 2016 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.docs;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.Parameters;

import java.net.URI;
import java.util.Collection;

public class OperationRequestAdapter implements OperationRequest {

    protected final OperationRequest delegate;

    public OperationRequestAdapter(OperationRequest delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] getContent() {
        return delegate.getContent();
    }

    @Override
    public String getContentAsString() {
        return delegate.getContentAsString();
    }

    @Override
    public HttpHeaders getHeaders() {
        return delegate.getHeaders();
    }

    @Override
    public HttpMethod getMethod() {
        return delegate.getMethod();
    }

    @Override
    public Parameters getParameters() {
        return delegate.getParameters();
    }

    @Override
    public Collection<OperationRequestPart> getParts() {
        return delegate.getParts();
    }

    @Override
    public URI getUri() {
        return delegate.getUri();
    }
}

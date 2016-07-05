// Copyright © 2016 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.docs;

import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessorAdapter;

import java.net.URI;
import java.net.URISyntaxException;

public class UriHostReplacingOperationPreprocessor extends OperationPreprocessorAdapter {

    public static OperationPreprocessorAdapter replaceUriHost(String scheme, String host, int port) {
        return new UriHostReplacingOperationPreprocessor(scheme, host, port);
    }

    private final String scheme;
    private final String host;
    private final int port;

    private UriHostReplacingOperationPreprocessor(String scheme, String host, int port) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
    }

    @Override
    public OperationRequest preprocess(OperationRequest request) {
        return new OperationRequestAdapter(request) {
            @Override
            public URI getUri() {
                URI uri = delegate.getUri();
                try {
                    return new URI(scheme, uri.getUserInfo(), host, port, uri.getPath(), uri.getQuery(), uri.getFragment());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}

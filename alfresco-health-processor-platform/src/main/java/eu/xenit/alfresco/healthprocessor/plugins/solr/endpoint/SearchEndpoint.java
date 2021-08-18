package eu.xenit.alfresco.healthprocessor.plugins.solr.endpoint;

import java.net.URI;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A search endpoint is configuration data for access to a solr search index
 */
@EqualsAndHashCode
public class SearchEndpoint {

    @Getter
    private final URI baseUri;

    @Getter
    private final String indexedStore;

    public SearchEndpoint(URI baseUri, String indexedStore) {
        if(!baseUri.getPath().endsWith("/")) {
            this.baseUri = URI.create(baseUri.toString()+"/");
        } else {
            this.baseUri = baseUri;
        }
        this.indexedStore = indexedStore;
    }
}

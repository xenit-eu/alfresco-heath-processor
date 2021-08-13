package eu.xenit.alfresco.healthprocessor.plugins.solr.filter;

import java.util.List;
import lombok.AllArgsConstructor;
import org.alfresco.service.cmr.repository.NodeRef.Status;

@AllArgsConstructor
public class AggregateFilter implements SolrNodeFilter{
    private List<SolrNodeFilter> filters;

    @Override
    public boolean isIgnored(Status nodeRefStatus) {
        for (SolrNodeFilter filter : filters) {
            if (filter.isIgnored(nodeRefStatus)) {
                return true;
            }
        }
        return false;
    }
}

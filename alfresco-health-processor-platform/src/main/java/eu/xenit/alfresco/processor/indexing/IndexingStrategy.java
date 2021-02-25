package eu.xenit.alfresco.processor.indexing;

import java.util.Set;
import org.alfresco.service.cmr.repository.NodeRef;

public interface IndexingStrategy {

    void reset();

    Set<NodeRef> getNextNodeIds(final int amount);


}

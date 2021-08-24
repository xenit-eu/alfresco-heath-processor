package eu.xenit.alfresco.healthprocessor.webscripts.console.model;

import eu.xenit.alfresco.healthprocessor.indexing.IndexingConfiguration;
import eu.xenit.alfresco.healthprocessor.indexing.IndexingStrategy.IndexingStrategyKey;
import eu.xenit.alfresco.healthprocessor.indexing.IndexingStrategy;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class IndexingStrategyView {

    String id;

    Map<String, String> state;
    Map<String, String> configuration;

    public IndexingStrategyView(IndexingConfiguration configuration, IndexingStrategy strategy) {
        this(
                configuration.getIndexingStrategy().getKey(),
                strategy.getState(),
                configuration.getConfiguration()
        );
    }
}

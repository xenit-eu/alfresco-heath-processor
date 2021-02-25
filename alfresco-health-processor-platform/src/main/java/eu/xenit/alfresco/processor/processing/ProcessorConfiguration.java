package eu.xenit.alfresco.processor.processing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProcessorConfiguration {

    private final boolean singleTenant;
    private final int nodeBatchSize;
    private final boolean readOnly;
}

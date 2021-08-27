package eu.xenit.alfresco.healthprocessor.fixer.api;

import lombok.Data;

@Data
public abstract class ToggleableHealthFixerPlugin implements HealthFixerPlugin {

    private boolean enabled;

}

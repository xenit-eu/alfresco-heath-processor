package eu.xenit.alfresco.healthprocessor.webscripts.console.model;

import eu.xenit.alfresco.healthprocessor.fixer.api.HealthFixerPlugin;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Value;

@Getter
public class FixersView {

    List<Fixer> fixers;

    public FixersView(List<HealthFixerPlugin> fixers) {
        this.fixers = toViewModel(fixers);
    }

    private static List<Fixer> toViewModel(List<HealthFixerPlugin> fixers) {
        if (fixers == null) {
            return Collections.emptyList();
        }
        return fixers.stream()
                .map(FixersView::toViewModel)
                .collect(Collectors.toList());
    }

    private static Fixer toViewModel(HealthFixerPlugin fixer) {
        return new Fixer(fixer.getClass().getSimpleName(), fixer.isEnabled());
    }

    @Value
    public static class Fixer {

        String name;
        boolean enabled;
    }

}

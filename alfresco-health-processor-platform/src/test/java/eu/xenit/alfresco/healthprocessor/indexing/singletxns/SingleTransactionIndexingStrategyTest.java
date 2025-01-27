package eu.xenit.alfresco.healthprocessor.indexing.singletxns;

import eu.xenit.alfresco.healthprocessor.indexing.IndexingStrategy;
import eu.xenit.alfresco.healthprocessor.indexing.NullCycleProgress;
import eu.xenit.alfresco.healthprocessor.indexing.TrackingComponent;
import eu.xenit.alfresco.healthprocessor.reporter.api.CycleProgress;
import lombok.NonNull;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static eu.xenit.alfresco.healthprocessor.indexing.singletxns.SingleTransactionIndexingStrategy.selectedIndexingStrategyPropertyKey;
import static eu.xenit.alfresco.healthprocessor.plugins.solr.SolrUndersizedTransactionsHealthProcessorPluginTest.generateRandomNodeRefs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class SingleTransactionIndexingStrategyTest {

    private final static long MAX_TXN = 2;

    TrackingComponent trackingComponent;
    SingleTransactionIndexingConfiguration configuration;
    SingleTransactionIndexingStrategy strategy;

    @BeforeEach
    void setUp() {
        trackingComponent = mock(TrackingComponent.class);
        when(trackingComponent.getMaxTxnId()).thenReturn((long) MAX_TXN);
        configuration = new SingleTransactionIndexingConfiguration(0, MAX_TXN);
        strategy = new SingleTransactionIndexingStrategy(trackingComponent, configuration);
    }

    @Test
    void getNextNodeIds() {
        ArrayList<Integer> fetchTransactions = new ArrayList<>(3);
        when(trackingComponent.getNodesForTxnIds(anyList())).thenAnswer(invocation -> {
            fetchTransactions.addAll(invocation.getArgument(0));
            return generateRandomNodeRefs(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 10).stream()
                    .map(nodeRef -> new TrackingComponent.NodeInfo(-1, -1, nodeRef))
                    .collect(Collectors.toSet());
        });

        strategy.onStart();
        for (int i = 0; i < 5; i ++) strategy.getNextNodeIds(Integer.MAX_VALUE);

        verify(trackingComponent, times((int) (MAX_TXN + 1))).getNodesForTxnIds(anyList());
        assertEquals(List.of(0L, 1L, 2L), fetchTransactions);
    }

    @Test
    void getState() {
        assertEquals(Map.of("current-txn-id", "-1"), strategy.getState());
        strategy.onStart();
        assertEquals(Map.of("current-txn-id", "0"), strategy.getState());
        strategy.getNextNodeIds(Integer.MAX_VALUE);
        assertEquals(Map.of("current-txn-id", "1"), strategy.getState());
    }

    @Test
    void getCycleProgress() {
        assertEquals(NullCycleProgress.getInstance(), strategy.getCycleProgress());

        strategy.onStart();
        CycleProgress cycleProgress = strategy.getCycleProgress();
        for (int i = 0; i < 3; i ++) {
            assertEquals( (i + 1.0f) / (MAX_TXN + 1.0f), cycleProgress.getProgress());
            strategy.getNextNodeIds(Integer.MAX_VALUE);
        }
    }

    @Test
    void testStartAnnouncements() {
        testListener(SingleTransactionIndexingStrategy::listenToIndexerStart, strategy::onStart);
    }

    @Test
    void testStopAnnouncements() {
        testListener(SingleTransactionIndexingStrategy::listenToIndexerStop, strategy::onStop);
    }

    private void testListener(@NonNull Consumer<Runnable> listenerRegistrationConsumer, @NonNull Runnable annoucementRunnable) {
        AtomicInteger counter = new AtomicInteger(0);
        Runnable listener = counter::incrementAndGet;
        listenerRegistrationConsumer.accept(listener);

        annoucementRunnable.run();
        assertEquals(1, counter.get());
    }

    @Test
    void isSelectedIndexingStrategy() {
        Properties properties = new Properties();
        for (IndexingStrategy.IndexingStrategyKey key : IndexingStrategy.IndexingStrategyKey.values()) {
            properties.setProperty(selectedIndexingStrategyPropertyKey, key.getKey());
            assertEquals(key.equals(IndexingStrategy.IndexingStrategyKey.SINGLE_TXNS), SingleTransactionIndexingStrategy.isSelectedIndexingStrategy(properties));
        }
    }
}
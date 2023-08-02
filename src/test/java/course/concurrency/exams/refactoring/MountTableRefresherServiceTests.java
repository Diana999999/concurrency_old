package course.concurrency.exams.refactoring;

import org.junit.jupiter.api.*;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.*;

public class MountTableRefresherServiceTests {

    private MountTableRefresherService service;

    private Others.RouterStore routerStore;
    private Others.MountTableManager manager;
    private Others.LoadingCache routerClientsCache;

    @BeforeEach
    public void setUpStreams() {
        service = new MountTableRefresherService();
        service.setCacheUpdateTimeout(1000);
        routerStore = mock(Others.RouterStore.class);
        manager = mock(Others.MountTableManager.class);
        service.setRouterStore(routerStore);
        routerClientsCache = mock(Others.LoadingCache.class);
        service.setRouterClientsCache(routerClientsCache);
        // service.serviceInit(); // needed for complex class testing, not for now
    }

    @AfterEach
    public void restoreStreams() {
        // service.serviceStop();
    }

    @Test
    @DisplayName("All tasks are completed successfully")
    public void allDone() throws Exception {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        List<Others.RouterState> states = addresses.stream()
            .map(Others.RouterState::new)
            .collect(toList());

        when(mockedService.getManager(anyString())).thenReturn(manager);
        when(manager.refresh()).thenReturn(true);

        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=4,failureCount=0");
        verify(routerClientsCache, never()).invalidate(anyString());
    }

    @Test
    @DisplayName("All tasks failed")
    public void noSuccessfulTasks() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        List<Others.RouterState> states = addresses.stream()
            .map(Others.RouterState::new)
            .collect(toList());

        when(mockedService.getManager(anyString())).thenReturn(manager);
        when(manager.refresh()).thenReturn(false);

        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=0,failureCount=4");
        verify(routerClientsCache, times(4)).invalidate(anyString());
    }

    @Test
    @DisplayName("Some tasks failed")
    public void halfSuccessedTasks() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        List<Others.RouterState> states = addresses.stream()
            .map(Others.RouterState::new)
            .collect(toList());

        var failedManager = mock(Others.MountTableManager.class);
        var firstAddress = states.get(0).getAdminAddress();
        when(mockedService.getManager(firstAddress)).thenReturn(failedManager);
        when(failedManager.refresh()).thenReturn(false);

        when(mockedService.getManager(AdditionalMatchers.not(ArgumentMatchers.eq(firstAddress)))).thenReturn(manager);
        when(manager.refresh()).thenReturn(true);

        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=3,failureCount=1");
        verify(routerClientsCache, times(1)).invalidate(anyString());
    }

    @Test
    @DisplayName("One task completed with exception")
    public void exceptionInOneTask() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        List<Others.RouterState> states = addresses.stream()
            .map(Others.RouterState::new)
            .collect(toList());

        var failedManager = mock(Others.MountTableManager.class);
        var firstAddress = states.get(0).getAdminAddress();
        when(mockedService.getManager(firstAddress)).thenReturn(failedManager);
        when(failedManager.refresh()).thenThrow(new RuntimeException());

        when(mockedService.getManager(AdditionalMatchers.not(ArgumentMatchers.eq(firstAddress)))).thenReturn(manager);
        when(manager.refresh()).thenReturn(true);

        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=3,failureCount=1");
        verify(routerClientsCache, times(1)).invalidate(anyString());
    }

    @Test
    @DisplayName("One task exceeds timeout")
    public void oneTaskExceedTimeout() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        List<Others.RouterState> states = addresses.stream()
            .map(Others.RouterState::new)
            .collect(toList());

        var failedManager = mock(Others.MountTableManager.class);
        var firstAddress = states.get(0).getAdminAddress();
        when(mockedService.getManager(firstAddress)).thenReturn(failedManager);
        when(failedManager.refresh()).thenAnswer((Answer<Boolean>) invocation -> {
            Thread.sleep(2000);
            return true;
        });

        when(mockedService.getManager(AdditionalMatchers.not(ArgumentMatchers.eq(firstAddress)))).thenReturn(manager);
        when(manager.refresh()).thenReturn(true);

        when(routerStore.getCachedRecords()).thenReturn(states);
        // smth more

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=3,failureCount=1");
        verify(routerClientsCache, times(1)).invalidate(anyString());
    }

}

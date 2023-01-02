package top.kwseeker.samples.flux.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.kwseeker.samples.flux.model.Employee;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
@Repository
public class EmployeeRepository {

    public Mono<Employee> findEmployeeById(String id) {
        return Mono.fromSupplier(() -> new Employee("Arvin" + id));
    }

    public Flux<Employee> findAllEmployees() {
        return Flux.fromStream(IntStream.range(1, 5).mapToObj(i -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {}
            return new Employee("Arvin" + i);
        }));
    }
}

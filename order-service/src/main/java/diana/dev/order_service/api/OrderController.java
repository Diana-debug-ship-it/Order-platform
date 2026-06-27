package diana.dev.order_service.api;

import diana.dev.order_service.domain.OrderProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProcessor orderProcessor;

    @PostMapping
    public ResponseEntity<OrderDto> create(@RequestBody OrderDto orderDto) {

        log.info("Creating order {}", orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderProcessor.createOrder(orderDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(
            @PathVariable("id") Long id
    ) {
        log.info("Retrieving order by id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(orderProcessor.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAll() {
        log.info("Retrieving all orders");
        return ResponseEntity.status(HttpStatus.OK).body(orderProcessor.getAllOrders());
    }

}

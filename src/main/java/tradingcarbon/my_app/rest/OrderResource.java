package tradingcarbon.my_app.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tradingcarbon.my_app.model.OrderDTO;
import tradingcarbon.my_app.service.OrderService;


@RestController
@RequestMapping(value = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderResource {

    private final OrderService orderService;

    public OrderResource(final OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable(name = "orderId") final Long orderId) {
        return ResponseEntity.ok(orderService.get(orderId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createOrder(@RequestBody @Valid final OrderDTO orderDTO) {
        final Long createdOrderId = orderService.create(orderDTO);
        return new ResponseEntity<>(createdOrderId, HttpStatus.CREATED);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Long> updateOrder(@PathVariable(name = "orderId") final Long orderId,
            @RequestBody @Valid final OrderDTO orderDTO) {
        orderService.update(orderId, orderDTO);
        return ResponseEntity.ok(orderId);
    }

    @DeleteMapping("/{orderId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteOrder(@PathVariable(name = "orderId") final Long orderId) {
        orderService.delete(orderId);
        return ResponseEntity.noContent().build();
    }

}

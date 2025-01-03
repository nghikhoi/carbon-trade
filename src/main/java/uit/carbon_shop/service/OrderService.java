package uit.carbon_shop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uit.carbon_shop.domain.Order;
import uit.carbon_shop.model.OrderDTO;
import uit.carbon_shop.model.OrderStatus;
import uit.carbon_shop.repos.AppUserRepository;
import uit.carbon_shop.repos.OrderRepository;
import uit.carbon_shop.repos.ProjectRepository;
import uit.carbon_shop.util.NotFoundException;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProjectRepository projectRepository;
    private final AppUserRepository appUserRepository;
    private final OrderMapper orderMapper;

    public OrderService(final OrderRepository orderRepository,
            final ProjectRepository projectRepository, final AppUserRepository appUserRepository,
            final OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.projectRepository = projectRepository;
        this.appUserRepository = appUserRepository;
        this.orderMapper = orderMapper;
    }

    public Page<OrderDTO> findAll(final String filter, final Pageable pageable) {
        Page<Order> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = orderRepository.findAllById(longFilter, pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(order -> orderMapper.updateOrderDTO(order, new OrderDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public Page<OrderDTO> findAllCreatedBy(final Long userId, final Pageable pageable) {
        final Page<Order> page = orderRepository.findByCreatedBy_Id(userId, pageable);
        return new PageImpl<>(page.getContent()
                .stream()
                .map(order -> orderMapper.updateOrderDTO(order, new OrderDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public Page<OrderDTO> findAllByStatusAndCreatedBy(final OrderStatus status, final Long userId, final Pageable pageable) {
        final Page<Order> page = orderRepository.findByCreatedBy_IdAndStatus(userId, status, pageable);
        return new PageImpl<>(page.getContent()
                .stream()
                .map(order -> orderMapper.updateOrderDTO(order, new OrderDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public Page<OrderDTO> findByOwnerCompany(final Long companyId, final Pageable pageable) {
        final Page<Order> page = orderRepository.findByProject_OwnerCompany_Id(companyId, pageable);
        return new PageImpl<>(page.getContent()
                .stream()
                .map(order -> orderMapper.updateOrderDTO(order, new OrderDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public Page<OrderDTO> findByStatusAndOwnerCompany(final OrderStatus status, final Long companyId, final Pageable pageable) {
        final Page<Order> page = orderRepository.findByProject_OwnerCompany_IdAndStatus(companyId, status, pageable);
        return new PageImpl<>(page.getContent()
                .stream()
                .map(order -> orderMapper.updateOrderDTO(order, new OrderDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public Page<OrderDTO> findByStatus(OrderStatus status, Pageable pageable) {
        final Page<Order> page = orderRepository.findByStatus(status, pageable);
        return new PageImpl<>(page.getContent()
                .stream()
                .map(order -> orderMapper.updateOrderDTO(order, new OrderDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public OrderDTO get(final Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> orderMapper.updateOrderDTO(order, new OrderDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final OrderDTO orderDTO) {
        final Order order = new Order();
        order.setId(orderDTO.getOrderId());
        orderMapper.updateOrder(orderDTO, order, projectRepository, appUserRepository);
        return orderRepository.save(order).getId();
    }

    public void update(final Long orderId, final OrderDTO orderDTO) {
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);
        orderMapper.updateOrder(orderDTO, order, projectRepository, appUserRepository);
        orderRepository.save(order);
    }

    public void delete(final Long orderId) {
        orderRepository.deleteById(orderId);
    }

}

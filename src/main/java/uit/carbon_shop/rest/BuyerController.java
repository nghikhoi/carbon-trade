package uit.carbon_shop.rest;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uit.carbon_shop.model.BuyerCreateOrder;
import uit.carbon_shop.model.BuyerReviewCompanyDTO;
import uit.carbon_shop.model.BuyerReviewProjectDTO;
import uit.carbon_shop.model.CompanyDTO;
import uit.carbon_shop.model.CompanyReviewDTO;
import uit.carbon_shop.model.OrderDTO;
import uit.carbon_shop.model.OrderStatus;
import uit.carbon_shop.model.PagedOrderDTO;
import uit.carbon_shop.model.PagedProjectDTO;
import uit.carbon_shop.model.ProjectDTO;
import uit.carbon_shop.model.ProjectReviewDTO;
import uit.carbon_shop.model.ProjectStatus;
import uit.carbon_shop.model.UserRole;
import uit.carbon_shop.model.UserUserDetails;
import uit.carbon_shop.service.CompanyReviewService;
import uit.carbon_shop.service.CompanyService;
import uit.carbon_shop.service.IdGeneratorService;
import uit.carbon_shop.service.OrderService;
import uit.carbon_shop.service.ProjectReviewService;
import uit.carbon_shop.service.ProjectService;


@RestController
@RequestMapping(value = "/api/buyer", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyAuthority('" + UserRole.Fields.SELLER_OR_BUYER + "', '" + UserRole.Fields.MEDIATOR + "')")
@SecurityRequirement(name = "bearer-jwt")
@RequiredArgsConstructor
public class BuyerController {

    private final ProjectService projectService;
    private final OrderService orderService;
    private final ProjectReviewService projectReviewService;
    private final CompanyReviewService companyReviewService;
    private final CompanyService companyService;
    private final IdGeneratorService idGeneratorService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ProjectDTO> viewProject(
            @PathVariable(name = "projectId") final Long projectId) {
        return ResponseEntity.ok(projectService.get(projectId));
    }

    @PostMapping("/project/{projectId}/review")
    public ResponseEntity<Void> reviewProject(
            @PathVariable(name = "projectId") final Long projectId,
            @RequestBody @Valid final BuyerReviewProjectDTO buyerReviewProject,
            Authentication authentication) {
        ProjectReviewDTO projectReviewDTO = new ProjectReviewDTO();
        projectReviewDTO.setId(idGeneratorService.generateId());
        projectReviewDTO.setMessage(buyerReviewProject.getMessage());
        projectReviewDTO.setRate(buyerReviewProject.getRate());
        projectReviewDTO.setImages(buyerReviewProject.getImages());
        projectReviewDTO.setProject(projectId);
        projectReviewDTO.setReviewBy(((UserUserDetails) authentication.getPrincipal()).getUserId());
        projectReviewService.create(projectReviewDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/projects")
    public ResponseEntity<PagedProjectDTO> viewAllProject(
            @RequestParam(name = "status", required = false) final ProjectStatus status,
            @RequestParam(name = "filter", required = false) final String filter,
            @Parameter(hidden = true) @SortDefault(sort = "projectId") @PageableDefault(size = 20) final Pageable pageable) {
        Page<ProjectDTO> page = status == null ? projectService.findAll(filter, pageable)
                : projectService.findByStatus(status, filter, pageable);
        return ResponseEntity.ok(new PagedProjectDTO(page));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderDTO> viewOrder(@PathVariable(name = "orderId") final Long orderId) {
        return ResponseEntity.ok(orderService.get(orderId));
    }

    @PostMapping("/order")
    public ResponseEntity<OrderDTO> newOrder(
            @RequestBody @Valid final BuyerCreateOrder buyerCreateOrder,
            Authentication authentication) {
        final ProjectDTO project = projectService.get(buyerCreateOrder.getProjectId());
        OrderDTO orderDTO = new OrderDTO();
        long orderId = idGeneratorService.generateId();
        orderDTO.setOrderId(orderId);
        orderDTO.setPrice(project.getPrice());
        orderDTO.setCreditAmount(project.getCreditAmount());
        orderDTO.setProject(project.getProjectId());
        orderDTO.setCreatedBy(((UserUserDetails) authentication.getPrincipal()).getUserId());
        orderDTO.setStatus(OrderStatus.INIT);
        orderService.create(orderDTO);
        return ResponseEntity.ok(orderService.get(orderId));
    }

    @GetMapping("/orders")
    public ResponseEntity<PagedOrderDTO> viewAllOrders(
            @RequestParam(name = "status", required = false) OrderStatus status,
            @RequestParam(name = "filter", required = false) final String filter,
            @Parameter(hidden = true) @SortDefault(sort = "projectId") @PageableDefault(size = 20) final Pageable pageable,
            Authentication authentication) {
        Page<OrderDTO> page = status == null ? orderService.findAllCreatedBy(((UserUserDetails) authentication.getPrincipal()).getUserId(), pageable)
                : orderService.findAllByStatusAndCreatedBy(status, ((UserUserDetails) authentication.getPrincipal()).getUserId(), pageable);
        return ResponseEntity.ok(new PagedOrderDTO(page));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<CompanyDTO> viewCompany(
            @PathVariable(name = "companyId") final Long companyId) {
        return ResponseEntity.ok(companyService.get(companyId));
    }

    @PostMapping("/company/{companyId}/review")
    public ResponseEntity<Void> reviewCompany(
            @PathVariable(name = "companyId") final Long companyId,
            @RequestBody @Valid final BuyerReviewCompanyDTO buyerReviewCompany,
            Authentication authentication) {
        CompanyReviewDTO companyReviewDTO = new CompanyReviewDTO();
        companyReviewDTO.setId(idGeneratorService.generateId());
        companyReviewDTO.setMessage(buyerReviewCompany.getMessage());
        companyReviewDTO.setRate(buyerReviewCompany.getRate());
        companyReviewDTO.setImages(buyerReviewCompany.getImages());
        companyReviewDTO.setCompany(companyId);
        companyReviewDTO.setReviewBy(((UserUserDetails) authentication.getPrincipal()).getUserId());
        companyReviewService.create(companyReviewDTO);
        return ResponseEntity.ok().build();
    }

}

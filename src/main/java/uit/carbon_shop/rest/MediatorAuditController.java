package uit.carbon_shop.rest;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uit.carbon_shop.model.AppUserDTO;
import uit.carbon_shop.model.CompanyDTO;
import uit.carbon_shop.model.MediatorAnswerDTO;
import uit.carbon_shop.model.MediatorDoneOrderDTO;
import uit.carbon_shop.model.OrderDTO;
import uit.carbon_shop.model.OrderStatus;
import uit.carbon_shop.model.PagedAppUserDTO;
import uit.carbon_shop.model.PagedOrderDTO;
import uit.carbon_shop.model.PagedProjectDTO;
import uit.carbon_shop.model.PagedQuestionDTO;
import uit.carbon_shop.model.ProjectDTO;
import uit.carbon_shop.model.ProjectStatus;
import uit.carbon_shop.model.QuestionDTO;
import uit.carbon_shop.model.UserRole;
import uit.carbon_shop.model.UserStatus;
import uit.carbon_shop.model.UserUserDetails;
import uit.carbon_shop.service.AppUserService;
import uit.carbon_shop.service.CompanyService;
import uit.carbon_shop.service.OrderService;
import uit.carbon_shop.service.ProjectService;
import uit.carbon_shop.service.QuestionService;

@RestController
@RequestMapping(value = "/api/mediator/audit", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAuthority('" + UserRole.Fields.MEDIATOR + "')")
@SecurityRequirement(name = "bearer-jwt")
@RequiredArgsConstructor
public class MediatorAuditController {

    private final OrderService orderService;
    private final AppUserService appUserService;
    private final ProjectService projectService;
    private final QuestionService questionService;
    private final CompanyService companyService;

    @PatchMapping("/order/{orderId}/process")
    public ResponseEntity<OrderDTO> startProcessOrder(
            @PathVariable(name = "orderId") final Long orderId) {
        var order = orderService.get(orderId);
        order.setStatus(OrderStatus.PROCESSING);
        orderService.update(orderId, order);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/order/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelProcessOrder(
            @PathVariable(name = "orderId") final Long orderId) {
        var order = orderService.get(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        orderService.update(orderId, order);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/order/{orderId}/done")
    public ResponseEntity<OrderDTO> doneProcessOrder(
            @PathVariable(name = "orderId") final Long orderId,
            @RequestBody @Valid MediatorDoneOrderDTO mediatorDoneOrderDTO
    ) {
        var order = orderService.get(orderId);
        order.setStatus(OrderStatus.DONE);
        order.setContractFile(mediatorDoneOrderDTO.getContractFile());
        order.setCertImages(mediatorDoneOrderDTO.getCertImages());
        order.setPaymentBillFile(mediatorDoneOrderDTO.getPaymentBillFile());
        orderService.update(orderId, order);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderDTO> viewOrder(
            @PathVariable(name = "orderId") final Long orderId) {
        return ResponseEntity.ok(orderService.get(orderId));
    }

    @GetMapping("/orders")
    public ResponseEntity<PagedOrderDTO> viewAllOrder(
            @RequestParam(name = "status", required = false) OrderStatus status,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        Page<OrderDTO> page =
                status == null ? orderService.findAll(null, pageable) : orderService.findByStatus(status, pageable);
        return ResponseEntity.ok(new PagedOrderDTO(page));
    }

    @GetMapping("/users/init")
    public ResponseEntity<PagedAppUserDTO> viewAllUser(
            @RequestParam(name = "status", required = false) final UserStatus status,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        Page<AppUserDTO> page = status == null ? appUserService.findAll(null, pageable)
                : appUserService.findByStatus(status, pageable);
        return ResponseEntity.ok(new PagedAppUserDTO(page));
    }

    @PatchMapping("/user/{userId}/approve")
    public ResponseEntity<AppUserDTO> approveUserRegistration(
            @PathVariable(name = "userId") final Long userId) {
        var appUser = appUserService.get(userId);
        appUser.setApprovedAt(LocalDateTime.now());
        appUser.setStatus(UserStatus.APPROVED);
        appUserService.update(userId, appUser);
        return ResponseEntity.ok(appUser);
    }

    @PatchMapping("/user/{userId}/reject")
    public ResponseEntity<AppUserDTO> rejectUserRegistration(
            @PathVariable(name = "userId") final Long userId) {
        var appUser = appUserService.get(userId);
        appUser.setRejectedAt(LocalDateTime.now());
        appUser.setStatus(UserStatus.REJECTED);
        appUserService.update(userId, appUser);
        return ResponseEntity.ok(appUser);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<AppUserDTO> viewUser(
            @PathVariable(name = "userId") final Long userId) {
        return ResponseEntity.ok(appUserService.get(userId));
    }

    @GetMapping("/projects")
    public ResponseEntity<PagedProjectDTO> viewAllProject(
            @RequestParam(name = "status", required = false) final ProjectStatus status,
            @RequestParam(name = "filter", required = false) final String filter,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        Page<ProjectDTO> page = status == null ? projectService.findAll(filter, pageable)
                : projectService.findByStatus(status, filter, pageable);
        return ResponseEntity.ok(new PagedProjectDTO(page));
    }

    @PatchMapping("/project/{projectId}/approve")
    public ResponseEntity<Void> approveProject(
            @PathVariable(name = "projectId") final Long projectId,
            Authentication authentication) {
        var userId = ((UserUserDetails) authentication.getPrincipal()).getUserId();
        var project = projectService.get(projectId);
        project.setStatus(ProjectStatus.APPROVED);
        project.setAuditBy(userId);
        projectService.update(projectId, project);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/project/{projectId}/reject")
    public ResponseEntity<Void> rejectProject(
            @PathVariable(name = "projectId") final Long projectId) {
        var project = projectService.get(projectId);
        project.setStatus(ProjectStatus.REJECTED);
        projectService.update(projectId, project);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ProjectDTO> viewProject(
            @PathVariable(name = "projectId") final Long projectId) {
        return ResponseEntity.ok(projectService.get(projectId));
    }

    @GetMapping("/questions/init")
    public ResponseEntity<PagedQuestionDTO> viewAllQuestion(
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(new PagedQuestionDTO(questionService.findByAnswerIsNull(pageable)));
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<QuestionDTO> viewQuestion(
            @PathVariable(name = "questionId") final Long questionId) {
        return ResponseEntity.ok(questionService.get(questionId));
    }

    @PatchMapping("/question/{questionId}")
    public ResponseEntity<Void> answerQuestion(
            @PathVariable(name = "questionId") final Long questionId,
            @RequestBody @Valid final MediatorAnswerDTO mediatorAnswerDTO) {
        var question = questionService.get(questionId);
        question.setAnswer(mediatorAnswerDTO.getAnswer());
        questionService.update(questionId, question);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/question/{questionId}/answer")
    public ResponseEntity<Void> deleteQuestionAnswer(
            @PathVariable(name = "questionId") final Long questionId) {
        var question = questionService.get(questionId);
        question.setAnswer(null);
        questionService.update(questionId, question);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<CompanyDTO> viewCompany(
            @PathVariable(name = "companyId") final Long companyId) {
        return ResponseEntity.ok(companyService.get(companyId));
    }

}

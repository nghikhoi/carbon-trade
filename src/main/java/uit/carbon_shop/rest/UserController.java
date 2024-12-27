package uit.carbon_shop.rest;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uit.carbon_shop.model.AppUserDTO;
import uit.carbon_shop.model.ChatMessageDTO;
import uit.carbon_shop.model.CompanyDTO;
import uit.carbon_shop.model.ContactItemDTO;
import uit.carbon_shop.model.LikeResultDTO;
import uit.carbon_shop.model.PagedChatMessageDTO;
import uit.carbon_shop.model.PagedCompanyReviewDTO;
import uit.carbon_shop.model.PagedContactItemDTO;
import uit.carbon_shop.model.PagedProjectDTO;
import uit.carbon_shop.model.PagedProjectReviewDTO;
import uit.carbon_shop.model.PagedQuestionDTO;
import uit.carbon_shop.model.ProjectDTO;
import uit.carbon_shop.model.QuestionDTO;
import uit.carbon_shop.model.SendChatMessageDTO;
import uit.carbon_shop.model.UserAskDTO;
import uit.carbon_shop.model.UserRole;
import uit.carbon_shop.model.UserUserDetails;
import uit.carbon_shop.service.AppUserService;
import uit.carbon_shop.service.ChatMessageService;
import uit.carbon_shop.service.CompanyReviewService;
import uit.carbon_shop.service.CompanyService;
import uit.carbon_shop.service.IdGeneratorService;
import uit.carbon_shop.service.ProjectReviewService;
import uit.carbon_shop.service.ProjectService;
import uit.carbon_shop.service.QuestionService;


@RestController
@RequestMapping(value = "/api/user", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyAuthority('" + UserRole.Fields.SELLER_OR_BUYER + "', '" + UserRole.Fields.MEDIATOR + "')")
@SecurityRequirement(name = "bearer-jwt")
@RequiredArgsConstructor
public class UserController {

    private final QuestionService questionService;
    private final IdGeneratorService idGeneratorService;
    private final ProjectService projectService;
    private final CompanyReviewService companyReviewService;
    private final ProjectReviewService projectReviewService;
    private final AppUserService appUserService;
    private final CompanyService companyService;
    private final ChatMessageService chatMessageService;

    @PostMapping("/question")
    public ResponseEntity<QuestionDTO> newQuestion(@RequestBody @Valid final UserAskDTO userAskDTO,
            Authentication authentication) {
        var userId = ((UserUserDetails) authentication.getPrincipal()).getUserId();
        var question = new QuestionDTO();
        question.setId(idGeneratorService.generateId());
        question.setQuestion(userAskDTO.getQuestion());
        question.setAskedBy(userId);
        questionService.create(question);
        return ResponseEntity.ok(question);
    }

    @GetMapping("/questions")
    public ResponseEntity<PagedQuestionDTO> viewQuestions(
            @RequestParam(name = "self", required = false) final Boolean self,
            @RequestParam(name = "answered", required = false) final Boolean answered,
            @Parameter(hidden = true) @PageableDefault(size = 20) final Pageable pageable,
            Authentication authentication
    ) {
        var userId = ((UserUserDetails) authentication.getPrincipal()).getUserId();
        Page<QuestionDTO> page;
        if (self == null || !self) {
            page = answered == null ?
                    questionService.findAll(null, pageable)
                    : answered ? questionService.findByAnswerIsNotNull(pageable)
                            : questionService.findByAnswerIsNull(pageable);
        } else {
            page = answered == null ?
                    questionService.findByAskedBy(userId, pageable)
                    : answered ? questionService.findByAnswerIsNotNullAndAskedBy(userId, pageable)
                            : questionService.findByAnswerIsNullAndAskedBy(userId, pageable);
        }
        return ResponseEntity.ok(new PagedQuestionDTO(page));
    }

    @GetMapping("/projects")
    public ResponseEntity<PagedProjectDTO> viewAllProject(
            @RequestParam(name = "filter", required = false) final String filter,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable
    ) {
        return ResponseEntity.ok(new PagedProjectDTO(projectService.findAll(filter, pageable)));
    }

    @GetMapping("/project/{projectId}/reviews")
    public ResponseEntity<PagedProjectReviewDTO> viewProjectReviews(
            @PathVariable(name = "projectId") final Long projectId,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable
    ) {
        return ResponseEntity.ok(new PagedProjectReviewDTO(projectReviewService.findAllByProject(projectId, pageable)));
    }

    @GetMapping("/company/{companyId}/reviews")
    public ResponseEntity<PagedCompanyReviewDTO> viewCompanyReviews(
            @PathVariable(name = "companyId") final Long companyId,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable
    ) {
        return ResponseEntity.ok(new PagedCompanyReviewDTO(companyReviewService.findAllByCompany(companyId, pageable)));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ProjectDTO> viewProject(
            @PathVariable(name = "projectId") final Long projectId) {
        return ResponseEntity.ok(projectService.get(projectId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AppUserDTO> viewUser(
            @PathVariable(name = "userId") final Long userId) {
        return ResponseEntity.ok(appUserService.get(userId));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<CompanyDTO> viewCompany(
            @PathVariable(name = "companyId") final Long companyId) {
        return ResponseEntity.ok(companyService.get(companyId));
    }

    @GetMapping("/company/{companyId}/user")
    public ResponseEntity<AppUserDTO> viewCompanyUser(
            @PathVariable(name = "companyId") final Long companyId) {
        return ResponseEntity.ok(appUserService.findByCompany(companyId));
    }

    @GetMapping("/chat/conversations")
    public ResponseEntity<PagedContactItemDTO> getConversations(Authentication authentication,
            @Parameter(hidden = true) @PageableDefault(size = 20) final Pageable pageable) {
        var userId = ((UserUserDetails) authentication.getPrincipal()).getUserId();
        return ResponseEntity.ok(new PagedContactItemDTO(
                chatMessageService.findConversation(userId, pageable)
                        .map(conversationId -> {
                            var contactItem = new ContactItemDTO();
                            var latestMessage = chatMessageService.getLatestMessage(conversationId);
                            contactItem.setConversationId(conversationId);
                            contactItem.setChatUserId(
                                    latestMessage.getReceiver().equals(userId) ? latestMessage.getSender()
                                            : latestMessage.getReceiver());
                            contactItem.setLatestMessage(latestMessage);
                            return contactItem;
                        })
        ));
    }

    @GetMapping("/chat/conversation/{conversationId}/latest")
    public ResponseEntity<ChatMessageDTO> getLatestMessage(
            @PathVariable(name = "conversationId") final UUID conversationId) {
        return ResponseEntity.ok(chatMessageService.getLatestMessage(conversationId));
    }

    @GetMapping("/chat/conversation/{conversationId}")
    public ResponseEntity<PagedChatMessageDTO> getConversationMessages(
            @PathVariable(name = "conversationId") final UUID conversationId,
            @Parameter(hidden = true) @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(
                new PagedChatMessageDTO(chatMessageService.getConversationMessages(conversationId, pageable)));
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestBody @Valid final SendChatMessageDTO sendChatMessageDTO,
            Authentication authentication) {
        var userId = ((UserUserDetails) authentication.getPrincipal()).getUserId();
        var conversationId = chatMessageService.findConversation(userId, sendChatMessageDTO.getReceiver())
                .orElse(UUID.randomUUID());
        var chatMessage = new ChatMessageDTO();
        chatMessage.setId(idGeneratorService.generateId());
        chatMessage.setContent(sendChatMessageDTO.getContent());
        chatMessage.setFileId(sendChatMessageDTO.getFileId());
        chatMessage.setImageId(sendChatMessageDTO.getImageId());
        chatMessage.setVideoId(sendChatMessageDTO.getVideoId());
        chatMessage.setAudioId(sendChatMessageDTO.getAudioId());
        chatMessage.setConversationId(conversationId);
        chatMessage.setSender(userId);
        chatMessage.setReceiver(sendChatMessageDTO.getReceiver());
        var messageId = chatMessageService.create(chatMessage);
        return ResponseEntity.ok(chatMessageService.get(messageId));
    }

    @PatchMapping("/company/review/{reviewId}/like")
    public ResponseEntity<LikeResultDTO> likeCompanyReview(
            @PathVariable(name = "reviewId") final Long reviewId,
            Authentication authentication
    ) {
        LikeResultDTO resultDTO = new LikeResultDTO();
        var userId = ((UserUserDetails) authentication.getPrincipal()).getUserId();
        var appUser = appUserService.get(userId);
        if (appUser.getLikedCompanyReviews() != null && appUser.getLikedCompanyReviews().contains(reviewId)) {
            appUser.getLikedCompanyReviews().remove(reviewId);
            appUserService.update(appUser.getUserId(), appUser);

            var likeCount = companyReviewService.getLikeCount(reviewId);
            resultDTO.setLikeCount(likeCount);
            resultDTO.setLike(false);
        } else {
            if (appUser.getLikedCompanyReviews() == null) {
                appUser.setLikedCompanyReviews(new ArrayList<>());
            }
            appUser.getLikedCompanyReviews().add(reviewId);
            appUserService.update(appUser.getUserId(), appUser);

            var likeCount = companyReviewService.getLikeCount(reviewId);
            resultDTO.setLikeCount(likeCount);
            resultDTO.setLike(true);
        }
        return ResponseEntity.ok(resultDTO);
    }

    @PatchMapping("/project/review/{reviewId}/like")
    public ResponseEntity<LikeResultDTO> likeProjectReview(
            @PathVariable(name = "reviewId") final Long reviewId,
            Authentication authentication
    ) {
        LikeResultDTO resultDTO = new LikeResultDTO();
        var userId = ((UserUserDetails) authentication.getPrincipal()).getUserId();
        var appUser = appUserService.get(userId);
        if (appUser.getLikeProjectReviews() != null && appUser.getLikeProjectReviews().contains(reviewId)) {
            appUser.getLikeProjectReviews().remove(reviewId);
            appUserService.update(appUser.getUserId(), appUser);

            var likeCount = projectReviewService.getLikeCount(reviewId);
            resultDTO.setLikeCount(likeCount);
            resultDTO.setLike(false);
        } else {
            if (appUser.getLikeProjectReviews() == null) {
                appUser.setLikeProjectReviews(new ArrayList<>());
            }
            appUser.getLikeProjectReviews().add(reviewId);
            appUserService.update(appUser.getUserId(), appUser);

            var likeCount = projectReviewService.getLikeCount(reviewId);
            resultDTO.setLikeCount(likeCount);
            resultDTO.setLike(true);
        }
        return ResponseEntity.ok(resultDTO);
    }

}

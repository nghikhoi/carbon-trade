package uit.carbon_shop.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uit.carbon_shop.domain.AppUser;
import uit.carbon_shop.domain.ChatMessage;
import uit.carbon_shop.domain.CompanyReview;
import uit.carbon_shop.domain.Order;
import uit.carbon_shop.domain.Project;
import uit.carbon_shop.domain.ProjectReview;
import uit.carbon_shop.domain.Question;
import uit.carbon_shop.model.AppUserDTO;
import uit.carbon_shop.repos.AppUserRepository;
import uit.carbon_shop.repos.ChatMessageRepository;
import uit.carbon_shop.repos.CompanyRepository;
import uit.carbon_shop.repos.CompanyReviewRepository;
import uit.carbon_shop.repos.OrderRepository;
import uit.carbon_shop.repos.ProjectRepository;
import uit.carbon_shop.repos.ProjectReviewRepository;
import uit.carbon_shop.repos.QuestionRepository;
import uit.carbon_shop.util.NotFoundException;
import uit.carbon_shop.util.ReferencedWarning;


@Service
@Transactional
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final CompanyRepository companyRepository;
    private final ProjectRepository projectRepository;
    private final CompanyReviewRepository companyReviewRepository;
    private final ProjectReviewRepository projectReviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserMapper appUserMapper;
    private final OrderRepository orderRepository;
    private final QuestionRepository questionRepository;
    private final ChatMessageRepository chatMessageRepository;

    public AppUserService(final AppUserRepository appUserRepository,
            final CompanyRepository companyRepository, final ProjectRepository projectRepository,
            final CompanyReviewRepository companyReviewRepository,
            final ProjectReviewRepository projectReviewRepository,
            final PasswordEncoder passwordEncoder, final AppUserMapper appUserMapper,
            final OrderRepository orderRepository, final QuestionRepository questionRepository,
            final ChatMessageRepository chatMessageRepository) {
        this.appUserRepository = appUserRepository;
        this.companyRepository = companyRepository;
        this.projectRepository = projectRepository;
        this.companyReviewRepository = companyReviewRepository;
        this.projectReviewRepository = projectReviewRepository;
        this.passwordEncoder = passwordEncoder;
        this.appUserMapper = appUserMapper;
        this.orderRepository = orderRepository;
        this.questionRepository = questionRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public Page<AppUserDTO> findAll(final String filter, final Pageable pageable) {
        Page<AppUser> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = appUserRepository.findAllById(longFilter, pageable);
        } else {
            page = appUserRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(appUser -> appUserMapper.updateAppUserDTO(appUser, new AppUserDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public AppUserDTO get(final Long id) {
        return appUserRepository.findById(id)
                .map(appUser -> appUserMapper.updateAppUserDTO(appUser, new AppUserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AppUserDTO appUserDTO) {
        final AppUser appUser = new AppUser();
        appUserMapper.updateAppUser(appUserDTO, appUser, companyRepository, projectRepository, companyReviewRepository, projectReviewRepository, passwordEncoder);
        return appUserRepository.save(appUser).getId();
    }

    public void update(final Long id, final AppUserDTO appUserDTO) {
        final AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        appUserMapper.updateAppUser(appUserDTO, appUser, companyRepository, projectRepository, companyReviewRepository, projectReviewRepository, passwordEncoder);
        appUserRepository.save(appUser);
    }

    public void delete(final Long id) {
        appUserRepository.deleteById(id);
    }

    public boolean companyExists(final Long id) {
        return appUserRepository.existsByCompanyId(id);
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Project auditByProject = projectRepository.findFirstByAuditBy(appUser);
        if (auditByProject != null) {
            referencedWarning.setKey("appUser.project.auditBy.referenced");
            referencedWarning.addParam(auditByProject.getId());
            return referencedWarning;
        }
        final Order processByOrder = orderRepository.findFirstByProcessBy(appUser);
        if (processByOrder != null) {
            referencedWarning.setKey("appUser.order.processBy.referenced");
            referencedWarning.addParam(processByOrder.getId());
            return referencedWarning;
        }
        final Order createdByOrder = orderRepository.findFirstByCreatedBy(appUser);
        if (createdByOrder != null) {
            referencedWarning.setKey("appUser.order.createdBy.referenced");
            referencedWarning.addParam(createdByOrder.getId());
            return referencedWarning;
        }
        final CompanyReview reviewByCompanyReview = companyReviewRepository.findFirstByReviewBy(appUser);
        if (reviewByCompanyReview != null) {
            referencedWarning.setKey("appUser.companyReview.reviewBy.referenced");
            referencedWarning.addParam(reviewByCompanyReview.getId());
            return referencedWarning;
        }
        final ProjectReview reviewByProjectReview = projectReviewRepository.findFirstByReviewBy(appUser);
        if (reviewByProjectReview != null) {
            referencedWarning.setKey("appUser.projectReview.reviewBy.referenced");
            referencedWarning.addParam(reviewByProjectReview.getId());
            return referencedWarning;
        }
        final Question askedByQuestion = questionRepository.findFirstByAskedBy(appUser);
        if (askedByQuestion != null) {
            referencedWarning.setKey("appUser.question.askedBy.referenced");
            referencedWarning.addParam(askedByQuestion.getId());
            return referencedWarning;
        }
        final ChatMessage senderChatMessage = chatMessageRepository.findFirstBySender(appUser);
        if (senderChatMessage != null) {
            referencedWarning.setKey("appUser.chatMessage.sender.referenced");
            referencedWarning.addParam(senderChatMessage.getId());
            return referencedWarning;
        }
        final ChatMessage receiverChatMessage = chatMessageRepository.findFirstByReceiver(appUser);
        if (receiverChatMessage != null) {
            referencedWarning.setKey("appUser.chatMessage.receiver.referenced");
            referencedWarning.addParam(receiverChatMessage.getId());
            return referencedWarning;
        }
        return null;
    }

}

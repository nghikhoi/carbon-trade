package uit.carbon_shop.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uit.carbon_shop.domain.Order;
import uit.carbon_shop.domain.Project;
import uit.carbon_shop.domain.ProjectReview;
import uit.carbon_shop.model.ProjectDTO;
import uit.carbon_shop.repos.AppUserRepository;
import uit.carbon_shop.repos.CompanyRepository;
import uit.carbon_shop.repos.OrderRepository;
import uit.carbon_shop.repos.ProjectRepository;
import uit.carbon_shop.repos.ProjectReviewRepository;
import uit.carbon_shop.util.NotFoundException;
import uit.carbon_shop.util.ReferencedWarning;


@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CompanyRepository companyRepository;
    private final AppUserRepository appUserRepository;
    private final ProjectMapper projectMapper;
    private final OrderRepository orderRepository;
    private final ProjectReviewRepository projectReviewRepository;

    public ProjectService(final ProjectRepository projectRepository,
            final CompanyRepository companyRepository, final AppUserRepository appUserRepository,
            final ProjectMapper projectMapper, final OrderRepository orderRepository,
            final ProjectReviewRepository projectReviewRepository) {
        this.projectRepository = projectRepository;
        this.companyRepository = companyRepository;
        this.appUserRepository = appUserRepository;
        this.projectMapper = projectMapper;
        this.orderRepository = orderRepository;
        this.projectReviewRepository = projectReviewRepository;
    }

    public Page<ProjectDTO> findAll(final String filter, final Pageable pageable) {
        Page<Project> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = projectRepository.findAllById(longFilter, pageable);
        } else {
            page = projectRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(project -> projectMapper.updateProjectDTO(project, new ProjectDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public ProjectDTO get(final Long id) {
        return projectRepository.findById(id)
                .map(project -> projectMapper.updateProjectDTO(project, new ProjectDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ProjectDTO projectDTO) {
        final Project project = new Project();
        projectMapper.updateProject(projectDTO, project, companyRepository, appUserRepository);
        return projectRepository.save(project).getId();
    }

    public void update(final Long id, final ProjectDTO projectDTO) {
        final Project project = projectRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        projectMapper.updateProject(projectDTO, project, companyRepository, appUserRepository);
        projectRepository.save(project);
    }

    public void delete(final Long id) {
        final Project project = projectRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        appUserRepository.findAllByFavoriteProjects(project)
                .forEach(appUser -> appUser.getFavoriteProjects().remove(project));
        projectRepository.delete(project);
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Project project = projectRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Order projectOrder = orderRepository.findFirstByProject(project);
        if (projectOrder != null) {
            referencedWarning.setKey("project.order.project.referenced");
            referencedWarning.addParam(projectOrder.getId());
            return referencedWarning;
        }
        final ProjectReview projectProjectReview = projectReviewRepository.findFirstByProject(project);
        if (projectProjectReview != null) {
            referencedWarning.setKey("project.projectReview.project.referenced");
            referencedWarning.addParam(projectProjectReview.getId());
            return referencedWarning;
        }
        return null;
    }

}

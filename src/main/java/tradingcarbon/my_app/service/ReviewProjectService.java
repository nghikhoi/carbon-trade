package tradingcarbon.my_app.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tradingcarbon.my_app.domain.Project;
import tradingcarbon.my_app.domain.ReviewProject;
import tradingcarbon.my_app.model.ReviewProjectDTO;
import tradingcarbon.my_app.repos.ProjectRepository;
import tradingcarbon.my_app.repos.ReviewProjectRepository;
import tradingcarbon.my_app.util.NotFoundException;
import tradingcarbon.my_app.util.ReferencedWarning;


@Service
public class ReviewProjectService {

    private final ReviewProjectRepository reviewProjectRepository;
    private final ProjectRepository projectRepository;

    public ReviewProjectService(final ReviewProjectRepository reviewProjectRepository,
            final ProjectRepository projectRepository) {
        this.reviewProjectRepository = reviewProjectRepository;
        this.projectRepository = projectRepository;
    }

    public List<ReviewProjectDTO> findAll() {
        final List<ReviewProject> reviewProjects = reviewProjectRepository.findAll(Sort.by("reviewProjectId"));
        return reviewProjects.stream()
                .map(reviewProject -> mapToDTO(reviewProject, new ReviewProjectDTO()))
                .toList();
    }

    public ReviewProjectDTO get(final Long reviewProjectId) {
        return reviewProjectRepository.findById(reviewProjectId)
                .map(reviewProject -> mapToDTO(reviewProject, new ReviewProjectDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ReviewProjectDTO reviewProjectDTO) {
        final ReviewProject reviewProject = new ReviewProject();
        mapToEntity(reviewProjectDTO, reviewProject);
        return reviewProjectRepository.save(reviewProject).getReviewProjectId();
    }

    public void update(final Long reviewProjectId, final ReviewProjectDTO reviewProjectDTO) {
        final ReviewProject reviewProject = reviewProjectRepository.findById(reviewProjectId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(reviewProjectDTO, reviewProject);
        reviewProjectRepository.save(reviewProject);
    }

    public void delete(final Long reviewProjectId) {
        reviewProjectRepository.deleteById(reviewProjectId);
    }

    private ReviewProjectDTO mapToDTO(final ReviewProject reviewProject,
            final ReviewProjectDTO reviewProjectDTO) {
        reviewProjectDTO.setReviewProjectId(reviewProject.getReviewProjectId());
        reviewProjectDTO.setText(reviewProject.getText());
        reviewProjectDTO.setStar(reviewProject.getStar());
        reviewProjectDTO.setReviewImage(reviewProject.getReviewImage());
        return reviewProjectDTO;
    }

    private ReviewProject mapToEntity(final ReviewProjectDTO reviewProjectDTO,
            final ReviewProject reviewProject) {
        reviewProject.setText(reviewProjectDTO.getText());
        reviewProject.setStar(reviewProjectDTO.getStar());
        reviewProject.setReviewImage(reviewProjectDTO.getReviewImage());
        return reviewProject;
    }

    public ReferencedWarning getReferencedWarning(final Long reviewProjectId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final ReviewProject reviewProject = reviewProjectRepository.findById(reviewProjectId)
                .orElseThrow(NotFoundException::new);
        final Project reviewProjectIdProject = projectRepository.findFirstByReviewProjectId(reviewProject);
        if (reviewProjectIdProject != null) {
            referencedWarning.setKey("reviewProject.project.reviewProjectId.referenced");
            referencedWarning.addParam(reviewProjectIdProject.getProjectId());
            return referencedWarning;
        }
        return null;
    }

}

package uit.carbon_shop.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uit.carbon_shop.domain.AppUser;
import uit.carbon_shop.domain.Company;
import uit.carbon_shop.domain.Project;
import uit.carbon_shop.model.ProjectStatus;


public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findAllById(Long projectId, Pageable pageable);

    Page<Project> findByOwnerCompany_Id(Long id, Pageable pageable);

    Page<Project> findByOwnerCompany_IdAndStatus(Long id, ProjectStatus status, Pageable pageable);

    Page<Project> findByOwnerCompany_IdAndNameContainsIgnoreCase(Long id, String name, Pageable pageable);

    Page<Project> findByOwnerCompany_IdAndStatusAndNameContainsIgnoreCase(Long id, ProjectStatus status, String name, Pageable pageable);

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    Page<Project> findByStatusAndNameContainsIgnoreCase(ProjectStatus status, String name, Pageable pageable);

    Project findFirstByOwnerCompany(Company company);

    Project findFirstByAuditBy(AppUser appUser);

}

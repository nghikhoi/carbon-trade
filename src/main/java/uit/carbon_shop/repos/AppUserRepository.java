package uit.carbon_shop.repos;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uit.carbon_shop.domain.AppUser;
import uit.carbon_shop.domain.Company;
import uit.carbon_shop.domain.CompanyReview;
import uit.carbon_shop.domain.Project;
import uit.carbon_shop.domain.ProjectReview;


public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByEmailIgnoreCase(String email);

    AppUser findByResetPasswordUid(String resetPasswordUid);

    Page<AppUser> findAllById(Long id, Pageable pageable);

    boolean existsByEmailIgnoreCase(String email);

    AppUser findFirstByCompany(Company company);

    AppUser findFirstByFavoriteProjects(Project project);

    AppUser findFirstByLikedCompanyReviews(CompanyReview companyReview);

    AppUser findFirstByLikeProjectReviews(ProjectReview projectReview);

    List<AppUser> findAllByFavoriteProjects(Project project);

    List<AppUser> findAllByLikedCompanyReviews(CompanyReview companyReview);

    List<AppUser> findAllByLikeProjectReviews(ProjectReview projectReview);

    boolean existsByCompanyId(Long id);

}

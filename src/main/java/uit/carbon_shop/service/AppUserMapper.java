package uit.carbon_shop.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import uit.carbon_shop.domain.AppUser;
import uit.carbon_shop.domain.Company;
import uit.carbon_shop.domain.CompanyReview;
import uit.carbon_shop.domain.Project;
import uit.carbon_shop.domain.ProjectReview;
import uit.carbon_shop.model.AppUserDTO;
import uit.carbon_shop.repos.CompanyRepository;
import uit.carbon_shop.repos.CompanyReviewRepository;
import uit.carbon_shop.repos.ProjectRepository;
import uit.carbon_shop.repos.ProjectReviewRepository;
import uit.carbon_shop.util.NotFoundException;
import uit.carbon_shop.util.StaticConstants;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AppUserMapper {

    @Mapping(source = "id", target = "userId")
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "favoriteProjects", ignore = true)
    @Mapping(target = "likedCompanyReviews", ignore = true)
    @Mapping(target = "likeProjectReviews", ignore = true)
    @Mapping(target = "password", ignore = true)
    AppUserDTO updateAppUserDTO(AppUser appUser, @MappingTarget AppUserDTO appUserDTO);

    @AfterMapping
    default void afterUpdateAppUserDTO(AppUser appUser, @MappingTarget AppUserDTO appUserDTO) {
        appUserDTO.setCompany(appUser.getCompany() == null ? null : appUser.getCompany().getId());
        appUserDTO.setFavoriteProjects(appUser.getFavoriteProjects().stream()
                .map(project -> project.getId())
                .toList());
        appUserDTO.setLikedCompanyReviews(appUser.getLikedCompanyReviews().stream()
                .map(companyReview -> companyReview.getId())
                .toList());
        appUserDTO.setLikeProjectReviews(appUser.getLikeProjectReviews().stream()
                .map(projectReview -> projectReview.getId())
                .toList());
        if (appUserDTO.getAvatar() == null) {
            appUserDTO.setAvatar(StaticConstants.DEFAULT_AVATAR_ID);
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "favoriteProjects", ignore = true)
    @Mapping(target = "likedCompanyReviews", ignore = true)
    @Mapping(target = "likeProjectReviews", ignore = true)
    @Mapping(target = "password", ignore = true)
    AppUser updateAppUser(AppUserDTO appUserDTO, @MappingTarget AppUser appUser,
            @Context CompanyRepository companyRepository,
            @Context ProjectRepository projectRepository,
            @Context CompanyReviewRepository companyReviewRepository,
            @Context ProjectReviewRepository projectReviewRepository,
            @Context PasswordEncoder passwordEncoder);

    @AfterMapping
    default void afterUpdateAppUser(AppUserDTO appUserDTO, @MappingTarget AppUser appUser,
            @Context CompanyRepository companyRepository,
            @Context ProjectRepository projectRepository,
            @Context CompanyReviewRepository companyReviewRepository,
            @Context ProjectReviewRepository projectReviewRepository,
            @Context PasswordEncoder passwordEncoder) {
        final Company company = appUserDTO.getCompany() == null ? null : companyRepository.findById(appUserDTO.getCompany())
                .orElseThrow(() -> new NotFoundException("company not found"));
        appUser.setCompany(company);
        final List<Project> favoriteProjects = projectRepository.findAllById(
                appUserDTO.getFavoriteProjects() == null ? Collections.emptyList() : appUserDTO.getFavoriteProjects());
        if (favoriteProjects.size() != (appUserDTO.getFavoriteProjects() == null ? 0 : appUserDTO.getFavoriteProjects().size())) {
            throw new NotFoundException("one of favoriteProjects not found");
        }
        appUser.setFavoriteProjects(new HashSet<>(favoriteProjects));
        final List<CompanyReview> likedCompanyReviews = companyReviewRepository.findAllById(
                appUserDTO.getLikedCompanyReviews() == null ? Collections.emptyList() : appUserDTO.getLikedCompanyReviews());
        if (likedCompanyReviews.size() != (appUserDTO.getLikedCompanyReviews() == null ? 0 : appUserDTO.getLikedCompanyReviews().size())) {
            throw new NotFoundException("one of likedCompanyReviews not found");
        }
        appUser.setLikedCompanyReviews(new HashSet<>(likedCompanyReviews));
        final List<ProjectReview> likeProjectReviews = projectReviewRepository.findAllById(
                appUserDTO.getLikeProjectReviews() == null ? Collections.emptyList() : appUserDTO.getLikeProjectReviews());
        if (likeProjectReviews.size() != (appUserDTO.getLikeProjectReviews() == null ? 0 : appUserDTO.getLikeProjectReviews().size())) {
            throw new NotFoundException("one of likeProjectReviews not found");
        }
        appUser.setLikeProjectReviews(new HashSet<>(likeProjectReviews));
        appUser.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        if (appUser.getAvatar() == null) {
            appUser.setAvatar(StaticConstants.DEFAULT_AVATAR_ID);
        }
    }

}

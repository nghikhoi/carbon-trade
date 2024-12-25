package uit.carbon_shop.model;

import org.springframework.data.domain.Page;

public class PagedProjectReviewDTO extends PagedModel<ProjectReviewDTO> {

    public PagedProjectReviewDTO(Page<ProjectReviewDTO> delegate) {
        super(delegate);
    }

}

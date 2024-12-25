package uit.carbon_shop.model;

import org.springframework.data.domain.Page;

public class PagedCompanyReviewDTO extends PagedModel<CompanyReviewDTO> {

    public PagedCompanyReviewDTO(Page<CompanyReviewDTO> delegate) {
        super(delegate);
    }

}

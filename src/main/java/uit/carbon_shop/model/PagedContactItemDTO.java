package uit.carbon_shop.model;

import org.springframework.data.domain.Page;

public class PagedContactItemDTO extends PagedModel<ContactItemDTO> {

    public PagedContactItemDTO(Page<ContactItemDTO> delegate) {
        super(delegate);
    }

}

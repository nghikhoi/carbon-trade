package uit.carbon_shop.model;

import java.util.UUID;
import org.springframework.data.domain.Page;

public class PagedUUIDDTO extends PagedModel<UUID> {

    public PagedUUIDDTO(Page<UUID> delegate) {
        super(delegate);
    }

}

package uit.carbon_shop.model;

import org.springframework.data.domain.Page;

public class PagedChatMessageDTO extends PagedModel<ChatMessageDTO> {

    public PagedChatMessageDTO(Page<ChatMessageDTO> delegate) {
        super(delegate);
    }

}

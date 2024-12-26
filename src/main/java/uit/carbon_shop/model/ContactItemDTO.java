package uit.carbon_shop.model;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactItemDTO {

    private UUID conversationId;

    private long chatUserId;

    private ChatMessageDTO latestMessage;

}

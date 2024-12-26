package uit.carbon_shop.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Projection for {@link uit.carbon_shop.domain.ChatMessage}
 */
public interface ChatMessageConversationIdProjection {

    UUID getConversationId();

    OffsetDateTime getCreatedAt();

}
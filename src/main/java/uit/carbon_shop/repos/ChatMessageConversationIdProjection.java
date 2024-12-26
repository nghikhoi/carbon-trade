package uit.carbon_shop.repos;

import java.util.UUID;

/**
 * Projection for {@link uit.carbon_shop.domain.ChatMessage}
 */
public interface ChatMessageConversationIdProjection {

    UUID getConversationId();
}
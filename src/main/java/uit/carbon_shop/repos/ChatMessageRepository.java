package uit.carbon_shop.repos;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uit.carbon_shop.domain.AppUser;
import uit.carbon_shop.domain.ChatMessage;
import uit.carbon_shop.domain.ChatMessageConversationIdProjection;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findAllById(Long id, Pageable pageable);

    Page<ChatMessageConversationIdProjection> findDistinctBySender_IdOrReceiver_Id(Long id, Long id1,
            Pageable pageable);

    Page<ChatMessage> findByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);

    Optional<ChatMessage> findFirstByConversationIdOrderByCreatedAtDesc(UUID conversationId);

    Optional<ChatMessage> findFirstBySender_IdAndReceiver_Id(Long id, Long id1);

    ChatMessage findFirstBySender(AppUser appUser);

    ChatMessage findFirstByReceiver(AppUser appUser);

}

package uit.carbon_shop.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uit.carbon_shop.domain.AppUser;
import uit.carbon_shop.domain.ChatMessage;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findAllById(Long id, Pageable pageable);

    ChatMessage findFirstBySender(AppUser appUser);

    ChatMessage findFirstByReceiver(AppUser appUser);

}

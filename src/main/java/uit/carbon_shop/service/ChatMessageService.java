package uit.carbon_shop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uit.carbon_shop.domain.ChatMessage;
import uit.carbon_shop.model.ChatMessageDTO;
import uit.carbon_shop.repos.AppUserRepository;
import uit.carbon_shop.repos.ChatMessageRepository;
import uit.carbon_shop.util.NotFoundException;


@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final AppUserRepository appUserRepository;
    private final ChatMessageMapper chatMessageMapper;

    public ChatMessageService(final ChatMessageRepository chatMessageRepository,
            final AppUserRepository appUserRepository, final ChatMessageMapper chatMessageMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.appUserRepository = appUserRepository;
        this.chatMessageMapper = chatMessageMapper;
    }

    public Page<ChatMessageDTO> findAll(final String filter, final Pageable pageable) {
        Page<ChatMessage> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = chatMessageRepository.findAllById(longFilter, pageable);
        } else {
            page = chatMessageRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(chatMessage -> chatMessageMapper.updateChatMessageDTO(chatMessage, new ChatMessageDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public ChatMessageDTO get(final Long id) {
        return chatMessageRepository.findById(id)
                .map(chatMessage -> chatMessageMapper.updateChatMessageDTO(chatMessage, new ChatMessageDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ChatMessageDTO chatMessageDTO) {
        final ChatMessage chatMessage = new ChatMessage();
        chatMessageMapper.updateChatMessage(chatMessageDTO, chatMessage, appUserRepository, appUserRepository);
        return chatMessageRepository.save(chatMessage).getId();
    }

    public void update(final Long id, final ChatMessageDTO chatMessageDTO) {
        final ChatMessage chatMessage = chatMessageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        chatMessageMapper.updateChatMessage(chatMessageDTO, chatMessage, appUserRepository, appUserRepository);
        chatMessageRepository.save(chatMessage);
    }

    public void delete(final Long id) {
        chatMessageRepository.deleteById(id);
    }

}
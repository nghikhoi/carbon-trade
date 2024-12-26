package uit.carbon_shop.service;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uit.carbon_shop.domain.AppUser;
import uit.carbon_shop.domain.ChatMessage;
import uit.carbon_shop.model.ChatMessageDTO;
import uit.carbon_shop.repos.AppUserRepository;
import uit.carbon_shop.util.NotFoundException;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ChatMessageMapper {

    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    ChatMessageDTO updateChatMessageDTO(ChatMessage chatMessage,
            @MappingTarget ChatMessageDTO chatMessageDTO);

    @AfterMapping
    default void afterUpdateChatMessageDTO(ChatMessage chatMessage,
            @MappingTarget ChatMessageDTO chatMessageDTO) {
        chatMessageDTO.setSender(chatMessage.getSender() == null ? null : chatMessage.getSender().getId());
        chatMessageDTO.setReceiver(chatMessage.getReceiver() == null ? null : chatMessage.getReceiver().getId());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    ChatMessage updateChatMessage(ChatMessageDTO chatMessageDTO,
            @MappingTarget ChatMessage chatMessage, @Context AppUserRepository appUserRepository);

    @AfterMapping
    default void afterUpdateChatMessage(ChatMessageDTO chatMessageDTO,
            @MappingTarget ChatMessage chatMessage, @Context AppUserRepository appUserRepository) {
        final AppUser sender = chatMessageDTO.getSender() == null ? null : appUserRepository.findById(chatMessageDTO.getSender())
                .orElseThrow(() -> new NotFoundException("sender not found"));
        chatMessage.setSender(sender);
        final AppUser receiver = chatMessageDTO.getReceiver() == null ? null : appUserRepository.findById(chatMessageDTO.getReceiver())
                .orElseThrow(() -> new NotFoundException("receiver not found"));
        chatMessage.setReceiver(receiver);
    }

}

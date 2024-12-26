package uit.carbon_shop.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChatMessageDTO {

    private Long id;

    @Size(max = 255)
    private String content;

    private Long fileId;

    private Long imageId;

    private Long videoId;

    private Long audioId;

    @NotNull
    private UUID conversationId;

    private Long sender;

    private Long receiver;

    private LocalDateTime createdAt;

}

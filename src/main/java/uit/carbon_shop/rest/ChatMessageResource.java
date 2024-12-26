package uit.carbon_shop.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uit.carbon_shop.model.ChatMessageDTO;
import uit.carbon_shop.service.ChatMessageService;


@RestController
@RequestMapping(value = "/api/chatMessages", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatMessageResource {

    private final ChatMessageService chatMessageService;

    public ChatMessageResource(final ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @Operation(
            parameters = {
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "sort",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = String.class)
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<ChatMessageDTO>> getAllChatMessages(
            @RequestParam(name = "filter", required = false) final String filter,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(chatMessageService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatMessageDTO> getChatMessage(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(chatMessageService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createChatMessage(
            @RequestBody @Valid final ChatMessageDTO chatMessageDTO) {
        final Long createdId = chatMessageService.create(chatMessageDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateChatMessage(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ChatMessageDTO chatMessageDTO) {
        chatMessageService.update(id, chatMessageDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteChatMessage(@PathVariable(name = "id") final Long id) {
        chatMessageService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

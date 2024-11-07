package tradingcarbon.my_app.model;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReviewProjectDTO {

    private Long reviewProjectId;

    @Size(max = 255)
    private String text;

    @Size(max = 255)
    private String star;

    private List<@Size(max = 255) String> reviewImage;

}

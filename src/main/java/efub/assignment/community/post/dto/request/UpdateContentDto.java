package efub.assignment.community.post.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateContentDto {
    private String content;

    public void setContent(String content) {
        this.content = content;
    }
}

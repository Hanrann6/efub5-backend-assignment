package efub.assignment.community.member.dto.request;

import efub.assignment.community.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDto {

    @NotBlank(message = "학번을 입력해주세요.")
    @Size(max = 20, message = "학번은 최대 20자까지 입력할 수 있습니다.")
    private String studentId;

    @NotBlank(message = "학교를 입력해주세요.")
    @Size(max = 100, message = "학교는 최대 100자까지 입력할 수 있습니다.")
    private String university;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 1, max = 8, message = "닉네임은 1자 이상 8자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "메일을 입력해주세요.")
    @Size(max = 100, message = "메일은 최대 100자까지 입력할 수 있습니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{16,}$",
            message = "비밀번호는 16자 이상이며, 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    // Member 객체로 build
    public Member toEntity() {
        return Member.builder()
                .studentId(studentId)
                .university(university)
                .nickname(nickname)
                .email(email)
                .password(password)
                .build();
    }
}

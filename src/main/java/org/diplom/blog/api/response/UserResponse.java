package org.diplom.blog.api.response;

import com.sun.istack.Nullable;
import lombok.Getter;
import org.diplom.blog.dto.UserInfo;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends SimpleResponse {
    private final UserInfo user;

    public UserResponse() {
        this(null, false);
    }

    public UserResponse(UserInfo user) {
        this(user, true);
    }

    public UserResponse(@Nullable UserInfo user, boolean result) {
        super(result);
        this.user = user;
    }
}

package org.diplom.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

/**
 * @author Andrey.Kazakov
 * @date 08.08.2020
 */
@Data
/*@Builder*/
public class SettingsResponse extends HashMap<String, Boolean> {

    /*@JsonProperty("MULTIUSER_MODE")
    private boolean multipleuserMode;

    @JsonProperty("POST_PREMODERATION")
    private boolean postPremoderation;

    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticsIsPublic;*/

}

package org.diplom.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

/**
 * @author Andrey.Kazakov
 * @date 08.08.2020
 */
@Data
public class SettingsDto extends HashMap<String, Boolean> {

}

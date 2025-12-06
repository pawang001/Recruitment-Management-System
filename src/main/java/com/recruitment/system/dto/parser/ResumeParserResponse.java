package com.recruitment.system.dto.parser;
import lombok.Data;
import java.util.List;
import java.util.Map;
@Data
public class ResumeParserResponse {
    private List<Map<String, Object>> education;
    private String email;
    private List<Map<String, Object>> experience;
    private String name;
    private String phone;
    private List<String> skills;
}

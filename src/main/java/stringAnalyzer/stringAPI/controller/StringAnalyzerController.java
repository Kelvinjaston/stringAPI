package stringAnalyzer.stringAPI.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stringAnalyzer.stringAPI.exception.CustomExceptions;
import stringAnalyzer.stringAPI.service.StringAnalyzerService;
import stringAnalyzer.stringAPI.stringAnalysisModel.StringAnalysis;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/strings")
public class StringAnalyzerController {
    private final StringAnalyzerService service;

    @Data
    private static class StringRequest{
        private Object value;
    }
    @Data
    private static class NaturalLanguageResponse {
        private List<StringAnalysis> data;
        private int count;
        @JsonProperty("interpreted_query")
        private InterpretedQuery interpretedQuery;

        @Data
        private static class InterpretedQuery {
            private String original;
            @JsonProperty("parsed_filters")
            private Map<String, Object> parsedFilters;
        }
    }
    @Data
    private static class FilteredResponse {
        private List<StringAnalysis> data;
        private int count;
        @JsonProperty("filters_applied")
        private Map<String, Object> filtersApplied;
    }
    @PostMapping
    public ResponseEntity<StringAnalysis> createStringAnalysis(@RequestBody StringRequest request) {
        if (request.getValue() == null) {
            throw new CustomExceptions.InvalidInputException("Missing 'value' field in request body.");
        }
        if (!(request.getValue() instanceof String)) {
            throw new CustomExceptions.InvalidInputException("Invalid data type for 'value'. It must be a string.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        StringAnalysis result = service.createOrAnalyzeString((String) request.getValue());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    @GetMapping("/{string_value}")
    public ResponseEntity<StringAnalysis> getStringAnalysis(@PathVariable("string_value") String stringValue) {
        StringAnalysis result = service.getByValue(stringValue);
        return ResponseEntity.ok(result);
    }
    @GetMapping
    public ResponseEntity<FilteredResponse> getAllStrings(
            @RequestParam(value = "is_palindrome", required = false) Boolean isPalindrome,
            @RequestParam(value = "min_length", required = false) Integer minLength,
            @RequestParam(value = "max_length", required = false) Integer maxLength,
            @RequestParam(value = "word_count", required = false) Integer wordCount,
            @RequestParam(value = "contains_character", required = false) String containsCharacterStr) {

        Character containsCharacter = null;
        if (containsCharacterStr != null) {
            if (containsCharacterStr.length() != 1) {
                throw new CustomExceptions.InvalidInputException("Query parameter 'contains_character' must be a single character.");
            }
            containsCharacter = containsCharacterStr.charAt(0);
        }
        Map<String, Object> filters = new java.util.HashMap<>();
        if (isPalindrome != null) filters.put("is_palindrome", isPalindrome);
        if (minLength != null) filters.put("min_length", minLength);
        if (maxLength != null) filters.put("max_length", maxLength);
        if (wordCount != null) filters.put("word_count", wordCount);
        if (containsCharacter != null) filters.put("contains_character", containsCharacter);

        List<StringAnalysis> data = service.findAllFiltered(
                isPalindrome, minLength, maxLength, wordCount, containsCharacter);
        FilteredResponse response = new FilteredResponse();
        response.setData(data);
        response.setCount(data.size());
        response.setFiltersApplied(filters);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/filter-by-natural-language")
    public ResponseEntity<NaturalLanguageResponse> filterByNaturalLanguage(@RequestParam("query") String query) {
        Map<String, Object> parsedFilters = service.parseNaturalLanguageQuery(query);

        Boolean isPalindrome = (Boolean) parsedFilters.get("is_palindrome");
        Integer minLength = (Integer) parsedFilters.get("min_length");
        Integer maxLength = (Integer) parsedFilters.get("max_length");
        Integer wordCount = (Integer) parsedFilters.get("word_count");
        Character containsCharacter = (Character) parsedFilters.get("contains_character");
        List<StringAnalysis> data = service.findAllFiltered(
                isPalindrome, minLength, maxLength, wordCount, containsCharacter);

        NaturalLanguageResponse.InterpretedQuery iq = new NaturalLanguageResponse.InterpretedQuery();
        iq.setOriginal(query);
        iq.setParsedFilters(parsedFilters);
        NaturalLanguageResponse response = new NaturalLanguageResponse();
        response.setData(data);
        response.setCount(data.size());
        response.setInterpretedQuery(iq);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{string_value}")
    public ResponseEntity<Void> deleteStringAnalysis(@PathVariable("string_value") String stringValue) {
        service.deleteByValue(stringValue);
        return ResponseEntity.noContent().build();
    }
}
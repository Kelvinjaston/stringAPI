package stringAnalyzer.stringAPI.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stringAnalyzer.stringAPI.repo.StringAnalysisRepository;
import stringAnalyzer.stringAPI.stringAnalysisModel.StringAnalysis;
import stringAnalyzer.stringAPI.stringAnalysisModel.StringProperties;
import static stringAnalyzer.stringAPI.exception.CustomExceptions.ConflictException;
import static stringAnalyzer.stringAPI.exception.CustomExceptions.NotFoundException;


import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class StringAnalyzerService {

    private final StringAnalysisRepository stringAnalysisRepository;

    private  StringProperties calculateProperties(String value,String hash){
        int length = value.length();
        boolean isPalindrome = checkPalindrome(value);
        Map<Character,Integer> frequencyMap =getChartFrequencyMap(value);
        int uniqueCharacters = frequencyMap.size();
        int wordCount = getWordCount(value);

        return StringProperties.builder()
                .length(length)
                .isPalindrome(isPalindrome)
                .uniqueCharacters(uniqueCharacters)
                .wordCount(wordCount)
                .sha256Hash(hash)
                .characterFrequencyMap(frequencyMap)
                .build();
    }
    private boolean checkPalindrome( String value){
        String cleaned = value.replaceAll("[^a-zA-Z0-9]","").toLowerCase();
        String reversed = new StringBuilder(cleaned).reverse().toString();
        return cleaned.equals(reversed);
    }
    private Map<Character,Integer>getChartFrequencyMap(String value){
        Map<Character,Integer>frequencyMap = new ConcurrentHashMap<>();
        for (char c : value.toCharArray()){
            frequencyMap.put(c,frequencyMap.getOrDefault(c,0) + 1);
        }
        return frequencyMap;
    }
    private int getWordCount(String value){
        if (value==null || value.isEmpty()){
            return 0;
        }
        String [] words = value.trim().split("\\s+");
        return words.length;
    }
    public String sha256Hash(String value){
        return DigestUtils.sha256Hex(value);
    }
    @Transactional
    public StringAnalysis createOrAnalyzeString(String value){
        String hash = sha256Hash(value);
        if (stringAnalysisRepository.existsById(hash)){
            throw new ConflictException("String analysis for value already exists: " + value);
        }
        StringProperties properties = calculateProperties(value,hash);
        StringAnalysis analysis = StringAnalysis.builder()
                .id(hash)
                .value(value)
                .properties(properties)
                .createdAt(Instant.now())
                .build();
        return stringAnalysisRepository.save(analysis);
    }
    @Transactional(readOnly = true)
    public StringAnalysis getByValue(String value){
        String hash = sha256Hash(value);
        return stringAnalysisRepository.findById(hash)
                .orElseThrow(()->new NotFoundException("String analysis not found for value."));
    }
    @Transactional(readOnly = true)
    public List<StringAnalysis>findAllFiltered(
            Boolean isPalindrome,Integer minLength,Integer maxLength,Integer wordCount,Character containsCharacter){
        List<StringAnalysis> allData = stringAnalysisRepository.findAll();
        return allData.stream()
                .filter(stringAnalysis -> {
                    StringProperties p = stringAnalysis.getProperties();
                    if (isPalindrome != null && p.isPalindrome() !=isPalindrome){
                        return false;
                    }
                    if (minLength != null && p.getLength()< minLength){
                        return false;
                    }
                    if (maxLength != null && p.getLength() > maxLength){
                        return false;
                    }
                    if (wordCount != null && p.getWordCount() != wordCount){
                        return false;
                    }
                    if (containsCharacter != null){
                        return  stringAnalysis.getValue().toLowerCase().contains(containsCharacter.toString().toLowerCase());
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
    public Map<String,Object> parseNaturalLanguageQuery(String query) {
        Map<String, Object> filters = new ConcurrentHashMap<>();
        String lowerQuery = query.toLowerCase();
        if (lowerQuery.contains("palindrome") || lowerQuery.contains("palindromic")) {
            filters.put("is_palindrome", true);
        }
        if (lowerQuery.contains("single word")) {
            filters.put("word_count", 1);
        } else if (lowerQuery.contains("two word")) {
            filters.put("word_count", 2);
        } else if (lowerQuery.contains("three word")) {
            filters.put("word_count", 3);
        } else {
            Matcher wordCountMatcher = Pattern.compile("word count (\\d+)|(\\d+)\\s+words").matcher(lowerQuery);
            if (wordCountMatcher.find()) {
                String countStr = wordCountMatcher.group(1) != null ? wordCountMatcher.group(1) : wordCountMatcher.group(2);
                try {
                    filters.put("word_count", Integer.parseInt(countStr));
                } catch (NumberFormatException ignored) {}
            }
        }
        Pattern lengthPattern = Pattern.compile("(longer\\s+than|min(?:imum)?\\s+length|shorter\\s+than|max(?:imum)?\\s+length|exactly)\\s+(\\d+)");
        Matcher lengthMatcher = lengthPattern.matcher(lowerQuery);

        while (lengthMatcher.find()) {
            String type = lengthMatcher.group(1);
            int value = Integer.parseInt(lengthMatcher.group(2));
            if (type.contains("longer than") || type.contains("min")) {
                int min = value + (type.contains("longer") ? 1 : 0);
                filters.put("min_length", min);
            } else if (type.contains("shorter than") || type.contains("max")) {
                int max = value - (type.contains("shorter") ? 1 : 0);
                filters.put("max_length", max);
            } else if (type.contains("exactly")) {
                filters.put("min_length", value);
                filters.put("max_length", value);
            }
        }
        Pattern charPattern = Pattern.compile("contain[s]?(?:\\s+(?:the\\s+letter|character))?\\s+([a-z])");
        Matcher charMatcher = charPattern.matcher(lowerQuery);
        if (charMatcher.find()) {
            filters.put("contains_character", charMatcher.group(1).charAt(0));
        } else if (lowerQuery.contains("first vowel")){
            filters.put("contains_character", 'a');
        }
        if (filters.containsKey("min_length") && filters.containsKey("max_length")){
            int min = (int)filters.get("min_length");
            int max = (int)filters.get("max_length");
            if (min > max){
                throw new ConflictException("Query parsed but resulted in conflicting filters: minimum length (" + min + ") is greater than maximum length (" + max + ").");
            }
        }
        if (filters.isEmpty()){
            throw new ConflictException("Unable to parse natural language query into usable filters.");
        }
        return filters;
    }
    @Transactional
    public void deleteByValue(String value){
        String hash = sha256Hash(value);
        if (!stringAnalysisRepository.existsById(hash)){
            throw new NotFoundException("String analysis not found for value.");
        }
        stringAnalysisRepository.deleteById(hash);
    }
}

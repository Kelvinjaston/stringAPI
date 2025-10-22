package stringAnalyzer.stringAPI.stringAnalysisModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StringProperties {

    private int length;

    @JsonProperty("is_palindrome")
    private boolean isPalindrome;

    @JsonProperty("unique_characters")
    private int uniqueCharacters;

    @JsonProperty("word_count")
    private  int wordCount;

    @JsonProperty("sha256_hash")
    private String sha256Hash;
    @ElementCollection
    @CollectionTable(name = "char_frequency",joinColumns = @JoinColumn(name = "analysis_id"))
    @MapKeyColumn(name = "character_key", length =1)
    @Column(name = "count")
    @JsonProperty("character_frequency_map")
    private Map<Character,Integer> characterFrequencyMap;

}

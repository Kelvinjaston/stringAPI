package stringAnalyzer.stringAPI.stringAnalysisModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Entity
@Table(name = "string_analysis")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StringAnalysis {
    @Id
    private String id;

    @Lob
    @Column(name = "string_value")
    private String value;

    @Embedded
    private StringProperties properties;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss 'Z'",timezone = "UTC")
    private Instant createdAt;
}

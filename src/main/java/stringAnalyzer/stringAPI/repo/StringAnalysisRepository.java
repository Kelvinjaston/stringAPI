package stringAnalyzer.stringAPI.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import stringAnalyzer.stringAPI.stringAnalysisModel.StringAnalysis;

public interface StringAnalysisRepository extends JpaRepository<StringAnalysis,String> {


}

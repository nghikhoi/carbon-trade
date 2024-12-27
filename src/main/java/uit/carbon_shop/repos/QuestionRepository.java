package uit.carbon_shop.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uit.carbon_shop.domain.AppUser;
import uit.carbon_shop.domain.Question;


public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findAllById(Long id, Pageable pageable);

    Question findFirstByAskedBy(AppUser appUser);

    Page<Question> findByAnswerNull(Pageable pageable);

    Page<Question> findByAnswerNotNull(Pageable pageable);

    Page<Question> findByAskedBy_Id(Long id, Pageable pageable);

    Page<Question> findByAnswerNotNullAndAskedBy_Id(Long id, Pageable pageable);

    Page<Question> findByAnswerNullAndAskedBy_Id(Long id, Pageable pageable);

}

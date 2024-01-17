package ru.practicum.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.Compilation;

import java.util.List;
@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Integer> {
    @Query("SELECT c FROM Compilation AS c WHERE (c.pinned = :pinned OR :pinned = null)")
    List<Compilation> findByPinned(Boolean pinned, Pageable pageable);

    Boolean existsByTitle(String title);
}
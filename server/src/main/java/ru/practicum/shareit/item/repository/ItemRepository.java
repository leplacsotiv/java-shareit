package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @EntityGraph(attributePaths = {"owner", "request"})
    List<Item> findByRequestIdInOrderByIdAsc(Collection<Long> requestIds);

    @EntityGraph(attributePaths = {"owner", "request"})
    List<Item> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("""
            select i
            from Item i
            where i.available = true
              and (
                    lower(i.name) like lower(concat('%', :text, '%'))
                    or lower(i.description) like lower(concat('%', :text, '%'))
              )
            order by i.id asc
            """)
    List<Item> searchAvailableByText(String text, Pageable pageable);
}

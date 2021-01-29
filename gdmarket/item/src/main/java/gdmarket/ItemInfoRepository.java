package gdmarket;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemInfoRepository extends CrudRepository<ItemInfo, Long> {
    List<ItemInfo> findByItemNo(Integer itemNo);
    void deleteByItemNo(Integer itemNo);
}